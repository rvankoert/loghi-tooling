package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Relation {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels")
    private Labels labels;

    @JacksonXmlProperty(localName = "SourceRegionRef")
    private RegionRef sourceRegionRef;

    @JacksonXmlProperty(localName = "TargetRegionRef")
    private RegionRef targetRegionRef;

    // FIXME limited values: link, join
    // https://stackoverflow.com/questions/70012081/jackson-serializes-enum-to-name-not-value-xml-java
    @JacksonXmlProperty
    private String type;

    @JacksonXmlProperty
    private String custom;

    @JacksonXmlProperty
    private String comments;
}
