package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "RegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<RegionRef> regionRefList = new ArrayList<>();
    @JacksonXmlProperty(isAttribute = true)
    private String id;
    @JacksonXmlProperty(isAttribute = true)
    private Integer zIndex;
    @JacksonXmlProperty(isAttribute = true)
    private String caption;

    public List<RegionRef> getRegionRefList() {
        return regionRefList;
    }

    public void setRegionRefList(List<RegionRef> regionRefList) {
        this.regionRefList = regionRefList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getzIndex() {
        return zIndex;
    }

    public void setzIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void addRegionRef(RegionRef regionRef) {
        this.regionRefList.add(regionRef);
    }

}
