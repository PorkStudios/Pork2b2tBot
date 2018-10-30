/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.xml;

public class XmlProcessingInstruction {
    private final String data;
    private final String target;

    public XmlProcessingInstruction(String data, String target) {
        this.data = data;
        this.target = target;
    }

    public String data() {
        return this.data;
    }

    public String target() {
        return this.target;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        XmlProcessingInstruction that = (XmlProcessingInstruction)o;
        if (this.data != null ? !this.data.equals(that.data) : that.data != null) {
            return false;
        }
        if (this.target != null ? !this.target.equals(that.target) : that.target != null) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = this.data != null ? this.data.hashCode() : 0;
        result = 31 * result + (this.target != null ? this.target.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "XmlProcessingInstruction{data='" + this.data + '\'' + ", target='" + this.target + '\'' + '}';
    }
}

