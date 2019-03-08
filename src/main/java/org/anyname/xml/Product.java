package org.anyname.xml;

import org.anyname.nullsafety.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "product")
public class Product implements XMLObject {

    @NotNull
    @XmlAttribute(name = "id")
    private String productId;

    @XmlElement
    private @Nullable String description;

    @XmlElement
    private @Nullable String imageUrl;

    @NotNull
    @XmlElement
    private BigDecimal price;

    @XmlElement
    private @Nullable @Valid User createdBy;

    public Product() {
    } // for jaxb


    public Product(String productId, @Nullable String description, @Nullable String imageUrl,
                   BigDecimal price, @Nullable User createdBy) {
        this.productId = productId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}