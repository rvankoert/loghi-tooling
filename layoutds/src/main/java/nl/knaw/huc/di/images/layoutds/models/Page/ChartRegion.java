package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ChartRegion extends Region {
    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    @JacksonXmlProperty(isAttribute = true, localName = "colourDepth")
    private String colourDepth;
    // TODO make enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true, localName = "bgColour")
    private String bgColour;
    @JacksonXmlProperty(isAttribute = true, localName = "embText")
    private Boolean embText;
    @JacksonXmlProperty(isAttribute = true, localName = "numColours")
    private Integer numColours;
    // TODO make enum with values: bar, line, pie, scatter, surface, other
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public void setColourDepth(String colourDepth) {
        this.colourDepth = colourDepth;
    }

    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public String getColourDepth() {
        return colourDepth;
    }

    public void setBgColour(String bgColour) {
        this.bgColour = bgColour;
    }

    public String getBgColour() {
        return bgColour;
    }

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
