package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SeparatorRegion extends Region {
    @JacksonXmlProperty(isAttribute = true, localName = "colour")
    private String colour;

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }
}
