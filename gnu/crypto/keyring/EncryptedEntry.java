/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.keyring;

import gnu.crypto.Registry;
import gnu.crypto.cipher.CipherFactory;
import gnu.crypto.cipher.IBlockCipher;
import gnu.crypto.keyring.Entry;
import gnu.crypto.keyring.MalformedKeyringException;
import gnu.crypto.keyring.MaskableEnvelopeEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.mode.IMode;
import gnu.crypto.mode.ModeFactory;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.PadFactory;
import gnu.crypto.pad.WrongPaddingException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EncryptedEntry
extends MaskableEnvelopeEntry
implements Registry {
    public static final int TYPE = 0;

    public static EncryptedEntry decode(DataInputStream in) throws IOException {
        EncryptedEntry entry = new EncryptedEntry();
        entry.defaultDecode(in);
        if (!entry.properties.containsKey("cipher")) {
            throw new MalformedKeyringException("no cipher");
        }
        if (!entry.properties.containsKey("cipher")) {
            throw new MalformedKeyringException("no cipher");
        }
        return entry;
    }

    public void decrypt(byte[] key, byte[] iv) throws IllegalArgumentException, WrongPaddingException {
        if (!this.isMasked() || this.payload == null) {
            return;
        }
        IMode mode = this.getMode(key, iv, 2);
        IPad padding = null;
        padding = PadFactory.getInstance("PKCS7");
        padding.init(mode.currentBlockSize());
        byte[] buf = new byte[this.payload.length];
        int count = 0;
        int i = 0;
        while (i < this.payload.length) {
            mode.update(this.payload, count, buf, count);
            count += mode.currentBlockSize();
            ++i;
        }
        int padlen = padding.unpad(buf, 0, buf.length);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(buf, 0, buf.length - padlen));
        try {
            this.decodeEnvelope(in);
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException("decryption failed");
        }
        this.setMasked(false);
        this.payload = null;
    }

    public void encrypt(byte[] key, byte[] iv) throws IOException {
        IMode mode = this.getMode(key, iv, 1);
        IPad pad = PadFactory.getInstance("PKCS7");
        pad.init(mode.currentBlockSize());
        ByteArrayOutputStream bout = new ByteArrayOutputStream(1024);
        DataOutputStream out2 = new DataOutputStream(bout);
        Iterator it = this.entries.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            entry.encode(out2);
        }
        byte[] plaintext = bout.toByteArray();
        byte[] padding = pad.pad(plaintext, 0, plaintext.length);
        this.payload = new byte[plaintext.length + padding.length];
        byte[] lastBlock = new byte[mode.currentBlockSize()];
        int l = mode.currentBlockSize() - padding.length;
        System.arraycopy(plaintext, plaintext.length - l, lastBlock, 0, l);
        System.arraycopy(padding, 0, lastBlock, l, padding.length);
        int count = 0;
        while (count + mode.currentBlockSize() < plaintext.length) {
            mode.update(plaintext, count, this.payload, count);
            count += mode.currentBlockSize();
        }
        mode.update(lastBlock, 0, this.payload, count);
    }

    public void encodePayload() throws IOException {
        if (this.payload == null) {
            throw new IOException("not encrypted");
        }
    }

    private final IMode getMode(byte[] key, byte[] iv, int state) {
        IMode mode;
        IBlockCipher cipher = CipherFactory.getInstance(this.properties.get("cipher"));
        if (cipher == null) {
            throw new IllegalArgumentException("no such cipher: " + this.properties.get("cipher"));
        }
        int blockSize = cipher.defaultBlockSize();
        if (this.properties.containsKey("block-size")) {
            try {
                blockSize = Integer.parseInt(this.properties.get("block-size"));
            }
            catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("bad block size: " + nfe.getMessage());
            }
        }
        if ((mode = ModeFactory.getInstance(this.properties.get("mode"), cipher, blockSize)) == null) {
            throw new IllegalArgumentException("no such mode: " + this.properties.get("mode"));
        }
        HashMap<String, Object> modeAttr = new HashMap<String, Object>();
        modeAttr.put("gnu.crypto.cipher.key.material", key);
        modeAttr.put("gnu.crypto.mode.state", new Integer(state));
        modeAttr.put("gnu.crypto.mode.iv", iv);
        try {
            mode.init(modeAttr);
        }
        catch (InvalidKeyException ike) {
            throw new IllegalArgumentException(ike.toString());
        }
        return mode;
    }

    public EncryptedEntry(String cipher, String mode, Properties properties) {
        super(0, properties);
        if (cipher == null || mode == null) {
            throw new IllegalArgumentException("neither cipher nor mode can be null");
        }
        properties.put("cipher", cipher);
        properties.put("mode", mode);
        this.setMasked(false);
    }

    private EncryptedEntry() {
        this.setMasked(true);
    }
}

