/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

public class XmlAttribute {
    private final String type;
    private final String name;
    private final String prefix;
    private final String namespace;
    private final String value;

    public XmlAttribute(String type, String name, String prefix, String namespace, String value) {
        this.type = type;
        this.name = name;
        this.prefix = prefix;
        this.namespace = namespace;
        this.value = value;
    }

    public String type() {
        return this.type;
    }

    public String name() {
        return this.name;
    }

    public String prefix() {
        return this.prefix;
    }

    public String namespace() {
        return this.namespace;
    }

    public String value() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XmlAttribute that = (XmlAttribute)o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.namespace != null ? !this.namespace.equals(that.namespace) : that.namespace != null) {
            return false;
        }
        if (this.prefix != null ? !this.prefix.equals(that.prefix) : that.prefix != null) {
            return false;
        }
        if (this.type != null ? !this.type.equals(that.type) : that.type != null) {
            return false;
        }
        if (this.value != null ? !this.value.equals(that.value) : that.value != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + this.name.hashCode();
        result = 31 * result + (this.prefix != null ? this.prefix.hashCode() : 0);
        result = 31 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "XmlAttribute{type='" + this.type + '\'' + ", name='" + this.name + '\'' + ", prefix='" + this.prefix + '\'' + ", namespace='" + this.namespace + '\'' + ", value='" + this.value + '\'' + '}';
    }
}

