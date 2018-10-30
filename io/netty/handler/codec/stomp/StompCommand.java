/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.stomp;

public enum StompCommand {
    STOMP,
    CONNECT,
    CONNECTED,
    SEND,
    SUBSCRIBE,
    UNSUBSCRIBE,
    ACK,
    NACK,
    BEGIN,
    DISCONNECT,
    MESSAGE,
    RECEIPT,
    ERROR,
    UNKNOWN;
    

    private StompCommand() {
    }
}

