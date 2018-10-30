/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.handler.codec.smtp.DefaultSmtpRequest;
import io.netty.handler.codec.smtp.SmtpCommand;
import io.netty.handler.codec.smtp.SmtpRequest;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

public final class SmtpRequests {
    private static final SmtpRequest DATA = new DefaultSmtpRequest(SmtpCommand.DATA);
    private static final SmtpRequest NOOP = new DefaultSmtpRequest(SmtpCommand.NOOP);
    private static final SmtpRequest RSET = new DefaultSmtpRequest(SmtpCommand.RSET);
    private static final SmtpRequest HELP_NO_ARG = new DefaultSmtpRequest(SmtpCommand.HELP);
    private static final SmtpRequest QUIT = new DefaultSmtpRequest(SmtpCommand.QUIT);
    private static final AsciiString FROM_NULL_SENDER = AsciiString.cached("FROM:<>");

    public static SmtpRequest helo(CharSequence hostname) {
        return new DefaultSmtpRequest(SmtpCommand.HELO, hostname);
    }

    public static SmtpRequest ehlo(CharSequence hostname) {
        return new DefaultSmtpRequest(SmtpCommand.EHLO, hostname);
    }

    public static SmtpRequest noop() {
        return NOOP;
    }

    public static SmtpRequest data() {
        return DATA;
    }

    public static SmtpRequest rset() {
        return RSET;
    }

    public static SmtpRequest help(String cmd) {
        return cmd == null ? HELP_NO_ARG : new DefaultSmtpRequest(SmtpCommand.HELP, cmd);
    }

    public static SmtpRequest quit() {
        return QUIT;
    }

    public static /* varargs */ SmtpRequest mail(CharSequence sender, CharSequence ... mailParameters) {
        if (mailParameters == null || mailParameters.length == 0) {
            CharSequence[] arrcharSequence = new CharSequence[1];
            arrcharSequence[0] = sender != null ? "FROM:<" + sender + '>' : FROM_NULL_SENDER;
            return new DefaultSmtpRequest(SmtpCommand.MAIL, arrcharSequence);
        }
        ArrayList<CharSequence> params = new ArrayList<CharSequence>(mailParameters.length + 1);
        params.add(sender != null ? "FROM:<" + sender + '>' : FROM_NULL_SENDER);
        for (CharSequence param : mailParameters) {
            params.add(param);
        }
        return new DefaultSmtpRequest(SmtpCommand.MAIL, params);
    }

    public static /* varargs */ SmtpRequest rcpt(CharSequence recipient, CharSequence ... rcptParameters) {
        ObjectUtil.checkNotNull(recipient, "recipient");
        if (rcptParameters == null || rcptParameters.length == 0) {
            return new DefaultSmtpRequest(SmtpCommand.RCPT, "TO:<" + recipient + '>');
        }
        ArrayList<CharSequence> params = new ArrayList<CharSequence>(rcptParameters.length + 1);
        params.add("TO:<" + recipient + '>');
        for (CharSequence param : rcptParameters) {
            params.add(param);
        }
        return new DefaultSmtpRequest(SmtpCommand.RCPT, params);
    }

    public static SmtpRequest expn(CharSequence mailingList) {
        return new DefaultSmtpRequest(SmtpCommand.EXPN, ObjectUtil.checkNotNull(mailingList, "mailingList"));
    }

    public static SmtpRequest vrfy(CharSequence user) {
        return new DefaultSmtpRequest(SmtpCommand.VRFY, ObjectUtil.checkNotNull(user, "user"));
    }

    private SmtpRequests() {
    }
}

