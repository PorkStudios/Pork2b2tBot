/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.internal.ConstantTimeUtils;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class PlatformDependent0 {
    private static final InternalLogger logger;
    private static final long ADDRESS_FIELD_OFFSET;
    private static final long BYTE_ARRAY_BASE_OFFSET;
    private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;
    private static final boolean IS_EXPLICIT_NO_UNSAFE;
    private static final Method ALLOCATE_ARRAY_METHOD;
    private static final int JAVA_VERSION;
    private static final boolean IS_ANDROID;
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
    private static final Object INTERNAL_UNSAFE;
    static final Unsafe UNSAFE;
    static final int HASH_CODE_ASCII_SEED = -1028477387;
    static final int HASH_CODE_C1 = -862048943;
    static final int HASH_CODE_C2 = 461845907;
    private static final long UNSAFE_COPY_THRESHOLD = 0x100000L;
    private static final boolean UNALIGNED;

    static boolean isExplicitNoUnsafe() {
        return IS_EXPLICIT_NO_UNSAFE;
    }

    private static boolean explicitNoUnsafe0() {
        boolean noUnsafe = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
        logger.debug("-Dio.netty.noUnsafe: {}", (Object)noUnsafe);
        if (noUnsafe) {
            logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
            return true;
        }
        boolean tryUnsafe = SystemPropertyUtil.contains("io.netty.tryUnsafe") ? SystemPropertyUtil.getBoolean("io.netty.tryUnsafe", true) : SystemPropertyUtil.getBoolean("org.jboss.netty.tryUnsafe", true);
        if (!tryUnsafe) {
            logger.debug("sun.misc.Unsafe: unavailable (io.netty.tryUnsafe/org.jboss.netty.tryUnsafe)");
            return true;
        }
        return false;
    }

    static boolean isUnaligned() {
        return UNALIGNED;
    }

    static boolean hasUnsafe() {
        return UNSAFE != null;
    }

    static Throwable getUnsafeUnavailabilityCause() {
        return UNSAFE_UNAVAILABILITY_CAUSE;
    }

    static boolean unalignedAccess() {
        return UNALIGNED;
    }

    static void throwException(Throwable cause) {
        UNSAFE.throwException(ObjectUtil.checkNotNull(cause, "cause"));
    }

    static boolean hasDirectBufferNoCleanerConstructor() {
        return DIRECT_BUFFER_CONSTRUCTOR != null;
    }

    static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        return PlatformDependent0.newDirectBuffer(UNSAFE.reallocateMemory(PlatformDependent0.directBufferAddress(buffer), capacity), capacity);
    }

    static ByteBuffer allocateDirectNoCleaner(int capacity) {
        return PlatformDependent0.newDirectBuffer(UNSAFE.allocateMemory(capacity), capacity);
    }

    static boolean hasAllocateArrayMethod() {
        return ALLOCATE_ARRAY_METHOD != null;
    }

    static byte[] allocateUninitializedArray(int size) {
        try {
            return (byte[])ALLOCATE_ARRAY_METHOD.invoke(INTERNAL_UNSAFE, Byte.TYPE, size);
        }
        catch (IllegalAccessException e) {
            throw new Error(e);
        }
        catch (InvocationTargetException e) {
            throw new Error(e);
        }
    }

    static ByteBuffer newDirectBuffer(long address, int capacity) {
        ObjectUtil.checkPositiveOrZero(capacity, "capacity");
        try {
            return (ByteBuffer)DIRECT_BUFFER_CONSTRUCTOR.newInstance(address, capacity);
        }
        catch (Throwable cause) {
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            throw new Error(cause);
        }
    }

    static long directBufferAddress(ByteBuffer buffer) {
        return PlatformDependent0.getLong(buffer, ADDRESS_FIELD_OFFSET);
    }

    static long byteArrayBaseOffset() {
        return BYTE_ARRAY_BASE_OFFSET;
    }

    static Object getObject(Object object, long fieldOffset) {
        return UNSAFE.getObject(object, fieldOffset);
    }

    static int getInt(Object object, long fieldOffset) {
        return UNSAFE.getInt(object, fieldOffset);
    }

    private static long getLong(Object object, long fieldOffset) {
        return UNSAFE.getLong(object, fieldOffset);
    }

    static long objectFieldOffset(Field field) {
        return UNSAFE.objectFieldOffset(field);
    }

    static byte getByte(long address) {
        return UNSAFE.getByte(address);
    }

    static short getShort(long address) {
        return UNSAFE.getShort(address);
    }

    static int getInt(long address) {
        return UNSAFE.getInt(address);
    }

    static long getLong(long address) {
        return UNSAFE.getLong(address);
    }

    static byte getByte(byte[] data, int index) {
        return UNSAFE.getByte((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index);
    }

    static short getShort(byte[] data, int index) {
        return UNSAFE.getShort((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index);
    }

    static int getInt(byte[] data, int index) {
        return UNSAFE.getInt((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index);
    }

    static long getLong(byte[] data, int index) {
        return UNSAFE.getLong((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index);
    }

    static void putByte(long address, byte value) {
        UNSAFE.putByte(address, value);
    }

    static void putShort(long address, short value) {
        UNSAFE.putShort(address, value);
    }

    static void putInt(long address, int value) {
        UNSAFE.putInt(address, value);
    }

    static void putLong(long address, long value) {
        UNSAFE.putLong(address, value);
    }

    static void putByte(byte[] data, int index, byte value) {
        UNSAFE.putByte((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index, value);
    }

    static void putShort(byte[] data, int index, short value) {
        UNSAFE.putShort((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index, value);
    }

    static void putInt(byte[] data, int index, int value) {
        UNSAFE.putInt((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index, value);
    }

    static void putLong(byte[] data, int index, long value) {
        UNSAFE.putLong((Object)data, BYTE_ARRAY_BASE_OFFSET + (long)index, value);
    }

    static void copyMemory(long srcAddr, long dstAddr, long length) {
        while (length > 0L) {
            long size = Math.min(length, 0x100000L);
            UNSAFE.copyMemory(srcAddr, dstAddr, size);
            length -= size;
            srcAddr += size;
            dstAddr += size;
        }
    }

    static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        while (length > 0L) {
            long size = Math.min(length, 0x100000L);
            UNSAFE.copyMemory(src, srcOffset, dst, dstOffset, size);
            length -= size;
            srcOffset += size;
            dstOffset += size;
        }
    }

    static void setMemory(long address, long bytes, byte value) {
        UNSAFE.setMemory(address, bytes, value);
    }

    static void setMemory(Object o, long offset, long bytes, byte value) {
        UNSAFE.setMemory(o, offset, bytes, value);
    }

    static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        if (length <= 0) {
            return true;
        }
        long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + (long)startPos1;
        long baseOffset2 = BYTE_ARRAY_BASE_OFFSET + (long)startPos2;
        int remainingBytes = length & 7;
        long end = baseOffset1 + (long)remainingBytes;
        long i = baseOffset1 - 8L + (long)length;
        long j = baseOffset2 - 8L + (long)length;
        while (i >= end) {
            if (UNSAFE.getLong((Object)bytes1, i) != UNSAFE.getLong((Object)bytes2, j)) {
                return false;
            }
            i -= 8L;
            j -= 8L;
        }
        if (remainingBytes >= 4 && UNSAFE.getInt((Object)bytes1, baseOffset1 + (long)(remainingBytes -= 4)) != UNSAFE.getInt((Object)bytes2, baseOffset2 + (long)remainingBytes)) {
            return false;
        }
        if (remainingBytes >= 2) {
            return UNSAFE.getChar((Object)bytes1, baseOffset1) == UNSAFE.getChar((Object)bytes2, baseOffset2) && (remainingBytes == 2 || bytes1[startPos1 + 2] == bytes2[startPos2 + 2]);
        }
        return bytes1[startPos1] == bytes2[startPos2];
    }

    static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        long result = 0L;
        long baseOffset1 = BYTE_ARRAY_BASE_OFFSET + (long)startPos1;
        long baseOffset2 = BYTE_ARRAY_BASE_OFFSET + (long)startPos2;
        int remainingBytes = length & 7;
        long end = baseOffset1 + (long)remainingBytes;
        long i = baseOffset1 - 8L + (long)length;
        long j = baseOffset2 - 8L + (long)length;
        while (i >= end) {
            result |= UNSAFE.getLong((Object)bytes1, i) ^ UNSAFE.getLong((Object)bytes2, j);
            i -= 8L;
            j -= 8L;
        }
        switch (remainingBytes) {
            case 7: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getInt((Object)bytes1, baseOffset1 + 3L) ^ UNSAFE.getInt((Object)bytes2, baseOffset2 + 3L)) | (long)(UNSAFE.getChar((Object)bytes1, baseOffset1 + 1L) ^ UNSAFE.getChar((Object)bytes2, baseOffset2 + 1L)) | (long)(UNSAFE.getByte((Object)bytes1, baseOffset1) ^ UNSAFE.getByte((Object)bytes2, baseOffset2)), 0L);
            }
            case 6: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getInt((Object)bytes1, baseOffset1 + 2L) ^ UNSAFE.getInt((Object)bytes2, baseOffset2 + 2L)) | (long)(UNSAFE.getChar((Object)bytes1, baseOffset1) ^ UNSAFE.getChar((Object)bytes2, baseOffset2)), 0L);
            }
            case 5: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getInt((Object)bytes1, baseOffset1 + 1L) ^ UNSAFE.getInt((Object)bytes2, baseOffset2 + 1L)) | (long)(UNSAFE.getByte((Object)bytes1, baseOffset1) ^ UNSAFE.getByte((Object)bytes2, baseOffset2)), 0L);
            }
            case 4: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getInt((Object)bytes1, baseOffset1) ^ UNSAFE.getInt((Object)bytes2, baseOffset2)), 0L);
            }
            case 3: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getChar((Object)bytes1, baseOffset1 + 1L) ^ UNSAFE.getChar((Object)bytes2, baseOffset2 + 1L)) | (long)(UNSAFE.getByte((Object)bytes1, baseOffset1) ^ UNSAFE.getByte((Object)bytes2, baseOffset2)), 0L);
            }
            case 2: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getChar((Object)bytes1, baseOffset1) ^ UNSAFE.getChar((Object)bytes2, baseOffset2)), 0L);
            }
            case 1: {
                return ConstantTimeUtils.equalsConstantTime(result | (long)(UNSAFE.getByte((Object)bytes1, baseOffset1) ^ UNSAFE.getByte((Object)bytes2, baseOffset2)), 0L);
            }
        }
        return ConstantTimeUtils.equalsConstantTime(result, 0L);
    }

    static boolean isZero(byte[] bytes, int startPos, int length) {
        if (length <= 0) {
            return true;
        }
        long baseOffset = BYTE_ARRAY_BASE_OFFSET + (long)startPos;
        int remainingBytes = length & 7;
        long end = baseOffset + (long)remainingBytes;
        for (long i = baseOffset - 8L + (long)length; i >= end; i -= 8L) {
            if (UNSAFE.getLong((Object)bytes, i) == 0L) continue;
            return false;
        }
        if (remainingBytes >= 4 && UNSAFE.getInt((Object)bytes, baseOffset + (long)(remainingBytes -= 4)) != 0) {
            return false;
        }
        if (remainingBytes >= 2) {
            return UNSAFE.getChar((Object)bytes, baseOffset) == '\u0000' && (remainingBytes == 2 || bytes[startPos + 2] == 0);
        }
        return bytes[startPos] == 0;
    }

    static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        int hash = -1028477387;
        long baseOffset = BYTE_ARRAY_BASE_OFFSET + (long)startPos;
        int remainingBytes = length & 7;
        long end = baseOffset + (long)remainingBytes;
        for (long i = baseOffset - 8L + (long)length; i >= end; i -= 8L) {
            hash = PlatformDependent0.hashCodeAsciiCompute(UNSAFE.getLong((Object)bytes, i), hash);
        }
        switch (remainingBytes) {
            case 7: {
                return ((hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getByte((Object)bytes, baseOffset))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getShort((Object)bytes, baseOffset + 1L))) * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getInt((Object)bytes, baseOffset + 3L));
            }
            case 6: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getShort((Object)bytes, baseOffset))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getInt((Object)bytes, baseOffset + 2L));
            }
            case 5: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getByte((Object)bytes, baseOffset))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getInt((Object)bytes, baseOffset + 1L));
            }
            case 4: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getInt((Object)bytes, baseOffset));
            }
            case 3: {
                return (hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getByte((Object)bytes, baseOffset))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getShort((Object)bytes, baseOffset + 1L));
            }
            case 2: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getShort((Object)bytes, baseOffset));
            }
            case 1: {
                return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(UNSAFE.getByte((Object)bytes, baseOffset));
            }
        }
        return hash;
    }

    static int hashCodeAsciiCompute(long value, int hash) {
        return hash * -862048943 + PlatformDependent0.hashCodeAsciiSanitize((int)value) * 461845907 + (int)((value & 2242545357458243584L) >>> 32);
    }

    static int hashCodeAsciiSanitize(int value) {
        return value & 522133279;
    }

    static int hashCodeAsciiSanitize(short value) {
        return value & 7967;
    }

    static int hashCodeAsciiSanitize(byte value) {
        return value & 31;
    }

    static ClassLoader getClassLoader(final Class<?> clazz) {
        if (System.getSecurityManager() == null) {
            return clazz.getClassLoader();
        }
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        });
    }

    static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }

    static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }

    static int addressSize() {
        return UNSAFE.addressSize();
    }

    static long allocateMemory(long size) {
        return UNSAFE.allocateMemory(size);
    }

    static void freeMemory(long address) {
        UNSAFE.freeMemory(address);
    }

    static long reallocateMemory(long address, long newSize) {
        return UNSAFE.reallocateMemory(address, newSize);
    }

    static boolean isAndroid() {
        return IS_ANDROID;
    }

    private static boolean isAndroid0() {
        boolean android;
        try {
            Class.forName("android.app.Application", false, PlatformDependent0.getSystemClassLoader());
            android = true;
        }
        catch (Throwable ignored) {
            android = false;
        }
        if (android) {
            logger.debug("Platform: Android");
        }
        return android;
    }

    static int javaVersion() {
        return JAVA_VERSION;
    }

    private static int javaVersion0() {
        int majorVersion = PlatformDependent0.isAndroid0() ? 6 : PlatformDependent0.majorVersionFromJavaSpecificationVersion();
        logger.debug("Java version: {}", (Object)majorVersion);
        return majorVersion;
    }

    static int majorVersionFromJavaSpecificationVersion() {
        return PlatformDependent0.majorVersion(SystemPropertyUtil.get("java.specification.version", "1.6"));
    }

    static int majorVersion(String javaSpecVersion) {
        String[] components = javaSpecVersion.split("\\.");
        int[] version = new int[components.length];
        for (int i = 0; i < components.length; ++i) {
            version[i] = Integer.parseInt(components[i]);
        }
        if (version[0] == 1) {
            assert (version[1] >= 6);
            return version[1];
        }
        return version[0];
    }

    private PlatformDependent0() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        ByteBuffer direct;
        Unsafe unsafe;
        logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
        IS_EXPLICIT_NO_UNSAFE = PlatformDependent0.explicitNoUnsafe0();
        JAVA_VERSION = PlatformDependent0.javaVersion0();
        IS_ANDROID = PlatformDependent0.isAndroid0();
        Field addressField = null;
        Method allocateArrayMethod = null;
        Throwable unsafeUnavailabilityCause = null;
        Object internalUnsafe = null;
        if (PlatformDependent0.isExplicitNoUnsafe()) {
            direct = null;
            addressField = null;
            unsafeUnavailabilityCause = new UnsupportedOperationException("Unsafe explicitly disabled");
            unsafe = null;
            internalUnsafe = null;
        } else {
            long byteArrayIndexScale;
            Unsafe finalUnsafe;
            direct = ByteBuffer.allocateDirect(1);
            Object maybeUnsafe = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                        Throwable cause = ReflectionUtil.trySetAccessible(unsafeField);
                        if (cause != null) {
                            return cause;
                        }
                        return unsafeField.get(null);
                    }
                    catch (NoSuchFieldException e) {
                        return e;
                    }
                    catch (SecurityException e) {
                        return e;
                    }
                    catch (IllegalAccessException e) {
                        return e;
                    }
                    catch (NoClassDefFoundError e) {
                        return e;
                    }
                }
            });
            if (maybeUnsafe instanceof Throwable) {
                unsafe = null;
                unsafeUnavailabilityCause = (Throwable)maybeUnsafe;
                logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Throwable)maybeUnsafe);
            } else {
                unsafe = (Unsafe)maybeUnsafe;
                logger.debug("sun.misc.Unsafe.theUnsafe: available");
            }
            if (unsafe != null) {
                finalUnsafe = unsafe;
                Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        try {
                            finalUnsafe.getClass().getDeclaredMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                            return null;
                        }
                        catch (NoSuchMethodException e) {
                            return e;
                        }
                        catch (SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeException == null) {
                    logger.debug("sun.misc.Unsafe.copyMemory: available");
                } else {
                    unsafe = null;
                    unsafeUnavailabilityCause = (Throwable)maybeException;
                    logger.debug("sun.misc.Unsafe.copyMemory: unavailable", (Throwable)maybeException);
                }
            }
            if (unsafe != null) {
                finalUnsafe = unsafe;
                Object maybeAddressField = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        try {
                            Field field = Buffer.class.getDeclaredField("address");
                            long offset = finalUnsafe.objectFieldOffset(field);
                            long address = finalUnsafe.getLong((Object)direct, offset);
                            if (address == 0L) {
                                return null;
                            }
                            return field;
                        }
                        catch (NoSuchFieldException e) {
                            return e;
                        }
                        catch (SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeAddressField instanceof Field) {
                    addressField = (Field)maybeAddressField;
                    logger.debug("java.nio.Buffer.address: available");
                } else {
                    unsafeUnavailabilityCause = (Throwable)maybeAddressField;
                    logger.debug("java.nio.Buffer.address: unavailable", (Throwable)maybeAddressField);
                    unsafe = null;
                }
            }
            if (unsafe != null && (byteArrayIndexScale = (long)unsafe.arrayIndexScale(byte[].class)) != 1L) {
                logger.debug("unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", (Object)byteArrayIndexScale);
                unsafeUnavailabilityCause = new UnsupportedOperationException("Unexpected unsafe.arrayIndexScale");
                unsafe = null;
            }
        }
        UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause;
        UNSAFE = unsafe;
        if (unsafe == null) {
            ADDRESS_FIELD_OFFSET = -1L;
            BYTE_ARRAY_BASE_OFFSET = -1L;
            UNALIGNED = false;
            DIRECT_BUFFER_CONSTRUCTOR = null;
            ALLOCATE_ARRAY_METHOD = null;
        } else {
            boolean unaligned;
            Constructor directBufferConstructor;
            long address = -1L;
            try {
                Object maybeDirectBufferConstructor = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        try {
                            Constructor<?> constructor = direct.getClass().getDeclaredConstructor(Long.TYPE, Integer.TYPE);
                            Throwable cause = ReflectionUtil.trySetAccessible(constructor);
                            if (cause != null) {
                                return cause;
                            }
                            return constructor;
                        }
                        catch (NoSuchMethodException e) {
                            return e;
                        }
                        catch (SecurityException e) {
                            return e;
                        }
                    }
                });
                if (maybeDirectBufferConstructor instanceof Constructor) {
                    address = UNSAFE.allocateMemory(1L);
                    try {
                        ((Constructor)maybeDirectBufferConstructor).newInstance(address, 1);
                        directBufferConstructor = (Constructor)maybeDirectBufferConstructor;
                        logger.debug("direct buffer constructor: available");
                    }
                    catch (InstantiationException e) {
                        directBufferConstructor = null;
                    }
                    catch (IllegalAccessException e) {
                        directBufferConstructor = null;
                    }
                    catch (InvocationTargetException e) {
                        directBufferConstructor = null;
                    }
                } else {
                    logger.debug("direct buffer constructor: unavailable", (Throwable)maybeDirectBufferConstructor);
                    directBufferConstructor = null;
                }
            }
            finally {
                if (address != -1L) {
                    UNSAFE.freeMemory(address);
                }
            }
            DIRECT_BUFFER_CONSTRUCTOR = directBufferConstructor;
            ADDRESS_FIELD_OFFSET = PlatformDependent0.objectFieldOffset(addressField);
            BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
            Object maybeUnaligned = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        Class<?> bitsClass = Class.forName("java.nio.Bits", false, PlatformDependent0.getSystemClassLoader());
                        Method unalignedMethod = bitsClass.getDeclaredMethod("unaligned", new Class[0]);
                        Throwable cause = ReflectionUtil.trySetAccessible(unalignedMethod);
                        if (cause != null) {
                            return cause;
                        }
                        return unalignedMethod.invoke(null, new Object[0]);
                    }
                    catch (NoSuchMethodException e) {
                        return e;
                    }
                    catch (SecurityException e) {
                        return e;
                    }
                    catch (IllegalAccessException e) {
                        return e;
                    }
                    catch (ClassNotFoundException e) {
                        return e;
                    }
                    catch (InvocationTargetException e) {
                        return e;
                    }
                }
            });
            if (maybeUnaligned instanceof Boolean) {
                unaligned = (Boolean)maybeUnaligned;
                logger.debug("java.nio.Bits.unaligned: available, {}", (Object)unaligned);
            } else {
                String arch = SystemPropertyUtil.get("os.arch", "");
                unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
                Throwable t = (Throwable)maybeUnaligned;
                logger.debug("java.nio.Bits.unaligned: unavailable {}", (Object)unaligned, (Object)t);
            }
            UNALIGNED = unaligned;
            if (PlatformDependent0.javaVersion() >= 9) {
                Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                    @Override
                    public Object run() {
                        try {
                            Class<?> internalUnsafeClass = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass("jdk.internal.misc.Unsafe");
                            Method method = internalUnsafeClass.getDeclaredMethod("getUnsafe", new Class[0]);
                            return method.invoke(null, new Object[0]);
                        }
                        catch (Throwable e) {
                            return e;
                        }
                    }
                });
                if (!(maybeException instanceof Throwable)) {
                    final Object finalInternalUnsafe = internalUnsafe = maybeException;
                    maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

                        @Override
                        public Object run() {
                            try {
                                return finalInternalUnsafe.getClass().getDeclaredMethod("allocateUninitializedArray", Class.class, Integer.TYPE);
                            }
                            catch (NoSuchMethodException e) {
                                return e;
                            }
                            catch (SecurityException e) {
                                return e;
                            }
                        }
                    });
                    if (maybeException instanceof Method) {
                        try {
                            Method m = (Method)maybeException;
                            byte[] bytes = (byte[])m.invoke(finalInternalUnsafe, Byte.TYPE, 8);
                            assert (bytes.length == 8);
                            allocateArrayMethod = m;
                        }
                        catch (IllegalAccessException e) {
                            maybeException = e;
                        }
                        catch (InvocationTargetException e) {
                            maybeException = e;
                        }
                    }
                }
                if (maybeException instanceof Throwable) {
                    logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)maybeException);
                } else {
                    logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
                }
            } else {
                logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
            }
            ALLOCATE_ARRAY_METHOD = allocateArrayMethod;
        }
        INTERNAL_UNSAFE = internalUnsafe;
        logger.debug("java.nio.DirectByteBuffer.<init>(long, int): {}", (Object)(DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable"));
    }

}

