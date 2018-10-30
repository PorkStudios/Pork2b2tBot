/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class DataTag
implements Serializable {
    public static final File USER_FOLDER = new File(System.getProperty("user.dir"));
    public static final File HOME_FOLDER = new File(System.getProperty("user.home"));
    private static final long serialVersionUID = 1L;
    private final File file;
    private HashMap<String, Integer> ints;
    private HashMap<String, String> strings;
    private HashMap<String, Boolean> booleans;
    private HashMap<String, Byte> bytes;
    private HashMap<String, Float> floats;
    private HashMap<String, Short> shorts;
    private HashMap<String, Double> doubles;
    private HashMap<String, Long> longs;
    private HashMap<String, DataTag> tags;
    private HashMap<String, Serializable> objs;
    private HashMap<String, int[]> intArrays;
    private HashMap<String, String[]> stringArrays;
    private HashMap<String, boolean[]> booleanArrays;
    private HashMap<String, byte[]> byteArrays;
    private HashMap<String, float[]> floatArrays;
    private HashMap<String, short[]> shortArrays;
    private HashMap<String, double[]> doubleArrays;
    private HashMap<String, long[]> longArrays;
    private HashMap<String, Serializable[]> objArrays;

    public DataTag(File saveTo) {
        this.file = saveTo;
        this.set();
        this.init();
    }

    public DataTag(DataTag tag) {
        FileHelper.createFile(new File(tag.file.getParentFile(), tag.file.getName().replaceAll(".dat", "")).toString(), true);
        this.file = new File(tag.file.getParentFile(), tag.file.getName().replaceAll(".dat", "") + "/" + tag.file.getName().replaceAll(".dat", "") + " tag - " + tag.tags.size() + ".dat");
        this.set();
        this.init();
    }

    private void set() {
        this.ints = new HashMap();
        this.strings = new HashMap();
        this.booleans = new HashMap();
        this.bytes = new HashMap();
        this.floats = new HashMap();
        this.shorts = new HashMap();
        this.doubles = new HashMap();
        this.longs = new HashMap();
        this.tags = new HashMap();
        this.objs = new HashMap();
        this.intArrays = new HashMap();
        this.stringArrays = new HashMap();
        this.booleanArrays = new HashMap();
        this.byteArrays = new HashMap();
        this.floatArrays = new HashMap();
        this.shortArrays = new HashMap();
        this.doubleArrays = new HashMap();
        this.longArrays = new HashMap();
        this.objArrays = new HashMap();
    }

    public void init() {
        FileHelper.createFile(this.file.getPath());
        this.load();
    }

    private void check(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name Cannot be Null!", new NullPointerException());
        }
    }

    public int setInteger(String name, int value) {
        this.check(name);
        this.ints.put(name, value);
        return value;
    }

    public String setString(String name, String value) {
        this.check(name);
        this.strings.put(name, value);
        return value;
    }

    public boolean setBoolean(String name, boolean value) {
        this.check(name);
        this.booleans.put(name, value);
        return value;
    }

    public byte setByte(String name, byte value) {
        this.check(name);
        this.bytes.put(name, value);
        return value;
    }

    public float setFloat(String name, float value) {
        this.check(name);
        this.floats.put(name, Float.valueOf(value));
        return value;
    }

    public short setShort(String name, short value) {
        this.check(name);
        this.shorts.put(name, value);
        return value;
    }

    public double setDouble(String name, double value) {
        this.check(name);
        this.doubles.put(name, value);
        return value;
    }

    public long setLong(String name, long value) {
        this.check(name);
        this.longs.put(name, value);
        return value;
    }

    public DataTag setTag(String name, DataTag value) {
        this.check(name);
        this.tags.put(name, value);
        return value;
    }

    public Serializable setSerializable(String name, Serializable obj) {
        this.check(name);
        this.objs.put(name, obj);
        return obj;
    }

    public int[] setIntegerArray(String name, int[] value) {
        this.check(name);
        this.intArrays.put(name, value);
        return value;
    }

    public String[] setStringArray(String name, String[] value) {
        this.check(name);
        this.stringArrays.put(name, value);
        return value;
    }

    public boolean[] setBooleanArray(String name, boolean[] value) {
        this.check(name);
        this.booleanArrays.put(name, value);
        return value;
    }

    public byte[] setByteArray(String name, byte[] value) {
        this.check(name);
        this.byteArrays.put(name, value);
        return value;
    }

    public float[] setFloatArray(String name, float[] value) {
        this.check(name);
        this.floatArrays.put(name, value);
        return value;
    }

    public short[] setShortArray(String name, short[] value) {
        this.check(name);
        this.shortArrays.put(name, value);
        return value;
    }

    public double[] setDoubleArray(String name, double[] value) {
        this.check(name);
        this.doubleArrays.put(name, value);
        return value;
    }

    public long[] setLongArray(String name, long[] value) {
        this.check(name);
        this.longArrays.put(name, value);
        return value;
    }

    public Serializable[] setSerializableArray(String name, Serializable[] value) {
        this.check(name);
        this.objArrays.put(name, value);
        return value;
    }

    public int getInteger(String name, int def) {
        return this.ints.containsKey(name) ? this.ints.get(name).intValue() : this.setInteger(name, def);
    }

    public String getString(String name, String def) {
        return this.strings.containsKey(name) ? this.strings.get(name) : this.setString(name, def);
    }

    public boolean getBoolean(String name, boolean def) {
        return this.booleans.containsKey(name) ? this.booleans.get(name).booleanValue() : this.setBoolean(name, def);
    }

    public byte getByte(String name, byte def) {
        return this.bytes.containsKey(name) ? this.bytes.get(name).byteValue() : this.setByte(name, def);
    }

    public float getFloat(String name, float def) {
        return this.floats.containsKey(name) ? this.floats.get(name).floatValue() : this.setFloat(name, def);
    }

    public short getShort(String name, short def) {
        return this.shorts.containsKey(name) ? this.shorts.get(name).shortValue() : this.setShort(name, def);
    }

    public double getDouble(String name, double def) {
        return this.doubles.containsKey(name) ? this.doubles.get(name).doubleValue() : this.setDouble(name, def);
    }

    public long getLong(String name, long def) {
        return this.longs.containsKey(name) ? this.longs.get(name).longValue() : this.setLong(name, def);
    }

    public DataTag getTag(String name, DataTag def) {
        return this.tags.containsKey(name) ? this.tags.get(name).load() : this.setTag(name, def);
    }

    public Serializable getSerializable(String name, Serializable def) {
        return this.objs.containsKey(name) ? this.objs.get(name) : this.setSerializable(name, def);
    }

    public int[] getIntegerArray(String name, int[] def) {
        return this.intArrays.containsKey(name) ? this.intArrays.get(name) : this.setIntegerArray(name, def);
    }

    public String[] getStringArray(String name, String[] def) {
        return this.stringArrays.containsKey(name) ? this.stringArrays.get(name) : this.setStringArray(name, def);
    }

    public boolean[] getBooleanArray(String name, boolean[] def) {
        return this.booleanArrays.containsKey(name) ? this.booleanArrays.get(name) : this.setBooleanArray(name, def);
    }

    public byte[] getByteArray(String name, byte[] def) {
        return this.byteArrays.containsKey(name) ? this.byteArrays.get(name) : this.setByteArray(name, def);
    }

    public float[] getFloatArray(String name, float[] def) {
        return this.floatArrays.containsKey(name) ? this.floatArrays.get(name) : this.setFloatArray(name, def);
    }

    public short[] getShortArray(String name, short[] def) {
        return this.shortArrays.containsKey(name) ? this.shortArrays.get(name) : this.setShortArray(name, def);
    }

    public double[] getDoubleArray(String name, double[] def) {
        return this.doubleArrays.containsKey(name) ? this.doubleArrays.get(name) : this.setDoubleArray(name, def);
    }

    public long[] getLongArray(String name, long[] def) {
        return this.longArrays.containsKey(name) ? this.longArrays.get(name) : this.setLongArray(name, def);
    }

    public Serializable[] getSerializableArray(String name, Serializable[] def) {
        return this.objArrays.containsKey(name) ? this.objArrays.get(name) : this.setSerializableArray(name, def);
    }

    public int getInteger(String name) {
        return this.ints.containsKey(name) ? this.ints.get(name) : 0;
    }

    public String getString(String name) {
        return this.strings.containsKey(name) ? this.strings.get(name) : "";
    }

    public boolean getBoolean(String name) {
        return this.booleans.containsKey(name) ? this.booleans.get(name) : false;
    }

    public byte getByte(String name) {
        return this.bytes.containsKey(name) ? this.bytes.get(name) : (byte)0;
    }

    public float getFloat(String name) {
        return this.floats.containsKey(name) ? this.floats.get(name).floatValue() : 0.0f;
    }

    public short getShort(String name) {
        return this.shorts.containsKey(name) ? this.shorts.get(name) : (short)0;
    }

    public double getDouble(String name) {
        return this.doubles.containsKey(name) ? this.doubles.get(name) : 0.0;
    }

    public long getLong(String name) {
        return this.longs.containsKey(name) ? this.longs.get(name) : 0L;
    }

    public DataTag getTag(String name) {
        return this.tags.containsKey(name) ? this.tags.get(name).load() : new DataTag(this);
    }

    public Serializable getSerializable(String name) {
        return this.objs.containsKey(name) ? this.objs.get(name) : null;
    }

    public int[] getIntegerArray(String name) {
        return this.intArrays.containsKey(name) ? this.intArrays.get(name) : null;
    }

    public String[] getStringArray(String name) {
        return this.stringArrays.containsKey(name) ? this.stringArrays.get(name) : null;
    }

    public boolean[] getBooleanArray(String name) {
        return this.booleanArrays.containsKey(name) ? this.booleanArrays.get(name) : null;
    }

    public byte[] getByteArray(String name) {
        return this.byteArrays.containsKey(name) ? this.byteArrays.get(name) : null;
    }

    public float[] getFloatArray(String name) {
        return this.floatArrays.containsKey(name) ? this.floatArrays.get(name) : null;
    }

    public short[] getShortArray(String name) {
        return this.shortArrays.containsKey(name) ? this.shortArrays.get(name) : null;
    }

    public double[] getDoubleArray(String name) {
        return this.doubleArrays.containsKey(name) ? this.doubleArrays.get(name) : null;
    }

    public long[] getLongArray(String name) {
        return this.longArrays.containsKey(name) ? this.longArrays.get(name) : null;
    }

    public Serializable[] getSerializableArray(String name) {
        return this.objArrays.containsKey(name) ? this.objArrays.get(name) : null;
    }

    private DataTag load() {
        block2 : {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(this.file));
                DataTag obj = (DataTag)in.readObject();
                this.ints = obj.ints;
                this.strings = obj.strings;
                this.booleans = obj.booleans;
                this.bytes = obj.bytes;
                this.floats = obj.floats;
                this.shorts = obj.shorts;
                this.doubles = obj.doubles;
                this.longs = obj.longs;
                this.tags = obj.tags;
                this.objs = obj.objs;
                this.intArrays = obj.intArrays;
                this.stringArrays = obj.stringArrays;
                this.booleanArrays = obj.booleanArrays;
                this.byteArrays = obj.byteArrays;
                this.floatArrays = obj.floatArrays;
                this.shortArrays = obj.shortArrays;
                this.doubleArrays = obj.doubleArrays;
                this.longArrays = obj.longArrays;
                this.objArrays = obj.objArrays;
                in.close();
            }
            catch (Exception i) {
                if (i.getClass().equals(EOFException.class)) break block2;
                System.err.println("Exception: " + i.getClass().getName());
                i.printStackTrace();
            }
        }
        return this;
    }

    public DataTag save() {
        try {
            this.file.delete();
            this.file.createNewFile();
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(this.file));
            out.writeObject(this);
            out.close();
        }
        catch (IOException e) {
            System.err.println("Exception: " + e.getClass().getName());
            e.printStackTrace();
        }
        return this;
    }

    public static class FileHelper {
        public static boolean createFile(String dir) {
            return FileHelper.createFile(dir, false);
        }

        public static boolean createFile(String dir, boolean isDirectory) {
            boolean returning = false;
            Path p = Paths.get(dir, new String[0]);
            try {
                if (Files.exists(p, new LinkOption[0])) {
                    returning = true;
                } else if (isDirectory) {
                    Files.createDirectory(p, new FileAttribute[0]);
                    returning = true;
                } else {
                    Files.createFile(p, new FileAttribute[0]);
                    returning = true;
                }
            }
            catch (IOException e) {
                System.err.println("Error Creating File!");
                System.err.println("Path: " + dir);
                System.err.println("Directory: " + isDirectory);
                e.printStackTrace();
            }
            return returning;
        }

        public static boolean deleteFile(String fileName) {
            Path p = Paths.get(fileName, new String[0]);
            try {
                return Files.deleteIfExists(p);
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private static ArrayList<File> files(File dir) {
            ArrayList<File> files = new ArrayList<File>();
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("dir Isn't a Directory! " + dir);
            }
            for (int i = 0; i < dir.listFiles().length; ++i) {
                if (dir.listFiles()[i].isDirectory()) {
                    files.addAll(FileHelper.files(dir.listFiles()[i]));
                }
                files.add(dir.listFiles()[i]);
            }
            return files;
        }

        public static String[] getFileContents(String fileName) {
            ArrayList<String> lines = new ArrayList<String>();
            String line = "";
            BufferedReader reader = FileHelper.getFileReader(fileName);
            try {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return lines.toArray(new String[0]);
        }

        public static BufferedReader getFileReader(String fileName) {
            Charset c = Charset.forName("US-ASCII");
            Path p = Paths.get(fileName, new String[0]);
            try {
                return Files.newBufferedReader(p, c);
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static File[] getFilesInFolder(File dir) {
            return FileHelper.files(dir).toArray(new File[0]);
        }

        public static void printFileContents(String fileName) {
            String[] lines = FileHelper.getFileContents(fileName);
            for (int i = 0; i < lines.length; ++i) {
                System.out.println("Line[" + i + "]: " + lines[i]);
            }
        }

        public static Path resetFile(String fileName) {
            return FileHelper.resetFile(fileName, "");
        }

        public static Path resetFile(String fileName, String textToAdd) {
            Path p = Paths.get(fileName, new String[0]);
            FileHelper.deleteFile(fileName);
            FileHelper.createFile(fileName, false);
            FileHelper.writeToFile(fileName, textToAdd, false);
            return p;
        }

        public static boolean writeToFile(String fileName, String stuff) {
            return FileHelper.writeToFile(fileName, stuff, true);
        }

        public static boolean writeToFile(String fileName, String stuff, boolean newLine) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
                writer.write(stuff);
                if (newLine) {
                    writer.newLine();
                }
                writer.close();
                return true;
            }
            catch (IOException x) {
                System.err.format("IOException: %s%n", x);
                x.printStackTrace();
                return false;
            }
        }
    }

}

