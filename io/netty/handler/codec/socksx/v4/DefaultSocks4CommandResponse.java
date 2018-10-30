/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.socksx.v4.AbstractSocks4Message;
import io.netty.handler.codec.socksx.v4.Socks4CommandResponse;
import io.netty.handler.codec.socksx.v4.Socks4CommandStatus;
import io.netty.util.NetUtil;
import io.netty.util.internal.StringUtil;

public class DefaultSocks4CommandResponse
extends AbstractSocks4Message
implements Socks4CommandResponse {
    private final Socks4CommandStatus status;
    private final String dstAddr;
    private final int dstPort;

    public DefaultSocks4CommandResponse(Socks4CommandStatus status) {
        this(status, null, 0);
    }

    public DefaultSocks4CommandResponse(Socks4CommandStatus status, String dstAddr, int dstPort) {
        if (status == null) {
            throw new NullPointerException("cmdStatus");
        }
        if (dstAddr != null && !NetUtil.isValidIpV4Address(dstAddr)) {
            throw new IllegalArgumentException("dstAddr: " + dstAddr + " (expected: a valid IPv4 address)");
        }
        if (dstPort < 0 || dstPort > 65535) {
            throw new IllegalArgumentException("dstPort: " + dstPort + " (expected: 0~65535)");
        }
        this.status = status;
        this.dstAddr = dstAddr;
        this.dstPort = dstPort;
    }

    @Override
    public Socks4CommandStatus status() {
        return this.status;
    }

    @Override
    public String dstAddr() {
        return this.dstAddr;
    }

    @Override
    public int dstPort() {
        return this.dstPort;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(96);
        buf.append(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", dstAddr: ");
        } else {
            buf.append("(dstAddr: ");
        }
        buf.append(this.dstAddr());
        buf.append(", dstPort: ");
        buf.append(this.dstPort());
        buf.append(')');
        return buf.toString();
    }
}

