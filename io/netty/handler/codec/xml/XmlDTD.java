/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

public class XmlDTD {
    private final String text;

    public XmlDTD(String text) {
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XmlDTD xmlDTD = (XmlDTD)o;
        if (this.text != null ? !this.text.equals(xmlDTD.text) : xmlDTD.text != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.text != null ? this.text.hashCode() : 0;
    }

    public String toString() {
        return "XmlDTD{text='" + this.text + '\'' + '}';
    }
}

