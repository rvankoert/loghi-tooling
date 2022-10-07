package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Margin {
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

    public Margin() {

    }

    public Margin(String id, Integer height, Integer width, Integer vpos, Integer hpos) {
        this.id = id;
        this.height = height;
        this.width = width;
        this.vpos = vpos;
        this.hpos = hpos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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


}
