/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log4J2LoggerFactory
extends InternalLoggerFactory {
    public static final InternalLoggerFactory INSTANCE = new Log4J2LoggerFactory();

    @Override
    public InternalLogger newInstance(String name) {
        return new Log4J2Logger(LogManager.getLogger((String)name));
    }
}

