package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GraphemeBase {
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TextEquiv> textEquiv;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private Integer index;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean ligature;

    // TODO make enum with values: base, combining
    @JacksonXmlProperty(isAttribute = true)
    private String charType;

    @JacksonXmlProperty(isAttribute = true)
    private String custom;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    public List<TextEquiv> getTextEquiv() {
        return textEquiv;
    }

    public void setTextEquiv(List<TextEquiv> textEquiv) {
        this.textEquiv = textEquiv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Boolean getLigature() {
        return ligature;
    }

    public void setLigature(Boolean ligature) {
        this.ligature = ligature;
    }

    public String getCharType() {
        return charType;
    }

    public void setCharType(String charType) {
        this.charType = charType;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
