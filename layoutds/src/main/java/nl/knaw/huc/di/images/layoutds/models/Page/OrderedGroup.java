package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderedGroup {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "caption")
    private String caption;

    @JacksonXmlProperty(localName = "RegionRefIndexed", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RegionRefIndexed> regionRefIndexedList;

    public OrderedGroup() {
        this.setId(null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (Strings.isNullOrEmpty(id)) {
            this.id = "orderedgroup_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(id.charAt(0))) {
            this.id = "orderedgroup_" + id;
        } else {
            this.id = id;
        }
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<RegionRefIndexed> getRegionRefIndexedList() {
        if (regionRefIndexedList == null) {
            regionRefIndexedList = new ArrayList<>();
        }
        return regionRefIndexedList;
    }

    public void setRegionRefIndexedList(List<RegionRefIndexed> regionRefIndexedList) {
        this.regionRefIndexedList = regionRefIndexedList;
    }
}
