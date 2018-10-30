/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.math;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.daporkchop.porklib.hash.Hash;

public class Base58 {
    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
    private static final char ENCODED_ZERO = ALPHABET[0];
    private static final int[] INDEXES = new int[128];

    public static String encode(byte version, String prefix, byte[] content) {
        int i;
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.length() > 57) {
            throw new IllegalArgumentException("Prefix too long! Should be max. 57 letters");
        }
        byte[] newData = new byte[1 + content.length + 4];
        newData[0] = version;
        for (i = 0; i < content.length; ++i) {
            newData[i + 1] = content[i];
        }
        for (i = newData.length - 4; i < newData.length; ++i) {
            newData[i] = version;
        }
        byte[] hash = Hash.whirlpool(newData);
        for (int i2 = 0; i2 < 4; ++i2) {
            newData[i2 + (newData.length - 4)] = hash[i2];
        }
        return "" + ALPHABET[prefix.length()] + prefix + Base58.encodeRaw(newData);
    }

    public static String encodeRaw(byte[] input) {
        int zeros;
        if (input.length == 0) {
            return "";
        }
        for (zeros = 0; zeros < input.length && input[zeros] == 0; ++zeros) {
        }
        input = Arrays.copyOf(input, input.length);
        char[] encoded = new char[input.length * 2];
        int outputStart = encoded.length;
        int inputStart = zeros;
        while (inputStart < input.length) {
            encoded[--outputStart] = ALPHABET[Base58.divmod(input, inputStart, 256, 58)];
            if (input[inputStart] != 0) continue;
            ++inputStart;
        }
        while (outputStart < encoded.length && encoded[outputStart] == ENCODED_ZERO) {
            ++outputStart;
        }
        while (--zeros >= 0) {
            encoded[--outputStart] = ENCODED_ZERO;
        }
        return new String(encoded, outputStart, encoded.length - outputStart);
    }

    public static DecodedData decode(String pork58) {
        int i;
        List<Character> chars = pork58.chars().mapToObj(e -> Character.valueOf((char)e)).collect(Collectors.toList());
        char prefixLengthChar = ((Character)chars.remove(0)).charValue();
        int prefixLength = -1;
        for (int i2 = 0; i2 < ALPHABET.length; ++i2) {
            if (ALPHABET[i2] != prefixLengthChar) continue;
            prefixLength = i2;
        }
        if (prefixLength == -1) {
            return null;
        }
        StringBuilder prefix = new StringBuilder(prefixLength);
        for (int i3 = 0; i3 < prefixLength; ++i3) {
            prefix.append(((Character)chars.remove(0)).charValue());
        }
        StringBuilder remainingData = new StringBuilder(chars.size());
        chars.forEach(character -> remainingData.append(character.charValue()));
        byte[] rawData = Base58.decodeRaw(remainingData.toString());
        byte version = rawData[0];
        byte[] data = new byte[rawData.length - 5];
        byte[] hash = new byte[4];
        for (i = 1; i < rawData.length - 4; ++i) {
            data[i - 1] = rawData[i];
        }
        for (i = rawData.length - 4; i < rawData.length; ++i) {
            hash[i - (rawData.length - 4)] = rawData[i];
            rawData[i] = version;
        }
        byte[] newHash = Hash.whirlpool(rawData);
        for (int i4 = 0; i4 < 4; ++i4) {
            if (hash[i4] == newHash[i4]) continue;
            throw new IllegalArgumentException("Invalid checksum!");
        }
        return new DecodedData(data, prefix.toString(), version);
    }

    public static String trimPrefix(String pork58) {
        char prefixLengthChar = pork58.charAt(0);
        int prefixLength = -1;
        for (int i = 0; i < ALPHABET.length; ++i) {
            if (ALPHABET[i] != prefixLengthChar) continue;
            prefixLength = i;
        }
        if (prefixLength == -1) {
            return null;
        }
        return "0" + pork58.substring(prefixLength + 1);
    }

    public static boolean hasPrefix(String pork58) {
        return pork58.charAt(0) != '0';
    }

    public static byte[] decodeRaw(String input) throws IllegalArgumentException {
        int zeros;
        if (input.length() == 0) {
            return new byte[0];
        }
        byte[] input58 = new byte[input.length()];
        for (int i = 0; i < input.length(); ++i) {
            int digit;
            char c = input.charAt(i);
            int n = digit = c < '' ? INDEXES[c] : -1;
            if (digit < 0) {
                throw new IllegalArgumentException("Illegal character " + c + " at position " + i);
            }
            input58[i] = (byte)digit;
        }
        for (zeros = 0; zeros < input58.length && input58[zeros] == 0; ++zeros) {
        }
        byte[] decoded = new byte[input.length()];
        int outputStart = decoded.length;
        int inputStart = zeros;
        while (inputStart < input58.length) {
            decoded[--outputStart] = Base58.divmod(input58, inputStart, 58, 256);
            if (input58[inputStart] != 0) continue;
            ++inputStart;
        }
        while (outputStart < decoded.length && decoded[outputStart] == 0) {
            ++outputStart;
        }
        return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
    }

    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        int remainder = 0;
        for (int i = firstDigit; i < number.length; ++i) {
            int digit = number[i] & 255;
            int temp = remainder * base + digit;
            number[i] = (byte)(temp / divisor);
            remainder = temp % divisor;
        }
        return (byte)remainder;
    }

    static {
        Arrays.fill(INDEXES, -1);
        int i = 0;
        while (i < ALPHABET.length) {
            Base58.INDEXES[Base58.ALPHABET[i]] = i++;
        }
    }

    public static final class DecodedData {
        public final byte[] content;
        public final String prefix;
        public final byte version;

        public DecodedData(byte[] content, String prefix, byte version) {
            this.content = content;
            this.prefix = prefix;
            this.version = version;
        }
    }

}

