/*
 * Decompiled with CFR 0_132.
 */
package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;

public interface IExtension {
    public void decodeFrame(Framedata var1) throws InvalidDataException;

    public void encodeFrame(Framedata var1);

    public boolean acceptProvidedExtensionAsServer(String var1);

    public boolean acceptProvidedExtensionAsClient(String var1);

    public void isFrameValid(Framedata var1) throws InvalidDataException;

    public String getProvidedExtensionAsClient();

    public String getProvidedExtensionAsServer();

    public IExtension copyInstance();

    public void reset();

    public String toString();
}

