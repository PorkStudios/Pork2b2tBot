/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

import io.netty.handler.codec.stomp.StompCommand;
import io.netty.handler.codec.stomp.StompHeaders;
import io.netty.handler.codec.stomp.StompSubframe;

public interface StompHeadersSubframe
extends StompSubframe {
    public StompCommand command();

    public StompHeaders headers();
}

