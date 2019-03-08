package org.anyname.nullsafety;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that fields in an annotated scope are non-null unless otherwise explicitly specified as {@link Nullable}.
 * Annotated scope can be package (in package-info.java), a class or enum type. Inner and nested classes are in scope of
 * enclosing them type.
 *
 * @see org.anyname.nullsafety
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Nonnull
@TypeQualifierDefault(ElementType.FIELD)
public @interface NonNullFields {
}
