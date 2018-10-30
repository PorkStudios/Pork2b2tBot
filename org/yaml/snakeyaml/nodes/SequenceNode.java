/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

public class SequenceNode
extends CollectionNode {
    private final List<Node> value;

    public SequenceNode(Tag tag, boolean resolved, List<Node> value, Mark startMark, Mark endMark, Boolean flowStyle) {
        super(tag, startMark, endMark, flowStyle);
        if (value == null) {
            throw new NullPointerException("value in a Node is required.");
        }
        this.value = value;
        this.resolved = resolved;
    }

    public SequenceNode(Tag tag, List<Node> value, Boolean flowStyle) {
        this(tag, true, value, null, null, flowStyle);
    }

    @Override
    public NodeId getNodeId() {
        return NodeId.sequence;
    }

    public List<Node> getValue() {
        return this.value;
    }

    public void setListType(Class<? extends Object> listType) {
        for (Node node : this.value) {
            node.setType(listType);
        }
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }
}

