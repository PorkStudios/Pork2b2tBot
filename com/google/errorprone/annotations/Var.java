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

@Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
@Retention(value=RetentionPolicy.RUNTIME)
@IncompatibleModifiers(value={Modifier.FINAL})
public @interface Var {
}

