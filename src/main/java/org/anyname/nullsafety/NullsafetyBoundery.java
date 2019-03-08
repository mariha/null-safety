package org.anyname.nullsafety;

/**
 * Marker interface for objects at the boundaries of the system which usually have fields filled at runtime with an
 * input values (by binding frameworks threw reflection).
 * <p>
 * Objects of types marked with this interface are verified to have all fields marked either as @Nullable (propagated
 * value is verified at compile-time) or as @NotNull (injected and verified at runtime, also a default for compile-time
 * verification when no @Nullable annotation is present).
 */
public interface NullsafetyBoundery {
}
