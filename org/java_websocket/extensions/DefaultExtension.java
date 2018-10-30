/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.framing.Framedata;

public class DefaultExtension
implements IExtension {
    public void decodeFrame(Framedata inputFrame) throws InvalidDataException {
    }

    public void encodeFrame(Framedata inputFrame) {
    }

    public boolean acceptProvidedExtensionAsServer(String inputExtension) {
        return true;
    }

    public boolean acceptProvidedExtensionAsClient(String inputExtension) {
        return true;
    }

    public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
        if (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3()) {
            throw new InvalidFrameException("bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3());
        }
    }

    public String getProvidedExtensionAsClient() {
        return "";
    }

    public String getProvidedExtensionAsServer() {
        return "";
    }

    public IExtension copyInstance() {
        return new DefaultExtension();
    }

    public void reset() {
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return this.getClass() == o.getClass();
    }
}

