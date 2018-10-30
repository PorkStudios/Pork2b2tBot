/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.BaseHash;
import gnu.crypto.hash.Haval;
import gnu.crypto.hash.IMessageDigest;
import gnu.crypto.hash.MD2;
import gnu.crypto.hash.MD4;
import gnu.crypto.hash.MD5;
import gnu.crypto.hash.RipeMD128;
import gnu.crypto.hash.RipeMD160;
import gnu.crypto.hash.Sha160;
import gnu.crypto.hash.Sha256;
import gnu.crypto.hash.Sha384;
import gnu.crypto.hash.Sha512;
import gnu.crypto.hash.Tiger;
import gnu.crypto.hash.Whirlpool;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HashFactory {
    public static IMessageDigest getInstance(String name) {
        if (name == null) {
            return null;
        }
        name = name.trim();
        BaseHash result = null;
        if (name.equalsIgnoreCase("whirlpool")) {
            result = new Whirlpool();
        } else if (name.equalsIgnoreCase("ripemd128") || name.equalsIgnoreCase("ripemd-128")) {
            result = new RipeMD128();
        } else if (name.equalsIgnoreCase("ripemd160") || name.equalsIgnoreCase("ripemd-160")) {
            result = new RipeMD160();
        } else if (name.equalsIgnoreCase("sha-160") || name.equalsIgnoreCase("sha-1") || name.equalsIgnoreCase("sha1") || name.equalsIgnoreCase("sha")) {
            result = new Sha160();
        } else if (name.equalsIgnoreCase("sha-256")) {
            result = new Sha256();
        } else if (name.equalsIgnoreCase("sha-384")) {
            result = new Sha384();
        } else if (name.equalsIgnoreCase("sha-512")) {
            result = new Sha512();
        } else if (name.equalsIgnoreCase("tiger")) {
            result = new Tiger();
        } else if (name.equalsIgnoreCase("haval")) {
            result = new Haval();
        } else if (name.equalsIgnoreCase("md5")) {
            result = new MD5();
        } else if (name.equalsIgnoreCase("md4")) {
            result = new MD4();
        } else if (name.equalsIgnoreCase("md2")) {
            result = new MD2();
        } else if (name.equalsIgnoreCase("haval")) {
            result = new Haval();
        }
        if (result != null && !result.selfTest()) {
            throw new InternalError(result.name());
        }
        return result;
    }

    public static final Set getNames() {
        HashSet<String> hs = new HashSet<String>();
        hs.add("whirlpool");
        hs.add("ripemd128");
        hs.add("ripemd160");
        hs.add("sha-160");
        hs.add("sha-256");
        hs.add("sha-384");
        hs.add("sha-512");
        hs.add("tiger");
        hs.add("haval");
        hs.add("md5");
        hs.add("md4");
        hs.add("md2");
        return Collections.unmodifiableSet(hs);
    }

    private HashFactory() {
    }
}

