/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebSocketExtensionUtil {
    private static final String EXTENSION_SEPARATOR = ",";
    private static final String PARAMETER_SEPARATOR = ";";
    private static final char PARAMETER_EQUAL = '=';
    private static final Pattern PARAMETER = Pattern.compile("^([^=]+)(=[\\\"]?([^\\\"]+)[\\\"]?)?$");

    static boolean isWebsocketUpgrade(HttpHeaders headers) {
        return headers.containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true) && headers.contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
    }

    public static List<WebSocketExtensionData> extractExtensions(String extensionHeader) {
        String[] rawExtensions = extensionHeader.split(EXTENSION_SEPARATOR);
        if (rawExtensions.length > 0) {
            ArrayList<WebSocketExtensionData> extensions = new ArrayList<WebSocketExtensionData>(rawExtensions.length);
            for (String rawExtension : rawExtensions) {
                Map<String, String> parameters;
                String[] extensionParameters = rawExtension.split(PARAMETER_SEPARATOR);
                String name = extensionParameters[0].trim();
                if (extensionParameters.length > 1) {
                    parameters = new HashMap(extensionParameters.length - 1);
                    for (int i = 1; i < extensionParameters.length; ++i) {
                        String parameter = extensionParameters[i].trim();
                        Matcher parameterMatcher = PARAMETER.matcher(parameter);
                        if (!parameterMatcher.matches() || parameterMatcher.group(1) == null) continue;
                        parameters.put(parameterMatcher.group(1), parameterMatcher.group(3));
                    }
                } else {
                    parameters = Collections.emptyMap();
                }
                extensions.add(new WebSocketExtensionData(name, parameters));
            }
            return extensions;
        }
        return Collections.emptyList();
    }

    static String appendExtension(String currentHeaderValue, String extensionName, Map<String, String> extensionParameters) {
        StringBuilder newHeaderValue = new StringBuilder(currentHeaderValue != null ? currentHeaderValue.length() : extensionName.length() + 1);
        if (currentHeaderValue != null && !currentHeaderValue.trim().isEmpty()) {
            newHeaderValue.append(currentHeaderValue);
            newHeaderValue.append(EXTENSION_SEPARATOR);
        }
        newHeaderValue.append(extensionName);
        for (Map.Entry<String, String> extensionParameter : extensionParameters.entrySet()) {
            newHeaderValue.append(PARAMETER_SEPARATOR);
            newHeaderValue.append(extensionParameter.getKey());
            if (extensionParameter.getValue() == null) continue;
            newHeaderValue.append('=');
            newHeaderValue.append(extensionParameter.getValue());
        }
        return newHeaderValue.toString();
    }

    private WebSocketExtensionUtil() {
    }
}

