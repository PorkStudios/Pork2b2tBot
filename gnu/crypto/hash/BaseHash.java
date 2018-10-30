/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.hash;

import gnu.crypto.hash.IMessageDigest;

public abstract class BaseHash
implements IMessageDigest {
    protected String name;
    protected int hashSize;
    protected int blockSize;
    protected long count;
    protected byte[] buffer;

    public String name() {
        return this.name;
    }

    public int hashSize() {
        return this.hashSize;
    }

    public int blockSize() {
        return this.blockSize;
    }

    public void update(byte b) {
        int i = (int)(this.count % (long)this.blockSize);
        ++this.count;
        this.buffer[i] = b;
        if (i == this.blockSize - 1) {
            this.transform(this.buffer, 0);
        }
    }

    public void update(byte[] b, int offset, int len) {
        int n = (int)(this.count % (long)this.blockSize);
        this.count += (long)len;
        int partLen = this.blockSize - n;
        int i = 0;
        if (len >= partLen) {
            System.arraycopy(b, offset, this.buffer, n, partLen);
            this.transform(this.buffer, 0);
            i = partLen;
            while (i + this.blockSize - 1 < len) {
                this.transform(b, offset + i);
                i += this.blockSize;
            }
            n = 0;
        }
        if (i < len) {
            System.arraycopy(b, offset + i, this.buffer, n, len - i);
        }
    }

    public byte[] digest() {
        byte[] tail = this.padBuffer();
        this.update(tail, 0, tail.length);
        byte[] result = this.getResult();
        this.reset();
        return result;
    }

    public void reset() {
        this.count = 0L;
        int i = 0;
        while (i < this.blockSize) {
            this.buffer[i++] = 0;
        }
        this.resetContext();
    }

    public abstract Object clone();

    public abstract boolean selfTest();

    protected abstract byte[] padBuffer();

    protected abstract byte[] getResult();

    protected abstract void resetContext();

    protected abstract void transform(byte[] var1, int var2);

    protected BaseHash(String name, int hashSize, int blockSize) {
        this.name = name;
        this.hashSize = hashSize;
        this.blockSize = blockSize;
        this.buffer = new byte[blockSize];
        this.resetContext();
    }
}

