/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpHeaders;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EmptyHttpHeaders
extends HttpHeaders {
    static final Iterator<Map.Entry<CharSequence, CharSequence>> EMPTY_CHARS_ITERATOR = Collections.emptyList().iterator();
    public static final EmptyHttpHeaders INSTANCE = EmptyHttpHeaders.instance();

    @Deprecated
    static EmptyHttpHeaders instance() {
        return EMPTY_HEADERS;
    }

    protected EmptyHttpHeaders() {
    }

    @Override
    public String get(String name) {
        return null;
    }

    @Override
    public Integer getInt(CharSequence name) {
        return null;
    }

    @Override
    public int getInt(CharSequence name, int defaultValue) {
        return defaultValue;
    }

    @Override
    public Short getShort(CharSequence name) {
        return null;
    }

    @Override
    public short getShort(CharSequence name, short defaultValue) {
        return defaultValue;
    }

    @Override
    public Long getTimeMillis(CharSequence name) {
        return null;
    }

    @Override
    public long getTimeMillis(CharSequence name, long defaultValue) {
        return defaultValue;
    }

    @Override
    public List<String> getAll(String name) {
        return Collections.emptyList();
    }

    @Override
    public List<Map.Entry<String, String>> entries() {
        return Collections.emptyList();
    }

    @Override
    public boolean contains(String name) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> names() {
        return Collections.emptySet();
    }

    @Override
    public HttpHeaders add(String name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders add(String name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders addInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders addShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders set(String name, Object value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders set(String name, Iterable<?> values) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders setInt(CharSequence name, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders setShort(CharSequence name, short value) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders remove(String name) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public HttpHeaders clear() {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return this.entries().iterator();
    }

    @Override
    public Iterator<Map.Entry<CharSequence, CharSequence>> iteratorCharSequence() {
        return EMPTY_CHARS_ITERATOR;
    }

    @Deprecated
    private static final class InstanceInitializer {
        @Deprecated
        private static final EmptyHttpHeaders EMPTY_HEADERS = new EmptyHttpHeaders();

        private InstanceInitializer() {
        }
    }

}

