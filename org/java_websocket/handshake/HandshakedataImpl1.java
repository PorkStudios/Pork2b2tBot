/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.handshake;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import org.java_websocket.handshake.HandshakeBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class HandshakedataImpl1
implements HandshakeBuilder {
    private byte[] content;
    private TreeMap<String, String> map = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    @Override
    public Iterator<String> iterateHttpFields() {
        return Collections.unmodifiableSet(this.map.keySet()).iterator();
    }

    @Override
    public String getFieldValue(String name) {
        String s = this.map.get(name);
        if (s == null) {
            return "";
        }
        return s;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public void put(String name, String value) {
        this.map.put(name, value);
    }

    @Override
    public boolean hasFieldValue(String name) {
        return this.map.containsKey(name);
    }
}

