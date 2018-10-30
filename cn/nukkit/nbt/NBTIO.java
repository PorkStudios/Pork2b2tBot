/*
 * Decompiled with CFR 0_132.
 */
package cn.nukkit.nbt;

import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NBTIO {
    public static CompoundTag read(File file) {
        return NBTIO.read(file, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(File file, ByteOrder endianness) {
        try {
            if (!file.exists()) {
                return null;
            }
            return NBTIO.read(new FileInputStream(file), endianness);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag read(InputStream inputStream) {
        return NBTIO.read(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(InputStream inputStream, ByteOrder endianness) {
        return NBTIO.read(inputStream, endianness, false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static CompoundTag read(InputStream inputStream, ByteOrder endianness, boolean network) {
        try {
            try (NBTInputStream stream = new NBTInputStream(inputStream, endianness, network);){
                Tag tag = Tag.readNamedTag(stream);
                inputStream.close();
                if (tag instanceof CompoundTag) {
                    CompoundTag compoundTag = (CompoundTag)tag;
                    return compoundTag;
                }
                throw new IOException("Root tag must be a named compound tag");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag read(byte[] data) {
        return NBTIO.read(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness) {
        return NBTIO.read(new ByteArrayInputStream(data), endianness);
    }

    public static CompoundTag read(byte[] data, ByteOrder endianness, boolean network) {
        return NBTIO.read(new ByteArrayInputStream(data), endianness, network);
    }

    public static CompoundTag readCompressed(InputStream inputStream) {
        return NBTIO.readCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(InputStream inputStream, ByteOrder endianness) {
        try {
            return NBTIO.read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readCompressed(byte[] data) {
        return NBTIO.readCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readCompressed(byte[] data, ByteOrder endianness) {
        try {
            return NBTIO.read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream) {
        return NBTIO.readNetworkCompressed(inputStream, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(InputStream inputStream, ByteOrder endianness) {
        try {
            return NBTIO.read(new BufferedInputStream(new GZIPInputStream(inputStream)), endianness);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static CompoundTag readNetworkCompressed(byte[] data) {
        return NBTIO.readNetworkCompressed(data, ByteOrder.BIG_ENDIAN);
    }

    public static CompoundTag readNetworkCompressed(byte[] data, ByteOrder endianness) {
        try {
            return NBTIO.read(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(data))), endianness, true);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] write(CompoundTag tag) {
        return NBTIO.write(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(CompoundTag tag, ByteOrder endianness) {
        return NBTIO.write(tag, endianness, false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte[] write(CompoundTag tag, ByteOrder endianness, boolean network) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network);){
                Tag.writeNamedTag(tag, stream);
                byte[] arrby = baos.toByteArray();
                return arrby;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] write(Collection<CompoundTag> tags) {
        return NBTIO.write(tags, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness) {
        return NBTIO.write(tags, endianness, false);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte[] write(Collection<CompoundTag> tags, ByteOrder endianness, boolean network) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (NBTOutputStream stream = new NBTOutputStream(baos, endianness, network);){
                for (CompoundTag tag : tags) {
                    Tag.writeNamedTag(tag, stream);
                }
                byte[] arrby = baos.toByteArray();
                return arrby;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void write(CompoundTag tag, File file) {
        NBTIO.write(tag, file, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, File file, ByteOrder endianness) {
        try {
            NBTIO.write(tag, new FileOutputStream(file), endianness);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void write(CompoundTag tag, OutputStream outputStream) {
        NBTIO.write(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        NBTIO.write(tag, outputStream, endianness, false);
    }

    public static void write(CompoundTag tag, OutputStream outputStream, ByteOrder endianness, boolean network) {
        try {
            try (NBTOutputStream stream = new NBTOutputStream(outputStream, endianness, network);){
                Tag.writeNamedTag(tag, stream);
                outputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag) {
        return NBTIO.writeGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeGZIPCompressed(CompoundTag tag, ByteOrder endianness) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTIO.writeGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream) {
        NBTIO.writeGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        try {
            NBTIO.write(tag, new GZIPOutputStream(outputStream), endianness);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag) {
        return NBTIO.writeNetworkGZIPCompressed(tag, ByteOrder.BIG_ENDIAN);
    }

    public static byte[] writeNetworkGZIPCompressed(CompoundTag tag, ByteOrder endianness) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTIO.writeNetworkGZIPCompressed(tag, baos, endianness);
        return baos.toByteArray();
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream) {
        NBTIO.writeNetworkGZIPCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeNetworkGZIPCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        try {
            NBTIO.write(tag, new GZIPOutputStream(outputStream), endianness, true);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream) {
        NBTIO.writeZLIBCompressed(tag, outputStream, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, ByteOrder endianness) {
        NBTIO.writeZLIBCompressed(tag, outputStream, -1, endianness);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level) {
        NBTIO.writeZLIBCompressed(tag, outputStream, level, ByteOrder.BIG_ENDIAN);
    }

    public static void writeZLIBCompressed(CompoundTag tag, OutputStream outputStream, int level, ByteOrder endianness) {
        NBTIO.write(tag, new DeflaterOutputStream(outputStream, new Deflater(level)), endianness);
    }

    public static void safeWrite(CompoundTag tag, File file) {
        try {
            File tmpFile = new File(file.getAbsolutePath() + "_tmp");
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            NBTIO.write(tag, tmpFile);
            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }
}

