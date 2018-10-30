/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.AnchorNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;

public abstract class BaseRepresenter {
    protected final Map<Class<?>, Represent> representers = new HashMap();
    protected Represent nullRepresenter;
    protected final Map<Class<?>, Represent> multiRepresenters = new LinkedHashMap();
    protected Character defaultScalarStyle;
    protected DumperOptions.FlowStyle defaultFlowStyle = DumperOptions.FlowStyle.AUTO;
    protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>(){
        private static final long serialVersionUID = -5576159264232131854L;

        @Override
        public Node put(Object key, Node value) {
            return super.put(key, new AnchorNode(value));
        }
    };
    protected Object objectToRepresent;
    private PropertyUtils propertyUtils;
    private boolean explicitPropertyUtils = false;

    public Node represent(Object data) {
        Node node = this.representData(data);
        this.representedObjects.clear();
        this.objectToRepresent = null;
        return node;
    }

    protected final Node representData(Object data) {
        Node node;
        this.objectToRepresent = data;
        if (this.representedObjects.containsKey(this.objectToRepresent)) {
            Node node2 = this.representedObjects.get(this.objectToRepresent);
            return node2;
        }
        if (data == null) {
            Node node3 = this.nullRepresenter.representData(null);
            return node3;
        }
        Class<?> clazz = data.getClass();
        if (this.representers.containsKey(clazz)) {
            Represent representer = this.representers.get(clazz);
            node = representer.representData(data);
        } else {
            Represent representer;
            for (Class<?> repr : this.multiRepresenters.keySet()) {
                if (repr == null || !repr.isInstance(data)) continue;
                Represent representer2 = this.multiRepresenters.get(repr);
                Node node4 = representer2.representData(data);
                return node4;
            }
            if (this.multiRepresenters.containsKey(null)) {
                representer = this.multiRepresenters.get(null);
                node = representer.representData(data);
            } else {
                representer = this.representers.get(null);
                node = representer.representData(data);
            }
        }
        return node;
    }

    protected Node representScalar(Tag tag, String value, Character style) {
        if (style == null) {
            style = this.defaultScalarStyle;
        }
        ScalarNode node = new ScalarNode(tag, value, null, null, style);
        return node;
    }

    protected Node representScalar(Tag tag, String value) {
        return this.representScalar(tag, value, null);
    }

    protected Node representSequence(Tag tag, Iterable<?> sequence, Boolean flowStyle) {
        int size = 10;
        if (sequence instanceof List) {
            size = ((List)sequence).size();
        }
        ArrayList<Node> value = new ArrayList<Node>(size);
        SequenceNode node = new SequenceNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        boolean bestStyle = true;
        for (Object item : sequence) {
            Node nodeItem = this.representData(item);
            if (!(nodeItem instanceof ScalarNode) || ((ScalarNode)nodeItem).getStyle() != null) {
                bestStyle = false;
            }
            value.add(nodeItem);
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
                node.setFlowStyle(this.defaultFlowStyle.getStyleBoolean());
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    protected Node representMapping(Tag tag, Map<?, ?> mapping, Boolean flowStyle) {
        ArrayList<NodeTuple> value = new ArrayList<NodeTuple>(mapping.size());
        MappingNode node = new MappingNode(tag, value, flowStyle);
        this.representedObjects.put(this.objectToRepresent, node);
        boolean bestStyle = true;
        for (Map.Entry<?, ?> entry : mapping.entrySet()) {
            Node nodeKey = this.representData(entry.getKey());
            Node nodeValue = this.representData(entry.getValue());
            if (!(nodeKey instanceof ScalarNode) || ((ScalarNode)nodeKey).getStyle() != null) {
                bestStyle = false;
            }
            if (!(nodeValue instanceof ScalarNode) || ((ScalarNode)nodeValue).getStyle() != null) {
                bestStyle = false;
            }
            value.add(new NodeTuple(nodeKey, nodeValue));
        }
        if (flowStyle == null) {
            if (this.defaultFlowStyle != DumperOptions.FlowStyle.AUTO) {
                node.setFlowStyle(this.defaultFlowStyle.getStyleBoolean());
            } else {
                node.setFlowStyle(bestStyle);
            }
        }
        return node;
    }

    public void setDefaultScalarStyle(DumperOptions.ScalarStyle defaultStyle) {
        this.defaultScalarStyle = defaultStyle.getChar();
    }

    public void setDefaultFlowStyle(DumperOptions.FlowStyle defaultFlowStyle) {
        this.defaultFlowStyle = defaultFlowStyle;
    }

    public DumperOptions.FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
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

}

