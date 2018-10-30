/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.DefaultBulkStringRedisContent;
import io.netty.handler.codec.redis.DefaultLastBulkStringRedisContent;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisCodecException;
import io.netty.handler.codec.redis.RedisCodecUtil;
import io.netty.handler.codec.redis.RedisConstants;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.RedisMessagePool;
import io.netty.handler.codec.redis.RedisMessageType;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import java.nio.charset.Charset;
import java.util.List;

public final class RedisDecoder
extends ByteToMessageDecoder {
    private final ToPositiveLongProcessor toPositiveLongProcessor = new ToPositiveLongProcessor();
    private final int maxInlineMessageLength;
    private final RedisMessagePool messagePool;
    private State state = State.DECODE_TYPE;
    private RedisMessageType type;
    private int remainingBulkLength;

    public RedisDecoder() {
        this(65536, FixedRedisMessagePool.INSTANCE);
    }

    public RedisDecoder(int maxInlineMessageLength, RedisMessagePool messagePool) {
        if (maxInlineMessageLength <= 0 || maxInlineMessageLength > 536870912) {
            throw new RedisCodecException("maxInlineMessageLength: " + maxInlineMessageLength + " (expected: <= " + 536870912 + ")");
        }
        this.maxInlineMessageLength = maxInlineMessageLength;
        this.messagePool = messagePool;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            block10 : do lbl-1000: // 6 sources:
            {
                switch (.$SwitchMap$io$netty$handler$codec$redis$RedisDecoder$State[this.state.ordinal()]) {
                    case 1: {
                        if (this.decodeType(in)) ** GOTO lbl-1000
                        return;
                    }
                    case 2: {
                        if (this.decodeInline(in, out)) ** GOTO lbl-1000
                        return;
                    }
                    case 3: {
                        if (this.decodeLength(in, out)) ** GOTO lbl-1000
                        return;
                    }
                    case 4: {
                        if (this.decodeBulkStringEndOfLine(in, out)) ** GOTO lbl-1000
                        return;
                    }
                    case 5: {
                        if (this.decodeBulkStringContent(in, out)) continue block10;
                        return;
                    }
                }
                break;
            } while (true);
            throw new RedisCodecException("Unknown state: " + (Object)this.state);
        }
        catch (RedisCodecException e) {
            this.resetDecoder();
            throw e;
        }
        catch (Exception e) {
            this.resetDecoder();
            throw new RedisCodecException(e);
        }
    }

    private void resetDecoder() {
        this.state = State.DECODE_TYPE;
        this.remainingBulkLength = 0;
    }

    private boolean decodeType(ByteBuf in) throws Exception {
        if (!in.isReadable()) {
            return false;
        }
        this.type = RedisMessageType.valueOf(in.readByte());
        this.state = this.type.isInline() ? State.DECODE_INLINE : State.DECODE_LENGTH;
        return true;
    }

    private boolean decodeInline(ByteBuf in, List<Object> out) throws Exception {
        ByteBuf lineBytes = RedisDecoder.readLine(in);
        if (lineBytes == null) {
            if (in.readableBytes() > this.maxInlineMessageLength) {
                throw new RedisCodecException("length: " + in.readableBytes() + " (expected: <= " + this.maxInlineMessageLength + ")");
            }
            return false;
        }
        out.add(this.newInlineRedisMessage(this.type, lineBytes));
        this.resetDecoder();
        return true;
    }

    private boolean decodeLength(ByteBuf in, List<Object> out) throws Exception {
        ByteBuf lineByteBuf = RedisDecoder.readLine(in);
        if (lineByteBuf == null) {
            return false;
        }
        long length = this.parseRedisNumber(lineByteBuf);
        if (length < -1L) {
            throw new RedisCodecException("length: " + length + " (expected: >= " + -1 + ")");
        }
        switch (this.type) {
            case ARRAY_HEADER: {
                out.add(new ArrayHeaderRedisMessage(length));
                this.resetDecoder();
                return true;
            }
            case BULK_STRING: {
                if (length > 0x20000000L) {
                    throw new RedisCodecException("length: " + length + " (expected: <= " + 536870912 + ")");
                }
                this.remainingBulkLength = (int)length;
                return this.decodeBulkString(in, out);
            }
        }
        throw new RedisCodecException("bad type: " + (Object)((Object)this.type));
    }

    private boolean decodeBulkString(ByteBuf in, List<Object> out) throws Exception {
        switch (this.remainingBulkLength) {
            case -1: {
                out.add(FullBulkStringRedisMessage.NULL_INSTANCE);
                this.resetDecoder();
                return true;
            }
            case 0: {
                this.state = State.DECODE_BULK_STRING_EOL;
                return this.decodeBulkStringEndOfLine(in, out);
            }
        }
        out.add(new BulkStringHeaderRedisMessage(this.remainingBulkLength));
        this.state = State.DECODE_BULK_STRING_CONTENT;
        return this.decodeBulkStringContent(in, out);
    }

    private boolean decodeBulkStringEndOfLine(ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 2) {
            return false;
        }
        RedisDecoder.readEndOfLine(in);
        out.add(FullBulkStringRedisMessage.EMPTY_INSTANCE);
        this.resetDecoder();
        return true;
    }

    private boolean decodeBulkStringContent(ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes == 0 || this.remainingBulkLength == 0 && readableBytes < 2) {
            return false;
        }
        if (readableBytes >= this.remainingBulkLength + 2) {
            ByteBuf content = in.readSlice(this.remainingBulkLength);
            RedisDecoder.readEndOfLine(in);
            out.add(new DefaultLastBulkStringRedisContent(content.retain()));
            this.resetDecoder();
            return true;
        }
        int toRead = Math.min(this.remainingBulkLength, readableBytes);
        this.remainingBulkLength -= toRead;
        out.add(new DefaultBulkStringRedisContent(in.readSlice(toRead).retain()));
        return true;
    }

    private static void readEndOfLine(ByteBuf in) {
        short delim = in.readShort();
        if (RedisConstants.EOL_SHORT == delim) {
            return;
        }
        byte[] bytes = RedisCodecUtil.shortToBytes(delim);
        throw new RedisCodecException("delimiter: [" + bytes[0] + "," + bytes[1] + "] (expected: \\r\\n)");
    }

    private RedisMessage newInlineRedisMessage(RedisMessageType messageType, ByteBuf content) {
        switch (messageType) {
            case SIMPLE_STRING: {
                SimpleStringRedisMessage cached = this.messagePool.getSimpleString(content);
                return cached != null ? cached : new SimpleStringRedisMessage(content.toString(CharsetUtil.UTF_8));
            }
            case ERROR: {
                ErrorRedisMessage cached = this.messagePool.getError(content);
                return cached != null ? cached : new ErrorRedisMessage(content.toString(CharsetUtil.UTF_8));
            }
            case INTEGER: {
                IntegerRedisMessage cached = this.messagePool.getInteger(content);
                return cached != null ? cached : new IntegerRedisMessage(this.parseRedisNumber(content));
            }
        }
        throw new RedisCodecException("bad type: " + (Object)((Object)messageType));
    }

    private static ByteBuf readLine(ByteBuf in) {
        if (!in.isReadable(2)) {
            return null;
        }
        int lfIndex = in.forEachByte(ByteProcessor.FIND_LF);
        if (lfIndex < 0) {
            return null;
        }
        ByteBuf data = in.readSlice(lfIndex - in.readerIndex() - 1);
        RedisDecoder.readEndOfLine(in);
        return data;
    }

    private long parseRedisNumber(ByteBuf byteBuf) {
        int extraOneByteForNegative;
        int readableBytes = byteBuf.readableBytes();
        boolean negative = readableBytes > 0 && byteBuf.getByte(byteBuf.readerIndex()) == 45;
        int n = extraOneByteForNegative = negative ? 1 : 0;
        if (readableBytes <= extraOneByteForNegative) {
            throw new RedisCodecException("no number to parse: " + byteBuf.toString(CharsetUtil.US_ASCII));
        }
        if (readableBytes > 19 + extraOneByteForNegative) {
            throw new RedisCodecException("too many characters to be a valid RESP Integer: " + byteBuf.toString(CharsetUtil.US_ASCII));
        }
        if (negative) {
            return - this.parsePositiveNumber(byteBuf.skipBytes(extraOneByteForNegative));
        }
        return this.parsePositiveNumber(byteBuf);
    }

    private long parsePositiveNumber(ByteBuf byteBuf) {
        this.toPositiveLongProcessor.reset();
        byteBuf.forEachByte(this.toPositiveLongProcessor);
        return this.toPositiveLongProcessor.content();
    }

    private static final class ToPositiveLongProcessor
    implements ByteProcessor {
        private long result;

        private ToPositiveLongProcessor() {
        }

        @Override
        public boolean process(byte value) throws Exception {
            if (value < 48 || value > 57) {
                throw new RedisCodecException("bad byte in number: " + value);
            }
            this.result = this.result * 10L + (long)(value - 48);
            return true;
        }

        public long content() {
            return this.result;
        }

        public void reset() {
            this.result = 0L;
        }
    }

    private static enum State {
        DECODE_TYPE,
        DECODE_INLINE,
        DECODE_LENGTH,
        DECODE_BULK_STRING_EOL,
        DECODE_BULK_STRING_CONTENT;
        

        private State() {
        }
    }

}

