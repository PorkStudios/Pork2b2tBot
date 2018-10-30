/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.handler.codec.DecoderResult;

public interface DecoderResultProvider {
    public DecoderResult decoderResult();

    public void setDecoderResult(DecoderResult var1);
}

