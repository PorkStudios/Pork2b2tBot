/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.text;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.daporkchop.toobeetooteebot.text.ITextComponent;
import net.daporkchop.toobeetooteebot.text.Style;
import net.daporkchop.toobeetooteebot.text.TextComponentBase;

public class TextComponentKeybind
extends TextComponentBase {
    public static Function<String, Supplier<String>> displaySupplierFunction = p_193635_0_ -> () -> p_193635_0_;
    private final String keybind;
    private Supplier<String> displaySupplier;

    public TextComponentKeybind(String keybind) {
        this.keybind = keybind;
    }

    @Override
    public String getUnformattedComponentText() {
        if (this.displaySupplier == null) {
            this.displaySupplier = displaySupplierFunction.apply(this.keybind);
        }
        return this.displaySupplier.get();
    }

    @Override
    public TextComponentKeybind createCopy() {
        TextComponentKeybind textcomponentkeybind = new TextComponentKeybind(this.keybind);
        textcomponentkeybind.setStyle(this.getStyle().createShallowCopy());
        for (ITextComponent itextcomponent : this.getSiblings()) {
            textcomponentkeybind.appendSibling(itextcomponent.createCopy());
        }
        return textcomponentkeybind;
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof TextComponentKeybind)) {
            return false;
        }
        TextComponentKeybind textcomponentkeybind = (TextComponentKeybind)p_equals_1_;
        return this.keybind.equals(textcomponentkeybind.keybind) && super.equals(p_equals_1_);
    }

    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKeybind() {
        return this.keybind;
    }
}

