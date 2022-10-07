package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "String")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AltoString extends TextLineElement {

    @JacksonXmlProperty(isAttribute = true, localName = "WC")
    Float wordConfidence;

    @JacksonXmlProperty(isAttribute = true, localName = "STYLEREFS")
    String styleRefs;

    @JacksonXmlProperty(isAttribute = true, localName = "STYLE")
    FontStylesType style;
    @JacksonXmlProperty(isAttribute = true, localName = "CONTENT")
    String content;

    @JacksonXmlProperty(isAttribute = true, localName = "SUBS_TYPE")
    SubsType subsType;

    @JacksonXmlProperty(isAttribute = true, localName = "SUBS_CONTENT")
    String subsContent;
    @JacksonXmlProperty(isAttribute = true, localName = "CC")
    private String characterConfidence;

    public Float getWordConfidence() {
        return wordConfidence;
    }

    public void setWordConfidence(Float wordConfidence) {
        this.wordConfidence = wordConfidence;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public void setCharacterConfidence(String cc) {
        this.characterConfidence = cc;
    }

    public String getCharacterConfidence() {
        return characterConfidence;
    }

    public void setStyleRefs(String styleRefs) {
        this.styleRefs = styleRefs;
    }

    public String getStyleRefs() {
        return styleRefs;
    }

    public void setSubsContent(String subsContent) {
        this.subsContent = subsContent;
    }

    public String getSubsContent() {
        return subsContent;
    }

    public void setSubsType(SubsType subsType) {
        this.subsType = subsType;
    }

    public SubsType getSubsType() {
        return subsType;
    }

    public void setStyle(FontStylesType style) {
        this.style = style;
    }

    public FontStylesType getStyle() {
        return style;
    }
}
