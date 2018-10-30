/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.serialization;

public interface ClassResolver {
    public Class<?> resolve(String var1) throws ClassNotFoundException;
}

