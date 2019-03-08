package org.anyname.xml;

import org.anyname.nullsafety.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class User implements XMLObject {

    @NotNull
    @XmlAttribute
    private Long id;

    @NotNull
    @XmlElement
    private String name;

    @XmlElement
    private @Nullable String email;

    public User() {
    } // for jaxb

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}