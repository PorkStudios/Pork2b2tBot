/*
 * Decompiled with CFR 0_132.
 */
package com.google.errorprone.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface DoNotMock {
    public String value() default "Create a real instance instead";
}

