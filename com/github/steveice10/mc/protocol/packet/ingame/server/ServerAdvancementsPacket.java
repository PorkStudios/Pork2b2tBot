/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.mc.protocol.packet.ingame.server;

import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.advancement.Advancement;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.MinecraftPacket;
import com.github.steveice10.mc.protocol.util.NetUtil;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ServerAdvancementsPacket
extends MinecraftPacket {
    public boolean reset;
    public List<Advancement> advancements;
    public List<String> removedAdvancements;
    public Map<String, Map<String, Long>> progress;

    public ServerAdvancementsPacket() {
    }

    public ServerAdvancementsPacket(boolean reset, List<Advancement> advancements, List<String> removedAdvancements, Map<String, Map<String, Long>> progress) {
        this.reset = reset;
        this.advancements = advancements;
        this.removedAdvancements = removedAdvancements;
        this.progress = progress;
    }

    public boolean doesReset() {
        return this.reset;
    }

    public List<Advancement> getAdvancements() {
        return this.advancements;
    }

    public List<String> getRemovedAdvancements() {
        return this.removedAdvancements;
    }

    public Map<String, Map<String, Long>> getProgress() {
        return this.progress;
    }

    public Map<String, Long> getProgress(String advancementId) {
        return this.getProgress().get(advancementId);
    }

    public Long getAchievedDate(String advancementId, String criterionId) {
        return this.getProgress(advancementId).get(criterionId);
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.reset = in.readBoolean();
        this.advancements = new ArrayList<Advancement>();
        int advancementCount = in.readVarInt();
        for (int i = 0; i < advancementCount; ++i) {
            String id = in.readString();
            String parentId = in.readBoolean() ? in.readString() : null;
            Advancement.DisplayData displayData = null;
            if (in.readBoolean()) {
                Message title = Message.fromString(in.readString());
                Message description = Message.fromString(in.readString());
                ItemStack icon = NetUtil.readItem(in);
                Advancement.DisplayData.FrameType frameType = MagicValues.key(Advancement.DisplayData.FrameType.class, in.readVarInt());
                int flags = in.readInt();
                boolean hasBackgroundTexture = (flags & 1) != 0;
                boolean showToast = (flags & 2) != 0;
                boolean hidden = (flags & 4) != 0;
                String backgroundTexture = hasBackgroundTexture ? in.readString() : null;
                float posX = in.readFloat();
                float posY = in.readFloat();
                displayData = new Advancement.DisplayData(title, description, icon, frameType, showToast, hidden, posX, posY, backgroundTexture);
            }
            ArrayList<String> criteria = new ArrayList<String>();
            int criteriaCount = in.readVarInt();
            for (int j = 0; j < criteriaCount; ++j) {
                criteria.add(in.readString());
            }
            ArrayList<List<String>> requirements = new ArrayList<List<String>>();
            int requirementCount = in.readVarInt();
            for (int j = 0; j < requirementCount; ++j) {
                ArrayList<String> requirement = new ArrayList<String>();
                int componentCount = in.readVarInt();
                for (int k = 0; k < componentCount; ++k) {
                    requirement.add(in.readString());
                }
                requirements.add(requirement);
            }
            this.advancements.add(new Advancement(id, parentId, criteria, requirements, displayData));
        }
        this.removedAdvancements = new ArrayList<String>();
        int removedCount = in.readVarInt();
        for (int i = 0; i < removedCount; ++i) {
            this.removedAdvancements.add(in.readString());
        }
        this.progress = new HashMap<String, Map<String, Long>>();
        int progressCount = in.readVarInt();
        for (int i = 0; i < progressCount; ++i) {
            String advancementId = in.readString();
            HashMap<String, Long> advancementProgress = new HashMap<String, Long>();
            int criterionCount = in.readVarInt();
            for (int j = 0; j < criterionCount; ++j) {
                String criterionId = in.readString();
                Long achievedDate = in.readBoolean() ? Long.valueOf(in.readLong()) : null;
                advancementProgress.put(criterionId, achievedDate);
            }
            this.progress.put(advancementId, advancementProgress);
        }
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeBoolean(this.reset);
        out.writeVarInt(this.advancements.size());
        for (Advancement advancement : this.advancements) {
            out.writeString(advancement.getId());
            if (advancement.getParentId() != null) {
                out.writeBoolean(true);
                out.writeString(advancement.getParentId());
            } else {
                out.writeBoolean(false);
            }
            Advancement.DisplayData displayData = advancement.getDisplayData();
            if (displayData != null) {
                out.writeBoolean(true);
                out.writeString(displayData.getTitle().toJsonString());
                out.writeString(displayData.getDescription().toJsonString());
                NetUtil.writeItem(out, displayData.getIcon());
                out.writeVarInt(MagicValues.value(Integer.class, (Object)displayData.getFrameType()));
                String backgroundTexture = displayData.getBackgroundTexture();
                int flags = 0;
                if (backgroundTexture != null) {
                    flags |= true;
                }
                if (displayData.doesShowToast()) {
                    flags |= 2;
                }
                if (displayData.isHidden()) {
                    flags |= 4;
                }
                out.writeInt(flags);
                if (backgroundTexture != null) {
                    out.writeString(backgroundTexture);
                }
                out.writeFloat(displayData.getPosX());
                out.writeFloat(displayData.getPosY());
            } else {
                out.writeBoolean(false);
            }
            out.writeVarInt(advancement.getCriteria().size());
            for (String criterion : advancement.getCriteria()) {
                out.writeString(criterion);
            }
            out.writeVarInt(advancement.getRequirements().size());
            for (List requirement : advancement.getRequirements()) {
                out.writeVarInt(requirement.size());
                for (String criterion : requirement) {
                    out.writeString(criterion);
                }
            }
        }
        out.writeVarInt(this.removedAdvancements.size());
        for (String id : this.removedAdvancements) {
            out.writeString(id);
        }
        out.writeVarInt(this.progress.size());
        for (Map.Entry advancement : this.progress.entrySet()) {
            out.writeString((String)advancement.getKey());
            Map advancementProgress = (Map)advancement.getValue();
            out.writeVarInt(advancementProgress.size());
            for (Map.Entry criterion : advancementProgress.entrySet()) {
                out.writeString((String)criterion.getKey());
                if (criterion.getValue() != null) {
                    out.writeBoolean(true);
                    out.writeLong((Long)criterion.getValue());
                    continue;
                }
                out.writeBoolean(false);
            }
        }
    }
}

