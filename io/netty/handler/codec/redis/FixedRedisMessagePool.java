/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.redis.AbstractStringRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisCodecUtil;
import io.netty.handler.codec.redis.RedisMessagePool;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;
import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public final class FixedRedisMessagePool
implements RedisMessagePool {
    private static final String[] DEFAULT_SIMPLE_STRINGS = new String[]{"OK", "PONG", "QUEUED"};
    private static final String[] DEFAULT_ERRORS = new String[]{"ERR", "ERR index out of range", "ERR no such key", "ERR source and destination objects are the same", "ERR syntax error", "BUSY Redis is busy running a script. You can only call SCRIPT KILL or SHUTDOWN NOSAVE.", "BUSYKEY Target key name already exists.", "EXECABORT Transaction discarded because of previous errors.", "LOADING Redis is loading the dataset in memory", "MASTERDOWN Link with MASTER is down and slave-serve-stale-data is set to 'no'.", "MISCONF Redis is configured to save RDB snapshots, but is currently not able to persist on disk. Commands that may modify the data set are disabled. Please check Redis logs for details about the error.", "NOAUTH Authentication required.", "NOREPLICAS Not enough good slaves to write.", "NOSCRIPT No matching script. Please use EVAL.", "OOM command not allowed when used memory > 'maxmemory'.", "READONLY You can't write against a read only slave.", "WRONGTYPE Operation against a key holding the wrong kind of value"};
    private static final long MIN_CACHED_INTEGER_NUMBER = -1L;
    private static final long MAX_CACHED_INTEGER_NUMBER = 128L;
    private static final int SIZE_CACHED_INTEGER_NUMBER = 129;
    public static final FixedRedisMessagePool INSTANCE = new FixedRedisMessagePool();
    private final Map<ByteBuf, SimpleStringRedisMessage> byteBufToSimpleStrings = new HashMap<ByteBuf, SimpleStringRedisMessage>(DEFAULT_SIMPLE_STRINGS.length, 1.0f);
    private final Map<String, SimpleStringRedisMessage> stringToSimpleStrings = new HashMap<String, SimpleStringRedisMessage>(DEFAULT_SIMPLE_STRINGS.length, 1.0f);
    private final Map<ByteBuf, ErrorRedisMessage> byteBufToErrors;
    private final Map<String, ErrorRedisMessage> stringToErrors;
    private final Map<ByteBuf, IntegerRedisMessage> byteBufToIntegers;
    private final LongObjectMap<IntegerRedisMessage> longToIntegers;
    private final LongObjectMap<byte[]> longToByteBufs;

    private FixedRedisMessagePool() {
        AbstractStringRedisMessage cached;
        ByteBuf key;
        for (String message : DEFAULT_SIMPLE_STRINGS) {
            key = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8))));
            cached = new SimpleStringRedisMessage(message);
            this.byteBufToSimpleStrings.put(key, (SimpleStringRedisMessage)cached);
            this.stringToSimpleStrings.put(message, (SimpleStringRedisMessage)cached);
        }
        this.byteBufToErrors = new HashMap<ByteBuf, ErrorRedisMessage>(DEFAULT_ERRORS.length, 1.0f);
        this.stringToErrors = new HashMap<String, ErrorRedisMessage>(DEFAULT_ERRORS.length, 1.0f);
        for (String message : DEFAULT_ERRORS) {
            key = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(message.getBytes(CharsetUtil.UTF_8))));
            cached = new ErrorRedisMessage(message);
            this.byteBufToErrors.put(key, (ErrorRedisMessage)cached);
            this.stringToErrors.put(message, (ErrorRedisMessage)cached);
        }
        this.byteBufToIntegers = new HashMap<ByteBuf, IntegerRedisMessage>(129, 1.0f);
        this.longToIntegers = new LongObjectHashMap<IntegerRedisMessage>(129, 1.0f);
        this.longToByteBufs = new LongObjectHashMap<byte[]>(129, 1.0f);
        for (long value = -1L; value < 128L; ++value) {
            byte[] keyBytes = RedisCodecUtil.longToAsciiBytes(value);
            ByteBuf keyByteBuf = Unpooled.unmodifiableBuffer(Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(keyBytes)));
            IntegerRedisMessage cached2 = new IntegerRedisMessage(value);
            this.byteBufToIntegers.put(keyByteBuf, cached2);
            this.longToIntegers.put(value, cached2);
            this.longToByteBufs.put(value, keyBytes);
        }
    }

    @Override
    public SimpleStringRedisMessage getSimpleString(String content) {
        return this.stringToSimpleStrings.get(content);
    }

    @Override
    public SimpleStringRedisMessage getSimpleString(ByteBuf content) {
        return this.byteBufToSimpleStrings.get(content);
    }

    @Override
    public ErrorRedisMessage getError(String content) {
        return this.stringToErrors.get(content);
    }

    @Override
    public ErrorRedisMessage getError(ByteBuf content) {
        return this.byteBufToErrors.get(content);
    }

    @Override
    public IntegerRedisMessage getInteger(long value) {
        return this.longToIntegers.get(value);
    }

    @Override
    public IntegerRedisMessage getInteger(ByteBuf content) {
        return this.byteBufToIntegers.get(content);
    }

    @Override
    public byte[] getByteBufOfInteger(long value) {
        return this.longToByteBufs.get(value);
    }
}

