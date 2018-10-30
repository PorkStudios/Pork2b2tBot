/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.hash;

import gnu.crypto.hash.MD2;
import gnu.crypto.hash.MD4;
import gnu.crypto.hash.MD5;
import gnu.crypto.hash.Sha160;
import gnu.crypto.hash.Sha256;
import gnu.crypto.hash.Sha384;
import gnu.crypto.hash.Sha512;
import gnu.crypto.hash.Whirlpool;

public class Hash {
    public static byte[] whirlpool(byte[] in) {
        Whirlpool whirlpool = new Whirlpool();
        whirlpool.update(in, 0, in.length);
        return whirlpool.digest();
    }

    public static byte[] whirlpool(byte[] in, int offset, int length) {
        Whirlpool whirlpool = new Whirlpool();
        whirlpool.update(in, offset, length);
        return whirlpool.digest();
    }

    public static byte[] md2(byte[] in) {
        MD2 md2 = new MD2();
        md2.update(in, 0, in.length);
        return md2.digest();
    }

    public static byte[] md2(byte[] in, int offset, int length) {
        MD2 md2 = new MD2();
        md2.update(in, offset, length);
        return md2.digest();
    }

    public static byte[] md4(byte[] in) {
        MD4 md4 = new MD4();
        md4.update(in, 0, in.length);
        return md4.digest();
    }

    public static byte[] md4(byte[] in, int offset, int length) {
        MD4 md4 = new MD4();
        md4.update(in, offset, length);
        return md4.digest();
    }

    public static byte[] md5(byte[] in) {
        MD5 md5 = new MD5();
        md5.update(in, 0, in.length);
        return md5.digest();
    }

    public static byte[] md5(byte[] in, int offset, int length) {
        MD5 md5 = new MD5();
        md5.update(in, offset, length);
        return md5.digest();
    }

    public static byte[] sha160(byte[] in) {
        Sha160 sha160 = new Sha160();
        sha160.update(in, 0, in.length);
        return sha160.digest();
    }

    public static byte[] sha160(byte[] in, int offset, int length) {
        Sha160 sha160 = new Sha160();
        sha160.update(in, offset, length);
        return sha160.digest();
    }

    public static byte[] sha256(byte[] in) {
        Sha256 sha256 = new Sha256();
        sha256.update(in, 0, in.length);
        return sha256.digest();
    }

    public static byte[] sha256(byte[] in, int offset, int length) {
        Sha256 sha256 = new Sha256();
        sha256.update(in, offset, length);
        return sha256.digest();
    }

    public static byte[] sha384(byte[] in) {
        Sha384 sha384 = new Sha384();
        sha384.update(in, 0, in.length);
        return sha384.digest();
    }

    public static byte[] sha384(byte[] in, int offset, int length) {
        Sha384 sha384 = new Sha384();
        sha384.update(in, offset, length);
        return sha384.digest();
    }

    public static byte[] sha512(byte[] in) {
        Sha512 sha512 = new Sha512();
        sha512.update(in, 0, in.length);
        return sha512.digest();
    }

    public static byte[] sha512(byte[] in, int offset, int length) {
        Sha512 sha512 = new Sha512();
        sha512.update(in, offset, length);
        return sha512.digest();
    }
}

