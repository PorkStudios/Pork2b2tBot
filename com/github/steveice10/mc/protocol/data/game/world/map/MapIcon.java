/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.data.game.world.map;

import com.github.steveice10.mc.protocol.data.game.world.map.MapIconType;
import com.github.steveice10.mc.protocol.util.ObjectUtil;

public class MapIcon {
    public int centerX;
    public int centerZ;
    public MapIconType iconType;
    public int iconRotation;

    public MapIcon(int centerX, int centerZ, MapIconType iconType, int iconRotation) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.iconType = iconType;
        this.iconRotation = iconRotation;
    }

    public int getCenterX() {
        return this.centerX;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    public MapIconType getIconType() {
        return this.iconType;
    }

    public int getIconRotation() {
        return this.iconRotation;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapIcon)) {
            return false;
        }
        MapIcon that = (MapIcon)o;
        return this.centerX == that.centerX && this.centerZ == that.centerZ && this.iconType == that.iconType && this.iconRotation == that.iconRotation;
    }

    public int hashCode() {
        return ObjectUtil.hashCode(new Object[]{this.centerX, this.centerZ, this.iconType, this.iconRotation});
    }

    public String toString() {
        return ObjectUtil.toString(this);
    }
}

