/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.game.statistic;

import com.github.steveice10.mc.protocol.data.game.statistic.Statistic;
import com.github.steveice10.mc.protocol.util.ObjectUtil;
import java.util.Objects;

public class DropItemStatistic
implements Statistic {
    public String id;

    public DropItemStatistic(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DropItemStatistic)) {
            return false;
        }
        DropItemStatistic that = (DropItemStatistic)o;
        return Objects.equals(this.id, that.id);
    }

    public int hashCode() {
        return ObjectUtil.hashCode(this.id);
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}

