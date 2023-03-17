package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class GraphicRegion extends Region {
    @JacksonXmlProperty(isAttribute = true, localName = "embText")
    private Boolean embText;
    @JacksonXmlProperty(isAttribute = true, localName = "numColours")
    private Integer numColours;
    // TODO make enum with values: string, logo, letterhead, decoration, frame, handwritten-annotation, stamp, signature, barcode, paper-grow, punch-hole, other
    @JacksonXmlProperty(isAttribute = true)
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
