/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.protocols;

import org.java_websocket.protocols.IProtocol;

public class Protocol
implements IProtocol {
    private final String providedProtocol;

    public Protocol(String providedProtocol) {
        if (providedProtocol == null) {
            throw new IllegalArgumentException();
        }
        this.providedProtocol = providedProtocol;
    }

    public boolean acceptProvidedProtocol(String inputProtocolHeader) {
        String[] headers;
        String protocolHeader = inputProtocolHeader.replaceAll(" ", "");
        for (String header : headers = protocolHeader.split(",")) {
            if (!this.providedProtocol.equals(header)) continue;
            return true;
        }
        return false;
    }

    public String getProvidedProtocol() {
        return this.providedProtocol;
    }

    public IProtocol copyInstance() {
        return new Protocol(this.getProvidedProtocol());
    }

    public String toString() {
        return this.getProvidedProtocol();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Protocol protocol = (Protocol)o;
        return this.providedProtocol.equals(protocol.providedProtocol);
    }

    public int hashCode() {
        return this.providedProtocol.hashCode();
    }
}

