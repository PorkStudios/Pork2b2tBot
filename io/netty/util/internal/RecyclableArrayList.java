/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import io.netty.util.Recycler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

public final class RecyclableArrayList
extends ArrayList<Object> {
    private static final long serialVersionUID = -8605125654176467947L;
    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private static final Recycler<RecyclableArrayList> RECYCLER = new Recycler<RecyclableArrayList>(){

        @Override
        protected RecyclableArrayList newObject(Recycler.Handle<RecyclableArrayList> handle) {
            return new RecyclableArrayList(handle);
        }
    };
    private boolean insertSinceRecycled;
    private final Recycler.Handle<RecyclableArrayList> handle;

    public static RecyclableArrayList newInstance() {
        return RecyclableArrayList.newInstance(8);
    }

    public static RecyclableArrayList newInstance(int minCapacity) {
        RecyclableArrayList ret = RECYCLER.get();
        ret.ensureCapacity(minCapacity);
        return ret;
    }

    private RecyclableArrayList(Recycler.Handle<RecyclableArrayList> handle) {
        this(handle, 8);
    }

    private RecyclableArrayList(Recycler.Handle<RecyclableArrayList> handle, int initialCapacity) {
        super(initialCapacity);
        this.handle = handle;
    }

    @Override
    public boolean addAll(Collection<?> c) {
        RecyclableArrayList.checkNullElements(c);
        if (super.addAll(c)) {
            this.insertSinceRecycled = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<?> c) {
        RecyclableArrayList.checkNullElements(c);
        if (super.addAll(index, c)) {
            this.insertSinceRecycled = true;
            return true;
        }
        return false;
    }

    private static void checkNullElements(Collection<?> c) {
        if (c instanceof RandomAccess && c instanceof List) {
            List list = (List)c;
            int size = list.size();
            for (int i = 0; i < size; ++i) {
                if (list.get(i) != null) continue;
                throw new IllegalArgumentException("c contains null values");
            }
        } else {
            for (Object element : c) {
                if (element != null) continue;
                throw new IllegalArgumentException("c contains null values");
            }
        }
    }

    @Override
    public boolean add(Object element) {
        if (element == null) {
            throw new NullPointerException("element");
        }
        if (super.add(element)) {
            this.insertSinceRecycled = true;
            return true;
        }
        return false;
    }

    @Override
    public void add(int index, Object element) {
        if (element == null) {
            throw new NullPointerException("element");
        }
        super.add(index, element);
        this.insertSinceRecycled = true;
    }

    @Override
    public Object set(int index, Object element) {
        if (element == null) {
            throw new NullPointerException("element");
        }
        Object old = super.set(index, element);
        this.insertSinceRecycled = true;
        return old;
    }

    public boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }

    public boolean recycle() {
        this.clear();
        this.insertSinceRecycled = false;
        this.handle.recycle(this);
        return true;
    }

}

