package org.anyname.nullsafety;

import org.anyname.nullsafety.NullableTest.NonNullApiClass.StandardApiExtention;
import org.anyname.nullsafety.NullableTest.StandardClass.NonNullApiExtention;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tests with idioms interesting for null-safety analysis. They more document behaviour of how nullable annotations are
 * interpreted by various tools then test anything.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class NullableTest {

    private final StandardClass standardApiClass = new StandardClass();
    private final NonNullApiClass nonNullApiClass = new NonNullApiClass();
    private final NonNullFieldsClass nonNullFieldsClass = new NonNullFieldsClass();

    private final NonNullApiExtention nonNullApiInStandardScope = new NonNullApiExtention();
    private final StandardApiExtention standardApiInNonNullScope = new StandardApiExtention();

    private final List<@Nullable Object> listOfNullables = new ArrayList<>();
    private final List<Object> listOfUnknowns = new ArrayList<>();

    @Test(expected = NullPointerException.class)
    public void dereferencingNullable_isPrevented() {
        // [NullAway] dereferenced expression this.nonNullFieldsClass.nullableField is @Nullable
        // IJ warns: method invocation 'toString' may produce 'NullPointerException'
        this.nonNullFieldsClass.nullableField.toString();

        this.standardApiClass.returnsNullable().toString();
        this.nonNullApiClass.returnsNullable().toString();
        this.nonNullFieldsClass.returnsNullable().toString();

        // detected by IJ, not detected by NullAway
        this.standardApiInNonNullScope.returnsNullable().toString(); // not specified, IJ: inherited from super
        this.listOfNullables.get(0).toString();
        synchronized (this.standardApiClass.returnsNullable()) {
        }
    }

    @Test
    public void dereferencingNullable_afterNullCheck_isOK() {
        final Object nullableField = this.nonNullFieldsClass.nullableField;
        if (nullableField != null) {
            nullableField.toString();
        }

        final Object nullableReturn = this.standardApiClass.returnsNullable();
        if (Objects.nonNull(nullableReturn)) {
            nullableReturn.toString(); // [NullAway] dereferenced expression nullableReturn is @Nullable
        }

        try {
            // could be useful at system boundaries if everything that comes from 3rd party libs was nullable by default
            Objects.requireNonNull(this.nonNullApiClass.returnsNullable()).toString();
        } catch (NullPointerException ignore) {
            // expected if we believed it is never null but actually at runtime it turns out to be null
            // (better detect incorrect data sooner than later)
        }
    }

    @Test
    public void dereferencingNonNull_isOK() {
        this.nonNullFieldsClass.nonNullField.toString();
        this.nonNullApiClass.returnsNonNull().toString();
        this.standardApiInNonNullScope.returnsNonNull().toString();
    }

    @Test(expected = NullPointerException.class)
    public void dereferencingUnknown_couldBePrevented() {
        this.standardApiClass.unknownField.toString();
        this.nonNullApiClass.unknownField.toString();

        this.standardApiClass.returnsUnknown().toString();
        this.standardApiInNonNullScope.returnsUnknown().toString();
        this.nonNullFieldsClass.returnsUnknown().toString();

        this.listOfUnknowns.get(0).toString();
    }

    @Test(expected = NullPointerException.class)
    public void externalLibraryCalls_couldBeDefensiveByDefault() {
        // NullAway, IJ: optimistically assumes non-null return
        System.getSecurityManager().toString();

        // NullAway, IJ: optimistically assumes nullable param
        JUnitMatchers.isException(null);
    }

    @Test(expected = NullPointerException.class)
    public void assigningNullToNonNull_isPrevented() {
        // [NullAway] passing @Nullable parameter 'null' where @NonNull is required
        // IJ warns: passing 'null' argument to parameter annotated as '@NonNull`
        this.nonNullApiClass.acceptsNonNull(null);
        this.nonNullApiInStandardScope.acceptsNonNull(null); // inherited from super
        this.standardApiInNonNullScope.acceptsNonNull(null); // unspecified reset by scope's default

        // [NullAway] assigning @Nullable expression to @NonNull field
        // IJ warns: 'null' is assigned to a variable that is annotated with @NotNull
        this.nonNullFieldsClass.nonNullField = null;
    }

    @Test
    public void assigningNullToNullable_isOK() {
        this.nonNullFieldsClass.nullableField = null;
        this.standardApiClass.nullableField = null;
        this.nonNullApiClass.nullableField = null;

        this.nonNullFieldsClass.acceptsNullable(null);
        this.standardApiClass.acceptsNullable(null);
        this.standardApiInNonNullScope.acceptsNullable(null); // inherited from super
        this.nonNullApiClass.acceptsNullable(null);

        this.listOfNullables.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void assigningNullToUnknown_couldBePrevented() {
        // default NonNull not set, so no warn and NPE is thrown
        // todo IJ: can be set with 'Suggest @Nullable annotations ...'
        this.nonNullFieldsClass.acceptsUnknown(null);
        this.standardApiClass.acceptsUnknown(null);
        this.standardApiInNonNullScope.acceptsUnknown(null);

        this.standardApiClass.unknownField = null;
        this.nonNullApiClass.unknownField = null;

        this.listOfUnknowns.add(null);
    }


    @Test
    public void assigningNonNull_isAlwaysOK() {
        this.nonNullFieldsClass.nullableField = new Object();
        this.nonNullFieldsClass.nonNullField = new Object();
        this.nonNullFieldsClass.acceptsNullable(new Object());
        this.nonNullFieldsClass.acceptsUnknown(new Object());

        this.standardApiClass.nullableField = new Object();
        this.standardApiClass.unknownField = new Object();
        this.standardApiClass.acceptsNullable(new Object());
        this.standardApiClass.acceptsUnknown(new Object());

        this.nonNullApiClass.nullableField = new Object();
        this.nonNullApiClass.unknownField = new Object();
        this.nonNullApiClass.acceptsNullable(new Object());
        this.nonNullApiClass.acceptsNonNull(new Object());
    }


    static class StandardClass {

        Object unknownField;
        @Nullable
        Object nullableField;


        @Nullable
        Object returnsNullable() {
            return null;
        }

        Object returnsUnknown() {
            return null;
        }

        void acceptsNullable(@Nullable final Object param) {
            // IJ warns: method invocation 'toString' may produce 'NullPointerException'
            // param.toString();

            if (param != null) {
                param.toString();
            }
        }

        void acceptsUnknown(final Object param) throws NullPointerException {
            param.toString();
        }


        static class NonNullApiExtention extends NonNullApiClass {
            @Override
            Object returnsNullable() {
                return super.returnsNullable();
            }

            // if @Nullable annotation is used (note: returning 'null' would violate the contract)
            // [NullAway] method returns @Nullable, but superclass method NonNullApiClass.returnsNonNull() returns @NonNull
            // IJ warns: Method annotated with @Nullable must not overwrite @NonNullApi method
            // currently IJ warns: Not annotated method overrides method annotated with @NonNullApi
            @Override
            Object returnsNonNull() {
                return super.returnsNonNull();
            }

            @Override
            void acceptsNullable(final Object param) {
                super.acceptsNullable(param);
            }

            // IJ warns: Not annotated param overrides method annotated with @NonNullApi
            @Override
            void acceptsNonNull(final Object param) {
                super.acceptsNonNull(param);
            }
        }
    }

    @NonNullApi
    static class NonNullApiClass {

        Object unknownField;
        @Nullable
        Object nullableField;

        @Nullable
        Object returnsNullable() {
            return null;
        }

        Object returnsNonNull() {
            // [NullAway] returning @Nullable expression from method with @NonNull return type
            // IJ warns: 'null' is returned by the method declared as NonNullApi
            // return null;

            return new Object();
        }

        void acceptsNullable(@Nullable final Object param) {
            // [NullAway] dereferenced expression param is @Nullable
            // IJ warns: method invocation 'toString' may produce 'NullPointerException'
            // param.toString();

            if (param != null) {
                param.toString();
            }
        }

        // IJ warns: Overridden method parameters are not annotated
        void acceptsNonNull(final Object param) {
            param.toString();
        }


        // in scope of @NonNullApi
        static class StandardApiExtention extends StandardClass {

            Object returnsNonNull() {
                return new Object();
            }

            void acceptsNonNull(final Object param) {
                param.toString();
            }

            // IJ: inherits @Nullable from super :/
            // [NullAway] returning @Nullable expression from method with @NonNull return type
            @Override
            Object returnsNullable() {
                return super.returnsNullable();
            }

            // unknown remains unknown
            @Override
            Object returnsUnknown() {
                return super.returnsUnknown();
            }

            @Override
            void acceptsNullable(@Nullable final Object param) {
                // if @Nullable on the param is missing (note: in non-null scope would violate the contract)
                // [NullAway] parameter param is @NonNull, but parameter in superclass method
                //            StandardClass.acceptsNullable(@Nullable Object) is @Nullable
                // IJ warns: 'Parameter annotated @Nullable must not override @Nullable parameter'
                super.acceptsNullable(param);
            }

            @Override
            void acceptsUnknown(final Object param) throws NullPointerException {
                super.acceptsUnknown(param);
            }
        }
    }

    @NonNullFields
    static class NonNullFieldsClass {

        // if not initialized (either here or in constructor), IJ warns
        // [NullAway] @NonNull field unknownField not initialized
        Object nonNullField = new Object();

        @Nullable
        Object nullableField;

        @Nullable
        Object returnsNullable() {
            return null;
        }

        Object returnsUnknown() {
            return null;
        }

        void acceptsNullable(@Nullable final Object param) {
            if (param != null) {
                param.toString();
            }
        }

        void acceptsUnknown(final Object param) throws NullPointerException {
            param.toString();
        }
    }
}