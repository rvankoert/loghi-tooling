package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TableCell {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "row")
    private String row;

    @JacksonXmlProperty(isAttribute = true, localName = "col")
    private String col;

    @JacksonXmlProperty(localName = "Coords")
    private Coords coords;

    @JacksonXmlProperty(localName = "CornerPts")
    private String cornerPts;





}
