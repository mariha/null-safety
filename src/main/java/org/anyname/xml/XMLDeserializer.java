package org.anyname.xml;

import org.anyname.nullsafety.Nullable;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class XMLDeserializer<T extends XMLObject> {

    private static final ValidatorFactory DEFAULT_VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private final ValidatorFactory validatorFactory;

    XMLDeserializer() {
        this(DEFAULT_VALIDATOR_FACTORY);
    }

    XMLDeserializer(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public static <S extends XMLObject> void serialize(S object, OutputStream outputStream)
            throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Product.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(object, outputStream);
    }

    public static <S extends XMLObject> S deserialize(Class<S> objectClass, InputStream inputStream)
            throws JAXBException, ConstraintViolationException {
        XMLDeserializer<S> xmlDeserializer = new XMLDeserializer<>();

        S xmlObject = xmlDeserializer.unmarshal(inputStream, objectClass);
        xmlDeserializer.validate(xmlObject);
        return xmlObject;
    }

    @Nullable
    private T unmarshal(InputStream inputStream, Class<T> objectClass) throws JAXBException {
        final JAXBContext context = JAXBContext.newInstance(objectClass);
        final Unmarshaller m = context.createUnmarshaller();

        return (T) m.unmarshal(inputStream);
    }

    void validate(@Nullable T xmlObject) throws ConstraintViolationException {
        if (xmlObject == null) {
            return;
        }

        final Set<ConstraintViolation<T>> violations = this.validatorFactory.getValidator()
                .validate(xmlObject);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}