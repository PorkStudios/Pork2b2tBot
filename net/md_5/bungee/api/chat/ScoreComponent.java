/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.api.chat;

import java.beans.ConstructorProperties;
import net.md_5.bungee.api.chat.BaseComponent;

public final class ScoreComponent
extends BaseComponent {
    private String name;
    private String objective;
    private String value = "";

    public ScoreComponent(String name, String objective) {
        this.setName(name);
        this.setObjective(objective);
    }

    public ScoreComponent(ScoreComponent original) {
        super(original);
        this.setName(original.getName());
        this.setObjective(original.getObjective());
        this.setValue(original.getValue());
    }

    @Override
    public ScoreComponent duplicate() {
        return new ScoreComponent(this);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append(this.value);
        super.toLegacyText(builder);
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    public String getValue() {
        return this.value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScoreComponent(name=" + this.getName() + ", objective=" + this.getObjective() + ", value=" + this.getValue() + ")";
    }

    @ConstructorProperties(value={"name", "objective", "value"})
    public ScoreComponent(String name, String objective, String value) {
        this.name = name;
        this.objective = objective;
        this.value = value;
    }
}

