package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TextRegion extends Region {
    // FIXME should be a collection
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextLine", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TextLine> textLines;

    @JacksonXmlProperty(localName = "TextStyle")
    private TextStyle textStyle;

    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    List<TextRegion> textRegions;
    // TODO make enum with values: left-to-right, right-to-left, top-to-bottom, bottom-to-top
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
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "secondaryLanguage")
    private String secondaryLanguage;
    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String primaryScript;

    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String secondaryScript;

    // TODO make enum with values: paragraph, heading, caption, caption, footer, page-number, drop-capital, credit, floating, signature-mark, catch-word, marginalia, footnote, footnote-continued, endnote, TOC-entry, list-label, other
    @JacksonXmlProperty(isAttribute = true)
    private String type;

    // The degree of space in points between the lines of text (line spacing)
    @JacksonXmlProperty(isAttribute = true)
    private int leading;

    // TODO make enum with values: left-to-right, right-to-left, top-to-bottom, bottom-to-top
    @JacksonXmlProperty(isAttribute = true)
    private String textLineOrder;

    // TODO make enum with values: left, center, right, justify
    @JacksonXmlProperty(isAttribute = true)
    private String align;

    // TODO make enum with values: printed, typewritten, handwritten-cursive, handwritten-printscript, medieval-manuscript, other
    @JacksonXmlProperty(isAttribute = true)
    private String production;


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

    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public List<TextRegion> getTextRegions() {
        if (textRegions == null) {
            textRegions = new ArrayList<>();
        }
        return textRegions;
    }

    // FIXME not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
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

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLeading() {
        return leading;
    }

    public void setLeading(int leading) {
        this.leading = leading;
    }

    public String getTextLineOrder() {
        return textLineOrder;
    }

    public void setTextLineOrder(String textLineOrder) {
        this.textLineOrder = textLineOrder;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public String getSecondaryScript() {
        return secondaryScript;
    }

    public void setSecondaryScript(String secondaryScript) {
        this.secondaryScript = secondaryScript;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }
}
