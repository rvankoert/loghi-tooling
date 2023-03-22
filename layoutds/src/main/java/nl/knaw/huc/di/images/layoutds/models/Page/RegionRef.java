package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RegionRef {
    @JacksonXmlProperty(isAttribute = true)
    private String regionRef;

    public String getRegionRef() {
        return regionRef;
    }

    public void setRegionRef(String regionRef) {
        this.regionRef = regionRef;
    }
}
