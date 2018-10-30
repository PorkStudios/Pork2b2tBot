/*
 * Decompiled with CFR 0_132.
 */
package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class UnsafeAllocator {
    public abstract <T> T newInstance(Class<T> var1) throws Exception;

    public static UnsafeAllocator create() {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field f = unsafeClass.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            final Object unsafe = f.get(null);
            final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);
            return new UnsafeAllocator(){

                @Override
                public <T> T newInstance(Class<T> c) throws Exception {
                    return (T)allocateInstance.invoke(unsafe, c);
                }
            };
        }
        catch (Exception ignored) {
            try {
                Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
                getConstructorId.setAccessible(true);
                final int constructorId = (Integer)getConstructorId.invoke(null, Object.class);
                final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
                newInstance.setAccessible(true);
                return new UnsafeAllocator(){

                    @Override
                    public <T> T newInstance(Class<T> c) throws Exception {
                        return (T)newInstance.invoke(null, c, constructorId);
                    }
                };
            }
            catch (Exception ignored2) {
                try {
                    final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
                    newInstance.setAccessible(true);
                    return new UnsafeAllocator(){

                        @Override
                        public <T> T newInstance(Class<T> c) throws Exception {
                            return (T)newInstance.invoke(null, c, Object.class);
                        }
                    };
                }
                catch (Exception ignored3) {
                    return new UnsafeAllocator(){

                        @Override
                        public <T> T newInstance(Class<T> c) {
                            throw new UnsupportedOperationException("Cannot allocate " + c);
                        }
                    };
                }
            }
        }
    }

}

