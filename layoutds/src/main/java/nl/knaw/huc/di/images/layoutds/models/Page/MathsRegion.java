package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class MathsRegion extends Region {
    @JacksonXmlProperty(isAttribute = true, localName = "bgColour")
    private String bgColour;

    public void setBgColour(String bgColour) {
        this.bgColour = bgColour;
    }

    public String getBgColour() {
        return bgColour;
    }
}
