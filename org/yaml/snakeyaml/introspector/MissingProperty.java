/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.introspector;

import org.yaml.snakeyaml.introspector.Property;

public class MissingProperty
extends Property {
    public MissingProperty(String name) {
        super(name, Object.class);
    }

    @Override
    public Class<?>[] getActualTypeArguments() {
        return new Class[0];
    }

    @Override
    public void set(Object object, Object value) throws Exception {
    }

    @Override
    public Object get(Object object) {
        return object;
    }
}

