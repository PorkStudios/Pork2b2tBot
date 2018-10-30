/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TextFormat {
    BLACK('0', 0),
    DARK_BLUE('1', 1),
    DARK_GREEN('2', 2),
    DARK_AQUA('3', 3),
    DARK_RED('4', 4),
    DARK_PURPLE('5', 5),
    GOLD('6', 6),
    GRAY('7', 7),
    DARK_GRAY('8', 8),
    BLUE('9', 9),
    GREEN('a', 10),
    AQUA('b', 11),
    RED('c', 12),
    LIGHT_PURPLE('d', 13),
    YELLOW('e', 14),
    WHITE('f', 15),
    OBFUSCATED('k', 16, true),
    BOLD('l', 17, true),
    STRIKETHROUGH('m', 18, true),
    UNDERLINE('n', 19, true),
    ITALIC('o', 20, true),
    RESET('r', 21);
    
    public static final char ESCAPE = '\u00a7';
    private static final Pattern CLEAN_PATTERN;
    private static final Map<Integer, TextFormat> BY_ID;
    private static final Map<Character, TextFormat> BY_CHAR;
    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;

    private TextFormat(char code, int intCode) {
        this(code, intCode, false);
    }

    private TextFormat(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{'\u00a7', code});
    }

    public static TextFormat getByChar(char code) {
        return BY_CHAR.get(Character.valueOf(code));
    }

    public static TextFormat getByChar(String code) {
        if (code == null || code.length() <= 1) {
            return null;
        }
        return BY_CHAR.get(Character.valueOf(code.charAt(0)));
    }

    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        return CLEAN_PATTERN.matcher(input).replaceAll("");
    }

    public static String colorize(char altFormatChar, String textToTranslate) {
        return TextFormat.colorize(altFormatChar, textToTranslate, false);
    }

    public static String colorize(char altFormatChar, String textToTranslate, boolean resetFormatting) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] != altFormatChar || "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) <= -1) continue;
            b[i] = 167;
            b[i + 1] = Character.toLowerCase(b[i + 1]);
        }
        String str = new String(b);
        if (resetFormatting) {
            Matcher match = Pattern.compile("\u00a7([0-9a-f])").matcher(str);
            int idx = 0;
            while (match.find()) {
                str = str.replace("\u00a7" + match.group(idx), "\u00a7r\u00a7" + match.group(idx));
                ++idx;
            }
        }
        return str;
    }

    public static String colorize(String textToTranslate) {
        return TextFormat.colorize('&', textToTranslate);
    }

    public static String getLastColors(String input) {
        String result = "";
        int length = input.length();
        for (int index = length - 1; index > -1; --index) {
            TextFormat color;
            char c;
            char section = input.charAt(index);
            if (section != '\u00a7' || index >= length - 1 || (color = TextFormat.getByChar(c = input.charAt(index + 1))) == null) continue;
            result = color.toString() + result;
            if (color.isColor() || color.equals((Object)RESET)) break;
        }
        return result;
    }

    public char getChar() {
        return this.code;
    }

    public String toString() {
        return this.toString;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    static {
        CLEAN_PATTERN = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");
        BY_ID = Maps.newTreeMap();
        BY_CHAR = new HashMap<Character, TextFormat>();
        for (TextFormat color : TextFormat.values()) {
            BY_ID.put(color.intCode, color);
            BY_CHAR.put(Character.valueOf(color.code), color);
        }
    }
}

