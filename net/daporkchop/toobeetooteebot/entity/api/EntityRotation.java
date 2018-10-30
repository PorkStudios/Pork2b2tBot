/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.entity.api;

import com.github.steveice10.mc.protocol.data.game.entity.attribute.Attribute;
import java.util.ArrayList;
import java.util.List;
import net.daporkchop.toobeetooteebot.entity.api.Entity;

public abstract class EntityRotation
extends Entity {
    public double motX;
    public double motY;
    public double motZ;
    public int leashedID;
    public boolean isLeashed;
    public List<Attribute> properties = new ArrayList<Attribute>();
}

