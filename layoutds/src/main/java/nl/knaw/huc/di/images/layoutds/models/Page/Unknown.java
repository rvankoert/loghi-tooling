package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Unknown extends Region {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    String id;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private Coords coords;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Coords getCoords() {
        if (coords ==null){
            coords = new Coords();
        }
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

}
