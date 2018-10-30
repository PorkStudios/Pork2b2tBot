/*
 * Decompiled with CFR 0_132.
 */
package net.daporkchop.toobeetooteebot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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

public class HTTPUtils {
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
        connection = HTTPUtils.createUrlConnection(url);
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
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            int statusCode = connection.getResponseCode();
            String string = result;
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HTTPRequest performPostRequestWithCode(URL url, String post, String contentType) throws IOException {
        HttpURLConnection connection;
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        connection = HTTPUtils.createUrlConnection(url);
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
            inputStream = connection.getInputStream();
            String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            int statusCode = connection.getResponseCode();
            HTTPRequest hTTPRequest = new HTTPRequest(result, statusCode);
            return hTTPRequest;
        }
        catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();
            if (inputStream != null) {
                String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                int statusCode = connection.getResponseCode();
                HTTPRequest hTTPRequest = new HTTPRequest(result, statusCode);
                return hTTPRequest;
            }
            throw e;
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String performPostRequestWithAuth(URL url, String post, String contentType, String auth) throws IOException {
        HttpURLConnection connection;
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        connection = HTTPUtils.createUrlConnection(url);
        byte[] postAsBytes = post.getBytes(Charsets.UTF_8);
        connection.setRequestProperty("Authorization", auth);
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
        HttpURLConnection connection = HTTPUtils.createUrlConnection(url);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] downloadImage(String address) {
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream();
        InputStream is = null;
        URL url = null;
        try {
            int n;
            url = new URL(address);
            is = url.openStream();
            byte[] byteChunk = new byte[4096];
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException e) {}
        }
        return baos.toByteArray();
    }

    public static class HTTPRequest {
        public String data;
        public int statusCode;

        public HTTPRequest(String a, int b) {
            this.data = a;
            this.statusCode = b;
        }
    }

}

