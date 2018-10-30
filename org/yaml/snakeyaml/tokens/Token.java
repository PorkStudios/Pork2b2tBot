/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

public abstract class Token {
    private final Mark startMark;
    private final Mark endMark;

    public Token(Mark startMark, Mark endMark) {
        if (startMark == null || endMark == null) {
            throw new YAMLException("Token requires marks.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public String toString() {
        return "<" + this.getClass().getName() + "(" + this.getArguments() + ")>";
    }

    public Mark getStartMark() {
        return this.startMark;
    }

    public Mark getEndMark() {
        return this.endMark;
    }

    protected String getArguments() {
        return "";
    }

    public abstract ID getTokenId();

    public boolean equals(Object obj) {
        if (obj instanceof Token) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public static enum ID {
        Alias,
        Anchor,
        BlockEnd,
        BlockEntry,
        BlockMappingStart,
        BlockSequenceStart,
        Directive,
        DocumentEnd,
        DocumentStart,
        FlowEntry,
        FlowMappingEnd,
        FlowMappingStart,
        FlowSequenceEnd,
        FlowSequenceStart,
        Key,
        Scalar,
        StreamEnd,
        StreamStart,
        Tag,
        Value,
        Whitespace,
        Comment,
        Error;
        

        private ID() {
        }
    }

}

