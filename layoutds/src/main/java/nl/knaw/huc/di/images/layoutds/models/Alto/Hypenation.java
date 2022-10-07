package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Hypenation extends TextLineElement {
    @JacksonXmlProperty(isAttribute = true, localName = "CONTENT")
    String content;
    @JacksonXmlProperty(isAttribute = true, localName = "SUBS_TYPE")
    String subsType;
    @JacksonXmlProperty(isAttribute = true, localName = "SUBS_CONTENT")
    String subsContent;

    @JacksonXmlProperty(isAttribute = true, localName = "WC")
    float wordConfidence;
    @JacksonXmlProperty(isAttribute = true, localName = "CC")
    float characterConfidence;

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
