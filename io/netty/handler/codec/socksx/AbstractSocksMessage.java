/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.SocksMessage;

public abstract class AbstractSocksMessage
implements SocksMessage {
    private DecoderResult decoderResult = DecoderResult.SUCCESS;

    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        if (decoderResult == null) {
            throw new NullPointerException("decoderResult");
        }
        this.decoderResult = decoderResult;
    }
}

