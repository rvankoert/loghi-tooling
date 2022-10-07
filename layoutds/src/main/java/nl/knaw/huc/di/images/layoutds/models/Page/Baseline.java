package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Baseline {
    @JacksonXmlProperty(isAttribute = true, localName = "points")
    private String points = "";

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
