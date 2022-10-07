package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

//TODO RUTGERCHECK: very similar to OCRProcessingSoftware.... maybe refactor?
public class ProcessingSoftware {
    @JacksonXmlProperty(localName = "softwareCreator", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareCreator;
    @JacksonXmlProperty(localName = "softwareName", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareName;
    @JacksonXmlProperty(localName = "softwareVersion", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareVersion;

    @JacksonXmlProperty(localName = "applicationDescription", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String applicationDescription;
}
