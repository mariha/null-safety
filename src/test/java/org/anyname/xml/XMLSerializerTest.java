package org.anyname.xml;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;
import org.junit.Test;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

public class XMLSerializerTest {

    @Test
    public void testNotNullValidation() throws Exception {
        final XMLSerializer<NotNullAnnotation> deserializer = new XMLSerializer();

        try {
            // NullAway won't allow to call new NotNullAnnotation(null) so we cheat it
            // so that we can test our validation code when 'null' value is injected at runtime
            NotNullAnnotation objToValidate = new NotNullAnnotation(new Object());
            NotNullAnnotation.class.getDeclaredField("field").set(objToValidate, null);

            deserializer.validate(objToValidate);
            Fail.fail("NotNull validation of 'null' object should have thrown an ex");
        } catch (final ConstraintViolationException ex) {
            // expected
        }

        deserializer.validate(new NotNullAnnotation(new Object()));
    }

    private static class NotNullAnnotation implements XMLObject {
        @NotNull
        Object field;

        private NotNullAnnotation(final Object field) {
            this.field = field;
        }
    }

    @Test
    public void serializeDeserialize() throws Exception {
        User user = new User(13L, "Jan", "jan@kowalski.com");
        Product orginalProduct = new Product("KS1024", "Ravensburger Krypt Silver", "https://ssl-static-images.ravensburger.de/images/produktseiten/1024/15964_1.jpg", new BigDecimal(19.99), user);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        XMLSerializer.serialize(orginalProduct, outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Product readProduct = XMLSerializer.deserialize(Product.class, inputStream);

        Assertions.assertThat(readProduct).isEqualToComparingFieldByFieldRecursively(orginalProduct);
    }
}