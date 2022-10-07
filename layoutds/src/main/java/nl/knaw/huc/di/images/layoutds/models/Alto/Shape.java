package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.Entity;

@Entity
public class Shape {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;

    @JacksonXmlProperty(localName = "Polygon")
    Polygon polygon;

    public Polygon getPolygon() {
        if (polygon == null) {
            polygon = new Polygon();
        }
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
