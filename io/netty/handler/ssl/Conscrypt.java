/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.ConscryptAlpnSslEngine;
import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
    private static final Class<?> ENGINES_CLASS = Conscrypt.getEnginesClass();

    static boolean isAvailable() {
        return ENGINES_CLASS != null && PlatformDependent.javaVersion() >= 8;
    }

    static boolean isEngineSupported(SSLEngine engine) {
        return Conscrypt.isAvailable() && Conscrypt.isConscryptEngine(engine, ENGINES_CLASS);
    }

    private static Class<?> getEnginesClass() {
        try {
            Class<?> engineClass = Class.forName("org.conscrypt.Conscrypt$Engines", true, ConscryptAlpnSslEngine.class.getClassLoader());
            Conscrypt.getIsConscryptMethod(engineClass);
            return engineClass;
        }
        catch (Throwable ignore) {
            return null;
        }
    }

    private static boolean isConscryptEngine(SSLEngine engine, Class<?> enginesClass) {
        try {
            Method method = Conscrypt.getIsConscryptMethod(enginesClass);
            return (Boolean)method.invoke(null, engine);
        }
        catch (Throwable ignore) {
            return false;
        }
    }

    private static Method getIsConscryptMethod(Class<?> enginesClass) throws NoSuchMethodException {
        return enginesClass.getMethod("isConscrypt", SSLEngine.class);
    }

    private Conscrypt() {
    }
}

