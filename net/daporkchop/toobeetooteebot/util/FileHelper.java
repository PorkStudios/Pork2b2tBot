/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;

public class FileHelper {
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

