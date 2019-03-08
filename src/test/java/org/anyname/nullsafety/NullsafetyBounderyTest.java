package org.anyname.nullsafety;

import org.assertj.core.api.Condition;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.condition.Not.not;

public class NullsafetyBounderyTest {

    private static final List<Class<? extends Annotation>> notNullAnnotations =
            Arrays.asList(NotNull.class, NotEmpty.class, NotBlank.class);

    private static final List<String> excludedPackages = Collections.emptyList();
    private static final List<Class<?>> excludedClasses = Collections.emptyList();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void verifyNullsafety() throws Exception {
        final Condition<Field> notNullAnnotated = new Condition<>(this::isNotNull,
                "annotated with @javax.validation.constraints.NotNull or similar");
        final Condition<Field> nullableAnnotated = new Condition<>(this::isNullable,
                "annotated with @org.anyname.nullsafety.Nullable");

        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages("org.anyname.nullsafety")
                .setExpandSuperTypes(true)
                .filterInputsBy(s -> s.endsWith(".class")));

        reflections.getSubTypesOf(NullsafetyBoundery.class).stream()
                .filter(xmlType -> !excludedPackages.contains(xmlType.getPackage().getName()))
                .filter(xmlType -> !excludedClasses.contains(xmlType))
                .flatMap(xmlType -> Arrays.stream(xmlType.getDeclaredFields()))
                .forEach(xmlField -> this.softly.assertThat(xmlField).is(eitherOr(notNullAnnotated, nullableAnnotated)));
    }

    // todo: it would be better to run validation on a null value assigned to the field
    // (or query for all annotations => associated validators) and see if it passes validation or not
    private boolean isNotNull(final Field field) {
        return notNullAnnotations.stream().anyMatch(field::isAnnotationPresent);
    }

    private boolean isNullable(final Field field) {
        return field.isAnnotationPresent(Nullable.class);
    }

    private static <T> EitherOr<T> eitherOr(final Condition<T> condition1, final Condition<T> condition2) {
        return new EitherOr<>(condition1, condition2);
    }

    public static class EitherOr<T> extends Condition<T> {
        private final Condition<T> condition1;
        private final Condition<T> condition2;

        private EitherOr(final Condition<T> condition1, final Condition<T> condition2) {
            this.condition1 = condition1;
            this.condition2 = condition2;
        }

        @Override
        public boolean matches(final T value) {
            return this.condition1.matches(value) ^ this.condition2.matches(value);
        }

        @Override
        public String toString() {
            return String.format("Either of: [%s] or [%s]", this.condition1, this.condition2);
        }
    }

    @Test
    public void testXor() throws Exception {
        final Condition<Object> alwaysTrue = new Condition<>(object -> true, "always true");
        final Condition<Object> alwaysFalse = new Condition<>(object -> false, "always false");

        final Object anything = new Object();
        this.softly.assertThat(anything).is(not(eitherOr(alwaysTrue, alwaysTrue)));
        this.softly.assertThat(anything).is(eitherOr(alwaysFalse, alwaysTrue));
        this.softly.assertThat(anything).is(eitherOr(alwaysTrue, alwaysFalse));
        this.softly.assertThat(anything).is(not(eitherOr(alwaysFalse, alwaysFalse)));
    }
}