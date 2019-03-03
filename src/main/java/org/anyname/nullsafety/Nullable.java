package org.anyname.nullsafety;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.CheckForNull;
import javax.annotation.meta.TypeQualifierNickname;

/**
 * Declares that an annotated element can be {@code null}.
 * Can be applied to a field, method's return value, parameter, or generic type argument.
 *
 * If used in scope of {@link NonNullApi} or {@link NonNullFields}, takes precedence over them.
 *
 * @see org.anyname.nullsafety
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CheckForNull
@TypeQualifierNickname
public @interface Nullable {
}