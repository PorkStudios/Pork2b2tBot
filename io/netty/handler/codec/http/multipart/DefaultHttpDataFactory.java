/*
 * Decompiled with CFR 0_132.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpData;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.codec.http.multipart.MemoryFileUpload;
import io.netty.handler.codec.http.multipart.MixedAttribute;
import io.netty.handler.codec.http.multipart.MixedFileUpload;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultHttpDataFactory
implements HttpDataFactory {
    public static final long MINSIZE = 16384L;
    public static final long MAXSIZE = -1L;
    private final boolean useDisk;
    private final boolean checkSize;
    private long minSize;
    private long maxSize = -1L;
    private Charset charset = HttpConstants.DEFAULT_CHARSET;
    private final Map<HttpRequest, List<HttpData>> requestFileDeleteMap = PlatformDependent.newConcurrentHashMap();

    public DefaultHttpDataFactory() {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = 16384L;
    }

    public DefaultHttpDataFactory(Charset charset) {
        this();
        this.charset = charset;
    }

    public DefaultHttpDataFactory(boolean useDisk) {
        this.useDisk = useDisk;
        this.checkSize = false;
    }

    public DefaultHttpDataFactory(boolean useDisk, Charset charset) {
        this(useDisk);
        this.charset = charset;
    }

    public DefaultHttpDataFactory(long minSize) {
        this.useDisk = false;
        this.checkSize = true;
        this.minSize = minSize;
    }

    public DefaultHttpDataFactory(long minSize, Charset charset) {
        this(minSize);
        this.charset = charset;
    }

    @Override
    public void setMaxLimit(long maxSize) {
        this.maxSize = maxSize;
    }

    private List<HttpData> getList(HttpRequest request) {
        List<HttpData> list = this.requestFileDeleteMap.get(request);
        if (list == null) {
            list = new ArrayList<HttpData>();
            this.requestFileDeleteMap.put(request, list);
        }
        return list;
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name) {
        if (this.useDisk) {
            DiskAttribute attribute = new DiskAttribute(name, this.charset);
            attribute.setMaxSize(this.maxSize);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute(name, this.minSize, this.charset);
            attribute.setMaxSize(this.maxSize);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        MemoryAttribute attribute = new MemoryAttribute(name);
        attribute.setMaxSize(this.maxSize);
        return attribute;
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name, long definedSize) {
        if (this.useDisk) {
            DiskAttribute attribute = new DiskAttribute(name, definedSize, this.charset);
            attribute.setMaxSize(this.maxSize);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute(name, definedSize, this.minSize, this.charset);
            attribute.setMaxSize(this.maxSize);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        MemoryAttribute attribute = new MemoryAttribute(name, definedSize);
        attribute.setMaxSize(this.maxSize);
        return attribute;
    }

    private static void checkHttpDataSize(HttpData data) {
        try {
            data.checkSize(data.length());
        }
        catch (IOException ignored) {
            throw new IllegalArgumentException("Attribute bigger than maxSize allowed");
        }
    }

    @Override
    public Attribute createAttribute(HttpRequest request, String name, String value) {
        if (this.useDisk) {
            Attribute attribute;
            try {
                attribute = new DiskAttribute(name, value, this.charset);
                attribute.setMaxSize(this.maxSize);
            }
            catch (IOException e) {
                attribute = new MixedAttribute(name, value, this.minSize, this.charset);
                attribute.setMaxSize(this.maxSize);
            }
            DefaultHttpDataFactory.checkHttpDataSize(attribute);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        if (this.checkSize) {
            MixedAttribute attribute = new MixedAttribute(name, value, this.minSize, this.charset);
            attribute.setMaxSize(this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize(attribute);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(attribute);
            return attribute;
        }
        try {
            MemoryAttribute attribute = new MemoryAttribute(name, value, this.charset);
            attribute.setMaxSize(this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize(attribute);
            return attribute;
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public FileUpload createFileUpload(HttpRequest request, String name, String filename, String contentType, String contentTransferEncoding, Charset charset, long size) {
        if (this.useDisk) {
            DiskFileUpload fileUpload = new DiskFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
            fileUpload.setMaxSize(this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize(fileUpload);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(fileUpload);
            return fileUpload;
        }
        if (this.checkSize) {
            MixedFileUpload fileUpload = new MixedFileUpload(name, filename, contentType, contentTransferEncoding, charset, size, this.minSize);
            fileUpload.setMaxSize(this.maxSize);
            DefaultHttpDataFactory.checkHttpDataSize(fileUpload);
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.add(fileUpload);
            return fileUpload;
        }
        MemoryFileUpload fileUpload = new MemoryFileUpload(name, filename, contentType, contentTransferEncoding, charset, size);
        fileUpload.setMaxSize(this.maxSize);
        DefaultHttpDataFactory.checkHttpDataSize(fileUpload);
        return fileUpload;
    }

    @Override
    public void removeHttpDataFromClean(HttpRequest request, InterfaceHttpData data) {
        if (data instanceof HttpData) {
            List<HttpData> fileToDelete = this.getList(request);
            fileToDelete.remove(data);
        }
    }

    @Override
    public void cleanRequestHttpData(HttpRequest request) {
        List<HttpData> fileToDelete = this.requestFileDeleteMap.remove(request);
        if (fileToDelete != null) {
            for (HttpData data : fileToDelete) {
                data.delete();
            }
            fileToDelete.clear();
        }
    }

    @Override
    public void cleanAllHttpData() {
        Iterator<Map.Entry<HttpRequest, List<HttpData>>> i = this.requestFileDeleteMap.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<HttpRequest, List<HttpData>> e = i.next();
            i.remove();
            List<HttpData> fileToDelete = e.getValue();
            if (fileToDelete == null) continue;
            for (HttpData data : fileToDelete) {
                data.delete();
            }
            fileToDelete.clear();
        }
    }

    @Override
    public void cleanRequestHttpDatas(HttpRequest request) {
        this.cleanRequestHttpData(request);
    }

    @Override
    public void cleanAllHttpDatas() {
        this.cleanAllHttpData();
    }
}

