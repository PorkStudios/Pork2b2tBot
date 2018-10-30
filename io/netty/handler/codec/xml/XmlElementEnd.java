/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

import io.netty.handler.codec.xml.XmlElement;

public class XmlElementEnd
extends XmlElement {
    public XmlElementEnd(String name, String namespace, String prefix) {
        super(name, namespace, prefix);
    }

    @Override
    public String toString() {
        return "XmlElementStart{" + super.toString() + "} ";
    }
}

