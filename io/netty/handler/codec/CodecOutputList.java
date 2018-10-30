/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec;

import io.netty.util.Recycler;
import io.netty.util.internal.ObjectUtil;
import java.util.AbstractList;
import java.util.RandomAccess;

final class CodecOutputList
extends AbstractList<Object>
implements RandomAccess {
    private static final Recycler<CodecOutputList> RECYCLER = new Recycler<CodecOutputList>(){

        @Override
        protected CodecOutputList newObject(Recycler.Handle<CodecOutputList> handle) {
            return new CodecOutputList(handle);
        }
    };
    private final Recycler.Handle<CodecOutputList> handle;
    private int size;
    private Object[] array = new Object[16];
    private boolean insertSinceRecycled;

    static CodecOutputList newInstance() {
        return RECYCLER.get();
    }

    private CodecOutputList(Recycler.Handle<CodecOutputList> handle) {
        this.handle = handle;
    }

    @Override
    public Object get(int index) {
        this.checkIndex(index);
        return this.array[index];
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean add(Object element) {
        ObjectUtil.checkNotNull(element, "element");
        try {
            this.insert(this.size, element);
        }
        catch (IndexOutOfBoundsException ignore) {
            this.expandArray();
            this.insert(this.size, element);
        }
        ++this.size;
        return true;
    }

    @Override
    public Object set(int index, Object element) {
        ObjectUtil.checkNotNull(element, "element");
        this.checkIndex(index);
        Object old = this.array[index];
        this.insert(index, element);
        return old;
    }

    @Override
    public void add(int index, Object element) {
        ObjectUtil.checkNotNull(element, "element");
        this.checkIndex(index);
        if (this.size == this.array.length) {
            this.expandArray();
        }
        if (index != this.size - 1) {
            System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
        }
        this.insert(index, element);
        ++this.size;
    }

    @Override
    public Object remove(int index) {
        this.checkIndex(index);
        Object old = this.array[index];
        int len = this.size - index - 1;
        if (len > 0) {
            System.arraycopy(this.array, index + 1, this.array, index, len);
        }
        this.array[--this.size] = null;
        return old;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    boolean insertSinceRecycled() {
        return this.insertSinceRecycled;
    }

    void recycle() {
        for (int i = 0; i < this.size; ++i) {
            this.array[i] = null;
        }
        this.clear();
        this.insertSinceRecycled = false;
        this.handle.recycle(this);
    }

    Object getUnsafe(int index) {
        return this.array[index];
    }

    private void checkIndex(int index) {
        if (index >= this.size) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void insert(int index, Object element) {
        this.array[index] = element;
        this.insertSinceRecycled = true;
    }

    private void expandArray() {
        int newCapacity = this.array.length << 1;
        if (newCapacity < 0) {
            throw new OutOfMemoryError();
        }
        Object[] newArray = new Object[newCapacity];
        System.arraycopy(this.array, 0, newArray, 0, this.array.length);
        this.array = newArray;
    }

}

