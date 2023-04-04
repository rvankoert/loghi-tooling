package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public class Labels {
    public static final String LABEL_PROPERTY_NAME = "label";
    @JacksonXmlElementWrapper(useWrapping = false)
    // FIXME localName should be Label
    @JacksonXmlProperty(localName = LABEL_PROPERTY_NAME,namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private ArrayList<Label> label = new ArrayList<>();

    @JacksonXmlProperty(isAttribute = true, localName = "externalModel")
    private String externalModel;

    // RDF resouce identifier
    @JacksonXmlProperty(isAttribute = true, localName = "externalId")
    private String externalId;
    @JacksonXmlProperty(isAttribute = true, localName = "prefix")
    private String prefix;

    @JacksonXmlProperty(isAttribute = true, localName = "comments")
    private String comments;

    public ArrayList<Label> getLabel() {
        return label;
    }

    public void setLabel(ArrayList<Label> label) {
        this.label = label;
    }

    public String getExternalModel() {
        return externalModel;
    }

    public void setExternalModel(String externalModel) {
        this.externalModel = externalModel;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void addLabel(Label label) {
        this.label.add(label);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
