/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.MaxMessagesRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.util.internal.ObjectUtil;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelConfig
implements ChannelConfig {
    private static final MessageSizeEstimator DEFAULT_MSG_SIZE_ESTIMATOR = DefaultMessageSizeEstimator.DEFAULT;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private static final AtomicIntegerFieldUpdater<DefaultChannelConfig> AUTOREAD_UPDATER = AtomicIntegerFieldUpdater.newUpdater(DefaultChannelConfig.class, "autoRead");
    private static final AtomicReferenceFieldUpdater<DefaultChannelConfig, WriteBufferWaterMark> WATERMARK_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultChannelConfig.class, WriteBufferWaterMark.class, "writeBufferWaterMark");
    protected final Channel channel;
    private volatile ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;
    private volatile RecvByteBufAllocator rcvBufAllocator;
    private volatile MessageSizeEstimator msgSizeEstimator = DEFAULT_MSG_SIZE_ESTIMATOR;
    private volatile int connectTimeoutMillis = 30000;
    private volatile int writeSpinCount = 16;
    private volatile int autoRead = 1;
    private volatile boolean autoClose = true;
    private volatile WriteBufferWaterMark writeBufferWaterMark = WriteBufferWaterMark.DEFAULT;
    private volatile boolean pinEventExecutor = true;

    public DefaultChannelConfig(Channel channel) {
        this(channel, new AdaptiveRecvByteBufAllocator());
    }

    protected DefaultChannelConfig(Channel channel, RecvByteBufAllocator allocator) {
        this.setRecvByteBufAllocator(allocator, channel.metadata());
        this.channel = channel;
    }

    @Override
    public Map<ChannelOption<?>, Object> getOptions() {
        return this.getOptions(null, ChannelOption.CONNECT_TIMEOUT_MILLIS, ChannelOption.MAX_MESSAGES_PER_READ, ChannelOption.WRITE_SPIN_COUNT, ChannelOption.ALLOCATOR, ChannelOption.AUTO_READ, ChannelOption.AUTO_CLOSE, ChannelOption.RCVBUF_ALLOCATOR, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, ChannelOption.WRITE_BUFFER_WATER_MARK, ChannelOption.MESSAGE_SIZE_ESTIMATOR, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    protected /* varargs */ Map<ChannelOption<?>, Object> getOptions(Map<ChannelOption<?>, Object> result, ChannelOption<?> ... options) {
        if (result == null) {
            result = new IdentityHashMap();
        }
        for (ChannelOption<?> o : options) {
            result.put(o, this.getOption(o));
        }
        return result;
    }

    @Override
    public boolean setOptions(Map<ChannelOption<?>, ?> options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        boolean setAllOptions = true;
        for (Map.Entry<ChannelOption<?>, ?> e : options.entrySet()) {
            if (this.setOption(e.getKey(), e.getValue())) continue;
            setAllOptions = false;
        }
        return setAllOptions;
    }

    @Override
    public <T> T getOption(ChannelOption<T> option) {
        if (option == null) {
            throw new NullPointerException("option");
        }
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            return this.getConnectTimeoutMillis();
        }
        if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            return this.getMaxMessagesPerRead();
        }
        if (option == ChannelOption.WRITE_SPIN_COUNT) {
            return this.getWriteSpinCount();
        }
        if (option == ChannelOption.ALLOCATOR) {
            return (T)this.getAllocator();
        }
        if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            return this.getRecvByteBufAllocator();
        }
        if (option == ChannelOption.AUTO_READ) {
            return this.isAutoRead();
        }
        if (option == ChannelOption.AUTO_CLOSE) {
            return this.isAutoClose();
        }
        if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            return this.getWriteBufferHighWaterMark();
        }
        if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            return this.getWriteBufferLowWaterMark();
        }
        if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
            return (T)this.getWriteBufferWaterMark();
        }
        if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            return (T)this.getMessageSizeEstimator();
        }
        if (option == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            return this.getPinEventExecutorPerGroup();
        }
        return null;
    }

    @Override
    public <T> boolean setOption(ChannelOption<T> option, T value) {
        this.validate(option, value);
        if (option == ChannelOption.CONNECT_TIMEOUT_MILLIS) {
            this.setConnectTimeoutMillis((Integer)value);
        } else if (option == ChannelOption.MAX_MESSAGES_PER_READ) {
            this.setMaxMessagesPerRead((Integer)value);
        } else if (option == ChannelOption.WRITE_SPIN_COUNT) {
            this.setWriteSpinCount((Integer)value);
        } else if (option == ChannelOption.ALLOCATOR) {
            this.setAllocator((ByteBufAllocator)value);
        } else if (option == ChannelOption.RCVBUF_ALLOCATOR) {
            this.setRecvByteBufAllocator((RecvByteBufAllocator)value);
        } else if (option == ChannelOption.AUTO_READ) {
            this.setAutoRead((Boolean)value);
        } else if (option == ChannelOption.AUTO_CLOSE) {
            this.setAutoClose((Boolean)value);
        } else if (option == ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK) {
            this.setWriteBufferHighWaterMark((Integer)value);
        } else if (option == ChannelOption.WRITE_BUFFER_LOW_WATER_MARK) {
            this.setWriteBufferLowWaterMark((Integer)value);
        } else if (option == ChannelOption.WRITE_BUFFER_WATER_MARK) {
            this.setWriteBufferWaterMark((WriteBufferWaterMark)value);
        } else if (option == ChannelOption.MESSAGE_SIZE_ESTIMATOR) {
            this.setMessageSizeEstimator((MessageSizeEstimator)value);
        } else if (option == ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP) {
            this.setPinEventExecutorPerGroup((Boolean)value);
        } else {
            return false;
        }
        return true;
    }

    protected <T> void validate(ChannelOption<T> option, T value) {
        if (option == null) {
            throw new NullPointerException("option");
        }
        option.validate(value);
    }

    @Override
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }

    @Override
    public ChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
        if (connectTimeoutMillis < 0) {
            throw new IllegalArgumentException(String.format("connectTimeoutMillis: %d (expected: >= 0)", connectTimeoutMillis));
        }
        this.connectTimeoutMillis = connectTimeoutMillis;
        return this;
    }

    @Deprecated
    @Override
    public int getMaxMessagesPerRead() {
        try {
            MaxMessagesRecvByteBufAllocator allocator = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
            return allocator.maxMessagesPerRead();
        }
        catch (ClassCastException e) {
            throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", e);
        }
    }

    @Deprecated
    @Override
    public ChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
        try {
            MaxMessagesRecvByteBufAllocator allocator = (MaxMessagesRecvByteBufAllocator)this.getRecvByteBufAllocator();
            allocator.maxMessagesPerRead(maxMessagesPerRead);
            return this;
        }
        catch (ClassCastException e) {
            throw new IllegalStateException("getRecvByteBufAllocator() must return an object of type MaxMessagesRecvByteBufAllocator", e);
        }
    }

    @Override
    public int getWriteSpinCount() {
        return this.writeSpinCount;
    }

    @Override
    public ChannelConfig setWriteSpinCount(int writeSpinCount) {
        if (writeSpinCount <= 0) {
            throw new IllegalArgumentException("writeSpinCount must be a positive integer.");
        }
        if (writeSpinCount == Integer.MAX_VALUE) {
            --writeSpinCount;
        }
        this.writeSpinCount = writeSpinCount;
        return this;
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return this.allocator;
    }

    @Override
    public ChannelConfig setAllocator(ByteBufAllocator allocator) {
        if (allocator == null) {
            throw new NullPointerException("allocator");
        }
        this.allocator = allocator;
        return this;
    }

    @Override
    public <T extends RecvByteBufAllocator> T getRecvByteBufAllocator() {
        return (T)this.rcvBufAllocator;
    }

    @Override
    public ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
        this.rcvBufAllocator = ObjectUtil.checkNotNull(allocator, "allocator");
        return this;
    }

    private void setRecvByteBufAllocator(RecvByteBufAllocator allocator, ChannelMetadata metadata) {
        if (allocator instanceof MaxMessagesRecvByteBufAllocator) {
            ((MaxMessagesRecvByteBufAllocator)allocator).maxMessagesPerRead(metadata.defaultMaxMessagesPerRead());
        } else if (allocator == null) {
            throw new NullPointerException("allocator");
        }
        this.setRecvByteBufAllocator(allocator);
    }

    @Override
    public boolean isAutoRead() {
        return this.autoRead == 1;
    }

    @Override
    public ChannelConfig setAutoRead(boolean autoRead) {
        boolean oldAutoRead;
        boolean bl = oldAutoRead = AUTOREAD_UPDATER.getAndSet(this, autoRead ? 1 : 0) == 1;
        if (autoRead && !oldAutoRead) {
            this.channel.read();
        } else if (!autoRead && oldAutoRead) {
            this.autoReadCleared();
        }
        return this;
    }

    protected void autoReadCleared() {
    }

    @Override
    public boolean isAutoClose() {
        return this.autoClose;
    }

    @Override
    public ChannelConfig setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
        return this;
    }

    @Override
    public int getWriteBufferHighWaterMark() {
        return this.writeBufferWaterMark.high();
    }

    @Override
    public ChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
        WriteBufferWaterMark waterMark;
        if (writeBufferHighWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferHighWaterMark must be >= 0");
        }
        do {
            if (writeBufferHighWaterMark >= (waterMark = this.writeBufferWaterMark).low()) continue;
            throw new IllegalArgumentException("writeBufferHighWaterMark cannot be less than writeBufferLowWaterMark (" + waterMark.low() + "): " + writeBufferHighWaterMark);
        } while (!WATERMARK_UPDATER.compareAndSet(this, waterMark, new WriteBufferWaterMark(waterMark.low(), writeBufferHighWaterMark, false)));
        return this;
    }

    @Override
    public int getWriteBufferLowWaterMark() {
        return this.writeBufferWaterMark.low();
    }

    @Override
    public ChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
        WriteBufferWaterMark waterMark;
        if (writeBufferLowWaterMark < 0) {
            throw new IllegalArgumentException("writeBufferLowWaterMark must be >= 0");
        }
        do {
            if (writeBufferLowWaterMark <= (waterMark = this.writeBufferWaterMark).high()) continue;
            throw new IllegalArgumentException("writeBufferLowWaterMark cannot be greater than writeBufferHighWaterMark (" + waterMark.high() + "): " + writeBufferLowWaterMark);
        } while (!WATERMARK_UPDATER.compareAndSet(this, waterMark, new WriteBufferWaterMark(writeBufferLowWaterMark, waterMark.high(), false)));
        return this;
    }

    @Override
    public ChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark writeBufferWaterMark) {
        this.writeBufferWaterMark = ObjectUtil.checkNotNull(writeBufferWaterMark, "writeBufferWaterMark");
        return this;
    }

    @Override
    public WriteBufferWaterMark getWriteBufferWaterMark() {
        return this.writeBufferWaterMark;
    }

    @Override
    public MessageSizeEstimator getMessageSizeEstimator() {
        return this.msgSizeEstimator;
    }

    @Override
    public ChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
        if (estimator == null) {
            throw new NullPointerException("estimator");
        }
        this.msgSizeEstimator = estimator;
        return this;
    }

    private ChannelConfig setPinEventExecutorPerGroup(boolean pinEventExecutor) {
        this.pinEventExecutor = pinEventExecutor;
        return this;
    }

    private boolean getPinEventExecutorPerGroup() {
        return this.pinEventExecutor;
    }
}

