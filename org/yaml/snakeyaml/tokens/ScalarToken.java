/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

public final class ScalarToken
extends Token {
    private final String value;
    private final boolean plain;
    private final char style;

    public ScalarToken(String value, Mark startMark, Mark endMark, boolean plain) {
        this(value, plain, startMark, endMark, '\u0000');
    }

    public ScalarToken(String value, boolean plain, Mark startMark, Mark endMark, char style) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        this.style = style;
    }

    public boolean getPlain() {
        return this.plain;
    }

    public String getValue() {
        return this.value;
    }

    public char getStyle() {
        return this.style;
    }

    @Override
    protected String getArguments() {
        return "value=" + this.value + ", plain=" + this.plain + ", style=" + this.style;
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Scalar;
    }
}

