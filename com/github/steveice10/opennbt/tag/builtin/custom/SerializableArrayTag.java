/*
 * Decompiled with CFR 0_132.
 */
package com.github.steveice10.opennbt.tag.builtin.custom;

import com.github.steveice10.opennbt.tag.builtin.Tag;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializableArrayTag
extends Tag {
    private Serializable[] value;

    public SerializableArrayTag(String name) {
        this(name, new Serializable[0]);
    }

    public SerializableArrayTag(String name, Serializable[] value) {
        super(name);
        this.value = value;
    }

    public Serializable[] getValue() {
        return (Serializable[])this.value.clone();
    }

    public void setValue(Serializable[] value) {
        if (value == null) {
            return;
        }
        this.value = (Serializable[])value.clone();
    }

    public Serializable getValue(int index) {
        return this.value[index];
    }

    public void setValue(int index, Serializable value) {
        this.value[index] = value;
    }

    public int length() {
        return this.value.length;
    }

    @Override
    public void read(DataInput in) throws IOException {
        this.value = new Serializable[in.readInt()];
        ObjectInputStream str = new ObjectInputStream(new DataInputInputStream(in));
        for (int index = 0; index < this.value.length; ++index) {
            try {
                this.value[index] = (Serializable)str.readObject();
                continue;
            }
            catch (ClassNotFoundException e) {
                throw new IOException("Class not found while reading SerializableArrayTag!", e);
            }
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        ObjectOutputStream str = new ObjectOutputStream(new DataOutputOutputStream(out));
        for (int index = 0; index < this.value.length; ++index) {
            str.writeObject(this.value[index]);
        }
    }

    @Override
    public SerializableArrayTag clone() {
        return new SerializableArrayTag(this.getName(), this.getValue());
    }

    private static class DataOutputOutputStream
    extends OutputStream {
        private DataOutput out;

        public DataOutputOutputStream(DataOutput out) {
            this.out = out;
        }

        @Override
        public void write(int b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            this.out.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            this.out.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static class DataInputInputStream
    extends InputStream {
        private DataInput in;

        public DataInputInputStream(DataInput in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            return this.in.readUnsignedByte();
        }

        @Override
        public int read(byte[] b) throws IOException {
            this.in.readFully(b);
            return b.length;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            this.in.readFully(b, off, len);
            return len;
        }

        @Override
        public long skip(long l) throws IOException {
            return this.in.skipBytes((int)l);
        }

        @Override
        public int available() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public synchronized void mark(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public synchronized void reset() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean markSupported() {
            return false;
        }
    }

}

