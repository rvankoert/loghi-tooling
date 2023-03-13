package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Layer {
    @JacksonXmlProperty(localName = "RegionRef")
    private RegionRef regionRef;
    @JacksonXmlProperty(localName = "ID")
    private String id;
    @JacksonXmlProperty(localName = "zIndex")
    private int zIndex;
    @JacksonXmlProperty
    private String caption;

    public RegionRef getRegionRef() {
        return regionRef;
    }

    public void setRegionRef(RegionRef regionRef) {
        this.regionRef = regionRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
