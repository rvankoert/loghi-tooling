package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class AlternativeImage {
    @JacksonXmlProperty(isAttribute = true, localName = "filename")
    private String fileName;
    @JacksonXmlProperty(isAttribute = true)
    private String comments;
    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    // TODO find a to make sure the value is between 0 and 1
    private Double confidence;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
