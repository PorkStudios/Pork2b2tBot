/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.api.chat;

import java.beans.ConstructorProperties;
import net.md_5.bungee.api.chat.BaseComponent;

public final class SelectorComponent
extends BaseComponent {
    private String selector;

    public SelectorComponent(SelectorComponent original) {
        super(original);
        this.setSelector(original.getSelector());
    }

    @Override
    public SelectorComponent duplicate() {
        return new SelectorComponent(this);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        builder.append(this.selector);
        super.toLegacyText(builder);
    }

    public String getSelector() {
        return this.selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        return "SelectorComponent(selector=" + this.getSelector() + ")";
    }

    @ConstructorProperties(value={"selector"})
    public SelectorComponent(String selector) {
        this.selector = selector;
    }
}

