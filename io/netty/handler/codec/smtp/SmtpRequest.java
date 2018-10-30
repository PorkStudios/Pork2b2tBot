/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.handler.codec.smtp.SmtpCommand;
import java.util.List;

public interface SmtpRequest {
    public SmtpCommand command();

    public List<CharSequence> parameters();
}

