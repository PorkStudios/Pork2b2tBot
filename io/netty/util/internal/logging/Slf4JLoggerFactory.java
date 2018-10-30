/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.helpers.NOPLoggerFactory
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4JLoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new Slf4JLoggerFactory();

    @Deprecated
    public Slf4JLoggerFactory() {
    }

    Slf4JLoggerFactory(boolean failIfNOP) {
        assert (failIfNOP);
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new Slf4JLogger(LoggerFactory.getLogger((String)name));
    }
}

