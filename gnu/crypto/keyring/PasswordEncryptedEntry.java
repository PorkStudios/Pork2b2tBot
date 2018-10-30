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
import gnu.crypto.keyring.PasswordProtectedEntry;
import gnu.crypto.keyring.Properties;
import gnu.crypto.mode.IMode;
import gnu.crypto.mode.ModeFactory;
import gnu.crypto.pad.IPad;
import gnu.crypto.pad.PadFactory;
import gnu.crypto.pad.WrongPaddingException;
import gnu.crypto.prng.IRandom;
import gnu.crypto.prng.LimitReachedException;
import gnu.crypto.prng.PRNGFactory;
import gnu.crypto.util.PRNG;
import gnu.crypto.util.Util;
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

public class PasswordEncryptedEntry
extends MaskableEnvelopeEntry
implements PasswordProtectedEntry,
Registry {
    public static final int TYPE = 1;

    public static PasswordEncryptedEntry decode(DataInputStream in, char[] password) throws IOException {
        PasswordEncryptedEntry entry = PasswordEncryptedEntry.decode(in);
        try {
            entry.decrypt(password);
        }
        catch (WrongPaddingException wpe) {
            throw new MalformedKeyringException("wrong padding in decrypted data");
        }
        return entry;
    }

    public static PasswordEncryptedEntry decode(DataInputStream in) throws IOException {
        PasswordEncryptedEntry entry = new PasswordEncryptedEntry();
        entry.defaultDecode(in);
        return entry;
    }

    public void decrypt(char[] password) throws IllegalArgumentException, WrongPaddingException {
        if (!this.isMasked() || this.payload == null) {
            return;
        }
        IMode mode = this.getMode(password, 2);
        IPad padding = PadFactory.getInstance("PKCS7");
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

    public void encrypt(char[] password) throws IOException {
        byte[] salt = new byte[8];
        PRNG.nextBytes(salt, 0, salt.length);
        this.properties.put("salt", Util.toString(salt));
        IMode mode = this.getMode(password, 1);
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

    public void encode(DataOutputStream out, char[] password) throws IOException {
        this.encrypt(password);
        this.encode(out);
    }

    protected void encodePayload() throws IOException {
        if (this.payload == null) {
            throw new IllegalStateException("not encrypted");
        }
    }

    private final IMode getMode(char[] password, int state) {
        int blockSize;
        IRandom kdf;
        IMode mode;
        int keylen;
        String s = this.properties.get("salt");
        if (s == null) {
            throw new IllegalArgumentException("no salt");
        }
        byte[] salt = Util.toBytesFromString(s);
        IBlockCipher cipher = CipherFactory.getInstance(this.properties.get("cipher"));
        if (cipher == null) {
            throw new IllegalArgumentException("no such cipher: " + this.properties.get("cipher"));
        }
        blockSize = cipher.defaultBlockSize();
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
        HashMap<String, Object> pbAttr = new HashMap<String, Object>();
        pbAttr.put("gnu.crypto.pbe.password", password);
        pbAttr.put("gnu.crypto.pbe.salt", salt);
        pbAttr.put("gnu.crypto.pbe.iteration.count", ITERATION_COUNT);
        kdf = PRNGFactory.getInstance("PBKDF2-HMAC-SHA");
        kdf.init(pbAttr);
        keylen = 0;
        if (!this.properties.containsKey("keylen")) {
            throw new IllegalArgumentException("no key length");
        }
        try {
            keylen = Integer.parseInt(this.properties.get("keylen"));
        }
        catch (NumberFormatException numberFormatException) {}
        byte[] dk = new byte[keylen];
        byte[] iv = new byte[blockSize];
        try {
            kdf.nextBytes(dk, 0, keylen);
            kdf.nextBytes(iv, 0, blockSize);
        }
        catch (LimitReachedException shouldNotHappen) {
            throw new Error(shouldNotHappen.toString());
        }
        HashMap<String, Object> modeAttr = new HashMap<String, Object>();
        modeAttr.put("gnu.crypto.cipher.key.material", dk);
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

    public PasswordEncryptedEntry(String cipher, String mode, int keylen, Properties properties) {
        super(1, properties);
        if (cipher == null || cipher.length() == 0 || mode == null || mode.length() == 0) {
            throw new IllegalArgumentException("cipher nor mode can be empty");
        }
        this.properties.put("cipher", cipher);
        this.properties.put("mode", mode);
        this.properties.put("keylen", String.valueOf(keylen));
        this.setMasked(false);
    }

    private PasswordEncryptedEntry() {
        this.setMasked(true);
    }
}

