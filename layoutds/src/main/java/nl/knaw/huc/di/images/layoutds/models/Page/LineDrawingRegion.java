package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class LineDrawingRegion extends Region {

    // not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    @JacksonXmlProperty(isAttribute = true, localName = "colourDepth")
    private String colourDepth;
    // TODO make enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true, localName = "bgColour")
    private String bgColour;
    @JacksonXmlProperty(isAttribute = true, localName = "embText")
    private Boolean embText;
    // TODO make enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true, localName = "penColour")
    private String penColour;

    // not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public void setColourDepth(String colourDepth) {
        this.colourDepth = colourDepth;
    }

    // not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
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

    public void setPenColour(String penColour) {
        this.penColour = penColour;
    }

    public String getPenColour() {
        return penColour;
    }
}
