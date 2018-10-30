/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.hash.HashFactory;
import gnu.crypto.jce.GnuCrypto;
import gnu.crypto.key.KeyPairGeneratorFactory;
import gnu.crypto.mac.MacFactory;
import gnu.crypto.sasl.ClientFactory;
import gnu.crypto.sasl.ServerFactory;
import gnu.crypto.sig.SignatureFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class GnuCrypto
extends Provider {
    public static final Set getMessageDigestNames() {
        return HashFactory.getNames();
    }

    public static final Set getSecureRandomNames() {
        HashSet<String> result = new HashSet<String>();
        Set md = HashFactory.getNames();
        Iterator it = md.iterator();
        while (it.hasNext()) {
            result.add(((String)it.next()).toUpperCase() + "PRNG");
        }
        result.add("icm".toUpperCase());
        result.add("umac-kdf".toUpperCase());
        result.add("arcfour".toUpperCase());
        return Collections.unmodifiableSet(result);
    }

    public static final Set getKeyPairGeneratorNames() {
        return KeyPairGeneratorFactory.getNames();
    }

    public static final Set getSignatureNames() {
        return SignatureFactory.getNames();
    }

    public static final Set getCipherNames() {
        HashSet<String> s = new HashSet<String>();
        s.addAll(CipherFactory.getNames());
        s.add("arcfour");
        return s;
    }

    public static final Set getMacNames() {
        return MacFactory.getNames();
    }

    public static final Set getSaslClientMechanismNames() {
        return ClientFactory.getNames();
    }

    public static final Set getSaslServerMechanismNames() {
        return ServerFactory.getNames();
    }

    public GnuCrypto() {
        super("GNU-CRYPTO", 2, "GNU Crypto JCE Provider");
        AccessController.doPrivileged(new PrivilegedAction(this){
            final /* synthetic */ GnuCrypto this$0;

            public final Object run() {
                this.this$0.put("MessageDigest.HAVAL", "gnu.crypto.jce.hash.HavalSpi");
                this.this$0.put("MessageDigest.HAVAL ImplementedIn", "Software");
                this.this$0.put("MessageDigest.MD2", "gnu.crypto.jce.hash.MD2Spi");
                this.this$0.put("MessageDigest.MD2 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.MD4", "gnu.crypto.jce.hash.MD4Spi");
                this.this$0.put("MessageDigest.MD4 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.MD5", "gnu.crypto.jce.hash.MD5Spi");
                this.this$0.put("MessageDigest.MD5 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.RIPEMD128", "gnu.crypto.jce.hash.RipeMD128Spi");
                this.this$0.put("MessageDigest.RIPEMD128 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.RIPEMD160", "gnu.crypto.jce.hash.RipeMD160Spi");
                this.this$0.put("MessageDigest.RIPEMD160 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.SHA-160", "gnu.crypto.jce.hash.Sha160Spi");
                this.this$0.put("MessageDigest.SHA-160 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.SHA-256", "gnu.crypto.jce.hash.Sha256Spi");
                this.this$0.put("MessageDigest.SHA-256 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.SHA-384", "gnu.crypto.jce.hash.Sha384Spi");
                this.this$0.put("MessageDigest.SHA-384 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.SHA-512", "gnu.crypto.jce.hash.Sha512Spi");
                this.this$0.put("MessageDigest.SHA-512 ImplementedIn", "Software");
                this.this$0.put("MessageDigest.TIGER", "gnu.crypto.jce.hash.TigerSpi");
                this.this$0.put("MessageDigest.TIGER ImplementedIn", "Software");
                this.this$0.put("MessageDigest.WHIRLPOOL", "gnu.crypto.jce.hash.WhirlpoolSpi");
                this.this$0.put("MessageDigest.WHIRLPOOL ImplementedIn", "Software");
                this.this$0.put("SecureRandom.ARCFOUR", "gnu.crypto.jce.prng.ARCFourRandomSpi");
                this.this$0.put("SecureRandom.MD2PRNG", "gnu.crypto.jce.prng.MD2RandomSpi");
                this.this$0.put("SecureRandom.MD2PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.MD4PRNG", "gnu.crypto.jce.prng.MD4RandomSpi");
                this.this$0.put("SecureRandom.MD4PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.MD5PRNG", "gnu.crypto.jce.prng.MD5RandomSpi");
                this.this$0.put("SecureRandom.MD5PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.RIPEMD128PRNG", "gnu.crypto.jce.prng.RipeMD128RandomSpi");
                this.this$0.put("SecureRandom.RIPEMD128PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.RIPEMD160PRNG", "gnu.crypto.jce.prng.RipeMD160RandomSpi");
                this.this$0.put("SecureRandom.RIPEMD160PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.SHA-160PRNG", "gnu.crypto.jce.prng.Sha160RandomSpi");
                this.this$0.put("SecureRandom.SHA-160PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.SHA-256PRNG", "gnu.crypto.jce.prng.Sha256RandomSpi");
                this.this$0.put("SecureRandom.SHA-256PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.SHA-384PRNG", "gnu.crypto.jce.prng.Sha384RandomSpi");
                this.this$0.put("SecureRandom.SHA-384PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.SHA-512PRNG", "gnu.crypto.jce.prng.Sha512RandomSpi");
                this.this$0.put("SecureRandom.SHA-512PRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.TIGERPRNG", "gnu.crypto.jce.prng.TigerRandomSpi");
                this.this$0.put("SecureRandom.TIGERPRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.HAVALPRNG", "gnu.crypto.jce.prng.HavalRandomSpi");
                this.this$0.put("SecureRandom.HAVALPRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.WHIRLPOOLPRNG", "gnu.crypto.jce.prng.WhirlpoolRandomSpi");
                this.this$0.put("SecureRandom.WHIRLPOOLPRNG ImplementedIn", "Software");
                this.this$0.put("SecureRandom.ICM", "gnu.crypto.jce.prng.ICMRandomSpi");
                this.this$0.put("SecureRandom.ICM ImplementedIn", "Software");
                this.this$0.put("SecureRandom.UMAC-KDF", "gnu.crypto.jce.prng.UMacRandomSpi");
                this.this$0.put("SecureRandom.UMAC-KDF ImplementedIn", "Software");
                this.this$0.put("KeyPairGenerator.DSS", "gnu.crypto.jce.sig.DSSKeyPairGeneratorSpi");
                this.this$0.put("KeyPairGenerator.DSS KeySize", "1024");
                this.this$0.put("KeyPairGenerator.DSS ImplementedIn", "Software");
                this.this$0.put("KeyPairGenerator.RSA", "gnu.crypto.jce.sig.RSAKeyPairGeneratorSpi");
                this.this$0.put("KeyPairGenerator.RSA KeySize", "1024");
                this.this$0.put("KeyPairGenerator.RSA ImplementedIn", "Software");
                this.this$0.put("Signature.DSS/RAW", "gnu.crypto.jce.sig.DSSRawSignatureSpi");
                this.this$0.put("Signature.DSS/RAW KeySize", "1024");
                this.this$0.put("Signature.DSS/RAW ImplementedIn", "Software");
                this.this$0.put("Signature.RSA-PSS/RAW", "gnu.crypto.jce.sig.RSAPSSRawSignatureSpi");
                this.this$0.put("Signature.RSA-PSS/RAW KeySize", "1024");
                this.this$0.put("Signature.RSA-PSS/RAW ImplementedIn", "Software");
                this.this$0.put("Cipher.ANUBIS", "gnu.crypto.jce.cipher.AnubisSpi");
                this.this$0.put("Cipher.ANUBIS ImplementedIn", "Software");
                this.this$0.put("Cipher.ARCFOUR", "gnu.crypto.jce.cipher.ARCFourSpi");
                this.this$0.put("Cipher.ARCFOUR ImplementedIn", "Software");
                this.this$0.put("Cipher.BLOWFISH", "gnu.crypto.jce.cipher.BlowfishSpi");
                this.this$0.put("Cipher.BLOWFISH ImplementedIn", "Software");
                this.this$0.put("Cipher.DES", "gnu.crypto.jce.cipher.DESSpi");
                this.this$0.put("Cipher.DES ImplementedIn", "Software");
                this.this$0.put("Cipher.KHAZAD", "gnu.crypto.jce.cipher.KhazadSpi");
                this.this$0.put("Cipher.KHAZAD ImplementedIn", "Software");
                this.this$0.put("Cipher.NULL", "gnu.crypto.jce.cipher.NullCipherSpi");
                this.this$0.put("Cipher.NULL ImplementedIn", "Software");
                this.this$0.put("Cipher.AES", "gnu.crypto.jce.cipher.RijndaelSpi");
                this.this$0.put("Cipher.AES ImplementedIn", "Software");
                this.this$0.put("Cipher.RIJNDAEL", "gnu.crypto.jce.cipher.RijndaelSpi");
                this.this$0.put("Cipher.RIJNDAEL ImplementedIn", "Software");
                this.this$0.put("Cipher.SERPENT", "gnu.crypto.jce.cipher.SerpentSpi");
                this.this$0.put("Cipher.SERPENT ImplementedIn", "Software");
                this.this$0.put("Cipher.SQUARE", "gnu.crypto.jce.cipher.SquareSpi");
                this.this$0.put("Cipher.SQUARE ImplementedIn", "Software");
                this.this$0.put("Cipher.TRIPLEDES", "gnu.crypto.jce.cipher.TripleDESSpi");
                this.this$0.put("Cipher.TRIPLEDES ImplementedIn", "Software");
                this.this$0.put("Cipher.TWOFISH", "gnu.crypto.jce.cipher.TwofishSpi");
                this.this$0.put("Cipher.TWOFISH ImplementedIn", "Software");
                this.this$0.put("Cipher.CAST5", "gnu.crypto.jce.cipher.Cast5Spi");
                this.this$0.put("Cipher.CAST5 ImplementedIn", "Software");
                this.this$0.put("Cipher.PBEWithHMacHavalAndAES", "gnu.crypto.jce.cipher.PBES2$HMacHaval$AES");
                this.this$0.put("Cipher.PBEWithHMacHavalAndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Anubis");
                this.this$0.put("Cipher.PBEWithHMacHavalAndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacHavalAndCast5", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Cast5");
                this.this$0.put("Cipher.PBEWithHMacHavalAndDES", "gnu.crypto.jce.cipher.PBES2$HMacHaval$DES");
                this.this$0.put("Cipher.PBEWithHMacHavalAndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Khazad");
                this.this$0.put("Cipher.PBEWithHMacHavalAndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Serpent");
                this.this$0.put("Cipher.PBEWithHMacHavalAndSquare", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Square");
                this.this$0.put("Cipher.PBEWithHMacHavalAndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacHaval$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacHavalAndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacHaval$Twofish");
                this.this$0.put("Cipher.PBEWithHMacMD2AndAES", "gnu.crypto.jce.cipher.PBES2$HMacMD2$AES");
                this.this$0.put("Cipher.PBEWithHMacMD2AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Anubis");
                this.this$0.put("Cipher.PBEWithHMacMD2AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacMD2AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Cast5");
                this.this$0.put("Cipher.PBEWithHMacMD2AndDES", "gnu.crypto.jce.cipher.PBES2$HMacMD2$DES");
                this.this$0.put("Cipher.PBEWithHMacMD2AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Khazad");
                this.this$0.put("Cipher.PBEWithHMacMD2AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Serpent");
                this.this$0.put("Cipher.PBEWithHMacMD2AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Square");
                this.this$0.put("Cipher.PBEWithHMacMD2AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacMD2$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacMD2AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacMD2$Twofish");
                this.this$0.put("Cipher.PBEWithHMacMD4AndAES", "gnu.crypto.jce.cipher.PBES2$HMacMD4$AES");
                this.this$0.put("Cipher.PBEWithHMacMD4AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Anubis");
                this.this$0.put("Cipher.PBEWithHMacMD4AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacMD4AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Cast5");
                this.this$0.put("Cipher.PBEWithHMacMD4AndDES", "gnu.crypto.jce.cipher.PBES2$HMacMD4$DES");
                this.this$0.put("Cipher.PBEWithHMacMD4AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Khazad");
                this.this$0.put("Cipher.PBEWithHMacMD4AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Serpent");
                this.this$0.put("Cipher.PBEWithHMacMD4AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Square");
                this.this$0.put("Cipher.PBEWithHMacMD4AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacMD4$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacMD4AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacMD4$Twofish");
                this.this$0.put("Cipher.PBEWithHMacMD5AndAES", "gnu.crypto.jce.cipher.PBES2$HMacMD5$AES");
                this.this$0.put("Cipher.PBEWithHMacMD5AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Anubis");
                this.this$0.put("Cipher.PBEWithHMacMD5AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacMD5AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Cast5");
                this.this$0.put("Cipher.PBEWithHMacMD5AndDES", "gnu.crypto.jce.cipher.PBES2$HMacMD5$DES");
                this.this$0.put("Cipher.PBEWithHMacMD5AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Khazad");
                this.this$0.put("Cipher.PBEWithHMacMD5AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Serpent");
                this.this$0.put("Cipher.PBEWithHMacMD5AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Square");
                this.this$0.put("Cipher.PBEWithHMacMD5AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacMD5$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacMD5AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacMD5$Twofish");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndAES", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$AES");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Anubis");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Cast5");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$DES");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Khazad");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Serpent");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Square");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacSHA1AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacSHA1$Twofish");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndAES", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$AES");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Anubis");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Cast5");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$DES");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Khazad");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Serpent");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Square");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacSHA256AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacSHA256$Twofish");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndAES", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$AES");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Anubis");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Cast5");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$DES");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Khazad");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Serpent");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Square");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacSHA384AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacSHA384$Twofish");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndAES", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$AES");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Anubis");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndCast5", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Cast5");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$DES");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Khazad");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Serpent");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndSquare", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Square");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacSHA512AndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacSHA512$Twofish");
                this.this$0.put("Cipher.PBEWithHMacTigerAndAES", "gnu.crypto.jce.cipher.PBES2$HMacTiger$AES");
                this.this$0.put("Cipher.PBEWithHMacTigerAndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Anubis");
                this.this$0.put("Cipher.PBEWithHMacTigerAndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacTigerAndCast5", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Cast5");
                this.this$0.put("Cipher.PBEWithHMacTigerAndDES", "gnu.crypto.jce.cipher.PBES2$HMacTiger$DES");
                this.this$0.put("Cipher.PBEWithHMacTigerAndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Khazad");
                this.this$0.put("Cipher.PBEWithHMacTigerAndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Serpent");
                this.this$0.put("Cipher.PBEWithHMacTigerAndSquare", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Square");
                this.this$0.put("Cipher.PBEWithHMacTigerAndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacTiger$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacTigerAndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacTiger$Twofish");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndAES", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$AES");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndAnubis", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Anubis");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndBlowfish", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Blowfish");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndCast5", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Cast5");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndDES", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$DES");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndKhazad", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Khazad");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndSerpent", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Serpent");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndSquare", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Square");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndTripleDES", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$TripleDES");
                this.this$0.put("Cipher.PBEWithHMacWhirlpoolAndTwofish", "gnu.crypto.jce.cipher.PBES2$HMacWhirlpool$Twofish");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacHaval", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacHaval");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacMD2", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacMD2");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacMD4", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacMD4");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacMD5", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacMD5");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacSHA1", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacSHA1");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacSHA256", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacSHA256");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacSHA384", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacSHA384");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacSHA512", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacSHA512");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacTiger", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacTiger");
                this.this$0.put("SecretKeyFactory.PBKDF2WithHMacWhirlpool", "gnu.crypto.jce.PBKDF2SecretKeyFactory$HMacWhirlpool");
                this.this$0.put("AlgorithmParameters.BlockCipherParameters", "gnu.crypto.jce.params.BlockCipherParameters");
                this.this$0.put("Mac.HMAC-MD2", "gnu.crypto.jce.mac.HMacMD2Spi");
                this.this$0.put("Mac.HMAC-MD4", "gnu.crypto.jce.mac.HMacMD4Spi");
                this.this$0.put("Mac.HMAC-MD5", "gnu.crypto.jce.mac.HMacMD5Spi");
                this.this$0.put("Mac.HMAC-RIPEMD128", "gnu.crypto.jce.mac.HMacRipeMD128Spi");
                this.this$0.put("Mac.HMAC-RIPEMD160", "gnu.crypto.jce.mac.HMacRipeMD160Spi");
                this.this$0.put("Mac.HMAC-SHA160", "gnu.crypto.jce.mac.HMacSHA160Spi");
                this.this$0.put("Mac.HMAC-SHA256", "gnu.crypto.jce.mac.HMacSHA256Spi");
                this.this$0.put("Mac.HMAC-SHA384", "gnu.crypto.jce.mac.HMacSHA384Spi");
                this.this$0.put("Mac.HMAC-SHA512", "gnu.crypto.jce.mac.HMacSHA512Spi");
                this.this$0.put("Mac.HMAC-TIGER", "gnu.crypto.jce.mac.HMacTigerSpi");
                this.this$0.put("Mac.HMAC-HAVAL", "gnu.crypto.jce.mac.HMacHavalSpi");
                this.this$0.put("Mac.HMAC-WHIRLPOOL", "gnu.crypto.jce.mac.HMacWhirlpoolSpi");
                this.this$0.put("Mac.TMMH16", "gnu.crypto.jce.mac.TMMH16Spi");
                this.this$0.put("Mac.UHASH32", "gnu.crypto.jce.mac.UHash32Spi");
                this.this$0.put("Mac.UMAC32", "gnu.crypto.jce.mac.UMac32Spi");
                this.this$0.put("KeyStore.GKR", "gnu.crypto.jce.keyring.GnuKeyring");
                this.this$0.put("Alg.Alias.AlgorithmParameters.AES", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.BLOWFISH", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.ANUBIS", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.KHAZAD", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.NULL", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.RIJNDAEL", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.SERPENT", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.SQUARE", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.AlgorithmParameters.TWOFISH", "BlockCipherParameters");
                this.this$0.put("Alg.Alias.Cipher.RC4", "ARCFOUR");
                this.this$0.put("Alg.Alias.Cipher.3-DES", "TRIPLEDES");
                this.this$0.put("Alg.Alias.Cipher.3DES", "TRIPLEDES");
                this.this$0.put("Alg.Alias.Cipher.DES-EDE", "TRIPLEDES");
                this.this$0.put("Alg.Alias.Cipher.DESede", "TRIPLEDES");
                this.this$0.put("Alg.Alias.Cipher.CAST128", "CAST5");
                this.this$0.put("Alg.Alias.Cipher.CAST-128", "CAST5");
                this.this$0.put("Alg.Alias.MessageDigest.SHS", "SHA-160");
                this.this$0.put("Alg.Alias.MessageDigest.SHA", "SHA-160");
                this.this$0.put("Alg.Alias.MessageDigest.SHA1", "SHA-160");
                this.this$0.put("Alg.Alias.MessageDigest.SHA-1", "SHA-160");
                this.this$0.put("Alg.Alias.MessageDigest.SHA2-256", "SHA-256");
                this.this$0.put("Alg.Alias.MessageDigest.SHA2-384", "SHA-384");
                this.this$0.put("Alg.Alias.MessageDigest.SHA2-512", "SHA-512");
                this.this$0.put("Alg.Alias.MessageDigest.SHA256", "SHA-256");
                this.this$0.put("Alg.Alias.MessageDigest.SHA384", "SHA-384");
                this.this$0.put("Alg.Alias.MessageDigest.SHA512", "SHA-512");
                this.this$0.put("Alg.Alias.MessageDigest.RIPEMD-160", "RIPEMD160");
                this.this$0.put("Alg.Alias.MessageDigest.RIPEMD-128", "RIPEMD128");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHS", "HMAC-SHA160");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA", "HMAC-SHA160");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA1", "HMAC-SHA160");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA-160", "HMAC-SHA160");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA-256", "HMAC-SHA256");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA-384", "HMAC-SHA384");
                this.this$0.put("Alg.Alias.Mac.HMAC-SHA-512", "HMAC-SHA512");
                this.this$0.put("Alg.Alias.Mac.HMAC-RIPEMD-160", "HMAC-RIPEMD160");
                this.this$0.put("Alg.Alias.Mac.HMAC-RIPEMD-128", "HMAC-RIPEMD128");
                this.this$0.put("Alg.Alias.SecureRandom.RC4", "ARCFOUR");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-1PRNG", "SHA-160PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA1PRNG", "SHA-160PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHAPRNG", "SHA-160PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-256PRNG", "SHA-256PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-2-1PRNG", "SHA-256PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-384PRNG", "SHA-384PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-2-2PRNG", "SHA-384PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-512PRNG", "SHA-512PRNG");
                this.this$0.put("Alg.Alias.SecureRandom.SHA-2-3PRNG", "SHA-512PRNG");
                this.this$0.put("Alg.Alias.KeyPairGenerator.DSA", "DSS");
                this.this$0.put("Alg.Alias.Signature.DSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHAwithDSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA1withDSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA160withDSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA/DSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA1/DSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA-1/DSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.SHA-160/DSA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.DSAwithSHA", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.DSAwithSHA1", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.DSAwithSHA160", "DSS/RAW");
                this.this$0.put("Alg.Alias.Signature.RSA-PSS", "RSA-PSS/RAW");
                this.this$0.put("Alg.Alias.Signature.RSAPSS", "RSA-PSS/RAW");
                this.this$0.put("Alg.Alias.KeyStore.GnuKeyring", "GKR");
                this.this$0.put("SaslClientFactory.ANONYMOUS", "gnu.crypto.sasl.ClientFactory");
                this.this$0.put("SaslClientFactory.PLAIN", "gnu.crypto.sasl.ClientFactory");
                this.this$0.put("SaslClientFactory.CRAM-MD5", "gnu.crypto.sasl.ClientFactory");
                this.this$0.put("SaslClientFactory.SRP", "gnu.crypto.sasl.ClientFactory");
                this.this$0.put("SaslServerFactory.ANONYMOUS", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.PLAIN", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.CRAM-MD5", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-MD5", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-SHA-160", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-RIPEMD128", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-RIPEMD160", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-TIGER", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("SaslServerFactory.SRP-WHIRLPOOL", "gnu.crypto.sasl.ServerFactory");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-SHS", "SRP-SHA-160");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-SHA", "SRP-SHA-160");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-SHA1", "SRP-SHA-160");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-SHA-1", "SRP-SHA-160");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-SHA160", "SRP-SHA-160");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-RIPEMD-128", "SRP-RIPEMD128");
                this.this$0.put("Alg.Alias.SaslServerFactory.SRP-RIPEMD-160", "SRP-RIPEMD160");
                return null;
            }
            {
                this.this$0 = gnuCrypto;
            }
        });
    }
}

