/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.logging.FormattingTuple;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

final class MessageFormatter {
    private static final String DELIM_STR = "{}";
    private static final char ESCAPE_CHAR = '\\';

    static FormattingTuple format(String messagePattern, Object arg) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{arg});
    }

    static FormattingTuple format(String messagePattern, Object argA, Object argB) {
        return MessageFormatter.arrayFormat(messagePattern, new Object[]{argA, argB});
    }

    static FormattingTuple arrayFormat(String messagePattern, Object[] argArray) {
        Throwable throwable;
        if (argArray == null || argArray.length == 0) {
            return new FormattingTuple(messagePattern, null);
        }
        int lastArrIdx = argArray.length - 1;
        Object lastEntry = argArray[lastArrIdx];
        Throwable throwable2 = throwable = lastEntry instanceof Throwable ? (Throwable)lastEntry : null;
        if (messagePattern == null) {
            return new FormattingTuple(null, throwable);
        }
        int j = messagePattern.indexOf(DELIM_STR);
        if (j == -1) {
            return new FormattingTuple(messagePattern, throwable);
        }
        StringBuilder sbuf = new StringBuilder(messagePattern.length() + 50);
        int i = 0;
        int L = 0;
        do {
            boolean notEscaped;
            boolean bl = notEscaped = j == 0 || messagePattern.charAt(j - 1) != '\\';
            if (notEscaped) {
                sbuf.append(messagePattern, i, j);
            } else {
                sbuf.append(messagePattern, i, j - 1);
                notEscaped = j >= 2 && messagePattern.charAt(j - 2) == '\\';
            }
            i = j + 2;
            if (notEscaped) {
                MessageFormatter.deeplyAppendParameter(sbuf, argArray[L], null);
                if (++L <= lastArrIdx) continue;
                break;
            }
            sbuf.append(DELIM_STR);
        } while ((j = messagePattern.indexOf(DELIM_STR, i)) != -1);
        sbuf.append(messagePattern, i, messagePattern.length());
        return new FormattingTuple(sbuf.toString(), L <= lastArrIdx ? throwable : null);
    }

    private static void deeplyAppendParameter(StringBuilder sbuf, Object o, Set<Object[]> seenSet) {
        if (o == null) {
            sbuf.append("null");
            return;
        }
        Class<?> objClass = o.getClass();
        if (!objClass.isArray()) {
            if (Number.class.isAssignableFrom(objClass)) {
                if (objClass == Long.class) {
                    sbuf.append((Long)o);
                } else if (objClass == Integer.class || objClass == Short.class || objClass == Byte.class) {
                    sbuf.append(((Number)o).intValue());
                } else if (objClass == Double.class) {
                    sbuf.append((Double)o);
                } else if (objClass == Float.class) {
                    sbuf.append(((Float)o).floatValue());
                } else {
                    MessageFormatter.safeObjectAppend(sbuf, o);
                }
            } else {
                MessageFormatter.safeObjectAppend(sbuf, o);
            }
        } else {
            sbuf.append('[');
            if (objClass == boolean[].class) {
                MessageFormatter.booleanArrayAppend(sbuf, (boolean[])o);
            } else if (objClass == byte[].class) {
                MessageFormatter.byteArrayAppend(sbuf, (byte[])o);
            } else if (objClass == char[].class) {
                MessageFormatter.charArrayAppend(sbuf, (char[])o);
            } else if (objClass == short[].class) {
                MessageFormatter.shortArrayAppend(sbuf, (short[])o);
            } else if (objClass == int[].class) {
                MessageFormatter.intArrayAppend(sbuf, (int[])o);
            } else if (objClass == long[].class) {
                MessageFormatter.longArrayAppend(sbuf, (long[])o);
            } else if (objClass == float[].class) {
                MessageFormatter.floatArrayAppend(sbuf, (float[])o);
            } else if (objClass == double[].class) {
                MessageFormatter.doubleArrayAppend(sbuf, (double[])o);
            } else {
                MessageFormatter.objectArrayAppend(sbuf, (Object[])o, seenSet);
            }
            sbuf.append(']');
        }
    }

    private static void safeObjectAppend(StringBuilder sbuf, Object o) {
        try {
            String oAsString = o.toString();
            sbuf.append(oAsString);
        }
        catch (Throwable t) {
            System.err.println("SLF4J: Failed toString() invocation on an object of type [" + o.getClass().getName() + ']');
            t.printStackTrace();
            sbuf.append("[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuilder sbuf, Object[] a, Set<Object[]> seenSet) {
        if (a.length == 0) {
            return;
        }
        if (seenSet == null) {
            seenSet = new HashSet<Object[]>(a.length);
        }
        if (seenSet.add(a)) {
            MessageFormatter.deeplyAppendParameter(sbuf, a[0], seenSet);
            for (int i = 1; i < a.length; ++i) {
                sbuf.append(", ");
                MessageFormatter.deeplyAppendParameter(sbuf, a[i], seenSet);
            }
            seenSet.remove(a);
        } else {
            sbuf.append("...");
        }
    }

    private static void booleanArrayAppend(StringBuilder sbuf, boolean[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void byteArrayAppend(StringBuilder sbuf, byte[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void charArrayAppend(StringBuilder sbuf, char[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void shortArrayAppend(StringBuilder sbuf, short[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void intArrayAppend(StringBuilder sbuf, int[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void longArrayAppend(StringBuilder sbuf, long[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void floatArrayAppend(StringBuilder sbuf, float[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private static void doubleArrayAppend(StringBuilder sbuf, double[] a) {
        if (a.length == 0) {
            return;
        }
        sbuf.append(a[0]);
        for (int i = 1; i < a.length; ++i) {
            sbuf.append(", ");
            sbuf.append(a[i]);
        }
    }

    private MessageFormatter() {
    }
}

