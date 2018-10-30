/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.AsciiString;
import java.nio.ByteBuffer;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public final class EmptyArrays {
    public static final int[] EMPTY_INTS = new int[0];
    public static final byte[] EMPTY_BYTES = new byte[0];
    public static final char[] EMPTY_CHARS = new char[0];
    public static final Object[] EMPTY_OBJECTS = new Object[0];
    public static final Class<?>[] EMPTY_CLASSES = new Class[0];
    public static final String[] EMPTY_STRINGS = new String[0];
    public static final AsciiString[] EMPTY_ASCII_STRINGS = new AsciiString[0];
    public static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    public static final ByteBuffer[] EMPTY_BYTE_BUFFERS = new ByteBuffer[0];
    public static final Certificate[] EMPTY_CERTIFICATES = new Certificate[0];
    public static final X509Certificate[] EMPTY_X509_CERTIFICATES = new X509Certificate[0];
    public static final javax.security.cert.X509Certificate[] EMPTY_JAVAX_X509_CERTIFICATES = new javax.security.cert.X509Certificate[0];

    private EmptyArrays() {
    }
}

