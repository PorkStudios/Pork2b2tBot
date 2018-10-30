/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThreadLocalRandom;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.util.internal.UnpaddedInternalThreadLocalMap;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class InternalThreadLocalMap
extends UnpaddedInternalThreadLocalMap {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(InternalThreadLocalMap.class);
    private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;
    private static final int STRING_BUILDER_INITIAL_SIZE;
    private static final int STRING_BUILDER_MAX_SIZE;
    public static final Object UNSET;
    public long rp1;
    public long rp2;
    public long rp3;
    public long rp4;
    public long rp5;
    public long rp6;
    public long rp7;
    public long rp8;
    public long rp9;

    public static InternalThreadLocalMap getIfSet() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            return ((FastThreadLocalThread)thread).threadLocalMap();
        }
        return (InternalThreadLocalMap)slowThreadLocalMap.get();
    }

    public static InternalThreadLocalMap get() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            return InternalThreadLocalMap.fastGet((FastThreadLocalThread)thread);
        }
        return InternalThreadLocalMap.slowGet();
    }

    private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
        InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
        if (threadLocalMap == null) {
            threadLocalMap = new InternalThreadLocalMap();
            thread.setThreadLocalMap(threadLocalMap);
        }
        return threadLocalMap;
    }

    private static InternalThreadLocalMap slowGet() {
        ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
        InternalThreadLocalMap ret = slowThreadLocalMap.get();
        if (ret == null) {
            ret = new InternalThreadLocalMap();
            slowThreadLocalMap.set(ret);
        }
        return ret;
    }

    public static void remove() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            ((FastThreadLocalThread)thread).setThreadLocalMap(null);
        } else {
            slowThreadLocalMap.remove();
        }
    }

    public static void destroy() {
        slowThreadLocalMap.remove();
    }

    public static int nextVariableIndex() {
        int index = nextIndex.getAndIncrement();
        if (index < 0) {
            nextIndex.decrementAndGet();
            throw new IllegalStateException("too many thread-local indexed variables");
        }
        return index;
    }

    public static int lastVariableIndex() {
        return nextIndex.get() - 1;
    }

    private InternalThreadLocalMap() {
        super(InternalThreadLocalMap.newIndexedVariableTable());
    }

    private static Object[] newIndexedVariableTable() {
        Object[] array = new Object[32];
        Arrays.fill(array, UNSET);
        return array;
    }

    public int size() {
        int count = 0;
        if (this.futureListenerStackDepth != 0) {
            ++count;
        }
        if (this.localChannelReaderStackDepth != 0) {
            ++count;
        }
        if (this.handlerSharableCache != null) {
            ++count;
        }
        if (this.counterHashCode != null) {
            ++count;
        }
        if (this.random != null) {
            ++count;
        }
        if (this.typeParameterMatcherGetCache != null) {
            ++count;
        }
        if (this.typeParameterMatcherFindCache != null) {
            ++count;
        }
        if (this.stringBuilder != null) {
            ++count;
        }
        if (this.charsetEncoderCache != null) {
            ++count;
        }
        if (this.charsetDecoderCache != null) {
            ++count;
        }
        if (this.arrayList != null) {
            ++count;
        }
        for (Object o : this.indexedVariables) {
            if (o == UNSET) continue;
            ++count;
        }
        return count - 1;
    }

    public StringBuilder stringBuilder() {
        StringBuilder sb = this.stringBuilder;
        if (sb == null) {
            this.stringBuilder = new StringBuilder(STRING_BUILDER_INITIAL_SIZE);
            return this.stringBuilder;
        }
        if (sb.capacity() > STRING_BUILDER_MAX_SIZE) {
            sb.setLength(STRING_BUILDER_INITIAL_SIZE);
            sb.trimToSize();
        }
        sb.setLength(0);
        return sb;
    }

    public Map<Charset, CharsetEncoder> charsetEncoderCache() {
        IdentityHashMap cache = this.charsetEncoderCache;
        if (cache == null) {
            this.charsetEncoderCache = cache = new IdentityHashMap();
        }
        return cache;
    }

    public Map<Charset, CharsetDecoder> charsetDecoderCache() {
        IdentityHashMap cache = this.charsetDecoderCache;
        if (cache == null) {
            this.charsetDecoderCache = cache = new IdentityHashMap();
        }
        return cache;
    }

    public <E> ArrayList<E> arrayList() {
        return this.arrayList(8);
    }

    public <E> ArrayList<E> arrayList(int minCapacity) {
        ArrayList list = this.arrayList;
        if (list == null) {
            this.arrayList = new ArrayList(minCapacity);
            return this.arrayList;
        }
        list.clear();
        list.ensureCapacity(minCapacity);
        return list;
    }

    public int futureListenerStackDepth() {
        return this.futureListenerStackDepth;
    }

    public void setFutureListenerStackDepth(int futureListenerStackDepth) {
        this.futureListenerStackDepth = futureListenerStackDepth;
    }

    public ThreadLocalRandom random() {
        ThreadLocalRandom r = this.random;
        if (r == null) {
            this.random = r = new ThreadLocalRandom();
        }
        return r;
    }

    public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
        IdentityHashMap cache = this.typeParameterMatcherGetCache;
        if (cache == null) {
            this.typeParameterMatcherGetCache = cache = new IdentityHashMap();
        }
        return cache;
    }

    public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
        IdentityHashMap cache = this.typeParameterMatcherFindCache;
        if (cache == null) {
            this.typeParameterMatcherFindCache = cache = new IdentityHashMap();
        }
        return cache;
    }

    public IntegerHolder counterHashCode() {
        return this.counterHashCode;
    }

    public void setCounterHashCode(IntegerHolder counterHashCode) {
        this.counterHashCode = counterHashCode;
    }

    public Map<Class<?>, Boolean> handlerSharableCache() {
        WeakHashMap cache = this.handlerSharableCache;
        if (cache == null) {
            this.handlerSharableCache = cache = new WeakHashMap(4);
        }
        return cache;
    }

    public int localChannelReaderStackDepth() {
        return this.localChannelReaderStackDepth;
    }

    public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
        this.localChannelReaderStackDepth = localChannelReaderStackDepth;
    }

    public Object indexedVariable(int index) {
        Object[] lookup = this.indexedVariables;
        return index < lookup.length ? lookup[index] : UNSET;
    }

    public boolean setIndexedVariable(int index, Object value) {
        Object[] lookup = this.indexedVariables;
        if (index < lookup.length) {
            Object oldValue = lookup[index];
            lookup[index] = value;
            return oldValue == UNSET;
        }
        this.expandIndexedVariableTableAndSet(index, value);
        return true;
    }

    private void expandIndexedVariableTableAndSet(int index, Object value) {
        Object[] oldArray = this.indexedVariables;
        int oldCapacity = oldArray.length;
        int newCapacity = index;
        newCapacity |= newCapacity >>> 1;
        newCapacity |= newCapacity >>> 2;
        newCapacity |= newCapacity >>> 4;
        newCapacity |= newCapacity >>> 8;
        newCapacity |= newCapacity >>> 16;
        Object[] newArray = Arrays.copyOf(oldArray, ++newCapacity);
        Arrays.fill(newArray, oldCapacity, newArray.length, UNSET);
        newArray[index] = value;
        this.indexedVariables = newArray;
    }

    public Object removeIndexedVariable(int index) {
        Object[] lookup = this.indexedVariables;
        if (index < lookup.length) {
            Object v = lookup[index];
            lookup[index] = UNSET;
            return v;
        }
        return UNSET;
    }

    public boolean isIndexedVariableSet(int index) {
        Object[] lookup = this.indexedVariables;
        return index < lookup.length && lookup[index] != UNSET;
    }

    static {
        UNSET = new Object();
        STRING_BUILDER_INITIAL_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.initialSize", 1024);
        logger.debug("-Dio.netty.threadLocalMap.stringBuilder.initialSize: {}", (Object)STRING_BUILDER_INITIAL_SIZE);
        STRING_BUILDER_MAX_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalMap.stringBuilder.maxSize", 4096);
        logger.debug("-Dio.netty.threadLocalMap.stringBuilder.maxSize: {}", (Object)STRING_BUILDER_MAX_SIZE);
    }
}

