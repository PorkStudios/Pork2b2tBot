/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.jce.hash;

import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;
import java.security.DigestException;
import java.security.MessageDigestSpi;

class MessageDigestAdapter
extends MessageDigestSpi
implements Cloneable {
    private IMessageDigest adaptee;

    public Object clone() {
        return new MessageDigestAdapter((IMessageDigest)this.adaptee.clone());
    }

    public int engineGetDigestLength() {
        return this.adaptee.hashSize();
    }

    public void engineUpdate(byte input) {
        this.adaptee.update(input);
    }

    public void engineUpdate(byte[] input, int offset, int len) {
        this.adaptee.update(input, offset, len);
    }

    public byte[] engineDigest() {
        return this.adaptee.digest();
    }

    public int engineDigest(byte[] buf, int offset, int len) throws DigestException {
        int result = this.adaptee.hashSize();
        if (len < result) {
            throw new DigestException();
        }
        byte[] md = this.adaptee.digest();
        System.arraycopy(md, 0, buf, offset, result);
        return result;
    }

    public void engineReset() {
        this.adaptee.reset();
    }

    protected MessageDigestAdapter(String mdName) {
        this(HashFactory.getInstance(mdName));
    }

    private MessageDigestAdapter(IMessageDigest adaptee) {
        this.adaptee = adaptee;
    }
}

