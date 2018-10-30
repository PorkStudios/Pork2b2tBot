/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;

public interface HttpObject
extends DecoderResultProvider {
    @Deprecated
    public DecoderResult getDecoderResult();
}

