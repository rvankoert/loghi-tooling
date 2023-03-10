package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.Date;
import java.util.List;

//@JacksonXmlRootElement(localName = "MetadataItem")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MetadataItem {
    // FIXME Should be a list
    @JacksonXmlProperty(localName = "Labels",namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    Labels labels;

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    // should be one of "author", "imageProperties", "processingStep", "other"
    private String type;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;

    @JacksonXmlProperty(isAttribute = true, localName = "value")
    private String value;

    @JacksonXmlProperty(isAttribute = true, localName = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date date;

    public Labels getLabels() {
        return labels;
    }

    public void setLabels(Labels labels) {
        this.labels = labels;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
