/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLogger;
import org.apache.log4j.Logger;

public class Log4JLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new Log4JLoggerFactory();

    @Override
    public InternalLogger newInstance(String name) {
        return new Log4JLogger(Logger.getLogger((String)name));
    }
}

