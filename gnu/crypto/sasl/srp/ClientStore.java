/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.sasl.srp.SecurityContext;
import gnu.crypto.sasl.srp.StoreEntry;
import java.util.HashMap;

public class ClientStore {
    private static ClientStore singleton = null;
    private static final HashMap uid2ssc = new HashMap();
    private static final HashMap uid2ttl = new HashMap();
    private static final Object lock = new Object();

    static final synchronized ClientStore instance() {
        if (singleton == null) {
            singleton = new ClientStore();
        }
        return singleton;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    boolean isAlive(String uid) {
        Object object = lock;
        synchronized (object) {
            boolean result;
            StoreEntry sto;
            Object obj = uid2ssc.get(uid);
            boolean bl = false;
            if (obj != null) {
                bl = true;
            }
            if ((result = bl) && !(sto = (StoreEntry)uid2ttl.get(uid)).isAlive()) {
                uid2ssc.remove(uid);
                uid2ttl.remove(uid);
            }
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void cacheSession(String uid, int ttl, SecurityContext ctx) {
        Object object = lock;
        synchronized (object) {
            uid2ssc.put(uid, ctx);
            uid2ttl.put(uid, new StoreEntry(ttl));
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void invalidateSession(String uid) {
        Object object = lock;
        synchronized (object) {
            uid2ssc.remove(uid);
            uid2ttl.remove(uid);
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    SecurityContext restoreSession(String uid) {
        Object object = lock;
        synchronized (object) {
            SecurityContext result = (SecurityContext)uid2ssc.remove(uid);
            uid2ttl.remove(uid);
            return result;
        }
    }

    private ClientStore() {
    }
}

