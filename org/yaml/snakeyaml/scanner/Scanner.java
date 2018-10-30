/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.Token;

public interface Scanner {
    public /* varargs */ boolean checkToken(Token.ID ... var1);

    public Token peekToken();

    public Token getToken();
}

