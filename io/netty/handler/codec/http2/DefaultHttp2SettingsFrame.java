/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http2;

import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2SettingsFrame;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultHttp2SettingsFrame
implements Http2SettingsFrame {
    private final Http2Settings settings;

    public DefaultHttp2SettingsFrame(Http2Settings settings) {
        this.settings = ObjectUtil.checkNotNull(settings, "settings");
    }

    @Override
    public Http2Settings settings() {
        return this.settings;
    }

    @Override
    public String name() {
        return "SETTINGS";
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "(settings=" + this.settings + ')';
    }
}

