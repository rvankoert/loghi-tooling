package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(namespace = "http://www.loc.gov/standards/alto/ns-v2#")
public class Description {
    @JacksonXmlProperty(localName = "MeasurementUnit", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    MeasurementUnit measurementUnit;

    @JacksonXmlProperty(localName = "sourceImageInformation", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    SourceImageInformation sourceImageInformation;

    @JacksonXmlProperty(localName = "OCRProcessing", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    OCRProcessing ocrProcessing;


    public MeasurementUnit getMeasurementUnit() {
        if (measurementUnit == null) {
            measurementUnit = MeasurementUnit.pixel;
        }
        return measurementUnit;
    }

    public void setMeasurementUnit(MeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public OCRProcessing getOcrProcessing() {
        if (ocrProcessing == null) {
            ocrProcessing = new OCRProcessing();
        }
        return ocrProcessing;
    }

    public void setOcrProcessing(OCRProcessing ocrProcessing) {
        this.ocrProcessing = ocrProcessing;
    }

    public SourceImageInformation getSourceImageInformation() {
        return sourceImageInformation;
    }

    public void setSourceImageInformation(SourceImageInformation sourceImageInformation) {
        this.sourceImageInformation = sourceImageInformation;
    }
}
