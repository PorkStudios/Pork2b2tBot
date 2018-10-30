/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jce.ECNamedCurveTable
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.bouncycastle.jce.spec.ECNamedCurveParameterSpec
 */
package net.daporkchop.porklib.crypto;

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Random;
import javax.annotation.Nullable;
import net.daporkchop.porklib.crypto.KeyGenResult;
import net.daporkchop.porklib.crypto.TXVerification;
import net.daporkchop.porklib.hash.Hash;
import net.daporkchop.porklib.math.Base58;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class Crypto {
    public static KeyGenResult genKeysSHA1(@Nullable byte[] seed, int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            if (seed != null) {
                random.setSeed(seed);
            }
            keyGen.initialize(keySize, random);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            return new KeyGenResult(pub, priv);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] signSHA1(byte[] in, PrivateKey privateKey) {
        try {
            Signature ecdsaSign = Signature.getInstance("SHA1withECDSA");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(in);
            return ecdsaSign.sign();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifySigSHA1(byte[] data, byte[] sig, PublicKey publicKey) {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA1withECDSA");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(sig);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static KeyGenResult genKeysECDSA(@Nullable byte[] seed) {
        try {
            ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec((String)"brainpoolp512t1");
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom random = new SecureRandom();
            if (seed != null) {
                random.setSeed(seed);
            }
            keyGen.initialize((AlgorithmParameterSpec)ecSpec, random);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            return new KeyGenResult(pub, priv);
        }
        catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException();
        }
    }

    public static byte[] signSHA512(byte[] in, PrivateKey privateKey) {
        try {
            Signature ecdsaSign = Signature.getInstance("SHA512withECDSA", "BC");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(in);
            return ecdsaSign.sign();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new IllegalStateException();
        }
    }

    public static boolean verifySigSHA512(byte[] data, byte[] sig, PublicKey publicKey) {
        try {
            Signature ecdsaVerify = Signature.getInstance("SHA512withECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(sig);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new IllegalStateException();
        }
    }

    public static KeyGenResult genKeysWHIRLPOOL(@Nullable byte[] seed, int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            SecureRandom random = new SecureRandom();
            if (seed != null) {
                random.setSeed(seed);
            }
            keyGen.initialize(keySize, random);
            KeyPair pair = keyGen.generateKeyPair();
            PrivateKey priv = pair.getPrivate();
            PublicKey pub = pair.getPublic();
            return new KeyGenResult(pub, priv);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] signWHIRLPOOL(byte[] in, PrivateKey privateKey) {
        try {
            Signature ecdsaSign = Signature.getInstance("WhirlpoolWithRSA/X9.31", "BC");
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(in);
            return ecdsaSign.sign();
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            throw new IllegalStateException(e);
        }
    }

    public static boolean verifySigWHIRLPOOL(byte[] data, byte[] sig, PublicKey publicKey) {
        try {
            Signature ecdsaVerify = Signature.getInstance("WhirlpoolWithRSA/X9.31", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data);
            return ecdsaVerify.verify(sig);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static /* varargs */ void main(String ... args) {
        System.out.println("TESTING SHA1\n");
        byte[] randomData = new byte[1024];
        new Random().nextBytes(randomData);
        KeyGenResult result = Crypto.genKeysSHA1(randomData, 128);
        System.out.println("Private key: " + Base58.encodeRaw(result.privateKey.getEncoded()));
        System.out.println("" + result.privateKey.getEncoded().length + " bytes");
        System.out.println("Public key: " + Base58.encodeRaw(result.publicKey.getEncoded()));
        System.out.println("" + result.privateKey.getEncoded().length + " bytes");
        System.out.println("Address: " + Base58.encode((byte)5, "", result.publicKey.getEncoded()));
        System.out.println("Shrunken: " + Base58.encodeRaw(TXVerification.makeAddressPlain(result.publicKey.getEncoded(), data -> Hash.whirlpool(data), 16)));
        randomData = new byte[1024];
        new Random().nextBytes(randomData);
        byte[] signature = Crypto.signSHA1(randomData, result.privateKey);
        System.out.println("\nSignature: " + Hex.encodeHexString(signature));
        System.out.println("Valid: " + Crypto.verifySigSHA1(randomData, signature, result.publicKey));
        System.out.println("TESTING SHA512\n");
        randomData = new byte[1024];
        new Random().nextBytes(randomData);
        result = Crypto.genKeysECDSA(randomData);
        System.out.println("Private key: " + Base58.encodeRaw(result.privateKey.getEncoded()));
        System.out.println("" + result.privateKey.getEncoded().length + " bytes");
        System.out.println("Public key: " + Base58.encodeRaw(result.publicKey.getEncoded()));
        System.out.println("" + result.privateKey.getEncoded().length + " bytes");
        System.out.println("Address: " + Base58.encode((byte)5, "", result.publicKey.getEncoded()));
        System.out.println("Shrunken: " + Base58.encodeRaw(TXVerification.makeAddressPlain(result.publicKey.getEncoded(), data -> Hash.whirlpool(data), 16)));
        randomData = new byte[1024];
        new Random().nextBytes(randomData);
        signature = Crypto.signSHA512(randomData, result.privateKey);
        System.out.println("\nSignature: " + Hex.encodeHexString(signature));
        System.out.println("Valid: " + Crypto.verifySigSHA512(randomData, signature, result.publicKey));
        System.out.println("\n\nTESTING WHIRLPOOL\n");
        randomData = new byte[1024];
        new Random().nextBytes(randomData);
        result = Crypto.genKeysWHIRLPOOL(randomData, 4096);
        System.out.println("Private key: " + Base64.encodeBase64String(result.privateKey.getEncoded()));
        System.out.println("" + result.privateKey.getEncoded().length + " bytes");
        System.out.println("Public key: " + Base64.encodeBase64String(result.publicKey.getEncoded()));
        System.out.println("" + result.publicKey.getEncoded().length + " bytes");
        String pork58S = Base58.encode((byte)5, "", result.publicKey.getEncoded());
        System.out.println("Address: " + pork58S);
        System.out.println("Shrunken: " + Base58.encodeRaw(TXVerification.makeAddressPlain(result.publicKey.getEncoded(), data -> Hash.whirlpool(data), 16)));
        Base58.DecodedData decodedData = Base58.decode(pork58S);
        System.out.println("\nVersion: " + decodedData.version);
        System.out.println("Message: " + decodedData.prefix);
        System.out.println("Data: " + decodedData.content);
        randomData = new byte[1024];
        new Random().nextBytes(randomData);
        signature = Crypto.signWHIRLPOOL(randomData, result.privateKey);
        System.out.println("\nSignature: " + Hex.encodeHexString(signature));
        System.out.println("" + signature.length + " bytes");
        System.out.println("Valid: " + Crypto.verifySigWHIRLPOOL(randomData, signature, result.publicKey));
    }

    static {
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}

