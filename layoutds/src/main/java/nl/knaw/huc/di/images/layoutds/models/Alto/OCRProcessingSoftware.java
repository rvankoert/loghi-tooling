package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OCRProcessingSoftware {
    @JacksonXmlProperty(localName = "softwareCreator", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareCreator = "tesseract";
    @JacksonXmlProperty(localName = "softwareName", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareName = "tesseract";
    @JacksonXmlProperty(localName = "softwareVersion", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String softwareVersion;

    @JacksonXmlProperty(localName = "applicationDescription", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String applicationDescription;


    public String getSoftwareCreator() {
        return softwareCreator;
    }

    public void setSoftwareCreator(String softwareCreator) {
        this.softwareCreator = softwareCreator;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public void setApplicationDescription(String applicationDescription) {
        this.applicationDescription = applicationDescription;
    }

    public String getApplicationDescription() {
        return applicationDescription;
    }
}
