package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class GraphicRegion extends Region {
    @JacksonXmlProperty(isAttribute = true, localName = "embText")
    private Boolean embText;
    @JacksonXmlProperty(isAttribute = true, localName = "numColours")
    private Integer numColours;

    public void setEmbText(Boolean embText) {
        this.embText = embText;
    }

    public Boolean getEmbText() {
        return embText;
    }

    public void setNumColours(Integer numColours) {
        this.numColours = numColours;
    }

    public Integer getNumColours() {
        return numColours;
    }
}
