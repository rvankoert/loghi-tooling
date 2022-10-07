package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class ProcessingCategory {
    @JacksonXmlProperty(localName = "ocrProcessingStep", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    List<ProcessingStep> ocrProcessingStep;
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id = "test";

    @JacksonXmlProperty(localName = "preProcessingStep", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    private List<ProcessingStep> preProcessingStep;

    @JacksonXmlProperty(localName = "postProcessingStep", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    private List<ProcessingStep> postProcessingStep;

    @JacksonXmlProperty(localName = "processingDateTime", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    private String processingDateTime;


    public List<ProcessingStep> getOcrProcessingStep() {
        if (ocrProcessingStep == null) {
            ocrProcessingStep = new ArrayList<>();
        }
        return ocrProcessingStep;
    }

    public void setOcrProcessingStep(List<ProcessingStep> ocrProcessingStep) {
        this.ocrProcessingStep = ocrProcessingStep;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProcessingStep> getPreProcessingStep() {
        if (preProcessingStep == null) {
            preProcessingStep = new ArrayList<>();
        }
        return preProcessingStep;
    }

    public void setPreProcessingStep(List<ProcessingStep> preProcessingStep) {
        this.preProcessingStep = preProcessingStep;
    }

    public List<ProcessingStep> getPostProcessingStep() {
        if (postProcessingStep == null) {
            postProcessingStep = new ArrayList<>();
        }
        return postProcessingStep;
    }

    public void setPostProcessingStep(List<ProcessingStep> postProcessingStep) {
        this.postProcessingStep = postProcessingStep;
    }
}
