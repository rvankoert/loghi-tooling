package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Coords {
    @JacksonXmlProperty(isAttribute = true, localName = "points")
    private String points;

    // TODO find a to make sure the value is between 0 and 1
    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    private Double confidence;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }


    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
