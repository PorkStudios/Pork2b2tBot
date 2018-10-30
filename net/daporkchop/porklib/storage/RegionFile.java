/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class RegionFile {
    static final int CHUNK_HEADER_SIZE = 5;
    private static final int VERSION_GZIP = 1;
    private static final int VERSION_DEFLATE = 2;
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 1024;
    private static final byte[] emptySector = new byte[4096];
    private final File fileName;
    private final int[] offsets = new int[1024];
    private final int[] chunkTimestamps = new int[1024];
    private RandomAccessFile file;
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;
    private long lastModified = 0L;

    public RegionFile(File path) {
        this.fileName = path;
        this.debugln("REGION LOAD " + this.fileName);
        this.sizeDelta = 0;
        try {
            int i;
            int i2;
            if (path.exists()) {
                this.lastModified = path.lastModified();
            } else {
                path.getAbsoluteFile().getParentFile().mkdirs();
            }
            this.file = new RandomAccessFile(path, "rw");
            if (this.file.length() < 4096L) {
                for (i2 = 0; i2 < 1024; ++i2) {
                    this.file.writeInt(0);
                }
                for (i2 = 0; i2 < 1024; ++i2) {
                    this.file.writeInt(0);
                }
                this.sizeDelta += 8192;
            }
            if ((this.file.length() & 4095L) != 0L) {
                i2 = 0;
                while ((long)i2 < (this.file.length() & 4095L)) {
                    this.file.write(0);
                    ++i2;
                }
            }
            int nSectors = (int)this.file.length() / 4096;
            this.sectorFree = new ArrayList(nSectors);
            for (i = 0; i < nSectors; ++i) {
                this.sectorFree.add(true);
            }
            this.sectorFree.set(0, false);
            this.sectorFree.set(1, false);
            this.file.seek(0L);
            for (i = 0; i < 1024; ++i) {
                int offset;
                this.offsets[i] = offset = this.file.readInt();
                if (offset == 0 || (offset >> 8) + (offset & 255) > this.sectorFree.size()) continue;
                for (int sectorNum = 0; sectorNum < (offset & 255); ++sectorNum) {
                    this.sectorFree.set((offset >> 8) + sectorNum, false);
                }
            }
            for (i = 0; i < 1024; ++i) {
                int lastModValue;
                this.chunkTimestamps[i] = lastModValue = this.file.readInt();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long lastModified() {
        return this.lastModified;
    }

    public synchronized int getSizeDelta() {
        int ret = this.sizeDelta;
        this.sizeDelta = 0;
        return ret;
    }

    private void debug(String in) {
        System.out.print(in);
    }

    private void debugln(String in) {
        this.debug(in + "\n");
    }

    private void debug(String mode, int x, int z, String in) {
        this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + z + "] = " + in);
    }

    private void debug(String mode, int x, int z, int count, String in) {
        this.debug("REGION " + mode + " " + this.fileName.getName() + "[" + x + "," + z + "] " + count + "B = " + in);
    }

    private void debugln(String mode, int x, int z, String in) {
        this.debug(mode, x, z, in + "\n");
    }

    public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
        if (this.outOfBounds(x, z)) {
            this.debugln("READ", x, z, "out of bounds");
            return null;
        }
        try {
            int offset = this.getOffset(x, z);
            if (offset == 0) {
                this.debugln("READ", x, z, "miss");
                return null;
            }
            int sectorNumber = offset >> 8;
            int numSectors = offset & 255;
            if (sectorNumber + numSectors > this.sectorFree.size()) {
                this.debugln("READ", x, z, "invalid sector");
                return null;
            }
            this.file.seek(sectorNumber * 4096);
            int length = this.file.readInt();
            if (length > 4096 * numSectors) {
                this.debugln("READ", x, z, "invalid length: " + length + " > 4096 * " + numSectors);
                return null;
            }
            byte version = this.file.readByte();
            if (version == 1) {
                byte[] data = new byte[length - 1];
                this.file.read(data);
                DataInputStream ret = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))));
                return ret;
            }
            if (version == 2) {
                byte[] data = new byte[length - 1];
                this.file.read(data);
                DataInputStream ret = new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(data))));
                return ret;
            }
            this.debugln("READ", x, z, "unknown version " + version);
            return null;
        }
        catch (IOException e) {
            this.debugln("READ", x, z, "exception");
            return null;
        }
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        if (this.outOfBounds(x, z)) {
            return null;
        }
        return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(x, z)));
    }

    protected synchronized void write(int x, int z, byte[] data, int length) {
        try {
            int offset = this.getOffset(x, z);
            int sectorNumber = offset >> 8;
            int sectorsAllocated = offset & 255;
            int sectorsNeeded = (length + 5) / 4096 + 1;
            if (sectorsNeeded >= 256) {
                return;
            }
            if (sectorNumber != 0 && sectorsAllocated == sectorsNeeded) {
                this.debug("SAVE", x, z, length, "rewrite");
                this.write(sectorNumber, data, length);
            } else {
                int i;
                for (int i2 = 0; i2 < sectorsAllocated; ++i2) {
                    this.sectorFree.set(sectorNumber + i2, true);
                }
                int runStart = this.sectorFree.indexOf(true);
                int runLength = 0;
                if (runStart != -1) {
                    for (i = runStart; i < this.sectorFree.size(); ++i) {
                        if (runLength != 0) {
                            runLength = this.sectorFree.get(i).booleanValue() ? ++runLength : 0;
                        } else if (this.sectorFree.get(i).booleanValue()) {
                            runStart = i;
                            runLength = 1;
                        }
                        if (runLength >= sectorsNeeded) break;
                    }
                }
                if (runLength >= sectorsNeeded) {
                    this.debug("SAVE", x, z, length, "reuse");
                    sectorNumber = runStart;
                    this.setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
                    for (i = 0; i < sectorsNeeded; ++i) {
                        this.sectorFree.set(sectorNumber + i, false);
                    }
                    this.write(sectorNumber, data, length);
                } else {
                    this.debug("SAVE", x, z, length, "grow");
                    this.file.seek(this.file.length());
                    sectorNumber = this.sectorFree.size();
                    for (i = 0; i < sectorsNeeded; ++i) {
                        this.file.write(emptySector);
                        this.sectorFree.add(false);
                    }
                    this.sizeDelta += 4096 * sectorsNeeded;
                    this.write(sectorNumber, data, length);
                    this.setOffset(x, z, sectorNumber << 8 | sectorsNeeded);
                }
            }
            this.setTimestamp(x, z, (int)(System.currentTimeMillis() / 1000L));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        this.debugln(" " + sectorNumber);
        this.file.seek(sectorNumber * 4096);
        this.file.writeInt(length + 1);
        this.file.writeByte(2);
        this.file.write(data, 0, length);
    }

    private boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return this.offsets[x + z * 32];
    }

    public boolean hasChunk(int x, int z) {
        return this.getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        this.offsets[x + z * 32] = offset;
        this.file.seek((x + z * 32) * 4);
        this.file.writeInt(offset);
    }

    private void setTimestamp(int x, int z, int value) throws IOException {
        this.chunkTimestamps[x + z * 32] = value;
        this.file.seek(4096 + (x + z * 32) * 4);
        this.file.writeInt(value);
    }

    public void close() throws IOException {
        this.file.close();
    }

    class ChunkBuffer
    extends ByteArrayOutputStream {
        private int x;
        private int z;

        public ChunkBuffer(int x, int z) {
            super(8096);
            this.x = x;
            this.z = z;
        }

        @Override
        public void close() {
            RegionFile.this.write(this.x, this.z, this.buf, this.count);
        }
    }

}

