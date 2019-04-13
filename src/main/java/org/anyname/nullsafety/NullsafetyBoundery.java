package org.anyname.nullsafety;

/**
 * Marker interface for data-objects used at the boundaries of the system. Often, theirs fields are filled with input
 * data at runtime (by binding frameworks by reflection), so the values can not be verified at compile-time for
 * satisfying null-safety expectations.
 * <p/>
 * Objects of types implementing this interface are checked to have all fields marked either:
 * <ul>
 *   <li>as @{@link org.anyname.nullsafety.Nullable} - propagated value is verified at compile-time to be dereferenced
 *     after a null-check only; or</li>
 *   <li>as @{@link javax.validation.constraints.NotNull} - injected and verified at runtime, also a semantic default
 *     for compile-time verification when no {@code @Nullable} annotation is present.</li>
 * </ul>
 */
public interface NullsafetyBoundery {
}
