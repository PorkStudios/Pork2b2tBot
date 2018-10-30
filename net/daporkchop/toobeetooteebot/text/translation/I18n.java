/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.text.translation;

import net.daporkchop.toobeetooteebot.text.translation.LanguageMap;

@Deprecated
public class I18n {
    private static final LanguageMap localizedName = LanguageMap.getInstance();
    private static final LanguageMap fallbackTranslator = new LanguageMap();

    @Deprecated
    public static String translateToLocal(String key) {
        return localizedName.translateKey(key);
    }

    @Deprecated
    public static /* varargs */ String translateToLocalFormatted(String key, Object ... format) {
        return localizedName.translateKeyFormat(key, format);
    }

    @Deprecated
    public static String translateToFallback(String key) {
        return fallbackTranslator.translateKey(key);
    }

    @Deprecated
    public static boolean canTranslate(String key) {
        return localizedName.isKeyTranslated(key);
    }

    public static long getLastTranslationUpdateTimeInMilliseconds() {
        return localizedName.getLastUpdateTimeInMilliseconds();
    }
}

