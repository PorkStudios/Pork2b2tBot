/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostMultipartRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private String multipartDataBoundary;
    private String multipartMixedBoundary;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
    private Map<CharSequence, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold = 10485760;
    private static final String FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';

    public HttpPostMultipartRequestDecoder(HttpRequest request) {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostMultipartRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        this.request = ObjectUtil.checkNotNull(request, "request");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.factory = ObjectUtil.checkNotNull(factory, "factory");
        this.setMultipart(this.request.headers().get(HttpHeaderNames.CONTENT_TYPE));
        if (request instanceof HttpContent) {
            this.offer((HttpContent)((Object)request));
        } else {
            this.undecodedChunk = Unpooled.buffer();
            this.parseBody();
        }
    }

    private void setMultipart(String contentType) {
        String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentType);
        if (dataBoundary != null) {
            this.multipartDataBoundary = dataBoundary[0];
            if (dataBoundary.length > 1 && dataBoundary[1] != null) {
                this.charset = Charset.forName(dataBoundary[1]);
            }
        } else {
            this.multipartDataBoundary = null;
        }
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
    }

    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }

    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return true;
    }

    @Override
    public void setDiscardThreshold(int discardThreshold) {
        this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
    }

    @Override
    public int getDiscardThreshold() {
        return this.discardThreshold;
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas() {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyListHttpData;
    }

    @Override
    public List<InterfaceHttpData> getBodyHttpDatas(String name) {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyMapHttpData.get(name);
    }

    @Override
    public InterfaceHttpData getBodyHttpData(String name) {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public HttpPostMultipartRequestDecoder offer(HttpContent content) {
        this.checkDestroyed();
        ByteBuf buf = content.content();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = buf.copy();
        } else {
            this.undecodedChunk.writeBytes(buf);
        }
        if (content instanceof LastHttpContent) {
            this.isLastChunk = true;
        }
        this.parseBody();
        if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
            this.undecodedChunk.discardReadBytes();
        }
        return this;
    }

    @Override
    public boolean hasNext() {
        this.checkDestroyed();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new HttpPostRequestDecoder.EndOfDataDecoderException();
        }
        return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
    }

    @Override
    public InterfaceHttpData next() {
        this.checkDestroyed();
        if (this.hasNext()) {
            return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
        }
        return null;
    }

    @Override
    public InterfaceHttpData currentPartialHttpData() {
        if (this.currentFileUpload != null) {
            return this.currentFileUpload;
        }
        return this.currentAttribute;
    }

    private void parseBody() {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            return;
        }
        this.parseBodyMultipart();
    }

    protected void addHttpData(InterfaceHttpData data) {
        if (data == null) {
            return;
        }
        List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
        if (datas == null) {
            datas = new ArrayList<InterfaceHttpData>(1);
            this.bodyMapHttpData.put(data.getName(), datas);
        }
        datas.add(data);
        this.bodyListHttpData.add(data);
    }

    private void parseBodyMultipart() {
        if (this.undecodedChunk == null || this.undecodedChunk.readableBytes() == 0) {
            return;
        }
        InterfaceHttpData data = this.decodeMultipart(this.currentStatus);
        while (data != null) {
            this.addHttpData(data);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) break;
            data = this.decodeMultipart(this.currentStatus);
        }
    }

    private InterfaceHttpData decodeMultipart(HttpPostRequestDecoder.MultiPartStatus state) {
        switch (state) {
            case NOTSTARTED: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case PREAMBLE: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case HEADERDELIMITER: {
                return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
            }
            case DISPOSITION: {
                return this.findMultipartDisposition();
            }
            case FIELD: {
                Charset localCharset = null;
                Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName(charsetAttribute.getValue());
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (UnsupportedCharsetException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                }
                Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
                if (this.currentAttribute == null) {
                    long size;
                    Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
                    try {
                        size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (NumberFormatException ignored) {
                        size = 0L;
                    }
                    try {
                        this.currentAttribute = size > 0L ? this.factory.createAttribute(this.request, HttpPostMultipartRequestDecoder.cleanString(nameAttribute.getValue()), size) : this.factory.createAttribute(this.request, HttpPostMultipartRequestDecoder.cleanString(nameAttribute.getValue()));
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    if (localCharset != null) {
                        this.currentAttribute.setCharset(localCharset);
                    }
                }
                if (!HttpPostMultipartRequestDecoder.loadDataMultipart(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute)) {
                    return null;
                }
                Attribute finalAttribute = this.currentAttribute;
                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                return finalAttribute;
            }
            case FILEUPLOAD: {
                return this.getFileUpload(this.multipartDataBoundary);
            }
            case MIXEDDELIMITER: {
                return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            }
            case MIXEDDISPOSITION: {
                return this.findMultipartDisposition();
            }
            case MIXEDFILEUPLOAD: {
                return this.getFileUpload(this.multipartMixedBoundary);
            }
            case PREEPILOGUE: {
                return null;
            }
            case EPILOGUE: {
                return null;
            }
        }
        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
    }

    private static void skipControlCharacters(ByteBuf undecodedChunk) {
        if (!undecodedChunk.hasArray()) {
            try {
                HttpPostMultipartRequestDecoder.skipControlCharactersStandard(undecodedChunk);
            }
            catch (IndexOutOfBoundsException e1) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e1);
            }
            return;
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
        while (sao.pos < sao.limit) {
            char c;
            if (Character.isISOControl(c = (char)(sao.bytes[sao.pos++] & 255)) || Character.isWhitespace(c)) continue;
            sao.setReadPosition(1);
            return;
        }
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
    }

    private static void skipControlCharactersStandard(ByteBuf undecodedChunk) {
        char c;
        while (Character.isISOControl(c = (char)undecodedChunk.readUnsignedByte()) || Character.isWhitespace(c)) {
        }
        undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
    }

    private InterfaceHttpData findMultipartDelimiter(String delimiter, HttpPostRequestDecoder.MultiPartStatus dispositionStatus, HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) {
        String newline;
        int readerIndex = this.undecodedChunk.readerIndex();
        try {
            HttpPostMultipartRequestDecoder.skipControlCharacters(this.undecodedChunk);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        this.skipOneLine();
        try {
            newline = HttpPostMultipartRequestDecoder.readDelimiter(this.undecodedChunk, delimiter);
        }
        catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        if (newline.equals(delimiter)) {
            this.currentStatus = dispositionStatus;
            return this.decodeMultipart(dispositionStatus);
        }
        if (newline.equals(delimiter + "--")) {
            this.currentStatus = closeDelimiterStatus;
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
                this.currentFieldAttributes = null;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            }
            return null;
        }
        this.undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
    }

    private InterfaceHttpData findMultipartDisposition() {
        int readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap<CharSequence, Attribute>(CaseIgnoringComparator.INSTANCE);
        }
        while (!this.skipOneLine()) {
            String newline;
            Attribute attribute;
            Object values;
            try {
                HttpPostMultipartRequestDecoder.skipControlCharacters(this.undecodedChunk);
                newline = HttpPostMultipartRequestDecoder.readLine(this.undecodedChunk, this.charset);
            }
            catch (HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
            String[] contents = HttpPostMultipartRequestDecoder.splitMultipartHeader(newline);
            if (HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(contents[0])) {
                boolean checkSecondArg;
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                    checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(contents[1]);
                } else {
                    boolean bl = checkSecondArg = HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(contents[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(contents[1]);
                }
                if (!checkSecondArg) continue;
                for (int i = 2; i < contents.length; ++i) {
                    values = contents[i].split("=", 2);
                    try {
                        attribute = this.getContentDispositionAttribute((String)values);
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    this.currentFieldAttributes.put(attribute.getName(), attribute);
                }
                continue;
            }
            if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(contents[0])) {
                Attribute attribute2;
                try {
                    attribute2 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), HttpPostMultipartRequestDecoder.cleanString(contents[1]));
                }
                catch (NullPointerException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                catch (IllegalArgumentException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, attribute2);
                continue;
            }
            if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(contents[0])) {
                Attribute attribute3;
                try {
                    attribute3 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), HttpPostMultipartRequestDecoder.cleanString(contents[1]));
                }
                catch (NullPointerException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                catch (IllegalArgumentException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, attribute3);
                continue;
            }
            if (HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(contents[0])) {
                if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(contents[1])) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                        String values2 = StringUtil.substringAfter(contents[2], '=');
                        this.multipartMixedBoundary = "--" + values2;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                        return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                    }
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                }
                for (int i = 1; i < contents.length; ++i) {
                    Attribute attribute4;
                    String charsetHeader = HttpHeaderValues.CHARSET.toString();
                    if (contents[i].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
                        values = StringUtil.substringAfter(contents[i], '=');
                        try {
                            attribute = this.factory.createAttribute(this.request, charsetHeader, HttpPostMultipartRequestDecoder.cleanString((String)values));
                        }
                        catch (NullPointerException e) {
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                        }
                        catch (IllegalArgumentException e) {
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                        }
                        this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, attribute);
                        continue;
                    }
                    try {
                        attribute4 = this.factory.createAttribute(this.request, HttpPostMultipartRequestDecoder.cleanString(contents[0]), contents[i]);
                    }
                    catch (NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (IllegalArgumentException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    this.currentFieldAttributes.put(attribute4.getName(), attribute4);
                }
                continue;
            }
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Unknown Params: " + newline);
        }
        Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            if (filenameAttribute != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
            }
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
        }
        if (filenameAttribute != null) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
        }
        throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
    }

    private /* varargs */ Attribute getContentDispositionAttribute(String ... values) {
        String name = HttpPostMultipartRequestDecoder.cleanString(values[0]);
        String value = values[1];
        if (HttpHeaderValues.FILENAME.contentEquals(name)) {
            int last = value.length() - 1;
            if (last > 0 && value.charAt(0) == '\"' && value.charAt(last) == '\"') {
                value = value.substring(1, last);
            }
        } else if (FILENAME_ENCODED.equals(name)) {
            try {
                name = HttpHeaderValues.FILENAME.toString();
                String[] split = value.split("'", 3);
                value = QueryStringDecoder.decodeComponent(split[2], Charset.forName(split[0]));
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (UnsupportedCharsetException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        } else {
            value = HttpPostMultipartRequestDecoder.cleanString(value);
        }
        return this.factory.createAttribute(this.request, name, value);
    }

    protected InterfaceHttpData getFileUpload(String delimiter) {
        Attribute charsetAttribute;
        Attribute encoding = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        Charset localCharset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            String code;
            try {
                code = encoding.getValue().toLowerCase();
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                localCharset = CharsetUtil.US_ASCII;
            } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                localCharset = CharsetUtil.ISO_8859_1;
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            } else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            } else {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
            }
        }
        if ((charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET)) != null) {
            try {
                localCharset = Charset.forName(charsetAttribute.getValue());
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (UnsupportedCharsetException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        }
        if (this.currentFileUpload == null) {
            long size;
            Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
            Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
            Attribute contentTypeAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
            Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
            try {
                size = lengthAttribute != null ? Long.parseLong(lengthAttribute.getValue()) : 0L;
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (NumberFormatException ignored) {
                size = 0L;
            }
            try {
                String contentType = contentTypeAttribute != null ? contentTypeAttribute.getValue() : "application/octet-stream";
                this.currentFileUpload = this.factory.createFileUpload(this.request, HttpPostMultipartRequestDecoder.cleanString(nameAttribute.getValue()), HttpPostMultipartRequestDecoder.cleanString(filenameAttribute.getValue()), contentType, mechanism.value(), localCharset, size);
            }
            catch (NullPointerException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (IllegalArgumentException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
        }
        if (!HttpPostMultipartRequestDecoder.loadDataMultipart(this.undecodedChunk, delimiter, this.currentFileUpload)) {
            return null;
        }
        if (this.currentFileUpload.isCompleted()) {
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            } else {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                this.cleanMixedAttributes();
            }
            FileUpload fileUpload = this.currentFileUpload;
            this.currentFileUpload = null;
            return fileUpload;
        }
        return null;
    }

    @Override
    public void destroy() {
        this.checkDestroyed();
        this.cleanFiles();
        this.destroyed = true;
        if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
            this.undecodedChunk.release();
            this.undecodedChunk = null;
        }
        for (int i = this.bodyListHttpDataRank; i < this.bodyListHttpData.size(); ++i) {
            this.bodyListHttpData.get(i).release();
        }
    }

    @Override
    public void cleanFiles() {
        this.checkDestroyed();
        this.factory.cleanRequestHttpData(this.request);
    }

    @Override
    public void removeHttpDataFromClean(InterfaceHttpData data) {
        this.checkDestroyed();
        this.factory.removeHttpDataFromClean(this.request, data);
    }

    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
        this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
    }

    private static String readLineStandard(ByteBuf undecodedChunk, Charset charset) {
        int readerIndex = undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer(64);
            while (undecodedChunk.isReadable()) {
                byte nextByte = undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = undecodedChunk.getByte(undecodedChunk.readerIndex());
                    if (nextByte == 10) {
                        undecodedChunk.readByte();
                        return line.toString(charset);
                    }
                    line.writeByte(13);
                    continue;
                }
                if (nextByte == 10) {
                    return line.toString(charset);
                }
                line.writeByte(nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readLine(ByteBuf undecodedChunk, Charset charset) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.readLineStandard(undecodedChunk, charset);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
        int readerIndex = undecodedChunk.readerIndex();
        try {
            ByteBuf line = Unpooled.buffer(64);
            while (sao.pos < sao.limit) {
                byte nextByte;
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos < sao.limit) {
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            sao.setReadPosition(0);
                            return line.toString(charset);
                        }
                        --sao.pos;
                        line.writeByte(13);
                        continue;
                    }
                    line.writeByte(nextByte);
                    continue;
                }
                if (nextByte == 10) {
                    sao.setReadPosition(0);
                    return line.toString(charset);
                }
                line.writeByte(nextByte);
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readDelimiterStandard(ByteBuf undecodedChunk, String delimiter) {
        int readerIndex = undecodedChunk.readerIndex();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder(64);
            int len = delimiter.length();
            for (int delimiterPos = 0; undecodedChunk.isReadable() && delimiterPos < len; ++delimiterPos) {
                nextByte = undecodedChunk.readByte();
                if (nextByte == delimiter.charAt(delimiterPos)) {
                    sb.append((char)nextByte);
                    continue;
                }
                undecodedChunk.readerIndex(readerIndex);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
            if (undecodedChunk.isReadable()) {
                nextByte = undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = undecodedChunk.readByte();
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append('-');
                    nextByte = undecodedChunk.readByte();
                    if (nextByte == 45) {
                        sb.append('-');
                        if (undecodedChunk.isReadable()) {
                            nextByte = undecodedChunk.readByte();
                            if (nextByte == 13) {
                                nextByte = undecodedChunk.readByte();
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                undecodedChunk.readerIndex(readerIndex);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }
                            if (nextByte == 10) {
                                return sb.toString();
                            }
                            undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
                            return sb.toString();
                        }
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static String readDelimiter(ByteBuf undecodedChunk, String delimiter) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.readDelimiterStandard(undecodedChunk, delimiter);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
        int readerIndex = undecodedChunk.readerIndex();
        int len = delimiter.length();
        try {
            byte nextByte;
            StringBuilder sb = new StringBuilder(64);
            for (int delimiterPos = 0; sao.pos < sao.limit && delimiterPos < len; ++delimiterPos) {
                if ((nextByte = sao.bytes[sao.pos++]) == delimiter.charAt(delimiterPos)) {
                    sb.append((char)nextByte);
                    continue;
                }
                undecodedChunk.readerIndex(readerIndex);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
            if (sao.pos < sao.limit) {
                if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                    if (sao.pos < sao.limit) {
                        if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                            sao.setReadPosition(0);
                            return sb.toString();
                        }
                        undecodedChunk.readerIndex(readerIndex);
                        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                    }
                    undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                if (nextByte == 10) {
                    sao.setReadPosition(0);
                    return sb.toString();
                }
                if (nextByte == 45) {
                    sb.append('-');
                    if (sao.pos < sao.limit && (nextByte = sao.bytes[sao.pos++]) == 45) {
                        sb.append('-');
                        if (sao.pos < sao.limit) {
                            if ((nextByte = sao.bytes[sao.pos++]) == 13) {
                                if (sao.pos < sao.limit) {
                                    if ((nextByte = sao.bytes[sao.pos++]) == 10) {
                                        sao.setReadPosition(0);
                                        return sb.toString();
                                    }
                                    undecodedChunk.readerIndex(readerIndex);
                                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                                }
                                undecodedChunk.readerIndex(readerIndex);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }
                            if (nextByte == 10) {
                                sao.setReadPosition(0);
                                return sb.toString();
                            }
                            sao.setReadPosition(1);
                            return sb.toString();
                        }
                        sao.setReadPosition(0);
                        return sb.toString();
                    }
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }

    private static boolean loadDataMultipartStandard(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
        int startReaderIndex = undecodedChunk.readerIndex();
        int delimeterLength = delimiter.length();
        int index = 0;
        int lastPosition = startReaderIndex;
        int prevByte = 10;
        boolean delimiterFound = false;
        while (undecodedChunk.isReadable()) {
            byte nextByte = undecodedChunk.readByte();
            if (prevByte == 10 && nextByte == delimiter.codePointAt(index)) {
                if (delimeterLength != ++index) continue;
                delimiterFound = true;
                break;
            }
            lastPosition = undecodedChunk.readerIndex();
            if (nextByte == 10) {
                index = 0;
                lastPosition -= prevByte == 13 ? 2 : 1;
            }
            prevByte = nextByte;
        }
        if (prevByte == 13) {
            --lastPosition;
        }
        ByteBuf content = undecodedChunk.copy(startReaderIndex, lastPosition - startReaderIndex);
        try {
            httpData.addContent(content, delimiterFound);
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        return delimiterFound;
    }

    private static boolean loadDataMultipart(ByteBuf undecodedChunk, String delimiter, HttpData httpData) {
        if (!undecodedChunk.hasArray()) {
            return HttpPostMultipartRequestDecoder.loadDataMultipartStandard(undecodedChunk, delimiter, httpData);
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
        int startReaderIndex = undecodedChunk.readerIndex();
        int delimeterLength = delimiter.length();
        int index = 0;
        int lastRealPos = sao.pos;
        int prevByte = 10;
        boolean delimiterFound = false;
        while (sao.pos < sao.limit) {
            int nextByte = sao.bytes[sao.pos++];
            if (prevByte == 10 && nextByte == delimiter.codePointAt(index)) {
                if (delimeterLength != ++index) continue;
                delimiterFound = true;
                break;
            }
            lastRealPos = sao.pos;
            if (nextByte == 10) {
                index = 0;
                lastRealPos -= prevByte == 13 ? 2 : 1;
            }
            prevByte = nextByte;
        }
        if (prevByte == 13) {
            --lastRealPos;
        }
        int lastPosition = sao.getReadPosition(lastRealPos);
        ByteBuf content = undecodedChunk.copy(startReaderIndex, lastPosition - startReaderIndex);
        try {
            httpData.addContent(content, delimiterFound);
        }
        catch (IOException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        undecodedChunk.readerIndex(lastPosition);
        return delimiterFound;
    }

    private static String cleanString(String field) {
        int size = field.length();
        StringBuilder sb = new StringBuilder(size);
        block4 : for (int i = 0; i < size; ++i) {
            char nextChar = field.charAt(i);
            switch (nextChar) {
                case '\t': 
                case ',': 
                case ':': 
                case ';': 
                case '=': {
                    sb.append(' ');
                    continue block4;
                }
                case '\"': {
                    continue block4;
                }
                default: {
                    sb.append(nextChar);
                }
            }
        }
        return sb.toString().trim();
    }

    private boolean skipOneLine() {
        if (!this.undecodedChunk.isReadable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == 13) {
            if (!this.undecodedChunk.isReadable()) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            }
            nextByte = this.undecodedChunk.readByte();
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
            return false;
        }
        if (nextByte == 10) {
            return true;
        }
        this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
        return false;
    }

    private static String[] splitMultipartHeader(String sb) {
        int nameStart;
        int nameEnd;
        char ch;
        int colonEnd;
        ArrayList<String> headers = new ArrayList<String>(1);
        for (nameEnd = nameStart = HttpPostBodyUtil.findNonWhitespace((String)sb, (int)0); nameEnd < sb.length() && (ch = sb.charAt(nameEnd)) != ':' && !Character.isWhitespace(ch); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt(colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
        int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
        headers.add(sb.substring(nameStart, nameEnd));
        String svalue = sb.substring(valueStart, valueEnd);
        String[] values = svalue.indexOf(59) >= 0 ? HttpPostMultipartRequestDecoder.splitMultipartHeaderValues(svalue) : svalue.split(",");
        for (String value : values) {
            headers.add(value.trim());
        }
        String[] array = new String[headers.size()];
        for (int i = 0; i < headers.size(); ++i) {
            array[i] = (String)headers.get(i);
        }
        return array;
    }

    private static String[] splitMultipartHeaderValues(String svalue) {
        ArrayList<String> values = InternalThreadLocalMap.get().arrayList(1);
        boolean inQuote = false;
        boolean escapeNext = false;
        int start = 0;
        for (int i = 0; i < svalue.length(); ++i) {
            char c = svalue.charAt(i);
            if (inQuote) {
                if (escapeNext) {
                    escapeNext = false;
                    continue;
                }
                if (c == '\\') {
                    escapeNext = true;
                    continue;
                }
                if (c != '\"') continue;
                inQuote = false;
                continue;
            }
            if (c == '\"') {
                inQuote = true;
                continue;
            }
            if (c != ';') continue;
            values.add(svalue.substring(start, i));
            start = i + 1;
        }
        values.add(svalue.substring(start));
        return values.toArray(new String[values.size()]);
    }

}

