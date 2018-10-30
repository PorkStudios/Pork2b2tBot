/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.TagTuple;
import org.yaml.snakeyaml.tokens.Token;

public final class TagToken
extends Token {
    private final TagTuple value;

    public TagToken(TagTuple value, Mark startMark, Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }

    public TagTuple getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        return "value=[" + this.value.getHandle() + ", " + this.value.getSuffix() + "]";
    }

    @Override
    public Token.ID getTokenId() {
        return Token.ID.Tag;
    }
}

