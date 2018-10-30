/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.text;

import java.util.List;
import net.daporkchop.toobeetooteebot.text.ITextComponent;
import net.daporkchop.toobeetooteebot.text.Style;
import net.daporkchop.toobeetooteebot.text.TextComponentBase;

public class TextComponentSelector
extends TextComponentBase {
    private final String selector;

    public TextComponentSelector(String selectorIn) {
        this.selector = selectorIn;
    }

    public String getSelector() {
        return this.selector;
    }

    @Override
    public String getUnformattedComponentText() {
        return this.selector;
    }

    @Override
    public TextComponentSelector createCopy() {
        TextComponentSelector textcomponentselector = new TextComponentSelector(this.selector);
        textcomponentselector.setStyle(this.getStyle().createShallowCopy());
        for (ITextComponent itextcomponent : this.getSiblings()) {
            textcomponentselector.appendSibling(itextcomponent.createCopy());
        }
        return textcomponentselector;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof TextComponentSelector)) {
            return false;
        }
        TextComponentSelector textcomponentselector = (TextComponentSelector)p_equals_1_;
        return this.selector.equals(textcomponentselector.selector) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "SelectorComponent{pattern='" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}

