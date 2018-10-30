/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Stack;

public interface ByteStack
extends Stack<Byte> {
    @Override
    public void push(byte var1);

    public byte popByte();

    public byte topByte();

    public byte peekByte(int var1);

    @Deprecated
    @Override
    default public void push(Byte o) {
        this.push((byte)o);
    }

    @Deprecated
    @Override
    default public Byte pop() {
        return this.popByte();
    }

    @Deprecated
    @Override
    default public Byte top() {
        return this.topByte();
    }

    @Deprecated
    @Override
    default public Byte peek(int i) {
        return this.peekByte(i);
    }
}

