/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpExpectationFailedEvent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;
import java.util.List;

public abstract class HttpObjectDecoder
extends ByteToMessageDecoder {
    private static final String EMPTY_VALUE = "";
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    protected final boolean validateHeaders;
    private final HeaderParser headerParser;
    private final LineParser lineParser;
    private HttpMessage message;
    private long chunkSize;
    private long contentLength = Long.MIN_VALUE;
    private volatile boolean resetRequested;
    private CharSequence name;
    private CharSequence value;
    private LastHttpContent trailer;
    private State currentState = State.SKIP_CONTROL_CHARS;

    protected HttpObjectDecoder() {
        this(4096, 8192, 8192, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
    }

    protected HttpObjectDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean chunkedSupported, boolean validateHeaders, int initialBufferSize) {
        if (maxInitialLineLength <= 0) {
            throw new IllegalArgumentException("maxInitialLineLength must be a positive integer: " + maxInitialLineLength);
        }
        if (maxHeaderSize <= 0) {
            throw new IllegalArgumentException("maxHeaderSize must be a positive integer: " + maxHeaderSize);
        }
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize must be a positive integer: " + maxChunkSize);
        }
        AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);
        this.lineParser = new LineParser(seq, maxInitialLineLength);
        this.headerParser = new HeaderParser(seq, maxHeaderSize);
        this.maxChunkSize = maxChunkSize;
        this.chunkedSupported = chunkedSupported;
        this.validateHeaders = validateHeaders;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if (this.resetRequested) {
            this.resetNow();
        }
        switch (this.currentState) {
            case SKIP_CONTROL_CHARS: {
                if (!HttpObjectDecoder.skipControlCharacters(buffer)) {
                    return;
                }
                this.currentState = State.READ_INITIAL;
            }
            case READ_INITIAL: {
                try {
                    AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    String[] initialLine = HttpObjectDecoder.splitInitialLine(line);
                    if (initialLine.length < 3) {
                        this.currentState = State.SKIP_CONTROL_CHARS;
                        return;
                    }
                    this.message = this.createMessage(initialLine);
                    this.currentState = State.READ_HEADER;
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                    return;
                }
            }
            case READ_HEADER: {
                try {
                    State nextState = this.readHeaders(buffer);
                    if (nextState == null) {
                        return;
                    }
                    this.currentState = nextState;
                    switch (nextState) {
                        case SKIP_CONTROL_CHARS: {
                            out.add(this.message);
                            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                            this.resetNow();
                            return;
                        }
                        case READ_CHUNK_SIZE: {
                            if (!this.chunkedSupported) {
                                throw new IllegalArgumentException("Chunked messages not supported");
                            }
                            out.add(this.message);
                            return;
                        }
                    }
                    long contentLength = this.contentLength();
                    if (contentLength == 0L || contentLength == -1L && this.isDecodingRequest()) {
                        out.add(this.message);
                        out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                        this.resetNow();
                        return;
                    }
                    assert (nextState == State.READ_FIXED_LENGTH_CONTENT || nextState == State.READ_VARIABLE_LENGTH_CONTENT);
                    out.add(this.message);
                    if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
                        this.chunkSize = contentLength;
                    }
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                    return;
                }
            }
            case READ_VARIABLE_LENGTH_CONTENT: {
                int toRead = Math.min(buffer.readableBytes(), this.maxChunkSize);
                if (toRead > 0) {
                    ByteBuf content = buffer.readRetainedSlice(toRead);
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                int readLimit = buffer.readableBytes();
                if (readLimit == 0) {
                    return;
                }
                int toRead = Math.min(readLimit, this.maxChunkSize);
                if ((long)toRead > this.chunkSize) {
                    toRead = (int)this.chunkSize;
                }
                ByteBuf content = buffer.readRetainedSlice(toRead);
                this.chunkSize -= (long)toRead;
                if (this.chunkSize == 0L) {
                    out.add(new DefaultLastHttpContent(content, this.validateHeaders));
                    this.resetNow();
                } else {
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_CHUNK_SIZE: {
                try {
                    AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    int chunkSize = HttpObjectDecoder.getChunkSize(line.toString());
                    this.chunkSize = chunkSize;
                    if (chunkSize == 0) {
                        this.currentState = State.READ_CHUNK_FOOTER;
                        return;
                    }
                    this.currentState = State.READ_CHUNKED_CONTENT;
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                    return;
                }
            }
            case READ_CHUNKED_CONTENT: {
                assert (this.chunkSize <= Integer.MAX_VALUE);
                int toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
                if ((toRead = Math.min(toRead, buffer.readableBytes())) == 0) {
                    return;
                }
                DefaultHttpContent chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
                this.chunkSize -= (long)toRead;
                out.add(chunk);
                if (this.chunkSize != 0L) {
                    return;
                }
                this.currentState = State.READ_CHUNK_DELIMITER;
            }
            case READ_CHUNK_DELIMITER: {
                int wIdx = buffer.writerIndex();
                int rIdx = buffer.readerIndex();
                while (wIdx > rIdx) {
                    byte next;
                    if ((next = buffer.getByte(rIdx++)) != 10) continue;
                    this.currentState = State.READ_CHUNK_SIZE;
                    break;
                }
                buffer.readerIndex(rIdx);
                return;
            }
            case READ_CHUNK_FOOTER: {
                try {
                    LastHttpContent trailer = this.readTrailingHeaders(buffer);
                    if (trailer == null) {
                        return;
                    }
                    out.add(trailer);
                    this.resetNow();
                    return;
                }
                catch (Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                    return;
                }
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(buffer.readableBytes());
                break;
            }
            case UPGRADED: {
                int readableBytes = buffer.readableBytes();
                if (readableBytes <= 0) break;
                out.add(buffer.readBytes(readableBytes));
                break;
            }
        }
    }

    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        super.decodeLast(ctx, in, out);
        if (this.resetRequested) {
            this.resetNow();
        }
        if (this.message != null) {
            boolean prematureClosure;
            boolean chunked = HttpUtil.isTransferEncodingChunked(this.message);
            if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                this.resetNow();
                return;
            }
            if (this.currentState == State.READ_HEADER) {
                out.add(this.invalidMessage(Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
                this.resetNow();
                return;
            }
            if (this.isDecodingRequest() || chunked) {
                prematureClosure = true;
            } else {
                boolean bl = prematureClosure = this.contentLength() > 0L;
            }
            if (!prematureClosure) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }
            this.resetNow();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HttpExpectationFailedEvent) {
            switch (this.currentState) {
                case READ_CHUNK_SIZE: 
                case READ_VARIABLE_LENGTH_CONTENT: 
                case READ_FIXED_LENGTH_CONTENT: {
                    this.reset();
                    break;
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse)msg;
            int code = res.status().code();
            if (code >= 100 && code < 200) {
                return code != 101 || res.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || !res.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
            }
            switch (code) {
                case 204: 
                case 304: {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isSwitchingToNonHttp1Protocol(HttpResponse msg) {
        if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
            return false;
        }
        String newProtocol = msg.headers().get(HttpHeaderNames.UPGRADE);
        return newProtocol == null || !newProtocol.contains(HttpVersion.HTTP_1_0.text()) && !newProtocol.contains(HttpVersion.HTTP_1_1.text());
    }

    public void reset() {
        this.resetRequested = true;
    }

    private void resetNow() {
        HttpResponse res;
        HttpMessage message = this.message;
        this.message = null;
        this.name = null;
        this.value = null;
        this.contentLength = Long.MIN_VALUE;
        this.lineParser.reset();
        this.headerParser.reset();
        this.trailer = null;
        if (!this.isDecodingRequest() && (res = (HttpResponse)message) != null && this.isSwitchingToNonHttp1Protocol(res)) {
            this.currentState = State.UPGRADED;
            return;
        }
        this.resetRequested = false;
        this.currentState = State.SKIP_CONTROL_CHARS;
    }

    private HttpMessage invalidMessage(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        if (this.message != null) {
            this.message.setDecoderResult(DecoderResult.failure(cause));
        } else {
            this.message = this.createInvalidMessage();
            this.message.setDecoderResult(DecoderResult.failure(cause));
        }
        HttpMessage ret = this.message;
        this.message = null;
        return ret;
    }

    private HttpContent invalidChunk(ByteBuf in, Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        DefaultLastHttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        this.message = null;
        this.trailer = null;
        return chunk;
    }

    private static boolean skipControlCharacters(ByteBuf buffer) {
        boolean skiped = false;
        int wIdx = buffer.writerIndex();
        int rIdx = buffer.readerIndex();
        while (wIdx > rIdx) {
            short c;
            if (Character.isISOControl(c = buffer.getUnsignedByte(rIdx++)) || Character.isWhitespace(c)) continue;
            --rIdx;
            skiped = true;
            break;
        }
        buffer.readerIndex(rIdx);
        return skiped;
    }

    private State readHeaders(ByteBuf buffer) {
        State nextState;
        HttpMessage message = this.message;
        HttpHeaders headers = message.headers();
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                char firstChar = line.charAt(0);
                if (this.name != null && (firstChar == ' ' || firstChar == '\t')) {
                    String trimmedLine = line.toString().trim();
                    String valueStr = String.valueOf(this.value);
                    this.value = valueStr + ' ' + trimmedLine;
                } else {
                    if (this.name != null) {
                        headers.add(this.name, (Object)this.value);
                    }
                    this.splitHeader(line);
                }
                line = this.headerParser.parse(buffer);
                if (line != null) continue;
                return null;
            } while (line.length() > 0);
        }
        if (this.name != null) {
            headers.add(this.name, (Object)this.value);
        }
        this.name = null;
        this.value = null;
        if (this.isContentAlwaysEmpty(message)) {
            HttpUtil.setTransferEncodingChunked(message, false);
            nextState = State.SKIP_CONTROL_CHARS;
        } else {
            nextState = HttpUtil.isTransferEncodingChunked(message) ? State.READ_CHUNK_SIZE : (this.contentLength() >= 0L ? State.READ_FIXED_LENGTH_CONTENT : State.READ_VARIABLE_LENGTH_CONTENT);
        }
        return nextState;
    }

    private long contentLength() {
        if (this.contentLength == Long.MIN_VALUE) {
            this.contentLength = HttpUtil.getContentLength(this.message, -1L);
        }
        return this.contentLength;
    }

    private LastHttpContent readTrailingHeaders(ByteBuf buffer) {
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        CharSequence lastHeader = null;
        if (line.length() > 0) {
            LastHttpContent trailer = this.trailer;
            if (trailer == null) {
                trailer = this.trailer = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            }
            do {
                char firstChar = line.charAt(0);
                if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                    List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                    if (!current.isEmpty()) {
                        int lastPos = current.size() - 1;
                        String lineTrimmed = line.toString().trim();
                        String currentLastPos = current.get(lastPos);
                        current.set(lastPos, currentLastPos + lineTrimmed);
                    }
                } else {
                    this.splitHeader(line);
                    CharSequence headerName = this.name;
                    if (!(HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName) || HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName) || HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName))) {
                        trailer.trailingHeaders().add(headerName, (Object)this.value);
                    }
                    lastHeader = this.name;
                    this.name = null;
                    this.value = null;
                }
                line = this.headerParser.parse(buffer);
                if (line != null) continue;
                return null;
            } while (line.length() > 0);
            this.trailer = null;
            return trailer;
        }
        return LastHttpContent.EMPTY_LAST_CONTENT;
    }

    protected abstract boolean isDecodingRequest();

    protected abstract HttpMessage createMessage(String[] var1) throws Exception;

    protected abstract HttpMessage createInvalidMessage();

    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); ++i) {
            char c = hex.charAt(i);
            if (c != ';' && !Character.isWhitespace(c) && !Character.isISOControl(c)) continue;
            hex = hex.substring(0, i);
            break;
        }
        return Integer.parseInt(hex, 16);
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart = HttpObjectDecoder.findNonWhitespace(sb, 0);
        int aEnd = HttpObjectDecoder.findWhitespace(sb, aStart);
        int bStart = HttpObjectDecoder.findNonWhitespace(sb, aEnd);
        int bEnd = HttpObjectDecoder.findWhitespace(sb, bStart);
        int cStart = HttpObjectDecoder.findNonWhitespace(sb, bEnd);
        int cEnd = HttpObjectDecoder.findEndOfString(sb);
        String[] arrstring = new String[3];
        arrstring[0] = sb.subStringUnsafe(aStart, aEnd);
        arrstring[1] = sb.subStringUnsafe(bStart, bEnd);
        arrstring[2] = cStart < cEnd ? sb.subStringUnsafe(cStart, cEnd) : "";
        return arrstring;
    }

    private void splitHeader(AppendableCharSequence sb) {
        int nameEnd;
        int colonEnd;
        int nameStart;
        char ch;
        int length = sb.length();
        for (nameEnd = nameStart = HttpObjectDecoder.findNonWhitespace((AppendableCharSequence)sb, (int)0); nameEnd < length && (ch = sb.charAt(nameEnd)) != ':' && !Character.isWhitespace(ch); ++nameEnd) {
        }
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAt(colonEnd) != ':') continue;
            ++colonEnd;
            break;
        }
        this.name = sb.subStringUnsafe(nameStart, nameEnd);
        int valueStart = HttpObjectDecoder.findNonWhitespace(sb, colonEnd);
        if (valueStart == length) {
            this.value = "";
        } else {
            int valueEnd = HttpObjectDecoder.findEndOfString(sb);
            this.value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (Character.isWhitespace(sb.charAtUnsafe(result))) continue;
            return result;
        }
        return sb.length();
    }

    private static int findWhitespace(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) continue;
            return result;
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (Character.isWhitespace(sb.charAtUnsafe(result))) continue;
            return result + 1;
        }
        return 0;
    }

    private static final class LineParser
    extends HeaderParser {
        LineParser(AppendableCharSequence seq, int maxLength) {
            super(seq, maxLength);
        }

        @Override
        public AppendableCharSequence parse(ByteBuf buffer) {
            this.reset();
            return super.parse(buffer);
        }

        @Override
        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("An HTTP line is larger than " + maxLength + " bytes.");
        }
    }

    private static class HeaderParser
    implements ByteProcessor {
        private final AppendableCharSequence seq;
        private final int maxLength;
        private int size;

        HeaderParser(AppendableCharSequence seq, int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            int oldSize = this.size;
            this.seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                this.size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return this.seq;
        }

        public void reset() {
            this.size = 0;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char)(value & 255);
            if (nextByte == '\r') {
                return true;
            }
            if (nextByte == '\n') {
                return false;
            }
            if (++this.size > this.maxLength) {
                throw this.newException(this.maxLength);
            }
            this.seq.append(nextByte);
            return true;
        }

        protected TooLongFrameException newException(int maxLength) {
            return new TooLongFrameException("HTTP header is larger than " + maxLength + " bytes.");
        }
    }

    private static enum State {
        SKIP_CONTROL_CHARS,
        READ_INITIAL,
        READ_HEADER,
        READ_VARIABLE_LENGTH_CONTENT,
        READ_FIXED_LENGTH_CONTENT,
        READ_CHUNK_SIZE,
        READ_CHUNKED_CONTENT,
        READ_CHUNK_DELIMITER,
        READ_CHUNK_FOOTER,
        BAD_MESSAGE,
        UPGRADED;
        

        private State() {
        }
    }

}

