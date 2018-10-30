/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.assembly;

public final class Direction {
    public static final Direction FORWARD = new Direction(1);
    public static final Direction REVERSED = new Direction(2);
    private int value;

    public static final Direction reverse(Direction d) {
        return d.equals(FORWARD) ? REVERSED : FORWARD;
    }

    public final String toString() {
        return this == FORWARD ? "forward" : "reversed";
    }

    private Direction(int value) {
        this.value = value;
    }
}

