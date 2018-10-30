/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx;

import io.netty.handler.codec.DecoderResultProvider;
import io.netty.handler.codec.socksx.SocksVersion;

public interface SocksMessage
extends DecoderResultProvider {
    public SocksVersion version();
}

