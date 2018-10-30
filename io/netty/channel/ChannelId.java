/*
 * Decompiled with CFR 0_132.
 */
package io.netty.channel;

import java.io.Serializable;

public interface ChannelId
extends Serializable,
Comparable<ChannelId> {
    public String asShortText();

    public String asLongText();
}

