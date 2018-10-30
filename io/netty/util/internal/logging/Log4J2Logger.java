/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.MessageFactory
 *  org.apache.logging.log4j.spi.ExtendedLogger
 *  org.apache.logging.log4j.spi.ExtendedLoggerWrapper
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class Log4J2Logger
extends ExtendedLoggerWrapper
implements InternalLogger {
    private static final long serialVersionUID = 5485418394879791397L;
    private static final String EXCEPTION_MESSAGE = "Unexpected exception:";

    Log4J2Logger(Logger logger) {
        super((ExtendedLogger)logger, logger.getName(), logger.getMessageFactory());
    }

    @Override
    public String name() {
        return this.getName();
    }

    @Override
    public void trace(Throwable t) {
        this.log(Level.TRACE, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void debug(Throwable t) {
        this.log(Level.DEBUG, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void info(Throwable t) {
        this.log(Level.INFO, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void warn(Throwable t) {
        this.log(Level.WARN, EXCEPTION_MESSAGE, t);
    }

    @Override
    public void error(Throwable t) {
        this.log(Level.ERROR, EXCEPTION_MESSAGE, t);
    }

    @Override
    public boolean isEnabled(InternalLogLevel level) {
        return this.isEnabled(this.toLevel(level));
    }

    @Override
    public void log(InternalLogLevel level, String msg) {
        this.log(this.toLevel(level), msg);
    }

    @Override
    public void log(InternalLogLevel level, String format, Object arg) {
        this.log(this.toLevel(level), format, arg);
    }

    @Override
    public void log(InternalLogLevel level, String format, Object argA, Object argB) {
        this.log(this.toLevel(level), format, argA, argB);
    }

    @Override
    public /* varargs */ void log(InternalLogLevel level, String format, Object ... arguments) {
        this.log(this.toLevel(level), format, arguments);
    }

    @Override
    public void log(InternalLogLevel level, String msg, Throwable t) {
        this.log(this.toLevel(level), msg, t);
    }

    @Override
    public void log(InternalLogLevel level, Throwable t) {
        this.log(this.toLevel(level), EXCEPTION_MESSAGE, t);
    }

    protected Level toLevel(InternalLogLevel level) {
        switch (level) {
            case INFO: {
                return Level.INFO;
            }
            case DEBUG: {
                return Level.DEBUG;
            }
            case WARN: {
                return Level.WARN;
            }
            case ERROR: {
                return Level.ERROR;
            }
            case TRACE: {
                return Level.TRACE;
            }
        }
        throw new Error();
    }

}

