/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl.srp;

import gnu.crypto.sasl.srp.SecurityContext;
import gnu.crypto.sasl.srp.StoreEntry;
import java.util.HashMap;

public class ServerStore {
    private static ServerStore singleton = null;
    private static final HashMap sid2ssc = new HashMap();
    private static final HashMap sid2ttl = new HashMap();
    private static final Object lock = new Object();
    private static int counter = 0;

    static final synchronized ServerStore instance() {
        if (singleton == null) {
            singleton = new ServerStore();
        }
        return singleton;
    }

    static final synchronized byte[] getNewSessionID() {
        String sid = String.valueOf(++counter);
        return ("SID-" + "0000000000".substring(0, 10 - sid.length()) + sid).getBytes();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    boolean isAlive(byte[] sid) {
        boolean result = false;
        if (sid == null || sid.length == 0) return result;
        {
            Object object = lock;
            synchronized (object) {
                String key = new String(sid);
                StoreEntry ctx = (StoreEntry)sid2ttl.get(key);
                if (ctx == null || (result = ctx.isAlive())) return result;
                {
                    sid2ssc.remove(key);
                    sid2ttl.remove(key);
                }
                return result;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void cacheSession(int ttl, SecurityContext ctx) {
        Object object = lock;
        synchronized (object) {
            String key = new String(ctx.getSID());
            sid2ssc.put(key, ctx);
            sid2ttl.put(key, new StoreEntry(ttl));
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    SecurityContext restoreSession(byte[] sid) {
        String key = new String(sid);
        Object object = lock;
        synchronized (object) {
            SecurityContext result = (SecurityContext)sid2ssc.remove(key);
            sid2ttl.remove(key);
            return result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void invalidateSession(byte[] sid) {
        String key = new String(sid);
        Object object = lock;
        synchronized (object) {
            sid2ssc.remove(key);
            sid2ttl.remove(key);
            return;
        }
    }

    private ServerStore() {
    }
}

