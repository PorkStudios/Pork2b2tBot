/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto;

import gnu.crypto.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
public final class Properties {
    private static final String NAME = "Properties";
    private static final boolean DEBUG = true;
    private static final PrintWriter err = new PrintWriter(System.out, true);
    public static final String PROPERTIES_FILE = "gnu.crypto.properties.file";
    public static final String REPRODUCIBLE_PRNG = "gnu.crypto.with.reproducible.prng";
    public static final String CHECK_WEAK_KEYS = "gnu.crypto.with.check.for.weak.keys";
    public static final String DO_RSA_BLINDING = "gnu.crypto.with.rsa.blinding";
    private static final String TRUE = Boolean.TRUE.toString();
    private static final String FALSE = Boolean.FALSE.toString();
    private static final HashMap props = new HashMap();
    private static Properties singleton = null;
    private boolean reproducible;
    private boolean checkForWeakKeys;
    private boolean doRSABlinding;

    private static final void debug(String s) {
        err.println(">>> Properties: " + s);
    }

    public static final synchronized String getProperty(String key) {
        if (key == null) {
            return null;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "read"));
        }
        if ("".equals(key = key.trim().toLowerCase())) {
            return null;
        }
        return (String)props.get(key);
    }

    public static final synchronized void setProperty(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        if ("".equals(key = key.trim().toLowerCase())) {
            return;
        }
        if ("".equals(value = value.trim())) {
            return;
        }
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(key, "write"));
        }
        if (key.equals(REPRODUCIBLE_PRNG) && (value.equalsIgnoreCase(TRUE) || value.equalsIgnoreCase(FALSE))) {
            Properties.setReproducible(Boolean.valueOf(value));
        } else if (key.equals(CHECK_WEAK_KEYS) && (value.equalsIgnoreCase(TRUE) || value.equalsIgnoreCase(FALSE))) {
            Properties.setCheckForWeakKeys(Boolean.valueOf(value));
        } else if (key.equals(DO_RSA_BLINDING) && (value.equalsIgnoreCase(TRUE) || value.equalsIgnoreCase(FALSE))) {
            Properties.setDoRSABlinding(Boolean.valueOf(value));
        } else {
            props.put(key, value);
        }
    }

    public static final synchronized boolean isReproducible() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(REPRODUCIBLE_PRNG, "read"));
        }
        return Properties.instance().reproducible;
    }

    public static final synchronized boolean checkForWeakKeys() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(CHECK_WEAK_KEYS, "read"));
        }
        return Properties.instance().checkForWeakKeys;
    }

    public static final synchronized boolean doRSABlinding() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(DO_RSA_BLINDING, "read"));
        }
        return Properties.instance().doRSABlinding;
    }

    public static final synchronized void setReproducible(boolean value) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(REPRODUCIBLE_PRNG, "write"));
        }
        Properties.instance().reproducible = value;
        props.put(REPRODUCIBLE_PRNG, String.valueOf(value));
    }

    public static final synchronized void setCheckForWeakKeys(boolean value) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(CHECK_WEAK_KEYS, "write"));
        }
        Properties.instance().checkForWeakKeys = value;
        props.put(CHECK_WEAK_KEYS, String.valueOf(value));
    }

    public static final synchronized void setDoRSABlinding(boolean value) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PropertyPermission(DO_RSA_BLINDING, "write"));
        }
        Properties.instance().doRSABlinding = value;
        props.put(DO_RSA_BLINDING, String.valueOf(value));
    }

    private static final synchronized Properties instance() {
        if (singleton == null) {
            singleton = new Properties();
        }
        return singleton;
    }

    private final void init() {
        props.put(REPRODUCIBLE_PRNG, new Boolean(this.reproducible).toString());
        props.put(CHECK_WEAK_KEYS, new Boolean(this.checkForWeakKeys).toString());
        props.put(DO_RSA_BLINDING, new Boolean(this.doRSABlinding).toString());
        String propFile = null;
        try {
            propFile = (String)AccessController.doPrivileged(new PrivilegedAction(this){
                final /* synthetic */ Properties this$0;

                public final Object run() {
                    return System.getProperty("gnu.crypto.properties.file");
                }
                {
                    this.this$0 = properties;
                }
            });
        }
        catch (SecurityException se) {
            Properties.debug("Reading property gnu.crypto.properties.file not allowed. Ignored.");
        }
        if (propFile != null) {
            try {
                java.util.Properties temp = new java.util.Properties();
                FileInputStream fin = new FileInputStream(propFile);
                temp.load(fin);
                temp.list(System.out);
                props.putAll(temp);
            }
            catch (IOException ioe) {
                Properties.debug("IO error reading " + propFile + ": " + ioe.getMessage());
            }
            catch (SecurityException se) {
                Properties.debug("Security error reading " + propFile + ": " + se.getMessage());
            }
        }
        this.handleBooleanProperty(REPRODUCIBLE_PRNG);
        this.handleBooleanProperty(CHECK_WEAK_KEYS);
        this.handleBooleanProperty(DO_RSA_BLINDING);
        this.reproducible = new Boolean((String)props.get(REPRODUCIBLE_PRNG));
        this.checkForWeakKeys = new Boolean((String)props.get(CHECK_WEAK_KEYS));
        this.doRSABlinding = new Boolean((String)props.get(DO_RSA_BLINDING));
    }

    private final void handleBooleanProperty(String name) {
        String s = null;
        try {
            s = System.getProperty(name);
        }
        catch (SecurityException x) {
            Properties.debug("SecurityManager forbids reading system properties. Ignored");
        }
        if (s != null) {
            if ((s = s.trim().toLowerCase()).equals(TRUE) || s.equals(FALSE)) {
                Properties.debug("Setting " + name + " to '" + s + '\'');
                props.put(name, s);
            } else {
                Properties.debug("Invalid value for -D" + name + ": " + s + ". Ignored");
            }
        }
    }

    private final /* synthetic */ void this() {
        this.reproducible = false;
        this.checkForWeakKeys = true;
        this.doRSABlinding = true;
    }

    private Properties() {
        this.this();
        this.init();
    }
}

