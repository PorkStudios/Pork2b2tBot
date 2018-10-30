/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.entity.api;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collection;
import java.util.UUID;
import net.daporkchop.toobeetooteebot.Caches;
import net.daporkchop.toobeetooteebot.entity.EntityType;
import net.daporkchop.toobeetooteebot.entity.api.EntityEquipment;

public abstract class Entity {
    public float yaw;
    public float pitch;
    public float headYaw;
    public EntityType type;
    public double x;
    public double y;
    public double z;
    public int entityId;
    public UUID uuid;
    public EntityMetadata[] metadata = new EntityMetadata[0];
    public int[] passengerIds = new int[0];

    public static Entity getEntityByID(int id) {
        for (Entity entity : Caches.cachedEntities.values()) {
            if (entity.entityId != id) continue;
            return entity;
        }
        return null;
    }

    public static Entity getEntityBeingRiddenBy(int entityId) {
        for (Entity entity : Caches.cachedEntities.values()) {
            for (int pID : ((EntityEquipment)entity).passengerIds) {
                if (pID != entityId) continue;
                return entity;
            }
        }
        return null;
    }
}

