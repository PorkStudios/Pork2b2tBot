/*
 * Decompiled with CFR 0_132.
 */
package io.netty.util.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.SOURCE)
@Target(value={ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PACKAGE, ElementType.TYPE})
@Documented
public @interface UnstableApi {
}

