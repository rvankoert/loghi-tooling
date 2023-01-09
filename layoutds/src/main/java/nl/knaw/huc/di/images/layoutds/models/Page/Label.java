package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Label {
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    private String value;
    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String type;
    @JacksonXmlProperty(isAttribute = true, localName = "comments")
    private String comments;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
