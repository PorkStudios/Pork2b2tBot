/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

public class XmlNamespace {
    private final String prefix;
    private final String uri;

    public XmlNamespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String prefix() {
        return this.prefix;
    }

    public String uri() {
        return this.uri;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XmlNamespace that = (XmlNamespace)o;
        if (this.prefix != null ? !this.prefix.equals(that.prefix) : that.prefix != null) {
            return false;
        }
        if (this.uri != null ? !this.uri.equals(that.uri) : that.uri != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.prefix != null ? this.prefix.hashCode() : 0;
        result = 31 * result + (this.uri != null ? this.uri.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "XmlNamespace{prefix='" + this.prefix + '\'' + ", uri='" + this.uri + '\'' + '}';
    }
}

