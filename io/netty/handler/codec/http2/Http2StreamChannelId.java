/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.channel.ChannelId;

final class Http2StreamChannelId
implements ChannelId {
    private static final long serialVersionUID = -6642338822166867585L;
    private final int id;
    private final ChannelId parentId;

    Http2StreamChannelId(ChannelId parentId, int id) {
        this.parentId = parentId;
        this.id = id;
    }

    @Override
    public String asShortText() {
        return this.parentId.asShortText() + '/' + this.id;
    }

    @Override
    public String asLongText() {
        return this.parentId.asLongText() + '/' + this.id;
    }

    @Override
    public int compareTo(ChannelId o) {
        if (o instanceof Http2StreamChannelId) {
            Http2StreamChannelId otherId = (Http2StreamChannelId)o;
            int res = this.parentId.compareTo(otherId.parentId);
            if (res == 0) {
                return this.id - otherId.id;
            }
            return res;
        }
        return this.parentId.compareTo(o);
    }

    public int hashCode() {
        return this.id * 31 + this.parentId.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Http2StreamChannelId)) {
            return false;
        }
        Http2StreamChannelId otherId = (Http2StreamChannelId)obj;
        return this.id == otherId.id && this.parentId.equals(otherId.parentId);
    }

    public String toString() {
        return this.asShortText();
    }
}

