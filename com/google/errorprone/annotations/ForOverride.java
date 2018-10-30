/*
 * Decompiled with CFR 0_132.
 */
package com.google.errorprone.annotations;

import com.google.errorprone.annotations.IncompatibleModifiers;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.lang.model.element.Modifier;

@Retention(value=RetentionPolicy.CLASS)
@Target(value={ElementType.METHOD})
@IncompatibleModifiers(value={Modifier.PUBLIC, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL})
public @interface ForOverride {
}

