package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;

public class Labels {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Label",namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private ArrayList<Label> label;

    @JacksonXmlProperty(isAttribute = true, localName = "externalModel")
    private String externalModel;

    @JacksonXmlProperty(isAttribute = true, localName = "externalId")
    private String externalId;
    @JacksonXmlProperty(isAttribute = true, localName = "prefix")
    private String prefix;

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
}
