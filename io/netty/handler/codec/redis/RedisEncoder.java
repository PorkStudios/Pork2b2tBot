/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FixedRedisMessagePool;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.LastBulkStringRedisContent;
import io.netty.handler.codec.redis.RedisCodecUtil;
import io.netty.handler.codec.redis.RedisConstants;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.RedisMessagePool;
import io.netty.handler.codec.redis.RedisMessageType;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

public class RedisEncoder
extends MessageToMessageEncoder<RedisMessage> {
    private final RedisMessagePool messagePool;

    public RedisEncoder() {
        this(FixedRedisMessagePool.INSTANCE);
    }

    public RedisEncoder(RedisMessagePool messagePool) {
        this.messagePool = ObjectUtil.checkNotNull(messagePool, "messagePool");
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisMessage msg, List<Object> out) throws Exception {
        try {
            this.writeRedisMessage(ctx.alloc(), msg, out);
        }
        catch (CodecException e) {
            throw e;
        }
        catch (Exception e) {
            throw new CodecException(e);
        }
    }

    private void writeRedisMessage(ByteBufAllocator allocator, RedisMessage msg, List<Object> out) {
        if (msg instanceof SimpleStringRedisMessage) {
            RedisEncoder.writeSimpleStringMessage(allocator, (SimpleStringRedisMessage)msg, out);
        } else if (msg instanceof ErrorRedisMessage) {
            RedisEncoder.writeErrorMessage(allocator, (ErrorRedisMessage)msg, out);
        } else if (msg instanceof IntegerRedisMessage) {
            this.writeIntegerMessage(allocator, (IntegerRedisMessage)msg, out);
        } else if (msg instanceof FullBulkStringRedisMessage) {
            this.writeFullBulkStringMessage(allocator, (FullBulkStringRedisMessage)msg, out);
        } else if (msg instanceof BulkStringRedisContent) {
            RedisEncoder.writeBulkStringContent(allocator, (BulkStringRedisContent)msg, out);
        } else if (msg instanceof BulkStringHeaderRedisMessage) {
            this.writeBulkStringHeader(allocator, (BulkStringHeaderRedisMessage)msg, out);
        } else if (msg instanceof ArrayHeaderRedisMessage) {
            this.writeArrayHeader(allocator, (ArrayHeaderRedisMessage)msg, out);
        } else if (msg instanceof ArrayRedisMessage) {
            this.writeArrayMessage(allocator, (ArrayRedisMessage)msg, out);
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

    private static void writeSimpleStringMessage(ByteBufAllocator allocator, SimpleStringRedisMessage msg, List<Object> out) {
        RedisEncoder.writeString(allocator, RedisMessageType.SIMPLE_STRING.value(), msg.content(), out);
    }

    private static void writeErrorMessage(ByteBufAllocator allocator, ErrorRedisMessage msg, List<Object> out) {
        RedisEncoder.writeString(allocator, RedisMessageType.ERROR.value(), msg.content(), out);
    }

    private static void writeString(ByteBufAllocator allocator, byte type, String content, List<Object> out) {
        ByteBuf buf = allocator.ioBuffer(1 + ByteBufUtil.utf8MaxBytes(content) + 2);
        buf.writeByte(type);
        ByteBufUtil.writeUtf8(buf, (CharSequence)content);
        buf.writeShort(RedisConstants.EOL_SHORT);
        out.add(buf);
    }

    private void writeIntegerMessage(ByteBufAllocator allocator, IntegerRedisMessage msg, List<Object> out) {
        ByteBuf buf = allocator.ioBuffer(23);
        buf.writeByte(RedisMessageType.INTEGER.value());
        buf.writeBytes(this.numberToBytes(msg.value()));
        buf.writeShort(RedisConstants.EOL_SHORT);
        out.add(buf);
    }

    private void writeBulkStringHeader(ByteBufAllocator allocator, BulkStringHeaderRedisMessage msg, List<Object> out) {
        ByteBuf buf = allocator.ioBuffer(1 + (msg.isNull() ? 2 : 22));
        buf.writeByte(RedisMessageType.BULK_STRING.value());
        if (msg.isNull()) {
            buf.writeShort(RedisConstants.NULL_SHORT);
        } else {
            buf.writeBytes(this.numberToBytes(msg.bulkStringLength()));
            buf.writeShort(RedisConstants.EOL_SHORT);
        }
        out.add(buf);
    }

    private static void writeBulkStringContent(ByteBufAllocator allocator, BulkStringRedisContent msg, List<Object> out) {
        out.add(msg.content().retain());
        if (msg instanceof LastBulkStringRedisContent) {
            out.add(allocator.ioBuffer(2).writeShort(RedisConstants.EOL_SHORT));
        }
    }

    private void writeFullBulkStringMessage(ByteBufAllocator allocator, FullBulkStringRedisMessage msg, List<Object> out) {
        if (msg.isNull()) {
            ByteBuf buf = allocator.ioBuffer(5);
            buf.writeByte(RedisMessageType.BULK_STRING.value());
            buf.writeShort(RedisConstants.NULL_SHORT);
            buf.writeShort(RedisConstants.EOL_SHORT);
            out.add(buf);
        } else {
            ByteBuf headerBuf = allocator.ioBuffer(23);
            headerBuf.writeByte(RedisMessageType.BULK_STRING.value());
            headerBuf.writeBytes(this.numberToBytes(msg.content().readableBytes()));
            headerBuf.writeShort(RedisConstants.EOL_SHORT);
            out.add(headerBuf);
            out.add(msg.content().retain());
            out.add(allocator.ioBuffer(2).writeShort(RedisConstants.EOL_SHORT));
        }
    }

    private void writeArrayHeader(ByteBufAllocator allocator, ArrayHeaderRedisMessage msg, List<Object> out) {
        this.writeArrayHeader(allocator, msg.isNull(), msg.length(), out);
    }

    private void writeArrayMessage(ByteBufAllocator allocator, ArrayRedisMessage msg, List<Object> out) {
        if (msg.isNull()) {
            this.writeArrayHeader(allocator, msg.isNull(), -1L, out);
        } else {
            this.writeArrayHeader(allocator, msg.isNull(), msg.children().size(), out);
            for (RedisMessage child : msg.children()) {
                this.writeRedisMessage(allocator, child, out);
            }
        }
    }

    private void writeArrayHeader(ByteBufAllocator allocator, boolean isNull, long length, List<Object> out) {
        if (isNull) {
            ByteBuf buf = allocator.ioBuffer(5);
            buf.writeByte(RedisMessageType.ARRAY_HEADER.value());
            buf.writeShort(RedisConstants.NULL_SHORT);
            buf.writeShort(RedisConstants.EOL_SHORT);
            out.add(buf);
        } else {
            ByteBuf buf = allocator.ioBuffer(23);
            buf.writeByte(RedisMessageType.ARRAY_HEADER.value());
            buf.writeBytes(this.numberToBytes(length));
            buf.writeShort(RedisConstants.EOL_SHORT);
            out.add(buf);
        }
    }

    private byte[] numberToBytes(long value) {
        byte[] bytes = this.messagePool.getByteBufOfInteger(value);
        return bytes != null ? bytes : RedisCodecUtil.longToAsciiBytes(value);
    }
}

