/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl;

import java.io.PrintStream;

public class Constants {
    private static final boolean VERBOSE;
    public static final int DEFAULT_CAPACITY = 10;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    public static final byte DEFAULT_BYTE_NO_ENTRY_VALUE;
    public static final short DEFAULT_SHORT_NO_ENTRY_VALUE;
    public static final char DEFAULT_CHAR_NO_ENTRY_VALUE;
    public static final int DEFAULT_INT_NO_ENTRY_VALUE;
    public static final long DEFAULT_LONG_NO_ENTRY_VALUE;
    public static final float DEFAULT_FLOAT_NO_ENTRY_VALUE;
    public static final double DEFAULT_DOUBLE_NO_ENTRY_VALUE;

    static {
        String property;
        VERBOSE = System.getProperty("gnu.trove.verbose", null) != null;
        String property2 = System.getProperty("gnu.trove.no_entry.byte", "0");
        int value = "MAX_VALUE".equalsIgnoreCase(property2) ? 127 : ("MIN_VALUE".equalsIgnoreCase(property2) ? -128 : (int)Byte.valueOf(property2).byteValue());
        if (value > 127) {
            value = 127;
        } else if (value < -128) {
            value = -128;
        }
        DEFAULT_BYTE_NO_ENTRY_VALUE = (byte)value;
        if (VERBOSE) {
            System.out.println("DEFAULT_BYTE_NO_ENTRY_VALUE: " + DEFAULT_BYTE_NO_ENTRY_VALUE);
        }
        value = "MAX_VALUE".equalsIgnoreCase(property2 = System.getProperty("gnu.trove.no_entry.short", "0")) ? 32767 : ("MIN_VALUE".equalsIgnoreCase(property2) ? -32768 : (int)Short.valueOf(property2).shortValue());
        if (value > 32767) {
            value = 32767;
        } else if (value < -32768) {
            value = -32768;
        }
        DEFAULT_SHORT_NO_ENTRY_VALUE = (short)value;
        if (VERBOSE) {
            System.out.println("DEFAULT_SHORT_NO_ENTRY_VALUE: " + DEFAULT_SHORT_NO_ENTRY_VALUE);
        }
        value = "MAX_VALUE".equalsIgnoreCase(property2 = System.getProperty("gnu.trove.no_entry.char", "\u0000")) ? 65535 : ("MIN_VALUE".equalsIgnoreCase(property2) ? 0 : property2.toCharArray()[0]);
        if (value > 65535) {
            value = 65535;
        } else if (value < 0) {
            value = 0;
        }
        DEFAULT_CHAR_NO_ENTRY_VALUE = (char)value;
        if (VERBOSE) {
            System.out.println("DEFAULT_CHAR_NO_ENTRY_VALUE: " + Integer.valueOf(value));
        }
        value = "MAX_VALUE".equalsIgnoreCase(property2 = System.getProperty("gnu.trove.no_entry.int", "0")) ? Integer.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase(property2) ? Integer.MIN_VALUE : Integer.valueOf(property2));
        DEFAULT_INT_NO_ENTRY_VALUE = value;
        if (VERBOSE) {
            System.out.println("DEFAULT_INT_NO_ENTRY_VALUE: " + DEFAULT_INT_NO_ENTRY_VALUE);
        }
        long value2 = "MAX_VALUE".equalsIgnoreCase(property = System.getProperty("gnu.trove.no_entry.long", "0")) ? Long.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase(property) ? Long.MIN_VALUE : Long.valueOf(property));
        DEFAULT_LONG_NO_ENTRY_VALUE = value2;
        if (VERBOSE) {
            System.out.println("DEFAULT_LONG_NO_ENTRY_VALUE: " + DEFAULT_LONG_NO_ENTRY_VALUE);
        }
        float value3 = "MAX_VALUE".equalsIgnoreCase(property2 = System.getProperty("gnu.trove.no_entry.float", "0")) ? Float.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase(property2) ? Float.MIN_VALUE : ("MIN_NORMAL".equalsIgnoreCase(property2) ? Float.MIN_NORMAL : ("NEGATIVE_INFINITY".equalsIgnoreCase(property2) ? Float.NEGATIVE_INFINITY : ("POSITIVE_INFINITY".equalsIgnoreCase(property2) ? Float.POSITIVE_INFINITY : Float.valueOf(property2).floatValue()))));
        DEFAULT_FLOAT_NO_ENTRY_VALUE = value3;
        if (VERBOSE) {
            System.out.println("DEFAULT_FLOAT_NO_ENTRY_VALUE: " + DEFAULT_FLOAT_NO_ENTRY_VALUE);
        }
        double value4 = "MAX_VALUE".equalsIgnoreCase(property = System.getProperty("gnu.trove.no_entry.double", "0")) ? Double.MAX_VALUE : ("MIN_VALUE".equalsIgnoreCase(property) ? Double.MIN_VALUE : ("MIN_NORMAL".equalsIgnoreCase(property) ? Double.MIN_NORMAL : ("NEGATIVE_INFINITY".equalsIgnoreCase(property) ? Double.NEGATIVE_INFINITY : ("POSITIVE_INFINITY".equalsIgnoreCase(property) ? Double.POSITIVE_INFINITY : Double.valueOf(property)))));
        DEFAULT_DOUBLE_NO_ENTRY_VALUE = value4;
        if (VERBOSE) {
            System.out.println("DEFAULT_DOUBLE_NO_ENTRY_VALUE: " + DEFAULT_DOUBLE_NO_ENTRY_VALUE);
        }
    }
}

