/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.text.translation;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;

public class LanguageMap {
    private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);
    private static final LanguageMap instance = new LanguageMap();
    private final Map<String, String> languageList = Maps.newHashMap();
    private long lastUpdateTimeInMilliseconds;

    public LanguageMap() {
        InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.lang");
        LanguageMap.inject(this, inputstream);
    }

    public static void inject(InputStream inputstream) {
        LanguageMap.inject(instance, inputstream);
    }

    private static void inject(LanguageMap inst, InputStream inputstream) {
        Map<String, String> map = LanguageMap.parseLangFile(inputstream);
        inst.languageList.putAll(map);
        inst.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    public static Map<String, String> parseLangFile(InputStream inputstream) {
        HashMap<String, String> table = Maps.newHashMap();
        return table;
    }

    static LanguageMap getInstance() {
        return instance;
    }

    public static synchronized void replaceWith(Map<String, String> p_135063_0_) {
        LanguageMap.instance.languageList.clear();
        LanguageMap.instance.languageList.putAll(p_135063_0_);
        LanguageMap.instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    public synchronized String translateKey(String key) {
        return this.tryTranslateKey(key);
    }

    public synchronized /* varargs */ String translateKeyFormat(String key, Object ... format) {
        String s = this.tryTranslateKey(key);
        try {
            return String.format(s, format);
        }
        catch (IllegalFormatException var5) {
            return "Format error: " + s;
        }
    }

    private String tryTranslateKey(String key) {
        String s = this.languageList.get(key);
        return s == null ? key : s;
    }

    public synchronized boolean isKeyTranslated(String key) {
        return this.languageList.containsKey(key);
    }

    public long getLastUpdateTimeInMilliseconds() {
        return this.lastUpdateTimeInMilliseconds;
    }
}

