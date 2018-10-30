/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static void writeFile(String fileName, String content) throws IOException {
        FileUtils.writeFile(fileName, (InputStream)new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        FileUtils.writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        FileUtils.writeFile(file, (InputStream)new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        int length;
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        while ((length = content.read(buffer)) != -1) {
            stream.write(buffer, 0, length);
        }
        stream.close();
        content.close();
    }

    public static String readFile(File file) throws IOException {
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return FileUtils.readFile(new FileInputStream(file));
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        return FileUtils.readFile(new FileInputStream(file));
    }

    public static String readFile(InputStream inputStream) throws IOException {
        return FileUtils.readFile(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static String readFile(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        StringBuilder stringBuilder = new StringBuilder();
        String temp = br.readLine();
        while (temp != null) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(temp);
            temp = br.readLine();
        }
        br.close();
        reader.close();
        return stringBuilder.toString();
    }
}

