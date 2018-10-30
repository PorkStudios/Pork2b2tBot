/*
 * Decompiled with CFR 0_132.
 */
package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.nodes.Node;

public interface AnchorGenerator {
    public String nextAnchor(Node var1);
}

