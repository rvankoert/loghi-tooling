package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import java.util.UUID;

public abstract class Region implements IRegion {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "custom")
    private String custom;

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String regionType;

    @JacksonXmlProperty(isAttribute = true, localName = "orientation")
    private Double orientation;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private Coords coords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (Strings.isNullOrEmpty(id)) {
            this.id = "region_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(id.charAt(0))) {
            this.id = "region_" + id;
        } else {
            this.id = id;
        }
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getRegionType() {
        return regionType;
    }

    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    public Double getOrientation() {
        return orientation;
    }

    public void setOrientation(Double orientation) {
        this.orientation = orientation;
    }

    public Coords getCoords() {
        if (coords == null) {
            coords = new Coords();
        }
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

}
