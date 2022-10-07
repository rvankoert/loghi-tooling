package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class HocrMeta {
    @JacksonXmlProperty(isAttribute = true, localName = "http-equiv")
    private String equiv;

    @JacksonXmlProperty(isAttribute = true, localName = "name")
    private String name;
    @JacksonXmlProperty(isAttribute = true, localName = "content")
    private String content;

    public HocrMeta(){}

    public HocrMeta(String name, String content, String equiv){
        this.name = name;
        this.content = content;
        this.equiv = equiv;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setEquiv(String equiv) {
        this.equiv = equiv;
    }

    public String getEquiv() {
        return equiv;
    }
}
