/*
 * Decompiled with CFR 0_132.
 */
package gnu.crypto.sasl;

import gnu.crypto.sasl.AuthInfoProviderFactory;
import gnu.crypto.sasl.IAuthInfoProvider;
import gnu.crypto.sasl.IAuthInfoProviderFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class AuthInfo {
    private static final ArrayList factories = new ArrayList();

    public static IAuthInfoProvider getProvider(String mechanism) {
        Iterator it = factories.iterator();
        while (it.hasNext()) {
            IAuthInfoProviderFactory factory = (IAuthInfoProviderFactory)it.next();
            IAuthInfoProvider result = factory.getInstance(mechanism);
            if (result == null) continue;
            return result;
        }
        return null;
    }

    private AuthInfo() {
    }

    private static final {
        AuthInfoProviderFactory ours = new AuthInfoProviderFactory();
        String pkgs = System.getProperty("gnu.crypto.sasl.auth.info.provider.pkgs", null);
        if (pkgs != null) {
            StringTokenizer st = new StringTokenizer(pkgs, "|");
            while (st.hasMoreTokens()) {
                String clazz = st.nextToken();
                if ("gnu.crypto.sasl".equals(clazz)) continue;
                clazz = clazz + ".AuthInfoProviderFactory";
                try {
                    IAuthInfoProviderFactory factory = (IAuthInfoProviderFactory)Class.forName(clazz).newInstance();
                    factories.add(factory);
                }
                catch (ClassCastException classCastException) {
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
                catch (InstantiationException instantiationException) {
                }
                catch (IllegalAccessException illegalAccessException) {}
            }
        }
        if (!factories.contains(ours)) {
            factories.add(ours);
        }
    }
}

