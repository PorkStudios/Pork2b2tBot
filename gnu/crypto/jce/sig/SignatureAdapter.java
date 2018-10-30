/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.sig;

import gnu.crypto.sig.ISignature;
import gnu.crypto.sig.ISignatureCodec;
import gnu.crypto.sig.SignatureFactory;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

class SignatureAdapter
extends SignatureSpi
implements Cloneable {
    private ISignature adaptee;
    private ISignatureCodec codec;

    public Object clone() {
        return new SignatureAdapter((ISignature)this.adaptee.clone(), this.codec);
    }

    public void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        HashMap<String, PublicKey> attributes = new HashMap<String, PublicKey>();
        attributes.put("gnu.crypto.sig.public.key", publicKey);
        try {
            this.adaptee.setupVerify(attributes);
        }
        catch (IllegalArgumentException x) {
            throw new InvalidKeyException(String.valueOf(x));
        }
    }

    public void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        HashMap<String, PrivateKey> attributes = new HashMap<String, PrivateKey>();
        attributes.put("gnu.crypto.sig.private.key", privateKey);
        try {
            this.adaptee.setupSign(attributes);
        }
        catch (IllegalArgumentException x) {
            throw new InvalidKeyException(String.valueOf(x));
        }
    }

    public void engineInitSign(PrivateKey privateKey, SecureRandom random) throws InvalidKeyException {
        HashMap<String, Serializable> attributes = new HashMap<String, Serializable>();
        attributes.put("gnu.crypto.sig.private.key", privateKey);
        attributes.put("gnu.crypto.sig.prng", random);
        try {
            this.adaptee.setupSign(attributes);
        }
        catch (IllegalArgumentException x) {
            throw new InvalidKeyException(String.valueOf(x));
        }
    }

    public void engineUpdate(byte b) throws SignatureException {
        try {
            this.adaptee.update(b);
        }
        catch (IllegalStateException x) {
            throw new SignatureException(String.valueOf(x));
        }
    }

    public void engineUpdate(byte[] b, int off, int len) throws SignatureException {
        try {
            this.adaptee.update(b, off, len);
        }
        catch (IllegalStateException x) {
            throw new SignatureException(String.valueOf(x));
        }
    }

    public byte[] engineSign() throws SignatureException {
        Object signature = null;
        try {
            signature = this.adaptee.sign();
        }
        catch (IllegalStateException x) {
            throw new SignatureException(String.valueOf(x));
        }
        byte[] result = this.codec.encodeSignature(signature);
        return result;
    }

    public int engineSign(byte[] outbuf, int offset, int len) throws SignatureException {
        byte[] signature = this.engineSign();
        int result = signature.length;
        if (result > len) {
            throw new SignatureException("len");
        }
        System.arraycopy(signature, 0, outbuf, offset, result);
        return result;
    }

    public boolean engineVerify(byte[] sigBytes) throws SignatureException {
        Object signature = this.codec.decodeSignature(sigBytes);
        boolean result = false;
        try {
            result = this.adaptee.verify(signature);
        }
        catch (IllegalStateException x) {
            throw new SignatureException(String.valueOf(x));
        }
        return result;
    }

    public void engineSetParameter(String param, Object value) throws InvalidParameterException {
        throw new InvalidParameterException("deprecated");
    }

    public void engineSetParameter(AlgorithmParameterSpec params) throws InvalidAlgorithmParameterException {
    }

    public Object engineGetParameter(String param) throws InvalidParameterException {
        throw new InvalidParameterException("deprecated");
    }

    protected SignatureAdapter(String sigName, ISignatureCodec codec) {
        this(SignatureFactory.getInstance(sigName), codec);
    }

    private SignatureAdapter(ISignature adaptee, ISignatureCodec codec) {
        this.adaptee = adaptee;
        this.codec = codec;
    }
}

