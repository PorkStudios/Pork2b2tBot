/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.stomp.DefaultStompHeaders;
import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.handler.codec.stomp.StompHeadersSubframe;

public class DefaultStompHeadersSubframe
implements StompHeadersSubframe {
    protected final StompCommand command;
    protected DecoderResult decoderResult = DecoderResult.SUCCESS;
    protected final StompHeaders headers = new DefaultStompHeaders();

    public DefaultStompHeadersSubframe(StompCommand command) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        this.command = command;
    }

    @Override
    public StompCommand command() {
        return this.command;
    }

    @Override
    public StompHeaders headers() {
        return this.headers;
    }

    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }

    @Override
    public void setDecoderResult(DecoderResult decoderResult) {
        this.decoderResult = decoderResult;
    }

    public String toString() {
        return "StompFrame{command=" + (Object)((Object)this.command) + ", headers=" + this.headers + '}';
    }
}

