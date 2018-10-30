/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

public class XmlDocumentStart {
    private final String encoding;
    private final String version;
    private final boolean standalone;
    private final String encodingScheme;

    public XmlDocumentStart(String encoding, String version, boolean standalone, String encodingScheme) {
        this.encoding = encoding;
        this.version = version;
        this.standalone = standalone;
        this.encodingScheme = encodingScheme;
    }

    public String encoding() {
        return this.encoding;
    }

    public String version() {
        return this.version;
    }

    public boolean standalone() {
        return this.standalone;
    }

    public String encodingScheme() {
        return this.encodingScheme;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XmlDocumentStart that = (XmlDocumentStart)o;
        if (this.standalone != that.standalone) {
            return false;
        }
        if (this.encoding != null ? !this.encoding.equals(that.encoding) : that.encoding != null) {
            return false;
        }
        if (this.encodingScheme != null ? !this.encodingScheme.equals(that.encodingScheme) : that.encodingScheme != null) {
            return false;
        }
        if (this.version != null ? !this.version.equals(that.version) : that.version != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.encoding != null ? this.encoding.hashCode() : 0;
        result = 31 * result + (this.version != null ? this.version.hashCode() : 0);
        result = 31 * result + (this.standalone ? 1 : 0);
        result = 31 * result + (this.encodingScheme != null ? this.encodingScheme.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "XmlDocumentStart{encoding='" + this.encoding + '\'' + ", version='" + this.version + '\'' + ", standalone=" + this.standalone + ", encodingScheme='" + this.encodingScheme + '\'' + '}';
    }
}

