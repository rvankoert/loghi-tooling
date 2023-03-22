package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Layer {
    @JacksonXmlProperty(localName = "RegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<RegionRef> regionRefList;
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private Integer zIndex;
    @JacksonXmlProperty(isAttribute = true)
    private String caption;

    public List<RegionRef> getRegionRef() {
        return regionRefList;
    }

    public void setRegionRef(List<RegionRef> regionRefList) {
        this.regionRefList = regionRefList;
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
