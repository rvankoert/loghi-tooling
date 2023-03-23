package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class UserAttribute {
    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;
    @JacksonXmlProperty(isAttribute = true, localName = "description")
    private String description;

    // TODO make enum with values: string, integer, boolean, float
    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String type;
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
