package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class AlternativeImage {
    @JacksonXmlProperty(localName = "filename")
    private String fileName;
    @JacksonXmlProperty
    private String comments;
    @JacksonXmlProperty(localName = "conf")
    // TODO check for range when deserializing
    private float confidence;
}
