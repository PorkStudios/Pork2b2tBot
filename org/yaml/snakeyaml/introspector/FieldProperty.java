/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.introspector;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.GenericProperty;

public class FieldProperty
extends GenericProperty {
    private final Field field;

    public FieldProperty(Field field) {
        super(field.getName(), field.getType(), field.getGenericType());
        this.field = field;
        field.setAccessible(true);
    }

    @Override
    public void set(Object object, Object value) throws Exception {
        this.field.set(object, value);
    }

    @Override
    public Object get(Object object) {
        try {
            return this.field.get(object);
        }
        catch (Exception e) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + e);
        }
    }
}

