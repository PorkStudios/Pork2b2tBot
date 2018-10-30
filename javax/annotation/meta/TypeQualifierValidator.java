/*
 * Decompiled with CFR 0_132.
 */
package javax.annotation.meta;

import java.lang.annotation.Annotation;
import javax.annotation.Nonnull;
import javax.annotation.meta.When;

public interface TypeQualifierValidator<A extends Annotation> {
    @Nonnull
    public When forConstantValue(@Nonnull A var1, Object var2);
}

