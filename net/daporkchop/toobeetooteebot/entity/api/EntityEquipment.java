/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.entity.api;

import com.github.steveice10.mc.protocol.data.game.entity.EquipmentSlot;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import net.daporkchop.toobeetooteebot.entity.PotionEffect;
import net.daporkchop.toobeetooteebot.entity.api.EntityRotation;

public abstract class EntityEquipment
extends EntityRotation {
    public ArrayList<PotionEffect> potionEffects = new ArrayList();
    public HashMap<EquipmentSlot, ItemStack> equipment = new HashMap();

    public EntityEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.equipment.put(slot, null);
        }
    }
}

