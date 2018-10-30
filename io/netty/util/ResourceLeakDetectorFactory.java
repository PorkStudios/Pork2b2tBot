/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class ResourceLeakDetectorFactory {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ResourceLeakDetectorFactory.class);
    private static volatile ResourceLeakDetectorFactory factoryInstance = new DefaultResourceLeakDetectorFactory();

    public static ResourceLeakDetectorFactory instance() {
        return factoryInstance;
    }

    public static void setResourceLeakDetectorFactory(ResourceLeakDetectorFactory factory) {
        factoryInstance = ObjectUtil.checkNotNull(factory, "factory");
    }

    public final <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> resource) {
        return this.newResourceLeakDetector(resource, 128);
    }

    @Deprecated
    public abstract <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> var1, int var2, long var3);

    public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> resource, int samplingInterval) {
        return this.newResourceLeakDetector(resource, 128, Long.MAX_VALUE);
    }

    private static final class DefaultResourceLeakDetectorFactory
    extends ResourceLeakDetectorFactory {
        private final Constructor<?> obsoleteCustomClassConstructor;
        private final Constructor<?> customClassConstructor;

        DefaultResourceLeakDetectorFactory() {
            String customLeakDetector;
            try {
                customLeakDetector = (String)AccessController.doPrivileged(new PrivilegedAction<String>(){

                    @Override
                    public String run() {
                        return SystemPropertyUtil.get("io.netty.customResourceLeakDetector");
                    }
                });
            }
            catch (Throwable cause) {
                logger.error("Could not access System property: io.netty.customResourceLeakDetector", cause);
                customLeakDetector = null;
            }
            if (customLeakDetector == null) {
                this.customClassConstructor = null;
                this.obsoleteCustomClassConstructor = null;
            } else {
                this.obsoleteCustomClassConstructor = DefaultResourceLeakDetectorFactory.obsoleteCustomClassConstructor(customLeakDetector);
                this.customClassConstructor = DefaultResourceLeakDetectorFactory.customClassConstructor(customLeakDetector);
            }
        }

        private static Constructor<?> obsoleteCustomClassConstructor(String customLeakDetector) {
            try {
                Class<?> detectorClass = Class.forName(customLeakDetector, true, PlatformDependent.getSystemClassLoader());
                if (ResourceLeakDetector.class.isAssignableFrom(detectorClass)) {
                    return detectorClass.getConstructor(Class.class, Integer.TYPE, Long.TYPE);
                }
                logger.error("Class {} does not inherit from ResourceLeakDetector.", (Object)customLeakDetector);
            }
            catch (Throwable t) {
                logger.error("Could not load custom resource leak detector class provided: {}", (Object)customLeakDetector, (Object)t);
            }
            return null;
        }

        private static Constructor<?> customClassConstructor(String customLeakDetector) {
            try {
                Class<?> detectorClass = Class.forName(customLeakDetector, true, PlatformDependent.getSystemClassLoader());
                if (ResourceLeakDetector.class.isAssignableFrom(detectorClass)) {
                    return detectorClass.getConstructor(Class.class, Integer.TYPE);
                }
                logger.error("Class {} does not inherit from ResourceLeakDetector.", (Object)customLeakDetector);
            }
            catch (Throwable t) {
                logger.error("Could not load custom resource leak detector class provided: {}", (Object)customLeakDetector, (Object)t);
            }
            return null;
        }

        @Override
        public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> resource, int samplingInterval, long maxActive) {
            if (this.obsoleteCustomClassConstructor != null) {
                try {
                    ResourceLeakDetector leakDetector = (ResourceLeakDetector)this.obsoleteCustomClassConstructor.newInstance(resource, samplingInterval, maxActive);
                    logger.debug("Loaded custom ResourceLeakDetector: {}", (Object)this.obsoleteCustomClassConstructor.getDeclaringClass().getName());
                    return leakDetector;
                }
                catch (Throwable t) {
                    logger.error("Could not load custom resource leak detector provided: {} with the given resource: {}", this.obsoleteCustomClassConstructor.getDeclaringClass().getName(), resource, t);
                }
            }
            ResourceLeakDetector resourceLeakDetector = new ResourceLeakDetector(resource, samplingInterval, maxActive);
            logger.debug("Loaded default ResourceLeakDetector: {}", (Object)resourceLeakDetector);
            return resourceLeakDetector;
        }

        @Override
        public <T> ResourceLeakDetector<T> newResourceLeakDetector(Class<T> resource, int samplingInterval) {
            if (this.customClassConstructor != null) {
                try {
                    ResourceLeakDetector leakDetector = (ResourceLeakDetector)this.customClassConstructor.newInstance(resource, samplingInterval);
                    logger.debug("Loaded custom ResourceLeakDetector: {}", (Object)this.customClassConstructor.getDeclaringClass().getName());
                    return leakDetector;
                }
                catch (Throwable t) {
                    logger.error("Could not load custom resource leak detector provided: {} with the given resource: {}", this.customClassConstructor.getDeclaringClass().getName(), resource, t);
                }
            }
            ResourceLeakDetector resourceLeakDetector = new ResourceLeakDetector(resource, samplingInterval);
            logger.debug("Loaded default ResourceLeakDetector: {}", (Object)resourceLeakDetector);
            return resourceLeakDetector;
        }

    }

}

