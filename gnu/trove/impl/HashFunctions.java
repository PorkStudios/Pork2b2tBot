/*
 * Decompiled with CFR 0_132.
 */
package gnu.trove.impl;

public final class HashFunctions {
    public static int hash(double value) {
        assert (!Double.isNaN(value));
        long bits = Double.doubleToLongBits(value);
        return (int)(bits ^ bits >>> 32);
    }

    public static int hash(float value) {
        assert (!Float.isNaN(value));
        return Float.floatToIntBits(value * 6.6360896E8f);
    }

    public static int hash(int value) {
        return value;
    }

    public static int hash(long value) {
        return (int)(value ^ value >>> 32);
    }

    public static int hash(Object object) {
        return object == null ? 0 : object.hashCode();
    }

    public static int fastCeil(float v) {
        int possible_result = (int)v;
        if (v - (float)possible_result > 0.0f) {
            ++possible_result;
        }
        return possible_result;
    }
}

