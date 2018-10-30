/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.CaseIgnoringComparator;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostBodyUtil;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpPostRequestDecoder;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostStandardRequestDecoder
implements InterfaceHttpPostRequestDecoder {
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold = 10485760;

    public HttpPostStandardRequestDecoder(HttpRequest request) {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }

    public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
        this.request = ObjectUtil.checkNotNull(request, "request");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.factory = ObjectUtil.checkNotNull(factory, "factory");
        if (request instanceof HttpContent) {
            this.offer((HttpContent)((Object)request));
        } else {
            this.undecodedChunk = Unpooled.buffer();
            this.parseBody();
        }
    }

    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }

    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return false;
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
    public HttpPostStandardRequestDecoder offer(HttpContent content) {
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
        return this.currentAttribute;
    }

    private void parseBody() {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            return;
        }
        this.parseBodyAttributes();
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

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void parseBodyAttributesStandard() {
        int firstpos;
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block7 : while (this.undecodedChunk.isReadable() && contRead) {
                char read = (char)this.undecodedChunk.readUnsignedByte();
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostStandardRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue block7;
                        }
                        if (read != '&') continue block7;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostStandardRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                        this.currentAttribute = this.factory.createAttribute(this.request, key);
                        this.currentAttribute.setValue("");
                        this.addHttpData(this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block7;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue block7;
                        }
                        if (read == '\r') {
                            if (this.undecodedChunk.isReadable()) {
                                read = (char)this.undecodedChunk.readUnsignedByte();
                                if (read != '\n') throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                ampersandpos = ++currentpos - 2;
                                this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue block7;
                            }
                            --currentpos;
                            continue block7;
                        }
                        if (read != '\n') continue block7;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                        firstpos = currentpos;
                        contRead = false;
                        continue block7;
                    }
                }
                contRead = false;
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                } else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            if (contRead && this.currentAttribute != null) {
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                    this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                    firstpos = currentpos;
                }
                this.undecodedChunk.readerIndex(firstpos);
                return;
            }
            this.undecodedChunk.readerIndex(firstpos);
            return;
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void parseBodyAttributes() {
        int firstpos;
        if (!this.undecodedChunk.hasArray()) {
            this.parseBodyAttributesStandard();
            return;
        }
        HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        int currentpos = firstpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            int ampersandpos;
            block8 : while (sao.pos < sao.limit) {
                char read = (char)(sao.bytes[sao.pos++] & 255);
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        String key;
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            int equalpos = currentpos - 1;
                            key = HttpPostStandardRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue block8;
                        }
                        if (read != '&') continue block8;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                        ampersandpos = currentpos - 1;
                        key = HttpPostStandardRequestDecoder.decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                        this.currentAttribute = this.factory.createAttribute(this.request, key);
                        this.currentAttribute.setValue("");
                        this.addHttpData(this.currentAttribute);
                        this.currentAttribute = null;
                        firstpos = currentpos;
                        contRead = true;
                        continue block8;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue block8;
                        }
                        if (read == '\r') {
                            if (sao.pos < sao.limit) {
                                read = (char)(sao.bytes[sao.pos++] & 255);
                                ++currentpos;
                                if (read != '\n') {
                                    sao.setReadPosition(0);
                                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                                }
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                ampersandpos = currentpos - 2;
                                sao.setReadPosition(0);
                                this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                break block8;
                            }
                            if (sao.limit <= 0) continue block8;
                            --currentpos;
                            continue block8;
                        }
                        if (read != '\n') continue block8;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                        ampersandpos = currentpos - 1;
                        sao.setReadPosition(0);
                        this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                        firstpos = currentpos;
                        contRead = false;
                        break block8;
                    }
                }
                sao.setReadPosition(0);
                contRead = false;
                break;
            }
            if (this.isLastChunk) {
                if (this.currentAttribute != null) {
                    ampersandpos = currentpos;
                    if (ampersandpos > firstpos) {
                        this.setFinalBuffer(this.undecodedChunk.copy(firstpos, ampersandpos - firstpos));
                    } else if (!this.currentAttribute.isCompleted()) {
                        this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                    }
                    firstpos = currentpos;
                    this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
                    this.undecodedChunk.readerIndex(firstpos);
                    return;
                }
                if (contRead && this.currentAttribute != null) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                        this.currentAttribute.addContent(this.undecodedChunk.copy(firstpos, currentpos - firstpos), false);
                        firstpos = currentpos;
                    }
                    this.undecodedChunk.readerIndex(firstpos);
                    return;
                }
            }
            this.undecodedChunk.readerIndex(firstpos);
            return;
        }
        catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (IOException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
        catch (IllegalArgumentException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
        }
    }

    private void setFinalBuffer(ByteBuf buffer) throws IOException {
        this.currentAttribute.addContent(buffer, true);
        String value = HttpPostStandardRequestDecoder.decodeAttribute(this.currentAttribute.getByteBuf().toString(this.charset), this.charset);
        this.currentAttribute.setValue(value);
        this.addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }

    private static String decodeAttribute(String s, Charset charset) {
        try {
            return QueryStringDecoder.decodeComponent(s, charset);
        }
        catch (IllegalArgumentException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e);
        }
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

}

