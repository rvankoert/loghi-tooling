package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "TextLine")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextLine implements  Comparable<TextLine>
{
    @JacksonXmlProperty(isAttribute = true, localName = "HEIGHT")
    Integer height;
    @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
    Integer width;
    @JacksonXmlProperty(isAttribute = true, localName = "VPOS")
    Integer vpos;
    @JacksonXmlProperty(isAttribute = true, localName = "HPOS")
    Integer hpos;
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "BASELINE")
    Integer BASELINE;

    @JacksonXmlProperty(isAttribute = true, localName = "CS")
    private Boolean cs;

    @JacksonXmlProperty()
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonSerialize(using = TextLineElementSerializer.class)
    private List<TextLineElement> textLineElements = new ArrayList<>();

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

    @Override
    public int compareTo(TextLine b) {
        int c;
        c = this.getHpos() - b.getHpos();
        if (c == 0)
            c = this.getVpos() - b.getVpos();
        return c;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBASELINE() {
        return BASELINE;
    }

    public void setBASELINE(Integer BASELINE) {

        this.BASELINE = BASELINE;
    }

    public List<TextLineElement> getTextLineElements() {
        return textLineElements;
    }

    public void setTextLineElements(List<TextLineElement> test) {
        this.textLineElements = test;
    }

    public void setCS(Boolean cs) {
        this.cs = cs;
    }

    public Boolean getCS() {
        return cs;
    }
}
