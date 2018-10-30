/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.assembly.Assembly;
import gnu.crypto.assembly.Cascade;
import gnu.crypto.assembly.Direction;
import gnu.crypto.assembly.Stage;
import gnu.crypto.assembly.Transformer;
import gnu.crypto.assembly.TransformerException;
import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.mode.ModeFactory;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.PadFactory;
import gnu.crypto.sasl.ConfidentialityException;
import gnu.crypto.sasl.srp.KDF;
import java.util.HashMap;
import java.util.Map;
import javax.security.sasl.SaslException;

public final class CALG {
    private Assembly assembly;
    private Object modeNdx;
    private int blockSize;
    private int keySize;

    static final synchronized CALG getInstance(String algorithm) {
        IBlockCipher cipher = CipherFactory.getInstance(algorithm);
        int blockSize = cipher.defaultBlockSize();
        int keySize = cipher.defaultKeySize();
        Cascade ofbCipher = new Cascade();
        Object modeNdx = ofbCipher.append(Stage.getInstance(ModeFactory.getInstance("ofb", cipher, blockSize), Direction.FORWARD));
        IPad pkcs7 = PadFactory.getInstance("pkcs7");
        Assembly asm = new Assembly();
        asm.addPreTransformer(Transformer.getCascadeTransformer(ofbCipher));
        asm.addPreTransformer(Transformer.getPaddingTransformer(pkcs7));
        return new CALG(blockSize, keySize, modeNdx, asm);
    }

    public final void init(KDF kdf, byte[] iv, Direction dir) throws SaslException {
        byte[] realIV;
        if (iv.length == this.blockSize) {
            realIV = iv;
        } else {
            realIV = new byte[this.blockSize];
            if (iv.length > this.blockSize) {
                System.arraycopy(iv, 0, realIV, 0, this.blockSize);
            } else {
                System.arraycopy(iv, 0, realIV, 0, iv.length);
            }
        }
        HashMap<String, byte[]> modeAttributes = new HashMap<String, byte[]>();
        byte[] sk = kdf.derive(this.keySize);
        modeAttributes.put("gnu.crypto.cipher.key.material", sk);
        modeAttributes.put("gnu.crypto.mode.iv", realIV);
        HashMap<Object, Object> attributes = new HashMap<Object, Object>();
        attributes.put("gnu.crypto.assembly.assembly.direction", dir);
        attributes.put(this.modeNdx, modeAttributes);
        try {
            this.assembly.init(attributes);
        }
        catch (TransformerException x) {
            throw new SaslException("getInstance()", x);
        }
    }

    public final byte[] doFinal(byte[] data) throws ConfidentialityException {
        return this.doFinal(data, 0, data.length);
    }

    public final byte[] doFinal(byte[] data, int offset, int length) throws ConfidentialityException {
        byte[] result;
        try {
            result = this.assembly.lastUpdate(data, offset, length);
        }
        catch (TransformerException x) {
            throw new ConfidentialityException("doFinal()", x);
        }
        return result;
    }

    private CALG(int blockSize, int keySize, Object modeNdx, Assembly assembly) {
        this.blockSize = blockSize;
        this.keySize = keySize;
        this.modeNdx = modeNdx;
        this.assembly = assembly;
    }
}

