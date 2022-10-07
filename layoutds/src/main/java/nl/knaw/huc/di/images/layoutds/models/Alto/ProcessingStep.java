package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProcessingStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", locale = "nl_NL")
    @JacksonXmlProperty(localName = "processingDateTime", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String processingDate;
    @JacksonXmlProperty(localName = "processingSoftware", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    OCRProcessingSoftware processingSoftware;

    @JacksonXmlProperty(localName = "processingAgency", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String processingAgency;

    @JacksonXmlProperty(localName = "processingStepDescription", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    String processingStepDescription;
    private ProcessingCategory processingCategory;
    private String processingStepSettings;


    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

    public OCRProcessingSoftware getProcessingSoftware() {
        if (processingSoftware == null) {
            processingSoftware = new OCRProcessingSoftware();
        }
        return processingSoftware;
    }

    public void setProcessingSoftware(OCRProcessingSoftware processingSoftware) {
        this.processingSoftware = processingSoftware;
    }

    public void setProcessingCategory(ProcessingCategory processingCategory) {
        this.processingCategory = processingCategory;
    }

    public ProcessingCategory getProcessingCategory() {
        return processingCategory;
    }

    public void setProcessingStepDescription(String processingStepDescription) {
        this.processingStepDescription = processingStepDescription;
    }

    public String getProcessingStepDescription() {
        return processingStepDescription;
    }

    public void setProcessingStepSettings(String processingStepSettings) {
        this.processingStepSettings = processingStepSettings;
    }

    public String getProcessingStepSettings() {
        return processingStepSettings;
    }

    public void setProcessingAgency(String processingAgency) {
        this.processingAgency = processingAgency;
    }

    public String getProcessingAgency() {
        return processingAgency;
    }
}
