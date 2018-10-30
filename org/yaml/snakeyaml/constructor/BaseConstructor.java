/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.constructor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public abstract class BaseConstructor {
    protected final Map<NodeId, Construct> yamlClassConstructors = new EnumMap<NodeId, Construct>(NodeId.class);
    protected final Map<Tag, Construct> yamlConstructors = new HashMap<Tag, Construct>();
    protected final Map<String, Construct> yamlMultiConstructors = new HashMap<String, Construct>();
    protected Composer composer;
    private final Map<Node, Object> constructedObjects = new HashMap<Node, Object>();
    private final Set<Node> recursiveObjects = new HashSet<Node>();
    private final ArrayList<RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>> maps2fill = new ArrayList();
    private final ArrayList<RecursiveTuple<Set<Object>, Object>> sets2fill = new ArrayList();
    protected Tag rootTag = null;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils = false;
    private boolean allowDuplicateKeys = true;

    public void setComposer(Composer composer) {
        this.composer = composer;
    }

    public boolean checkData() {
        return this.composer.checkNode();
    }

    public Object getData() {
        this.composer.checkNode();
        Node node = this.composer.getNode();
        if (this.rootTag != null) {
            node.setTag(this.rootTag);
        }
        return this.constructDocument(node);
    }

    public Object getSingleData(Class<?> type) {
        Node node = this.composer.getSingleNode();
        if (node != null) {
            if (Object.class != type) {
                node.setTag(new Tag(type));
            } else if (this.rootTag != null) {
                node.setTag(this.rootTag);
            }
            return this.constructDocument(node);
        }
        return null;
    }

    protected final Object constructDocument(Node node) {
        Object data = this.constructObject(node);
        this.fillRecursive();
        this.constructedObjects.clear();
        this.recursiveObjects.clear();
        return data;
    }

    private void fillRecursive() {
        if (!this.maps2fill.isEmpty()) {
            for (RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>> entry : this.maps2fill) {
                RecursiveTuple<Object, Object> key_value = entry._2();
                entry._1().put(key_value._1(), key_value._2());
            }
            this.maps2fill.clear();
        }
        if (!this.sets2fill.isEmpty()) {
            for (RecursiveTuple<Object, Object> value : this.sets2fill) {
                ((Set)value._1()).add(value._2());
            }
            this.sets2fill.clear();
        }
    }

    protected Object constructObject(Node node) {
        if (this.constructedObjects.containsKey(node)) {
            return this.constructedObjects.get(node);
        }
        if (this.recursiveObjects.contains(node)) {
            throw new ConstructorException(null, null, "found unconstructable recursive node", node.getStartMark());
        }
        this.recursiveObjects.add(node);
        Construct constructor = this.getConstructor(node);
        Object data = constructor.construct(node);
        this.constructedObjects.put(node, data);
        this.recursiveObjects.remove(node);
        if (node.isTwoStepsConstruction()) {
            constructor.construct2ndStep(node, data);
        }
        return data;
    }

    protected Construct getConstructor(Node node) {
        if (node.useClassConstructor()) {
            return this.yamlClassConstructors.get((Object)node.getNodeId());
        }
        Construct constructor = this.yamlConstructors.get(node.getTag());
        if (constructor == null) {
            for (String prefix : this.yamlMultiConstructors.keySet()) {
                if (!node.getTag().startsWith(prefix)) continue;
                return this.yamlMultiConstructors.get(prefix);
            }
            return this.yamlConstructors.get(null);
        }
        return constructor;
    }

    protected Object constructScalar(ScalarNode node) {
        return node.getValue();
    }

    protected List<Object> createDefaultList(int initSize) {
        return new ArrayList<Object>(initSize);
    }

    protected Set<Object> createDefaultSet(int initSize) {
        return new LinkedHashSet<Object>(initSize);
    }

    protected Object createArray(Class<?> type, int size) {
        return Array.newInstance(type.getComponentType(), size);
    }

    protected List<? extends Object> constructSequence(SequenceNode node) {
        List result;
        if (List.class.isAssignableFrom(node.getType()) && !node.getType().isInterface()) {
            try {
                result = (List)node.getType().newInstance();
            }
            catch (Exception e) {
                throw new YAMLException(e);
            }
        } else {
            result = this.createDefaultList(node.getValue().size());
        }
        this.constructSequenceStep2(node, result);
        return result;
    }

    protected Set<? extends Object> constructSet(SequenceNode node) {
        Set<Object> result;
        if (!node.getType().isInterface()) {
            try {
                result = (Set<Object>)node.getType().newInstance();
            }
            catch (Exception e) {
                throw new YAMLException(e);
            }
        } else {
            result = this.createDefaultSet(node.getValue().size());
        }
        this.constructSequenceStep2(node, result);
        return result;
    }

    protected Object constructArray(SequenceNode node) {
        return this.constructArrayStep2(node, this.createArray(node.getType(), node.getValue().size()));
    }

    protected void constructSequenceStep2(SequenceNode node, Collection<Object> collection) {
        for (Node child : node.getValue()) {
            collection.add(this.constructObject(child));
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Object constructArrayStep2(SequenceNode node, Object array) {
        Class<?> componentType = node.getType().getComponentType();
        int index = 0;
        for (Node child : node.getValue()) {
            if (child.getType() == Object.class) {
                child.setType(componentType);
            }
            Object value = this.constructObject(child);
            if (componentType.isPrimitive()) {
                if (value == null) {
                    throw new NullPointerException("Unable to construct element value for " + child);
                }
                if (Byte.TYPE.equals(componentType)) {
                    Array.setByte(array, index, ((Number)value).byteValue());
                } else if (Short.TYPE.equals(componentType)) {
                    Array.setShort(array, index, ((Number)value).shortValue());
                } else if (Integer.TYPE.equals(componentType)) {
                    Array.setInt(array, index, ((Number)value).intValue());
                } else if (Long.TYPE.equals(componentType)) {
                    Array.setLong(array, index, ((Number)value).longValue());
                } else if (Float.TYPE.equals(componentType)) {
                    Array.setFloat(array, index, ((Number)value).floatValue());
                } else if (Double.TYPE.equals(componentType)) {
                    Array.setDouble(array, index, ((Number)value).doubleValue());
                } else if (Character.TYPE.equals(componentType)) {
                    Array.setChar(array, index, ((Character)value).charValue());
                } else {
                    if (!Boolean.TYPE.equals(componentType)) throw new YAMLException("unexpected primitive type");
                    Array.setBoolean(array, index, (Boolean)value);
                }
            } else {
                Array.set(array, index, value);
            }
            ++index;
        }
        return array;
    }

    protected Map<Object, Object> createDefaultMap() {
        return new LinkedHashMap<Object, Object>();
    }

    protected Set<Object> createDefaultSet() {
        return new LinkedHashSet<Object>();
    }

    protected Set<Object> constructSet(MappingNode node) {
        Set<Object> set = this.createDefaultSet();
        this.constructSet2ndStep(node, set);
        return set;
    }

    protected Map<Object, Object> constructMapping(MappingNode node) {
        Map<Object, Object> mapping = this.createDefaultMap();
        this.constructMapping2ndStep(node, mapping);
        return mapping;
    }

    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        List<NodeTuple> nodeValue = node.getValue();
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Node valueNode = tuple.getValueNode();
            Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException("while constructing a mapping", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            Object value = this.constructObject(valueNode);
            if (keyNode.isTwoStepsConstruction()) {
                this.maps2fill.add(0, new RecursiveTuple<Map<Object, Object>, RecursiveTuple<Object, Object>>(mapping, new RecursiveTuple<Object, Object>(key, value)));
                continue;
            }
            mapping.put(key, value);
        }
    }

    protected void constructSet2ndStep(MappingNode node, Set<Object> set) {
        List<NodeTuple> nodeValue = node.getValue();
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Object key = this.constructObject(keyNode);
            if (key != null) {
                try {
                    key.hashCode();
                }
                catch (Exception e) {
                    throw new ConstructorException("while constructing a Set", node.getStartMark(), "found unacceptable key " + key, tuple.getKeyNode().getStartMark(), e);
                }
            }
            if (keyNode.isTwoStepsConstruction()) {
                this.sets2fill.add(0, new RecursiveTuple<Set<Object>, Object>(set, key));
                continue;
            }
            set.add(key);
        }
    }

    public void setPropertyUtils(PropertyUtils propertyUtils) {
        this.propertyUtils = propertyUtils;
        this.explicitPropertyUtils = true;
    }

    public final PropertyUtils getPropertyUtils() {
        if (this.propertyUtils == null) {
            this.propertyUtils = new PropertyUtils();
        }
        return this.propertyUtils;
    }

    public final boolean isExplicitPropertyUtils() {
        return this.explicitPropertyUtils;
    }

    public boolean isAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }

    public void setAllowDuplicateKeys(boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
    }

    private static class RecursiveTuple<T, K> {
        private final T _1;
        private final K _2;

        public RecursiveTuple(T _1, K _2) {
            this._1 = _1;
            this._2 = _2;
        }

        public K _2() {
            return this._2;
        }

        public T _1() {
            return this._1;
        }
    }

}

