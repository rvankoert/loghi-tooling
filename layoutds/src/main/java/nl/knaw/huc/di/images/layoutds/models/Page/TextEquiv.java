package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JacksonXmlRootElement(localName = "TextEquiv")
public class TextEquiv {

    public TextEquiv() {
    }

    // FIXME should be a floating point number
    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    private String confidence;

    @JacksonXmlProperty(isAttribute = true)
    private Integer index;

    // TODO make enum with values: float, integer, boolean, date, time, dateTime, string, other
    @JacksonXmlProperty(isAttribute = true)
    private String dataType;

    @JacksonXmlProperty(isAttribute = true)
    private String dataTypeDetails;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    @JacksonXmlProperty(localName = "PlainText", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String plainText;

    @JacksonXmlProperty(localName = "Unicode", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private String unicode;

    public TextEquiv(Double totalConf, String plainText, String unicode) {
        if (totalConf != null) {
            this.confidence = Double.toString(totalConf);
        }
        this.plainText = plainText;
        this.unicode = unicode;

    }

    public TextEquiv(Double totalConf, String text) {
        if (totalConf != null) {
            this.confidence = Double.toString(totalConf);
        }
        this.plainText = text;
        this.unicode = text;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getPlainText() {
        return plainText;
    }

    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataTypeDetails() {
        return dataTypeDetails;
    }

    public void setDataTypeDetails(String dataTypeDetails) {
        this.dataTypeDetails = dataTypeDetails;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
