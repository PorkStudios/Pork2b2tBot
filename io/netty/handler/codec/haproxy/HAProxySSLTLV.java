/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.haproxy.HAProxyTLV;
import java.util.Collections;
import java.util.List;

public final class HAProxySSLTLV
extends HAProxyTLV {
    private final int verify;
    private final List<HAProxyTLV> tlvs;
    private final byte clientBitField;

    HAProxySSLTLV(int verify, byte clientBitField, List<HAProxyTLV> tlvs, ByteBuf rawContent) {
        super(HAProxyTLV.Type.PP2_TYPE_SSL, (byte)32, rawContent);
        this.verify = verify;
        this.tlvs = Collections.unmodifiableList(tlvs);
        this.clientBitField = clientBitField;
    }

    public boolean isPP2ClientCertConn() {
        return (this.clientBitField & 2) != 0;
    }

    public boolean isPP2ClientSSL() {
        return (this.clientBitField & 1) != 0;
    }

    public boolean isPP2ClientCertSess() {
        return (this.clientBitField & 4) != 0;
    }

    public int verify() {
        return this.verify;
    }

    public List<HAProxyTLV> encapsulatedTLVs() {
        return this.tlvs;
    }
}

