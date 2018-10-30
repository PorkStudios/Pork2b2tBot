/*
 * Decompiled with CFR 0_132.
 */
package it.unimi.dsi.fastutil.io;

import java.io.IOException;

public interface RepositionableStream {
    public void position(long var1) throws IOException;

    public long position() throws IOException;
}

