/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.constructor;

import java.beans.IntrospectionException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class Constructor
extends SafeConstructor {
    private final Map<Tag, Class<? extends Object>> typeTags;
    protected final Map<Class<? extends Object>, TypeDescription> typeDefinitions;

    public Constructor() {
        this(Object.class);
    }

    public Constructor(Class<? extends Object> theRoot) {
        this(new TypeDescription(Constructor.checkRoot(theRoot)));
    }

    private static Class<? extends Object> checkRoot(Class<? extends Object> theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root class must be provided.");
        }
        return theRoot;
    }

    public Constructor(TypeDescription theRoot) {
        if (theRoot == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        this.yamlConstructors.put(null, new ConstructYamlObject());
        if (!Object.class.equals(theRoot.getType())) {
            this.rootTag = new Tag(theRoot.getType());
        }
        this.typeTags = new HashMap<Tag, Class<? extends Object>>();
        this.typeDefinitions = new HashMap<Class<? extends Object>, TypeDescription>();
        this.yamlClassConstructors.put(NodeId.scalar, new ConstructScalar());
        this.yamlClassConstructors.put(NodeId.mapping, new ConstructMapping());
        this.yamlClassConstructors.put(NodeId.sequence, new ConstructSequence());
        this.addTypeDescription(theRoot);
    }

    public Constructor(String theRoot) throws ClassNotFoundException {
        this(Class.forName(Constructor.check(theRoot)));
    }

    private static final String check(String s) {
        if (s == null) {
            throw new NullPointerException("Root type must be provided.");
        }
        if (s.trim().length() == 0) {
            throw new YAMLException("Root type must be provided.");
        }
        return s;
    }

    public TypeDescription addTypeDescription(TypeDescription definition) {
        if (definition == null) {
            throw new NullPointerException("TypeDescription is required.");
        }
        Tag tag = definition.getTag();
        this.typeTags.put(tag, definition.getType());
        return this.typeDefinitions.put(definition.getType(), definition);
    }

    protected Class<?> getClassForNode(Node node) {
        Class<? extends Object> classForTag = this.typeTags.get(node.getTag());
        if (classForTag == null) {
            Class<?> cl;
            String name = node.getTag().getClassName();
            try {
                cl = this.getClassForName(name);
            }
            catch (ClassNotFoundException e) {
                throw new YAMLException("Class not found: " + name);
            }
            this.typeTags.put(node.getTag(), cl);
            return cl;
        }
        return classForTag;
    }

    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        try {
            return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e) {
            return Class.forName(name);
        }
    }

    protected class ConstructSequence
    implements Construct {
        protected ConstructSequence() {
        }

        @Override
        public Object construct(Node node) {
            SequenceNode snode = (SequenceNode)node;
            if (Set.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Set cannot be recursive.");
                }
                return Constructor.this.constructSet(snode);
            }
            if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createDefaultList(snode.getValue().size());
                }
                return Constructor.this.constructSequence(snode);
            }
            if (node.getType().isArray()) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createArray(node.getType(), snode.getValue().size());
                }
                return Constructor.this.constructArray(snode);
            }
            ArrayList possibleConstructors = new ArrayList(snode.getValue().size());
            for (java.lang.reflect.Constructor<?> constructor : node.getType().getDeclaredConstructors()) {
                if (snode.getValue().size() != constructor.getParameterTypes().length) continue;
                possibleConstructors.add(constructor);
            }
            if (!possibleConstructors.isEmpty()) {
                Object argumentList;
                int index;
                if (possibleConstructors.size() == 1) {
                    argumentList = new Object[snode.getValue().size()];
                    java.lang.reflect.Constructor c = (java.lang.reflect.Constructor)possibleConstructors.get(0);
                    index = 0;
                    for (Node argumentNode : snode.getValue()) {
                        Class<?> type = c.getParameterTypes()[index];
                        argumentNode.setType(type);
                        argumentList[index++] = Constructor.this.constructObject(argumentNode);
                    }
                    try {
                        c.setAccessible(true);
                        return c.newInstance((Object[])argumentList);
                    }
                    catch (Exception e) {
                        throw new YAMLException(e);
                    }
                }
                argumentList = Constructor.this.constructSequence(snode);
                Class[] parameterTypes = new Class[argumentList.size()];
                index = 0;
                Iterator<Object> i$ = argumentList.iterator();
                while (i$.hasNext()) {
                    Object parameter = i$.next();
                    parameterTypes[index] = parameter.getClass();
                    ++index;
                }
                for (java.lang.reflect.Constructor c : possibleConstructors) {
                    Class<?>[] argTypes = c.getParameterTypes();
                    boolean foundConstructor = true;
                    for (int i = 0; i < argTypes.length; ++i) {
                        if (this.wrapIfPrimitive(argTypes[i]).isAssignableFrom(parameterTypes[i])) continue;
                        foundConstructor = false;
                        break;
                    }
                    if (!foundConstructor) continue;
                    try {
                        c.setAccessible(true);
                        return c.newInstance(argumentList.toArray());
                    }
                    catch (Exception e) {
                        throw new YAMLException(e);
                    }
                }
            }
            throw new YAMLException("No suitable constructor with " + String.valueOf(snode.getValue().size()) + " arguments found for " + node.getType());
        }

        private final Class<? extends Object> wrapIfPrimitive(Class<?> clazz) {
            if (!clazz.isPrimitive()) {
                return clazz;
            }
            if (clazz == Integer.TYPE) {
                return Integer.class;
            }
            if (clazz == Float.TYPE) {
                return Float.class;
            }
            if (clazz == Double.TYPE) {
                return Double.class;
            }
            if (clazz == Boolean.TYPE) {
                return Boolean.class;
            }
            if (clazz == Long.TYPE) {
                return Long.class;
            }
            if (clazz == Character.TYPE) {
                return Character.class;
            }
            if (clazz == Short.TYPE) {
                return Short.class;
            }
            if (clazz == Byte.TYPE) {
                return Byte.class;
            }
            throw new YAMLException("Unexpected primitive " + clazz);
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            SequenceNode snode = (SequenceNode)node;
            if (List.class.isAssignableFrom(node.getType())) {
                List list = (List)object;
                Constructor.this.constructSequenceStep2(snode, list);
            } else if (node.getType().isArray()) {
                Constructor.this.constructArrayStep2(snode, object);
            } else {
                throw new YAMLException("Immutable objects cannot be recursive.");
            }
        }
    }

    protected class ConstructScalar
    extends AbstractConstruct {
        protected ConstructScalar() {
        }

        @Override
        public Object construct(Node nnode) {
            Object result;
            ScalarNode node = (ScalarNode)nnode;
            Class<? extends Object> type = node.getType();
            if (type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class || Date.class.isAssignableFrom(type) || type == Character.class || type == BigInteger.class || type == BigDecimal.class || Enum.class.isAssignableFrom(type) || Tag.BINARY.equals(node.getTag()) || Calendar.class.isAssignableFrom(type) || type == UUID.class) {
                result = this.constructStandardJavaInstance(type, node);
            } else {
                Object argument;
                java.lang.reflect.Constructor<?>[] javaConstructors = type.getDeclaredConstructors();
                int oneArgCount = 0;
                java.lang.reflect.Constructor<Object> javaConstructor = null;
                for (java.lang.reflect.Constructor<?> c : javaConstructors) {
                    if (c.getParameterTypes().length != 1) continue;
                    ++oneArgCount;
                    javaConstructor = c;
                }
                if (javaConstructor == null) {
                    throw new YAMLException("No single argument constructor found for " + type);
                }
                if (oneArgCount == 1) {
                    argument = this.constructStandardJavaInstance(javaConstructor.getParameterTypes()[0], node);
                } else {
                    argument = Constructor.this.constructScalar(node);
                    try {
                        javaConstructor = type.getDeclaredConstructor(String.class);
                    }
                    catch (Exception e) {
                        throw new YAMLException("Can't construct a java object for scalar " + node.getTag() + "; No String constructor found. Exception=" + e.getMessage(), e);
                    }
                }
                try {
                    javaConstructor.setAccessible(true);
                    result = javaConstructor.newInstance(argument);
                }
                catch (Exception e) {
                    throw new ConstructorException(null, null, "Can't construct a java object for scalar " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
                }
            }
            return result;
        }

        private Object constructStandardJavaInstance(Class type, ScalarNode node) {
            Object result;
            if (type == String.class) {
                Construct stringConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.STR);
                result = stringConstructor.construct(node);
            } else if (type == Boolean.class || type == Boolean.TYPE) {
                Construct boolConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.BOOL);
                result = boolConstructor.construct(node);
            } else if (type == Character.class || type == Character.TYPE) {
                Construct charConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.STR);
                String ch = (String)charConstructor.construct(node);
                if (ch.length() == 0) {
                    result = null;
                } else {
                    if (ch.length() != 1) {
                        throw new YAMLException("Invalid node Character: '" + ch + "'; length: " + ch.length());
                    }
                    result = Character.valueOf(ch.charAt(0));
                }
            } else if (Date.class.isAssignableFrom(type)) {
                Construct dateConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.TIMESTAMP);
                Date date = (Date)dateConstructor.construct(node);
                if (type == Date.class) {
                    result = date;
                } else {
                    try {
                        java.lang.reflect.Constructor constr = type.getConstructor(Long.TYPE);
                        result = constr.newInstance(date.getTime());
                    }
                    catch (RuntimeException e) {
                        throw e;
                    }
                    catch (Exception e) {
                        throw new YAMLException("Cannot construct: '" + type + "'");
                    }
                }
            } else if (type == Float.class || type == Double.class || type == Float.TYPE || type == Double.TYPE || type == BigDecimal.class) {
                if (type == BigDecimal.class) {
                    result = new BigDecimal(node.getValue());
                } else {
                    Construct doubleConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.FLOAT);
                    result = doubleConstructor.construct(node);
                    if (type == Float.class || type == Float.TYPE) {
                        result = new Float((Double)result);
                    }
                }
            } else if (type == Byte.class || type == Short.class || type == Integer.class || type == Long.class || type == BigInteger.class || type == Byte.TYPE || type == Short.TYPE || type == Integer.TYPE || type == Long.TYPE) {
                Construct intConstructor = (Construct)Constructor.this.yamlConstructors.get(Tag.INT);
                result = intConstructor.construct(node);
                result = type == Byte.class || type == Byte.TYPE ? (Number)Byte.valueOf(result.toString()) : (Number)(type == Short.class || type == Short.TYPE ? (Number)Short.valueOf(result.toString()) : (Number)(type == Integer.class || type == Integer.TYPE ? (Number)Integer.parseInt(result.toString()) : (Number)(type == Long.class || type == Long.TYPE ? Long.valueOf(result.toString()) : new BigInteger(result.toString()))));
            } else if (Enum.class.isAssignableFrom(type)) {
                String enumValueName = node.getValue();
                try {
                    result = Enum.valueOf(type, enumValueName);
                }
                catch (Exception ex) {
                    throw new YAMLException("Unable to find enum value '" + enumValueName + "' for enum class: " + type.getName());
                }
            } else if (Calendar.class.isAssignableFrom(type)) {
                SafeConstructor.ConstructYamlTimestamp contr = new SafeConstructor.ConstructYamlTimestamp();
                contr.construct(node);
                result = contr.getCalendar();
            } else if (Number.class.isAssignableFrom(type)) {
                SafeConstructor.ConstructYamlNumber contr = new SafeConstructor.ConstructYamlNumber(Constructor.this);
                result = contr.construct(node);
            } else if (UUID.class == type) {
                result = UUID.fromString(node.getValue());
            } else if (Constructor.this.yamlConstructors.containsKey(node.getTag())) {
                result = ((Construct)Constructor.this.yamlConstructors.get(node.getTag())).construct(node);
            } else {
                throw new YAMLException("Unsupported class: " + type);
            }
            return result;
        }
    }

    protected class ConstructYamlObject
    implements Construct {
        protected ConstructYamlObject() {
        }

        private Construct getConstructor(Node node) {
            Class<?> cl = Constructor.this.getClassForNode(node);
            node.setType(cl);
            Construct constructor = (Construct)Constructor.this.yamlClassConstructors.get((Object)node.getNodeId());
            return constructor;
        }

        @Override
        public Object construct(Node node) {
            Object result = null;
            try {
                result = this.getConstructor(node).construct(node);
            }
            catch (ConstructorException e) {
                throw e;
            }
            catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
            return result;
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            try {
                this.getConstructor(node).construct2ndStep(node, object);
            }
            catch (Exception e) {
                throw new ConstructorException(null, null, "Can't construct a second step for a java object for " + node.getTag() + "; exception=" + e.getMessage(), node.getStartMark(), e);
            }
        }
    }

    protected class ConstructMapping
    implements Construct {
        protected ConstructMapping() {
        }

        @Override
        public Object construct(Node node) {
            MappingNode mnode = (MappingNode)node;
            if (Properties.class.isAssignableFrom(node.getType())) {
                Properties properties = new Properties();
                if (node.isTwoStepsConstruction()) {
                    throw new YAMLException("Properties must not be recursive.");
                }
                Constructor.this.constructMapping2ndStep(mnode, properties);
                return properties;
            }
            if (SortedMap.class.isAssignableFrom(node.getType())) {
                TreeMap<Object, Object> map = new TreeMap<Object, Object>();
                if (!node.isTwoStepsConstruction()) {
                    Constructor.this.constructMapping2ndStep(mnode, map);
                }
                return map;
            }
            if (Map.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createDefaultMap();
                }
                return Constructor.this.constructMapping(mnode);
            }
            if (SortedSet.class.isAssignableFrom(node.getType())) {
                TreeSet<Object> set = new TreeSet<Object>();
                Constructor.this.constructSet2ndStep(mnode, set);
                return set;
            }
            if (Collection.class.isAssignableFrom(node.getType())) {
                if (node.isTwoStepsConstruction()) {
                    return Constructor.this.createDefaultSet();
                }
                return Constructor.this.constructSet(mnode);
            }
            if (node.isTwoStepsConstruction()) {
                return this.createEmptyJavaBean(mnode);
            }
            return this.constructJavaBean2ndStep(mnode, this.createEmptyJavaBean(mnode));
        }

        @Override
        public void construct2ndStep(Node node, Object object) {
            if (Map.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructMapping2ndStep((MappingNode)node, (Map)object);
            } else if (Set.class.isAssignableFrom(node.getType())) {
                Constructor.this.constructSet2ndStep((MappingNode)node, (Set)object);
            } else {
                this.constructJavaBean2ndStep((MappingNode)node, object);
            }
        }

        protected Object createEmptyJavaBean(MappingNode node) {
            try {
                java.lang.reflect.Constructor<? extends Object> c = node.getType().getDeclaredConstructor(new Class[0]);
                c.setAccessible(true);
                return c.newInstance(new Object[0]);
            }
            catch (Exception e) {
                throw new YAMLException(e);
            }
        }

        protected Object constructJavaBean2ndStep(MappingNode node, Object object) {
            Constructor.this.flattenMapping(node);
            Class<? extends Object> beanType = node.getType();
            List<NodeTuple> nodeValue = node.getValue();
            for (NodeTuple tuple : nodeValue) {
                if (!(tuple.getKeyNode() instanceof ScalarNode)) {
                    throw new YAMLException("Keys must be scalars but found: " + tuple.getKeyNode());
                }
                ScalarNode keyNode = (ScalarNode)tuple.getKeyNode();
                Node valueNode = tuple.getValueNode();
                keyNode.setType(String.class);
                String key = (String)Constructor.this.constructObject(keyNode);
                try {
                    MappingNode mnode;
                    Class<?>[] arguments;
                    Property property = this.getProperty(beanType, key);
                    valueNode.setType(property.getType());
                    TypeDescription memberDescription = Constructor.this.typeDefinitions.get(beanType);
                    boolean typeDetected = false;
                    if (memberDescription != null) {
                        switch (valueNode.getNodeId()) {
                            case sequence: {
                                SequenceNode snode = (SequenceNode)valueNode;
                                Class<? extends Object> memberType = memberDescription.getListPropertyType(key);
                                if (memberType != null) {
                                    snode.setListType(memberType);
                                    typeDetected = true;
                                    break;
                                }
                                if (!property.getType().isArray()) break;
                                snode.setListType(property.getType().getComponentType());
                                typeDetected = true;
                                break;
                            }
                            case mapping: {
                                mnode = (MappingNode)valueNode;
                                Class<? extends Object> keyType = memberDescription.getMapKeyType(key);
                                if (keyType == null) break;
                                mnode.setTypes(keyType, memberDescription.getMapValueType(key));
                                typeDetected = true;
                                break;
                            }
                        }
                    }
                    if (!typeDetected && valueNode.getNodeId() != NodeId.scalar && (arguments = property.getActualTypeArguments()) != null && arguments.length > 0) {
                        Class<?> t;
                        if (valueNode.getNodeId() == NodeId.sequence) {
                            t = arguments[0];
                            SequenceNode snode = (SequenceNode)valueNode;
                            snode.setListType(t);
                        } else if (valueNode.getTag().equals(Tag.SET)) {
                            t = arguments[0];
                            mnode = (MappingNode)valueNode;
                            mnode.setOnlyKeyType(t);
                            mnode.setUseClassConstructor(true);
                        } else if (property.getType().isAssignableFrom(Map.class)) {
                            Class<?> ketType = arguments[0];
                            Class<?> valueType = arguments[1];
                            MappingNode mnode2 = (MappingNode)valueNode;
                            mnode2.setTypes(ketType, valueType);
                            mnode2.setUseClassConstructor(true);
                        }
                    }
                    Object value = Constructor.this.constructObject(valueNode);
                    if ((property.getType() == Float.TYPE || property.getType() == Float.class) && value instanceof Double) {
                        value = Float.valueOf(((Double)value).floatValue());
                    }
                    if (property.getType() == String.class && Tag.BINARY.equals(valueNode.getTag()) && value instanceof byte[]) {
                        value = new String((byte[])value);
                    }
                    property.set(object, value);
                }
                catch (Exception e) {
                    throw new ConstructorException("Cannot create property=" + key + " for JavaBean=" + object, node.getStartMark(), e.getMessage(), valueNode.getStartMark(), e);
                }
            }
            return object;
        }

        protected Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
            return Constructor.this.getPropertyUtils().getProperty(type, name);
        }
    }

}

