/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.porklib.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

public class HttpUtils {
    public static HttpURLConnection createUrlConnection(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String performPostRequest(URL url, String post, String contentType) throws IOException {
        HttpURLConnection connection;
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        connection = HttpUtils.createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);
        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);
        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        }
        finally {
            IOUtils.closeQuietly(outputStream);
        }
        InputStream inputStream = null;
        try {
            String result;
            inputStream = connection.getInputStream();
            String string = result = IOUtils.toString(inputStream, Charsets.UTF_8);
            return string;
        }
        catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();
            if (inputStream != null) {
                String result;
                String string = result = IOUtils.toString(inputStream, Charsets.UTF_8);
                return string;
            }
            throw e;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static String performGetRequest(URL url) throws IOException {
        Validate.notNull(url);
        HttpURLConnection connection = HttpUtils.createUrlConnection(url);
        InputStream inputStream = null;
        try {
            String result;
            inputStream = connection.getInputStream();
            String string = result = IOUtils.toString(inputStream, Charsets.UTF_8);
            return string;
        }
        catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();
            if (inputStream != null) {
                String result;
                String string = result = IOUtils.toString(inputStream, Charsets.UTF_8);
                return string;
            }
            throw e;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static URL constantURL(String url) {
        try {
            return new URL(url);
        }
        catch (MalformedURLException ex) {
            throw new Error("Couldn't create constant for " + url, ex);
        }
    }

    public static String buildQuery(Map<String, Object> query) {
        if (query == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
            if (entry.getValue() == null) continue;
            builder.append('=');
            try {
                builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {}
        }
        return builder.toString();
    }

    public static URL concatenateURL(URL url, String query) {
        try {
            if (url.getQuery() != null && url.getQuery().length() > 0) {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query);
            }
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
        }
        catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", ex);
        }
    }
}

