/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.plain;

import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.UserAlreadyExistsException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class PasswordFile {
    private static String DEFAULT_FILE = System.getProperty("gnu.crypto.sasl.plain.password.file", "/etc/tpasswd");
    private Hashtable entries;
    private File passwdFile;
    private long lastmod;

    public synchronized void add(String user, String passwd, String[] attributes) throws IOException {
        this.checkCurrent();
        if (this.entries.containsKey(user)) {
            throw new UserAlreadyExistsException(user);
        }
        if (attributes.length != 5) {
            throw new IllegalArgumentException("Wrong number of attributes");
        }
        String[] fields = new String[7];
        fields[0] = user;
        fields[1] = passwd;
        System.arraycopy(attributes, 0, fields, 2, 5);
        this.entries.put(user, fields);
        this.savePasswd();
    }

    public synchronized void changePasswd(String user, String passwd) throws IOException {
        this.checkCurrent();
        if (!this.entries.containsKey(user)) {
            throw new NoSuchUserException(user);
        }
        String[] fields = (String[])this.entries.get(user);
        fields[1] = passwd;
        this.entries.remove(user);
        this.entries.put(user, fields);
        this.savePasswd();
    }

    public synchronized String[] lookup(String user) throws IOException {
        this.checkCurrent();
        if (!this.entries.containsKey(user)) {
            throw new NoSuchUserException(user);
        }
        return (String[])this.entries.get(user);
    }

    public synchronized boolean contains(String s) throws IOException {
        this.checkCurrent();
        return this.entries.containsKey(s);
    }

    private final synchronized void update() throws IOException {
        this.lastmod = this.passwdFile.lastModified();
        this.readPasswd(new FileInputStream(this.passwdFile));
    }

    private final void checkCurrent() throws IOException {
        if (this.passwdFile.lastModified() > this.lastmod) {
            this.update();
        }
    }

    private final synchronized void readPasswd(InputStream in) throws IOException {
        String line;
        BufferedReader din = new BufferedReader(new InputStreamReader(in));
        this.entries = new Hashtable();
        String[] fields = new String[7];
        while ((line = din.readLine()) != null) {
            block13 : {
                StringTokenizer st = new StringTokenizer(line, ":", true);
                try {
                    fields[0] = st.nextToken();
                    st.nextToken();
                    fields[1] = st.nextToken();
                    if (fields[1].equals(":")) {
                        fields[1] = "";
                    } else {
                        st.nextToken();
                    }
                    fields[2] = st.nextToken();
                    if (fields[2].equals(":")) {
                        fields[2] = "";
                    } else {
                        st.nextToken();
                    }
                    fields[3] = st.nextToken();
                    if (fields[3].equals(":")) {
                        fields[3] = "";
                    } else {
                        st.nextToken();
                    }
                    fields[4] = st.nextToken();
                    if (fields[4].equals(":")) {
                        fields[4] = "";
                    } else {
                        st.nextToken();
                    }
                    fields[5] = st.nextToken();
                    if (fields[5].equals(":")) {
                        fields[5] = "";
                    } else {
                        st.nextToken();
                    }
                    fields[6] = st.nextToken();
                    if (!fields[6].equals(":")) break block13;
                    fields[6] = "";
                }
                catch (NoSuchElementException ignored) {
                    continue;
                }
            }
            this.entries.put(fields[0], fields);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final synchronized void savePasswd() throws IOException {
        FileOutputStream fos;
        block15 : {
            if (this.passwdFile == null) return;
            fos = new FileOutputStream(this.passwdFile);
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(fos);
                Enumeration keys = this.entries.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    String[] fields = (String[])this.entries.get(key);
                    StringBuffer sb = new StringBuffer(fields[0]);
                    int i = 1;
                    while (i < fields.length) {
                        sb.append(":" + fields[i]);
                        ++i;
                    }
                    pw.println(sb.toString());
                }
                Object var4_10 = null;
                if (pw == null) break block15;
            }
            catch (Throwable throwable) {
                Object var4_9 = null;
                if (pw != null) {
                    try {
                        pw.flush();
                        Object var6_14 = null;
                        pw.close();
                    }
                    catch (Throwable throwable2) {
                        Object var6_13 = null;
                        pw.close();
                        throw throwable2;
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    }
                    catch (IOException iOException) {}
                }
                this.lastmod = this.passwdFile.lastModified();
                throw throwable;
            }
            try {}
            catch (Throwable throwable) {
                Object var6_15 = null;
                pw.close();
                throw throwable;
            }
            pw.flush();
            Object var6_16 = null;
            pw.close();
        }
        if (fos != null) {
            try {}
            catch (IOException iOException) {}
            fos.close();
        }
        this.lastmod = this.passwdFile.lastModified();
    }

    public PasswordFile() throws IOException {
        this(DEFAULT_FILE);
    }

    public PasswordFile(File pwFile) throws IOException {
        this(pwFile.getAbsolutePath());
    }

    public PasswordFile(String fileName) throws IOException {
        this.passwdFile = new File(fileName);
        this.update();
    }
}

