package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SourceImageInformation {
    @JacksonXmlProperty(localName = "fileName", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
