/*
 * Decompiled with CFR 0_132.
 */
package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public final class TranslatableComponent
extends BaseComponent {
    private final ResourceBundle locales = ResourceBundle.getBundle("mojang-translations/en_US");
    private final Pattern format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private String translate;
    private List<BaseComponent> with;

    public TranslatableComponent(TranslatableComponent original) {
        super(original);
        this.setTranslate(original.getTranslate());
        if (original.getWith() != null) {
            ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
            for (BaseComponent baseComponent : original.getWith()) {
                temp.add(baseComponent.duplicate());
            }
            this.setWith(temp);
        }
    }

    public /* varargs */ TranslatableComponent(String translate, Object ... with) {
        this.setTranslate(translate);
        if (with != null && with.length != 0) {
            ArrayList<BaseComponent> temp = new ArrayList<BaseComponent>();
            for (Object w : with) {
                if (w instanceof String) {
                    temp.add(new TextComponent((String)w));
                    continue;
                }
                temp.add((BaseComponent)w);
            }
            this.setWith(temp);
        }
    }

    @Override
    public BaseComponent duplicate() {
        return new TranslatableComponent(this);
    }

    public void setWith(List<BaseComponent> components) {
        for (BaseComponent component : components) {
            component.parent = this;
        }
        this.with = components;
    }

    public void addWith(String text) {
        this.addWith(new TextComponent(text));
    }

    public void addWith(BaseComponent component) {
        if (this.with == null) {
            this.with = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.with.add(component);
    }

    @Override
    protected void toPlainText(StringBuilder builder) {
        String trans;
        try {
            trans = this.locales.getString(this.translate);
        }
        catch (MissingResourceException ex) {
            trans = this.translate;
        }
        Matcher matcher = this.format.matcher(trans);
        int position = 0;
        int i = 0;
        while (matcher.find(position)) {
            int pos = matcher.start();
            if (pos != position) {
                builder.append(trans.substring(position, pos));
            }
            position = matcher.end();
            String formatCode = matcher.group(2);
            switch (formatCode.charAt(0)) {
                case 'd': 
                case 's': {
                    String withIndex = matcher.group(1);
                    this.with.get(withIndex != null ? Integer.parseInt(withIndex) - 1 : i++).toPlainText(builder);
                    break;
                }
                case '%': {
                    builder.append('%');
                }
            }
        }
        if (trans.length() != position) {
            builder.append(trans.substring(position, trans.length()));
        }
        super.toPlainText(builder);
    }

    @Override
    protected void toLegacyText(StringBuilder builder) {
        String trans;
        try {
            trans = this.locales.getString(this.translate);
        }
        catch (MissingResourceException e) {
            trans = this.translate;
        }
        Matcher matcher = this.format.matcher(trans);
        int position = 0;
        int i = 0;
        while (matcher.find(position)) {
            int pos = matcher.start();
            if (pos != position) {
                this.addFormat(builder);
                builder.append(trans.substring(position, pos));
            }
            position = matcher.end();
            String formatCode = matcher.group(2);
            switch (formatCode.charAt(0)) {
                case 'd': 
                case 's': {
                    String withIndex = matcher.group(1);
                    this.with.get(withIndex != null ? Integer.parseInt(withIndex) - 1 : i++).toLegacyText(builder);
                    break;
                }
                case '%': {
                    this.addFormat(builder);
                    builder.append('%');
                }
            }
        }
        if (trans.length() != position) {
            this.addFormat(builder);
            builder.append(trans.substring(position, trans.length()));
        }
        super.toLegacyText(builder);
    }

    private void addFormat(StringBuilder builder) {
        builder.append((Object)this.getColor());
        if (this.isBold()) {
            builder.append((Object)ChatColor.BOLD);
        }
        if (this.isItalic()) {
            builder.append((Object)ChatColor.ITALIC);
        }
        if (this.isUnderlined()) {
            builder.append((Object)ChatColor.UNDERLINE);
        }
        if (this.isStrikethrough()) {
            builder.append((Object)ChatColor.STRIKETHROUGH);
        }
        if (this.isObfuscated()) {
            builder.append((Object)ChatColor.MAGIC);
        }
    }

    public ResourceBundle getLocales() {
        return this.locales;
    }

    public Pattern getFormat() {
        return this.format;
    }

    public String getTranslate() {
        return this.translate;
    }

    public List<BaseComponent> getWith() {
        return this.with;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    @Override
    public String toString() {
        return "TranslatableComponent(locales=" + this.getLocales() + ", format=" + this.getFormat() + ", translate=" + this.getTranslate() + ", with=" + this.getWith() + ")";
    }

    public TranslatableComponent() {
    }
}

