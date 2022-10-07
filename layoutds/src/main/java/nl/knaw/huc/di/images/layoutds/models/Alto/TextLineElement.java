package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

//@JacksonXmlRootElement(localName = "TextLineElement")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextLineElement {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "HEIGHT")
    Integer height;
    @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
    Integer width;
    @JacksonXmlProperty(isAttribute = true, localName = "VPOS")
    Integer vpos;
    @JacksonXmlProperty(isAttribute = true, localName = "HPOS")
    Integer hpos;

    @JsonIgnore
    private String type;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getVpos() {
        return vpos;
    }

    public void setVpos(Integer vpos) {
        this.vpos = vpos;
    }

    public Integer getHpos() {
        return hpos;
    }

    public void setHpos(Integer hpos) {
        this.hpos = hpos;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
