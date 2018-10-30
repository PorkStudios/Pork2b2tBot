/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt.stream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NBTInputStream
implements DataInput,
AutoCloseable {
    private final DataInputStream stream;
    private final ByteOrder endianness;
    private final boolean network;

    public NBTInputStream(InputStream stream) {
        this(stream, ByteOrder.BIG_ENDIAN);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness) {
        this(stream, endianness, false);
    }

    public NBTInputStream(InputStream stream, ByteOrder endianness, boolean network) {
        this.stream = stream instanceof DataInputStream ? (DataInputStream)stream : new DataInputStream(stream);
        this.endianness = endianness;
        this.network = network;
    }

    public ByteOrder getEndianness() {
        return this.endianness;
    }

    public boolean isNetwork() {
        return this.network;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.stream.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.stream.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.stream.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.stream.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.stream.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.stream.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short s = this.stream.readShort();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Short.reverseBytes(s);
        }
        return s;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int s = this.stream.readUnsignedShort();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            s = Integer.reverseBytes(s) >> 16;
        }
        return s;
    }

    @Override
    public char readChar() throws IOException {
        char c = this.stream.readChar();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            c = Character.reverseBytes(c);
        }
        return c;
    }

    @Override
    public int readInt() throws IOException {
        int i = this.stream.readInt();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            i = Integer.reverseBytes(i);
        }
        return i;
    }

    @Override
    public long readLong() throws IOException {
        long l = this.stream.readLong();
        if (this.endianness == ByteOrder.LITTLE_ENDIAN) {
            l = Long.reverseBytes(l);
        }
        return l;
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Deprecated
    @Override
    public String readLine() throws IOException {
        return this.stream.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        int length = this.readUnsignedShort();
        byte[] bytes = new byte[length];
        this.stream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public int available() throws IOException {
        return this.stream.available();
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}

