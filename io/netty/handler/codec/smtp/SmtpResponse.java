/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import java.util.List;

public interface SmtpResponse {
    public int code();

    public List<CharSequence> details();
}

