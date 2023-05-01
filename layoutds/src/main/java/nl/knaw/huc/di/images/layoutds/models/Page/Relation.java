package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Relation {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Labels labels;

    @JacksonXmlProperty(localName = "SourceRegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private RegionRef sourceRegionRef;

    @JacksonXmlProperty(localName = "TargetRegionRef", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private RegionRef targetRegionRef;

    // FIXME limited values: link, join
    // https://stackoverflow.com/questions/70012081/jackson-serializes-enum-to-name-not-value-xml-java
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    @JacksonXmlProperty(isAttribute = true)
    private String custom;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    public Labels getLabels() {
        return labels;
    }

    public void setLabels(Labels labels) {
        this.labels = labels;
    }

    public RegionRef getSourceRegionRef() {
        return sourceRegionRef;
    }

    public void setSourceRegionRef(RegionRef sourceRegionRef) {
        this.sourceRegionRef = sourceRegionRef;
    }

    public RegionRef getTargetRegionRef() {
        return targetRegionRef;
    }

    public void setTargetRegionRef(RegionRef targetRegionRef) {
        this.targetRegionRef = targetRegionRef;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
