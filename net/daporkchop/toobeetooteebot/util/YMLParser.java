/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.daporkchop.toobeetooteebot.util.ConfigSection;
import net.daporkchop.toobeetooteebot.util.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class YMLParser {
    public static final int DETECT = -1;
    public static final int PROPERTIES = 0;
    public static final int CNF = 0;
    public static final int JSON = 1;
    public static final int YAML = 2;
    public static final int ENUM = 5;
    public static final int ENUMERATION = 5;
    public static final Map<String, Integer> format = new TreeMap<String, Integer>();
    private final Map<String, Object> nestedCache = new HashMap<String, Object>();
    private ConfigSection config = new ConfigSection();
    private File file;
    private boolean correct = false;
    private int type = -1;

    public YMLParser(int type) {
        this.type = type;
        this.correct = true;
        this.config = new ConfigSection();
    }

    public YMLParser() {
        this(2);
    }

    public YMLParser(String file) {
        this(file, -1);
    }

    public YMLParser(File file) {
        this(file.toString(), -1);
    }

    public YMLParser(String file, int type) {
        this(file, type, new ConfigSection());
    }

    public YMLParser(File file, int type) {
        this(file.toString(), type, new ConfigSection());
    }

    @Deprecated
    public YMLParser(String file, int type, LinkedHashMap<String, Object> defaultMap) {
        this.load(file, type, new ConfigSection(defaultMap));
    }

    public YMLParser(String file, int type, ConfigSection defaultMap) {
        this.load(file, type, defaultMap);
    }

    @Deprecated
    public YMLParser(File file, int type, LinkedHashMap<String, Object> defaultMap) {
        this(file.toString(), type, new ConfigSection(defaultMap));
    }

    public void reload() {
        this.config.clear();
        this.nestedCache.clear();
        this.correct = false;
        if (this.file == null) {
            throw new IllegalStateException("Failed to reload Config. File object is undefined.");
        }
        this.load(this.file.toString(), this.type);
    }

    public boolean load(String file) {
        return this.load(file, -1);
    }

    public boolean load(String file, int type) {
        return this.load(file, type, new ConfigSection());
    }

    public boolean load(String file, int type, ConfigSection defaultMap) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            this.config = defaultMap;
            this.save();
        } else {
            if (this.type == -1) {
                String extension = "";
                if (this.file.getName().lastIndexOf(".") != -1 && this.file.getName().lastIndexOf(".") != 0) {
                    extension = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
                }
                if (format.containsKey(extension)) {
                    this.type = format.get(extension);
                } else {
                    this.correct = false;
                }
            }
            if (this.correct) {
                String content = "";
                try {
                    content = FileUtils.readFile(this.file);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                this.parseContent(content);
                if (!this.correct) {
                    return false;
                }
                if (this.setDefault(defaultMap) > 0) {
                    this.save();
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean load(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        }
        if (this.correct) {
            String content;
            try {
                content = FileUtils.readFile(inputStream);
            }
            catch (IOException e) {
                return false;
            }
            this.parseContent(content);
        }
        return this.correct;
    }

    public boolean loadRaw(String content) {
        if (this.correct) {
            this.parseContent(content);
        }
        return this.correct;
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public boolean save(File file, boolean async) {
        this.file = file;
        return this.save(async);
    }

    public boolean save(File file) {
        this.file = file;
        return this.save();
    }

    public boolean save() {
        return this.save(false);
    }

    public boolean save(Boolean async) {
        if (this.file == null) {
            throw new IllegalStateException("Failed to save Config. File object is undefined.");
        }
        if (this.correct) {
            String content = "";
            switch (this.type) {
                case 0: {
                    content = this.writeProperties();
                    break;
                }
                case 1: {
                    content = new GsonBuilder().setPrettyPrinting().create().toJson(this.config);
                    break;
                }
                case 2: {
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    Yaml yaml = new Yaml(dumperOptions);
                    content = yaml.dump(this.config);
                    break;
                }
                case 5: {
                    Iterator iterator = this.config.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry o;
                        Map.Entry entry = o = iterator.next();
                        content = content + String.valueOf(entry.getKey()) + "\r\n";
                    }
                    break;
                }
            }
            try {
                FileUtils.writeFile(this.file, content);
            }
            catch (IOException dumperOptions) {
                // empty catch block
            }
            return true;
        }
        return false;
    }

    public void set(String key, Object value) {
        this.config.set(key, value);
    }

    public Object get(String key) {
        return this.get(key, null);
    }

    public <T> T get(String key, T defaultValue) {
        return this.correct ? this.config.get(key, defaultValue) : defaultValue;
    }

    public ConfigSection getSection(String key) {
        return this.correct ? this.config.getSection(key) : new ConfigSection();
    }

    public boolean isSection(String key) {
        return this.config.isSection(key);
    }

    public ConfigSection getSections(String key) {
        return this.correct ? this.config.getSections(key) : new ConfigSection();
    }

    public ConfigSection getSections() {
        return this.correct ? this.config.getSections() : new ConfigSection();
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return this.correct ? this.config.getInt(key, defaultValue) : defaultValue;
    }

    public boolean isInt(String key) {
        return this.config.isInt(key);
    }

    public long getLong(String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        return this.correct ? this.config.getLong(key, defaultValue) : defaultValue;
    }

    public boolean isLong(String key) {
        return this.config.isLong(key);
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0.0);
    }

    public double getDouble(String key, double defaultValue) {
        return this.correct ? this.config.getDouble(key, defaultValue) : defaultValue;
    }

    public boolean isDouble(String key) {
        return this.config.isDouble(key);
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return this.correct ? this.config.getString(key, defaultValue) : defaultValue;
    }

    public boolean isString(String key) {
        return this.config.isString(key);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.correct ? this.config.getBoolean(key, defaultValue) : defaultValue;
    }

    public boolean isBoolean(String key) {
        return this.config.isBoolean(key);
    }

    public List getList(String key) {
        return this.getList(key, null);
    }

    public List getList(String key, List defaultList) {
        return this.correct ? this.config.getList(key, defaultList) : defaultList;
    }

    public boolean isList(String key) {
        return this.config.isList(key);
    }

    public List<String> getStringList(String key) {
        return this.config.getStringList(key);
    }

    public List<Integer> getIntegerList(String key) {
        return this.config.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return this.config.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key) {
        return this.config.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return this.config.getFloatList(key);
    }

    public List<Long> getLongList(String key) {
        return this.config.getLongList(key);
    }

    public List<Byte> getByteList(String key) {
        return this.config.getByteList(key);
    }

    public List<Character> getCharacterList(String key) {
        return this.config.getCharacterList(key);
    }

    public List<Short> getShortList(String key) {
        return this.config.getShortList(key);
    }

    public List<Map> getMapList(String key) {
        return this.config.getMapList(key);
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.config = new ConfigSection(map);
    }

    public boolean exists(String key) {
        return this.config.exists(key);
    }

    public boolean exists(String key, boolean ignoreCase) {
        return this.config.exists(key, ignoreCase);
    }

    public void remove(String key) {
        this.config.remove(key);
    }

    public Map<String, Object> getAll() {
        return this.config.getAllMap();
    }

    public void setAll(ConfigSection section) {
        this.config = section;
    }

    public ConfigSection getRootSection() {
        return this.config;
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        return this.setDefault(new ConfigSection(map));
    }

    public int setDefault(ConfigSection map) {
        int size = this.config.size();
        this.config = this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }

    private ConfigSection fillDefaults(ConfigSection defaultMap, ConfigSection data) {
        for (String key : defaultMap.keySet()) {
            if (data.containsKey(key)) continue;
            data.put(key, defaultMap.get(key));
        }
        return data;
    }

    private void parseList(String content) {
        content = content.replace("\r\n", "\n");
        for (String v : content.split("\n")) {
            if (v.trim().isEmpty()) continue;
            this.config.put(v, true);
        }
    }

    private String writeProperties() {
        String content = "#Properties Config file\r\n#" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + "\r\n";
        Iterator iterator = this.config.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            Object v = entry.getValue();
            Object k = entry.getKey();
            if (v instanceof Boolean) {
                v = (Boolean)v != false ? "on" : "off";
            }
            content = content + String.valueOf(k) + "=" + String.valueOf(v) + "\r\n";
        }
        return content;
    }

    private void parseProperties(String content) {
        block12 : for (String line : content.split("\n")) {
            if (!Pattern.compile("[a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()) continue;
            String[] b = line.split("=", -1);
            String k = b[0];
            String v = b[1].trim();
            String v_lower = v.toLowerCase();
            if (this.config.containsKey(k)) {
                // empty if block
            }
            switch (v_lower) {
                case "on": 
                case "true": 
                case "yes": {
                    this.config.put(k, true);
                    continue block12;
                }
                case "off": 
                case "false": 
                case "no": {
                    this.config.put(k, false);
                    continue block12;
                }
                default: {
                    this.config.put(k, v);
                }
            }
        }
    }

    @Deprecated
    public Object getNested(String key) {
        return this.get(key);
    }

    @Deprecated
    public <T> T getNested(String key, T defaultValue) {
        return this.get(key, defaultValue);
    }

    @Deprecated
    public <T> T getNestedAs(String key, Class<T> type) {
        return (T)this.get(key);
    }

    @Deprecated
    public void removeNested(String key) {
        this.remove(key);
    }

    private void parseContent(String content) {
        switch (this.type) {
            case 0: {
                this.parseProperties(content);
                break;
            }
            case 1: {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                this.config = new ConfigSection((LinkedHashMap)gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>(){}.getType()));
                break;
            }
            case 2: {
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                this.config = new ConfigSection(yaml.loadAs(content, LinkedHashMap.class));
                if (this.config != null) break;
                this.config = new ConfigSection();
                break;
            }
            case 5: {
                this.parseList(content);
                break;
            }
            default: {
                this.correct = false;
            }
        }
    }

    public Set<String> getKeys() {
        if (this.correct) {
            return this.config.getKeys();
        }
        return new HashSet<String>();
    }

    public Set<String> getKeys(boolean child) {
        if (this.correct) {
            return this.config.getKeys(child);
        }
        return new HashSet<String>();
    }

    static {
        format.put("properties", 0);
        format.put("con", 0);
        format.put("conf", 0);
        format.put("config", 0);
        format.put("js", 1);
        format.put("json", 1);
        format.put("yml", 2);
        format.put("yaml", 2);
        format.put("txt", 5);
        format.put("list", 5);
        format.put("enum", 5);
    }

}

