/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.cipher;

import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.jce.spec.BlockCipherParameterSpec;
import gnu.crypto.mode.IMode;
import gnu.crypto.mode.ModeFactory;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.PadFactory;
import gnu.crypto.pad.WrongPaddingException;
import java.io.PrintStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

class CipherAdapter
extends CipherSpi {
    protected IBlockCipher cipher;
    protected IMode mode;
    protected IPad pad;
    protected int keyLen;
    protected Map attributes;
    protected byte[] partBlock;
    protected int partLen;
    protected int blockLen;
    static /* synthetic */ Class class$gnu$crypto$jce$spec$BlockCipherParameterSpec;

    protected void engineSetMode(String modeName) throws NoSuchAlgorithmException {
        if (modeName.length() >= 3 && modeName.substring(0, 3).equalsIgnoreCase("CFB")) {
            if (modeName.length() > 3) {
                try {
                    int bs = Integer.parseInt(modeName.substring(3));
                    this.attributes.put("gnu.crypto.mode.block.size", new Integer(bs / 8));
                }
                catch (NumberFormatException nfe) {
                    throw new NoSuchAlgorithmException(modeName);
                }
                modeName = "CFB";
            }
        } else {
            this.attributes.remove("gnu.crypto.mode.block.size");
        }
        this.mode = ModeFactory.getInstance(modeName, this.cipher, this.blockLen);
        if (this.mode == null) {
            throw new NoSuchAlgorithmException(modeName);
        }
    }

    protected void engineSetPadding(String padName) throws NoSuchPaddingException {
        if (padName.equalsIgnoreCase("NoPadding")) {
            this.pad = null;
            return;
        }
        this.pad = PadFactory.getInstance(padName);
        if (this.pad == null) {
            throw new NoSuchPaddingException(padName);
        }
    }

    protected int engineGetBlockSize() {
        if (this.cipher != null) {
            return this.blockLen;
        }
        return 0;
    }

    protected int engineGetOutputSize(int inputLen) {
        int blockSize = this.mode.currentBlockSize();
        return (inputLen + this.partLen) / blockSize * blockSize;
    }

    protected byte[] engineGetIV() {
        byte[] iv = (byte[])this.attributes.get("gnu.crypto.mode.iv");
        if (iv == null) {
            return null;
        }
        return (byte[])iv.clone();
    }

    protected AlgorithmParameters engineGetParameters() {
        AlgorithmParameters params;
        BlockCipherParameterSpec spec = new BlockCipherParameterSpec((byte[])this.attributes.get("gnu.crypto.mode.iv"), this.cipher.currentBlockSize(), this.keyLen);
        try {
            params = AlgorithmParameters.getInstance("BlockCipherParameters");
            params.init(spec);
        }
        catch (NoSuchAlgorithmException nsae) {
            return null;
        }
        catch (InvalidParameterSpecException ipse) {
            return null;
        }
        return params;
    }

    protected void engineInit(int opmode, Key key, SecureRandom random) throws InvalidKeyException {
        switch (opmode) {
            case 1: {
                this.attributes.put("gnu.crypto.mode.state", new Integer(1));
                break;
            }
            case 2: {
                this.attributes.put("gnu.crypto.mode.state", new Integer(2));
                break;
            }
        }
        if (!key.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeyException("bad key format " + key.getFormat());
        }
        byte[] kb = key.getEncoded();
        if (this.keyLen == 0) {
            this.keyLen = kb.length;
        } else if (this.keyLen < kb.length) {
            byte[] kbb = kb;
            kb = new byte[this.keyLen];
            System.arraycopy(kbb, 0, kb, 0, this.keyLen);
        }
        this.attributes.put("gnu.crypto.cipher.key.material", kb);
        this.mode.reset();
        this.mode.init(this.attributes);
        if (this.pad != null) {
            this.pad.reset();
            this.pad.init(this.blockLen);
        }
        this.partBlock = new byte[this.blockLen];
        this.partLen = 0;
    }

    protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params == null) {
            byte[] iv = new byte[this.blockLen];
            random.nextBytes(iv);
            this.attributes.put("gnu.crypto.mode.iv", iv);
            this.blockLen = this.cipher.defaultBlockSize();
            this.attributes.put("gnu.crypto.cipher.block.size", new Integer(this.blockLen));
            this.keyLen = 0;
        } else if (params instanceof BlockCipherParameterSpec) {
            this.attributes.put("gnu.crypto.cipher.block.size", new Integer(((BlockCipherParameterSpec)params).getBlockSize()));
            this.attributes.put("gnu.crypto.mode.iv", ((BlockCipherParameterSpec)params).getIV());
            this.keyLen = ((BlockCipherParameterSpec)params).getKeySize();
            this.blockLen = ((BlockCipherParameterSpec)params).getBlockSize();
        } else if (params instanceof IvParameterSpec) {
            this.attributes.put("gnu.crypto.mode.iv", ((IvParameterSpec)params).getIV());
            this.blockLen = this.cipher.defaultBlockSize();
            this.attributes.put("gnu.crypto.cipher.block.size", new Integer(this.blockLen));
            this.keyLen = 0;
        }
        this.engineInit(opmode, key, random);
    }

    protected void engineInit(int opmode, Key key, AlgorithmParameters params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec spec;
        spec = null;
        try {
            if (params != null) {
                Class class_ = class$gnu$crypto$jce$spec$BlockCipherParameterSpec;
                if (class_ == null) {
                    class_ = class$gnu$crypto$jce$spec$BlockCipherParameterSpec = CipherAdapter.class$("[Lgnu.crypto.jce.spec.BlockCipherParameterSpec;", false);
                }
                spec = (AlgorithmParameterSpec)params.getParameterSpec(class_);
            }
        }
        catch (InvalidParameterSpecException invalidParameterSpecException) {}
        this.engineInit(opmode, key, spec, random);
    }

    protected byte[] engineUpdate(byte[] input, int off, int len) {
        int blockSize = this.mode.currentBlockSize();
        int count = (this.partLen + len) / blockSize;
        byte[] out = new byte[count * blockSize];
        try {
            this.engineUpdate(input, off, len, out, 0);
        }
        catch (ShortBufferException x) {
            x.printStackTrace(System.err);
        }
        return out;
    }

    protected int engineUpdate(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws ShortBufferException {
        byte[] buf;
        if (inLen == 0) {
            return 0;
        }
        int blockSize = this.mode.currentBlockSize();
        int blockCount = (this.partLen + inLen) / blockSize;
        int result = blockCount * blockSize;
        if (result > out.length - outOff) {
            throw new ShortBufferException();
        }
        if (blockCount == 0) {
            System.arraycopy(in, inOff, this.partBlock, this.partLen, inLen);
            this.partLen += inLen;
            return 0;
        }
        if (this.partLen == 0) {
            buf = in;
        } else {
            buf = new byte[this.partLen + inLen];
            System.arraycopy(this.partBlock, 0, buf, 0, this.partLen);
            if (in != null && inLen > 0) {
                System.arraycopy(in, inOff, buf, this.partLen, inLen);
            }
            inOff = 0;
        }
        int i = 0;
        while (i < blockCount) {
            this.mode.update(buf, inOff, out, outOff);
            inOff += blockSize;
            outOff += blockSize;
            ++i;
        }
        this.partLen += inLen - result;
        if (this.partLen > 0) {
            System.arraycopy(buf, inOff, this.partBlock, 0, this.partLen);
        }
        return result;
    }

    protected byte[] engineDoFinal(byte[] input, int off, int len) throws IllegalBlockSizeException, BadPaddingException {
        byte[] result;
        byte[] buf = this.engineUpdate(input, off, len);
        if (this.pad != null) {
            switch ((Integer)this.attributes.get("gnu.crypto.mode.state")) {
                case 1: {
                    byte[] padding = this.pad.pad(this.partBlock, 0, this.partLen);
                    byte[] buf2 = this.engineUpdate(padding, 0, padding.length);
                    result = new byte[buf.length + buf2.length];
                    System.arraycopy(buf, 0, result, 0, buf.length);
                    System.arraycopy(buf2, 0, result, buf.length, buf2.length);
                    break;
                }
                case 2: {
                    int padLen;
                    try {
                        padLen = this.pad.unpad(buf, 0, buf.length);
                    }
                    catch (WrongPaddingException wpe) {
                        throw new BadPaddingException(wpe.getMessage());
                    }
                    result = new byte[buf.length - padLen];
                    System.arraycopy(buf, 0, result, 0, result.length);
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        } else {
            if (this.partLen > 0) {
                throw new IllegalBlockSizeException("" + this.partLen + " trailing bytes");
            }
            result = buf;
        }
        return result;
    }

    protected int engineDoFinal(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws BadPaddingException, IllegalBlockSizeException, ShortBufferException {
        byte[] buf = this.engineDoFinal(in, inOff, inLen);
        if (out.length + outOff < buf.length) {
            throw new ShortBufferException();
        }
        System.arraycopy(buf, 0, out, outOff, buf.length);
        return buf.length;
    }

    static /* synthetic */ Class class$(String string, boolean bl) {
        try {
            Class<?> class_ = Class.forName(string);
            if (!bl) {
                class_ = class_.getComponentType();
            }
            return class_;
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError().initCause(classNotFoundException);
        }
    }

    protected CipherAdapter(String cipherName, int blockLen) {
        this.cipher = CipherFactory.getInstance(cipherName);
        this.attributes = new HashMap();
        this.blockLen = blockLen;
        this.mode = ModeFactory.getInstance("ECB", this.cipher, blockLen);
        this.attributes.put("gnu.crypto.cipher.block.size", new Integer(blockLen));
    }

    protected CipherAdapter(String cipherName) {
        this.cipher = CipherFactory.getInstance(cipherName);
        this.blockLen = this.cipher.defaultBlockSize();
        this.attributes = new HashMap();
        this.mode = ModeFactory.getInstance("ECB", this.cipher, this.blockLen);
        this.attributes.put("gnu.crypto.cipher.block.size", new Integer(this.blockLen));
    }
}

