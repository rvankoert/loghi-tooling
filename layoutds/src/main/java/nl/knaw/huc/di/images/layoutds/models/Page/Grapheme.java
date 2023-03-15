package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Grapheme extends GraphemeBase {
    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }
}
