package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import java.util.UUID;

public class RegionRefIndexed {
    @JacksonXmlProperty(isAttribute = true, localName = "index")
    private int index;
    @JacksonXmlProperty(isAttribute = true, localName = "regionRef")
    private String regionRef;

    public RegionRefIndexed() {

        this.setRegionRef(null);
    }

    public RegionRefIndexed(String id, int i) {
        this.setRegionRef(id);
        this.setIndex(i);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getRegionRef() {
        return regionRef;
    }

    public void setRegionRef(String regionRef) {
        if (Strings.isNullOrEmpty(regionRef)) {
            this.regionRef = "region_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(regionRef.charAt(0))) {
            this.regionRef = "region_" + regionRef;
        } else {
            this.regionRef = regionRef;
        }
    }
}
