/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLogger;
import java.util.logging.Logger;

public class JdkLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new JdkLoggerFactory();

    @Override
    public InternalLogger newInstance(String name) {
        return new JdkLogger(Logger.getLogger(name));
    }
}

