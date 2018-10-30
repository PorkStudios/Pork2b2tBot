/*
 * Decompiled with CFR 0_132.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public enum BoundType {
    OPEN(false),
    CLOSED(true);
    
    final boolean inclusive;

    private BoundType(boolean inclusive) {
        this.inclusive = inclusive;
    }

    static BoundType forBoolean(boolean inclusive) {
        return inclusive ? CLOSED : OPEN;
    }

    BoundType flip() {
        return BoundType.forBoolean(!this.inclusive);
    }
}

