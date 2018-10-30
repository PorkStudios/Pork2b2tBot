/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.handler.codec.smtp.SmtpUtils;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class DefaultSmtpRequest
implements SmtpRequest {
    private final SmtpCommand command;
    private final List<CharSequence> parameters;

    public DefaultSmtpRequest(SmtpCommand command) {
        this.command = ObjectUtil.checkNotNull(command, "command");
        this.parameters = Collections.emptyList();
    }

    public /* varargs */ DefaultSmtpRequest(SmtpCommand command, CharSequence ... parameters) {
        this.command = ObjectUtil.checkNotNull(command, "command");
        this.parameters = SmtpUtils.toUnmodifiableList(parameters);
    }

    public /* varargs */ DefaultSmtpRequest(CharSequence command, CharSequence ... parameters) {
        this(SmtpCommand.valueOf(command), parameters);
    }

    DefaultSmtpRequest(SmtpCommand command, List<CharSequence> parameters) {
        this.command = ObjectUtil.checkNotNull(command, "command");
        this.parameters = parameters != null ? Collections.unmodifiableList(parameters) : Collections.emptyList();
    }

    @Override
    public SmtpCommand command() {
        return this.command;
    }

    @Override
    public List<CharSequence> parameters() {
        return this.parameters;
    }

    public int hashCode() {
        return this.command.hashCode() * 31 + this.parameters.hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DefaultSmtpRequest)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        DefaultSmtpRequest other = (DefaultSmtpRequest)o;
        return this.command().equals(other.command()) && this.parameters().equals(other.parameters());
    }

    public String toString() {
        return "DefaultSmtpRequest{command=" + this.command + ", parameters=" + this.parameters + '}';
    }
}

