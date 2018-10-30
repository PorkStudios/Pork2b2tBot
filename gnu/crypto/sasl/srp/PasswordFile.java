/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.key.srp6.SRPAlgorithm;
import gnu.crypto.sasl.NoSuchUserException;
import gnu.crypto.sasl.UserAlreadyExistsException;
import gnu.crypto.sasl.srp.SRP;
import gnu.crypto.sasl.srp.SRPRegistry;
import gnu.crypto.util.Util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public class PasswordFile {
    private static final String USER_FIELD = "user";
    private static final String VERIFIERS_FIELD = "verifier";
    private static final String SALT_FIELD = "salt";
    private static final String CONFIG_FIELD = "config";
    private static String DEFAULT_FILE = System.getProperty("gnu.crypto.sasl.srp.password.file", "/etc/tpasswd");
    private static final HashMap srps;
    private static final BigInteger[] Nsrp;
    private String confName;
    private String pwName;
    private String pw2Name;
    private File configFile;
    private File passwdFile;
    private File passwd2File;
    private long lastmodPasswdFile;
    private long lastmodPasswd2File;
    private HashMap entries;
    private HashMap configurations;

    private static final String nameToID(String mdName) {
        if ("sha".equalsIgnoreCase(mdName) || "sha1".equalsIgnoreCase(mdName) || "sha-160".equalsIgnoreCase(mdName)) {
            return "0";
        }
        if ("md5".equalsIgnoreCase(mdName)) {
            return "1";
        }
        if ("ripemd128".equalsIgnoreCase(mdName)) {
            return "2";
        }
        if ("ripemd160".equalsIgnoreCase(mdName)) {
            return "3";
        }
        if ("sha-256".equalsIgnoreCase(mdName)) {
            return "4";
        }
        if ("sha-384".equalsIgnoreCase(mdName)) {
            return "5";
        }
        if ("sha-512".equalsIgnoreCase(mdName)) {
            return "6";
        }
        return "0";
    }

    public synchronized boolean containsConfig(String index) throws IOException {
        this.checkCurrent();
        return this.configurations.containsKey(index);
    }

    public synchronized String[] lookupConfig(String index) throws IOException {
        this.checkCurrent();
        String[] result = null;
        if (this.configurations.containsKey(index)) {
            result = (String[])this.configurations.get(index);
        }
        return result;
    }

    public synchronized boolean contains(String user) throws IOException {
        this.checkCurrent();
        return this.entries.containsKey(user);
    }

    public synchronized void add(String user, String passwd, byte[] salt, String index) throws IOException {
        this.checkCurrent();
        if (this.entries.containsKey(user)) {
            throw new UserAlreadyExistsException(user);
        }
        HashMap<String, Object> fields = new HashMap<String, Object>(4);
        fields.put(USER_FIELD, user);
        fields.put(VERIFIERS_FIELD, this.newVerifiers(user, salt, passwd, index));
        fields.put(SALT_FIELD, Util.toBase64(salt));
        fields.put(CONFIG_FIELD, index);
        this.entries.put(user, fields);
        this.savePasswd();
    }

    public synchronized void changePasswd(String user, String passwd) throws IOException {
        byte[] salt;
        this.checkCurrent();
        if (!this.entries.containsKey(user)) {
            throw new NoSuchUserException(user);
        }
        HashMap fields = (HashMap)this.entries.get(user);
        try {
            salt = Util.fromBase64((String)fields.get(SALT_FIELD));
        }
        catch (NumberFormatException x) {
            throw new IOException("Password file corrupt");
        }
        String index = (String)fields.get(CONFIG_FIELD);
        fields.put(VERIFIERS_FIELD, this.newVerifiers(user, salt, passwd, index));
        this.entries.put(user, fields);
        this.savePasswd();
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public synchronized void savePasswd() throws IOException {
        block23 : {
            block22 : {
                block18 : {
                    f1 = new FileOutputStream(this.passwdFile);
                    f2 = new FileOutputStream(this.passwd2File);
                    pw1 = null;
                    pw2 = null;
                    try {
                        pw1 = new PrintWriter(f1, true);
                        pw2 = new PrintWriter(f2, true);
                        this.writePasswd(pw1, pw2);
                        var6_7 = null;
                        if (pw1 == null) break block18;
                    }
                    catch (Throwable var5_5) {
                        block21 : {
                            var6_6 = null;
                            if (pw1 != null) {
                                try {
                                    pw1.flush();
                                    var8_13 = null;
                                    pw1.close();
                                }
                                catch (Throwable var7_8) {
                                    var8_12 = null;
                                    pw1.close();
                                    throw var7_8;
                                }
                            }
                            if (pw2 == null) break block21;
                            try {
                                pw2.flush();
                                var8_13 = null;
                                pw2.close();
                                ** GOTO lbl-1000
                            }
                            catch (Throwable var7_9) {
                                var8_13 = null;
                                pw2.close();
                                throw var7_9;
                            }
                        }
                        try lbl-1000: // 2 sources:
                        {
                            f1.close();
                        }
                        catch (IOException v0) {}
                        try {
                            f2.close();
                            throw var5_5;
                        }
                        catch (IOException v1) {}
                        throw var5_5;
                    }
                    try {}
                    catch (Throwable var7_10) {
                        var8_14 = null;
                        pw1.close();
                        throw var7_10;
                    }
                    pw1.flush();
                    var8_15 = null;
                    pw1.close();
                }
                if (pw2 == null) break block22;
                try {}
                catch (Throwable var7_11) {
                    var8_15 = null;
                    pw2.close();
                    throw var7_11;
                }
                pw2.flush();
                var8_15 = null;
                pw2.close();
                break block23;
            }
            try {}
            catch (IOException v2) {}
        }
        f1.close();
        try {}
        catch (IOException v3) {}
        f2.close();
        this.lastmodPasswdFile = this.passwdFile.lastModified();
        this.lastmodPasswd2File = this.passwd2File.lastModified();
    }

    public synchronized String[] lookup(String user, String mdName) throws IOException {
        this.checkCurrent();
        if (!this.entries.containsKey(user)) {
            throw new NoSuchUserException(user);
        }
        HashMap fields = (HashMap)this.entries.get(user);
        HashMap verifiers = (HashMap)fields.get(VERIFIERS_FIELD);
        String salt = (String)fields.get(SALT_FIELD);
        String index = (String)fields.get(CONFIG_FIELD);
        String verifier = (String)verifiers.get(PasswordFile.nameToID(mdName));
        return new String[]{verifier, salt, index};
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private final synchronized void readOrCreateConf() throws IOException {
        this.configurations.clear();
        this.configFile = new File(this.confName);
        try {
            fis = new FileInputStream(this.configFile);
            this.readConf(fis);
            return;
        }
        catch (FileNotFoundException x) {
            g = Util.toBase64(Util.trim(new BigInteger("2")));
            i = 0;
            ** while (i < PasswordFile.Nsrp.length)
        }
lbl-1000: // 1 sources:
        {
            index = String.valueOf(i + 1);
            N = Util.toBase64(Util.trim(PasswordFile.Nsrp[i]));
            this.configurations.put(index, new String[]{N, g});
            ++i;
            continue;
        }
lbl16: // 1 sources:
        f0 = null;
        pw0 = null;
        try {
            f0 = new FileOutputStream(this.configFile);
            pw0 = new PrintWriter(f0, true);
            this.writeConf(pw0);
            var9_11 = null;
            if (pw0 != null) {
                pw0.close();
                return;
            }
        }
        catch (Throwable var8_9) {
            var9_10 = null;
            if (pw0 != null) {
                pw0.close();
                throw var8_9;
            }
            if (f0 == null) throw var8_9;
            f0.close();
            throw var8_9;
        }
        if (f0 == null) return;
        f0.close();
    }

    private final void readConf(InputStream in) throws IOException {
        String line;
        BufferedReader din = new BufferedReader(new InputStreamReader(in));
        while ((line = din.readLine()) != null) {
            String g;
            String N;
            String index;
            StringTokenizer st = new StringTokenizer(line, ":");
            try {
                index = st.nextToken();
                N = st.nextToken();
                g = st.nextToken();
            }
            catch (NoSuchElementException x) {
                throw new IOException("SRP password configuration file corrupt");
            }
            this.configurations.put(index, new String[]{N, g});
        }
    }

    private final void writeConf(PrintWriter pw) {
        Iterator it = this.configurations.keySet().iterator();
        while (it.hasNext()) {
            String ndx = (String)it.next();
            String[] mpi = (String[])this.configurations.get(ndx);
            StringBuffer sb = new StringBuffer(ndx).append(":").append(mpi[0]).append(":").append(mpi[1]);
            pw.println(sb.toString());
        }
    }

    private final HashMap newVerifiers(String user, byte[] s, String password, String index) throws UnsupportedEncodingException {
        String[] mpi = (String[])this.configurations.get(index);
        BigInteger N = new BigInteger(1, Util.fromBase64(mpi[0]));
        BigInteger g = new BigInteger(1, Util.fromBase64(mpi[1]));
        HashMap<String, String> result = new HashMap<String, String>(srps.size());
        int i = 0;
        while (i < srps.size()) {
            String digestID = String.valueOf(i);
            SRP srp = (SRP)srps.get(digestID);
            BigInteger x = new BigInteger(1, srp.computeX(s, user, password));
            BigInteger v = g.modPow(x, N);
            String verifier = Util.toBase64(v.toByteArray());
            result.put(digestID, verifier);
            ++i;
        }
        return result;
    }

    private final synchronized void update() throws IOException {
        FileInputStream fis;
        this.entries.clear();
        this.passwdFile = new File(this.pwName);
        this.lastmodPasswdFile = this.passwdFile.lastModified();
        try {
            fis = new FileInputStream(this.passwdFile);
            this.readPasswd(fis);
        }
        catch (FileNotFoundException fileNotFoundException) {}
        this.passwd2File = new File(this.pw2Name);
        this.lastmodPasswd2File = this.passwd2File.lastModified();
        try {
            fis = new FileInputStream(this.passwd2File);
            this.readPasswd2(fis);
        }
        catch (FileNotFoundException fileNotFoundException) {}
    }

    private final void checkCurrent() throws IOException {
        if (this.passwdFile.lastModified() > this.lastmodPasswdFile || this.passwd2File.lastModified() > this.lastmodPasswd2File) {
            this.update();
        }
    }

    private final void readPasswd(InputStream in) throws IOException {
        String line;
        BufferedReader din = new BufferedReader(new InputStreamReader(in));
        while ((line = din.readLine()) != null) {
            String salt;
            String verifier;
            String index;
            String user;
            StringTokenizer st = new StringTokenizer(line, ":");
            try {
                user = st.nextToken();
                verifier = st.nextToken();
                salt = st.nextToken();
                index = st.nextToken();
            }
            catch (NoSuchElementException x) {
                throw new IOException("SRP base password file corrupt");
            }
            HashMap<String, String> verifiers = new HashMap<String, String>(6);
            verifiers.put("0", verifier);
            HashMap<String, Object> fields = new HashMap<String, Object>(4);
            fields.put(USER_FIELD, user);
            fields.put(VERIFIERS_FIELD, verifiers);
            fields.put(SALT_FIELD, salt);
            fields.put(CONFIG_FIELD, index);
            this.entries.put(user, fields);
        }
    }

    private final void readPasswd2(InputStream in) throws IOException {
        String line;
        BufferedReader din = new BufferedReader(new InputStreamReader(in));
        while ((line = din.readLine()) != null) {
            String digestID;
            String verifier;
            String user;
            StringTokenizer st = new StringTokenizer(line, ":");
            try {
                digestID = st.nextToken();
                user = st.nextToken();
                verifier = st.nextToken();
            }
            catch (NoSuchElementException x) {
                throw new IOException("SRP extended password file corrupt");
            }
            HashMap fields = (HashMap)this.entries.get(user);
            if (fields == null) continue;
            HashMap verifiers = (HashMap)fields.get(VERIFIERS_FIELD);
            verifiers.put(digestID, verifier);
        }
    }

    private final void writePasswd(PrintWriter pw1, PrintWriter pw2) throws IOException {
        Iterator i = this.entries.keySet().iterator();
        while (i.hasNext()) {
            HashMap fields;
            String user = (String)i.next();
            if (!user.equals((fields = (HashMap)this.entries.get(user)).get(USER_FIELD))) {
                throw new IOException("Inconsistent SRP password data");
            }
            HashMap verifiers = (HashMap)fields.get(VERIFIERS_FIELD);
            StringBuffer sb1 = new StringBuffer().append(user).append(":").append((String)verifiers.get("0")).append(":").append((String)fields.get(SALT_FIELD)).append(":").append((String)fields.get(CONFIG_FIELD));
            pw1.println(sb1.toString());
            Iterator j = verifiers.keySet().iterator();
            while (j.hasNext()) {
                String digestID = (String)j.next();
                if ("0".equals(digestID)) continue;
                StringBuffer sb2 = new StringBuffer().append(digestID).append(":").append(user).append(":").append((String)verifiers.get(digestID));
                pw2.println(sb2.toString());
            }
        }
    }

    private final /* synthetic */ void this() {
        this.entries = new HashMap();
        this.configurations = new HashMap();
    }

    public PasswordFile() throws IOException {
        this(DEFAULT_FILE);
    }

    public PasswordFile(File pwFile) throws IOException {
        this(pwFile.getAbsolutePath());
    }

    public PasswordFile(String pwName) throws IOException {
        this(pwName, pwName + '2', pwName + ".conf");
    }

    public PasswordFile(String pwName, String confName) throws IOException {
        this(pwName, pwName + '2', confName);
    }

    public PasswordFile(String pwName, String pw2Name, String confName) throws IOException {
        this.this();
        this.pwName = pwName;
        this.pw2Name = pw2Name;
        this.confName = confName;
        this.readOrCreateConf();
        this.update();
    }

    private static final {
        HashMap<String, SRP> map = new HashMap<String, SRP>(SRPRegistry.SRP_ALGORITHMS.length);
        map.put("0", SRP.instance(SRPRegistry.SRP_ALGORITHMS[0]));
        int i = 1;
        while (i < SRPRegistry.SRP_ALGORITHMS.length) {
            try {
                map.put(String.valueOf(i), SRP.instance(SRPRegistry.SRP_ALGORITHMS[i]));
            }
            catch (Exception x) {
                System.err.println("Ignored: " + x);
                x.printStackTrace(System.err);
            }
            ++i;
        }
        srps = map;
        Nsrp = new BigInteger[]{SRPAlgorithm.N_2048, SRPAlgorithm.N_1536, SRPAlgorithm.N_1280, SRPAlgorithm.N_1024, SRPAlgorithm.N_768, SRPAlgorithm.N_640, SRPAlgorithm.N_512};
    }
}

