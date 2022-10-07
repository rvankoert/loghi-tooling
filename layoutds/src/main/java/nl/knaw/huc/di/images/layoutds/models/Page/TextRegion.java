package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TextRegion extends Region {
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextLine", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    List<TextLine> textLines;


    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    List<TextRegion> textRegions;
    @JacksonXmlProperty(isAttribute = true, localName = "readingDirection")
    private String readingDirection;
    @JacksonXmlProperty(isAttribute = true, localName = "textColour")
    private String textColour;
    @JacksonXmlProperty(isAttribute = true, localName = "bgColour")
    private String bgColour;
    @JacksonXmlProperty(isAttribute = true, localName = "reverseVideo")
    private Boolean reverseVideo;
    @JacksonXmlProperty(isAttribute = true, localName = "readingOrientation")
    private Double readingOrientation;
    @JacksonXmlProperty(isAttribute = true, localName = "indented")
    private Boolean indented;
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;
    @JacksonXmlProperty(isAttribute = true, localName = "primaryScript")
    private String primaryScript;


    public TextEquiv getTextEquiv() {
        return textEquiv;
    }

    public void setTextEquiv(TextEquiv textEquiv) {
        this.textEquiv = textEquiv;
    }

    public List<TextLine> getTextLines() {
        if (textLines == null) {
            textLines = new ArrayList<>();
        }
        return textLines;
    }

    public void setTextLines(List<TextLine> textLines) {
        this.textLines = textLines;
    }

    public List<TextRegion> getTextRegions() {
        if (textRegions == null) {
            textRegions = new ArrayList<>();
        }
        return textRegions;
    }

    public void setTextRegions(List<TextRegion> textRegions) {
        this.textRegions = textRegions;
    }

    public void setReadingDirection(String readingDirection) {
        this.readingDirection = readingDirection;
    }

    public String getReadingDirection() {
        return readingDirection;
    }

    public void setTextColour(String textColour) {
        this.textColour = textColour;
    }

    public String getTextColour() {
        return textColour;
    }

    public void setBgColour(String bgColour) {
        this.bgColour = bgColour;
    }

    public String getBgColour() {
        return bgColour;
    }

    public void setReverseVideo(Boolean reverseVideo) {
        this.reverseVideo = reverseVideo;
    }

    public Boolean getReverseVideo() {
        return reverseVideo;
    }

    public void setReadingOrientation(Double readingOrientation) {
        this.readingOrientation = readingOrientation;
    }

    public Double getReadingOrientation() {
        return readingOrientation;
    }

    public void setIndented(Boolean indented) {
        this.indented = indented;
    }

    public Boolean getIndented() {
        return indented;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryScript(String primaryScript) {
        this.primaryScript = primaryScript;
    }

    public String getPrimaryScript() {
        return primaryScript;
    }
}
