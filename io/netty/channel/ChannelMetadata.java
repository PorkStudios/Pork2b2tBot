/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

public final class ChannelMetadata {
    private final boolean hasDisconnect;
    private final int defaultMaxMessagesPerRead;

    public ChannelMetadata(boolean hasDisconnect) {
        this(hasDisconnect, 1);
    }

    public ChannelMetadata(boolean hasDisconnect, int defaultMaxMessagesPerRead) {
        if (defaultMaxMessagesPerRead <= 0) {
            throw new IllegalArgumentException("defaultMaxMessagesPerRead: " + defaultMaxMessagesPerRead + " (expected > 0)");
        }
        this.hasDisconnect = hasDisconnect;
        this.defaultMaxMessagesPerRead = defaultMaxMessagesPerRead;
    }

    public boolean hasDisconnect() {
        return this.hasDisconnect;
    }

    public int defaultMaxMessagesPerRead() {
        return this.defaultMaxMessagesPerRead;
    }
}

