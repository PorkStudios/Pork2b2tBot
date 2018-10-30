/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

class ConfigSection
extends LinkedHashMap<String, Object> {
    public ConfigSection() {
    }

    public ConfigSection(String key, Object value) {
        this();
        this.set(key, value);
    }

    public ConfigSection(LinkedHashMap<String, Object> map) {
        this();
        if (map == null || map.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof LinkedHashMap) {
                super.put(entry.getKey(), new ConfigSection((LinkedHashMap)entry.getValue()));
                continue;
            }
            super.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> getAllMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.putAll(this);
        return map;
    }

    public ConfigSection getAll() {
        return new ConfigSection(this);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    public <T> T get(String key, T defaultValue) {
        if (key == null || key.isEmpty()) {
            return defaultValue;
        }
        if (super.containsKey(key)) {
            return (T)super.get(key);
        }
        String[] keys = key.split("\\.", 2);
        if (!super.containsKey(keys[0])) {
            return defaultValue;
        }
        Object value = super.get(keys[0]);
        if (value != null && value instanceof ConfigSection) {
            ConfigSection section = (ConfigSection)value;
            return section.get(keys[1], defaultValue);
        }
        return defaultValue;
    }

    public void set(String key, Object value) {
        String[] subKeys = key.split("\\.", 2);
        if (subKeys.length > 1) {
            ConfigSection childSection = new ConfigSection();
            if (this.containsKey(subKeys[0]) && super.get(subKeys[0]) instanceof ConfigSection) {
                childSection = (ConfigSection)super.get(subKeys[0]);
            }
            childSection.set(subKeys[1], value);
            super.put(subKeys[0], childSection);
        } else {
            super.put(subKeys[0], value);
        }
    }

    public boolean isSection(String key) {
        Object value = this.get(key);
        return value instanceof ConfigSection;
    }

    public ConfigSection getSection(String key) {
        return this.get(key, new ConfigSection());
    }

    public ConfigSection getSections() {
        return this.getSections(null);
    }

    public ConfigSection getSections(String key) {
        ConfigSection parent;
        ConfigSection sections = new ConfigSection();
        ConfigSection configSection = parent = key == null || key.isEmpty() ? this.getAll() : this.getSection(key);
        if (parent == null) {
            return sections;
        }
        parent.entrySet().forEach(e -> {
            if (e.getValue() instanceof ConfigSection) {
                sections.put(e.getKey(), e.getValue());
            }
        });
        return sections;
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return ((Number)this.get(key, defaultValue)).intValue();
    }

    public boolean isInt(String key) {
        Object val = this.get(key);
        return val instanceof Integer;
    }

    public long getLong(String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        return ((Number)this.get(key, defaultValue)).longValue();
    }

    public boolean isLong(String key) {
        Object val = this.get(key);
        return val instanceof Long;
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0.0);
    }

    public double getDouble(String key, double defaultValue) {
        return ((Number)this.get(key, defaultValue)).doubleValue();
    }

    public boolean isDouble(String key) {
        Object val = this.get(key);
        return val instanceof Double;
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        String result = this.get(key, defaultValue);
        return String.valueOf(result);
    }

    public boolean isString(String key) {
        Object val = this.get(key);
        return val instanceof String;
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.get(key, defaultValue);
    }

    public boolean isBoolean(String key) {
        Object val = this.get(key);
        return val instanceof Boolean;
    }

    public List getList(String key) {
        return this.getList(key, null);
    }

    public List getList(String key, List defaultList) {
        return this.get(key, defaultList);
    }

    public boolean isList(String key) {
        Object val = this.get(key);
        return val instanceof List;
    }

    public List<String> getStringList(String key) {
        List value = this.getList(key);
        if (value == null) {
            return new ArrayList<String>(0);
        }
        ArrayList<String> result = new ArrayList<String>();
        for (Object o : value) {
            if (!(o instanceof String) && !(o instanceof Number) && !(o instanceof Boolean) && !(o instanceof Character)) continue;
            result.add(String.valueOf(o));
        }
        return result;
    }

    public List<Integer> getIntegerList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Integer>(0);
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        for (Object object : list) {
            if (object instanceof Integer) {
                result.add((Integer)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Integer.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add(Integer.valueOf(((Character)object).charValue()));
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(((Number)object).intValue());
        }
        return result;
    }

    public List<Boolean> getBooleanList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Boolean>(0);
        }
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        for (Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean)object);
                continue;
            }
            if (!(object instanceof String)) continue;
            if (Boolean.TRUE.toString().equals(object)) {
                result.add(true);
                continue;
            }
            if (!Boolean.FALSE.toString().equals(object)) continue;
            result.add(false);
        }
        return result;
    }

    public List<Double> getDoubleList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Double>(0);
        }
        ArrayList<Double> result = new ArrayList<Double>();
        for (Object object : list) {
            if (object instanceof Double) {
                result.add((Double)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Double.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add(Double.valueOf(((Character)object).charValue()));
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(((Number)object).doubleValue());
        }
        return result;
    }

    public List<Float> getFloatList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Float>(0);
        }
        ArrayList<Float> result = new ArrayList<Float>();
        for (Object object : list) {
            if (object instanceof Float) {
                result.add((Float)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Float.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add(Float.valueOf(((Character)object).charValue()));
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(Float.valueOf(((Number)object).floatValue()));
        }
        return result;
    }

    public List<Long> getLongList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Long>(0);
        }
        ArrayList<Long> result = new ArrayList<Long>();
        for (Object object : list) {
            if (object instanceof Long) {
                result.add((Long)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Long.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add(Long.valueOf(((Character)object).charValue()));
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(((Number)object).longValue());
        }
        return result;
    }

    public List<Byte> getByteList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Byte>(0);
        }
        ArrayList<Byte> result = new ArrayList<Byte>();
        for (Object object : list) {
            if (object instanceof Byte) {
                result.add((Byte)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Byte.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add((byte)((Character)object).charValue());
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(((Number)object).byteValue());
        }
        return result;
    }

    public List<Character> getCharacterList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Character>(0);
        }
        ArrayList<Character> result = new ArrayList<Character>();
        for (Object object : list) {
            if (object instanceof Character) {
                result.add((Character)object);
                continue;
            }
            if (object instanceof String) {
                String str = (String)object;
                if (str.length() != 1) continue;
                result.add(Character.valueOf(str.charAt(0)));
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(Character.valueOf((char)((Number)object).intValue()));
        }
        return result;
    }

    public List<Short> getShortList(String key) {
        List list = this.getList(key);
        if (list == null) {
            return new ArrayList<Short>(0);
        }
        ArrayList<Short> result = new ArrayList<Short>();
        for (Object object : list) {
            if (object instanceof Short) {
                result.add((Short)object);
                continue;
            }
            if (object instanceof String) {
                try {
                    result.add(Short.valueOf((String)object));
                }
                catch (Exception exception) {}
                continue;
            }
            if (object instanceof Character) {
                result.add((short)((Character)object).charValue());
                continue;
            }
            if (!(object instanceof Number)) continue;
            result.add(((Number)object).shortValue());
        }
        return result;
    }

    public List<Map> getMapList(String key) {
        List list = this.getList(key);
        ArrayList<Map> result = new ArrayList<Map>();
        if (list == null) {
            return result;
        }
        for (Object object : list) {
            if (!(object instanceof Map)) continue;
            result.add((Map)object);
        }
        return result;
    }

    public boolean exists(String key, boolean ignoreCase) {
        if (ignoreCase) {
            key = key.toLowerCase();
        }
        for (String existKey : this.getKeys(true)) {
            if (ignoreCase) {
                existKey = existKey.toLowerCase();
            }
            if (!existKey.equals(key)) continue;
            return true;
        }
        return false;
    }

    public boolean exists(String key) {
        return this.exists(key, false);
    }

    public void remove(String key) {
        String[] keys;
        if (key == null || key.isEmpty()) {
            return;
        }
        if (super.containsKey(key)) {
            super.remove(key);
        } else if (this.containsKey(".") && super.get((keys = key.split("\\.", 2))[0]) instanceof ConfigSection) {
            ConfigSection section = (ConfigSection)super.get(keys[0]);
            section.remove(keys[1]);
        }
    }

    public Set<String> getKeys(boolean child) {
        LinkedHashSet<String> keys = new LinkedHashSet<String>();
        this.entrySet().forEach(entry -> {
            keys.add((String)entry.getKey());
            if (entry.getValue() instanceof ConfigSection && child) {
                ((ConfigSection)entry.getValue()).getKeys(true).forEach(childKey -> keys.add((String)entry.getKey() + "." + childKey));
            }
        });
        return keys;
    }

    public Set<String> getKeys() {
        return this.getKeys(true);
    }
}

