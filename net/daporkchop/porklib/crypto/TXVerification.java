/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.crypto;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.daporkchop.porklib.math.Base58;

public class TXVerification {
    public static byte[] makeAddressPlain(byte[] pubKey, Function<byte[], byte[]> hashAlg, int significantHashPlaces) {
        byte[] hash = hashAlg.apply(pubKey);
        if (significantHashPlaces > hash.length) {
            throw new IllegalArgumentException("significantHashPlaces longer than actual hash!");
        }
        byte[] trimmedHash = new byte[significantHashPlaces];
        for (int i = 0; i < significantHashPlaces; ++i) {
            trimmedHash[i] = hash[i];
        }
        return trimmedHash;
    }

    public static String makeAddressWithSuffix(byte[] pubKey, Function<byte[], byte[]> hashAlg, int significantHashPlaces) {
        byte[] hash = hashAlg.apply(pubKey);
        if (significantHashPlaces > hash.length) {
            throw new IllegalArgumentException("significantHashPlaces longer than actual hash!");
        }
        byte[] trimmedHash = new byte[significantHashPlaces];
        for (int i = 0; i < significantHashPlaces; ++i) {
            trimmedHash[i] = hash[i];
        }
        return Base58.encode((byte)0, "", trimmedHash);
    }

    public static boolean verifySignature(byte[] signature, BiFunction<byte[], byte[], Boolean> sigVerifAlg, String senderAddress, byte[] pubKey, Function<byte[], byte[]> hashAlg, int significantHashPlaces) {
        if (Base58.hasPrefix(senderAddress)) {
            return false;
        }
        String pubkeyAddressified = TXVerification.makeAddressWithSuffix(pubKey, hashAlg, significantHashPlaces);
        if (!pubkeyAddressified.equals(senderAddress)) {
            return false;
        }
        return sigVerifAlg.apply(signature, pubKey);
    }
}

