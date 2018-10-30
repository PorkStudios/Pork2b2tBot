/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Map;

public final class SmtpCommand {
    public static final SmtpCommand EHLO = new SmtpCommand(AsciiString.cached("EHLO"));
    public static final SmtpCommand HELO = new SmtpCommand(AsciiString.cached("HELO"));
    public static final SmtpCommand MAIL = new SmtpCommand(AsciiString.cached("MAIL"));
    public static final SmtpCommand RCPT = new SmtpCommand(AsciiString.cached("RCPT"));
    public static final SmtpCommand DATA = new SmtpCommand(AsciiString.cached("DATA"));
    public static final SmtpCommand NOOP = new SmtpCommand(AsciiString.cached("NOOP"));
    public static final SmtpCommand RSET = new SmtpCommand(AsciiString.cached("RSET"));
    public static final SmtpCommand EXPN = new SmtpCommand(AsciiString.cached("EXPN"));
    public static final SmtpCommand VRFY = new SmtpCommand(AsciiString.cached("VRFY"));
    public static final SmtpCommand HELP = new SmtpCommand(AsciiString.cached("HELP"));
    public static final SmtpCommand QUIT = new SmtpCommand(AsciiString.cached("QUIT"));
    private static final Map<String, SmtpCommand> COMMANDS = new HashMap<String, SmtpCommand>();
    private final AsciiString name;

    public static SmtpCommand valueOf(CharSequence commandName) {
        ObjectUtil.checkNotNull(commandName, "commandName");
        SmtpCommand command = COMMANDS.get(commandName.toString());
        return command != null ? command : new SmtpCommand(AsciiString.of(commandName));
    }

    private SmtpCommand(AsciiString name) {
        this.name = name;
    }

    public AsciiString name() {
        return this.name;
    }

    void encode(ByteBuf buffer) {
        ByteBufUtil.writeAscii(buffer, (CharSequence)this.name);
    }

    boolean isContentExpected() {
        return this.equals(DATA);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SmtpCommand)) {
            return false;
        }
        return this.name.contentEqualsIgnoreCase(((SmtpCommand)obj).name());
    }

    public String toString() {
        return "SmtpCommand{name=" + this.name + '}';
    }

    static {
        COMMANDS.put(EHLO.name().toString(), EHLO);
        COMMANDS.put(HELO.name().toString(), HELO);
        COMMANDS.put(MAIL.name().toString(), MAIL);
        COMMANDS.put(RCPT.name().toString(), RCPT);
        COMMANDS.put(DATA.name().toString(), DATA);
        COMMANDS.put(NOOP.name().toString(), NOOP);
        COMMANDS.put(RSET.name().toString(), RSET);
        COMMANDS.put(EXPN.name().toString(), EXPN);
        COMMANDS.put(VRFY.name().toString(), VRFY);
        COMMANDS.put(HELP.name().toString(), HELP);
        COMMANDS.put(QUIT.name().toString(), QUIT);
    }
}

