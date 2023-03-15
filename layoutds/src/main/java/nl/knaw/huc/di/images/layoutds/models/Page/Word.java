package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Word {
    public Word() {
        this.id = "word_" + UUID.randomUUID().toString();
    }

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "custom")
    private String custom;
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "language")
    private String language;
    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String primaryScript;
    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String secondaryScript;
    // TODO make enum with values: left-to-right, right-to-left, top-to-bottom, bottom-to-top
    @JacksonXmlProperty(isAttribute = true, localName = "readingDirection")
    private String readingDirection;
    // TODO make enum with values: printed, typewritten, handwritten-cursive, handwritten-printscript, medieval-manuscript, other
    @JacksonXmlProperty(isAttribute = true)
    private String production;
    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;

    // FIXME should be a list
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlProperty(localName = "TextStyle", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextStyle textStyle;

    @JacksonXmlProperty(localName = "AlternativeImage", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<AlternativeImage> alternativeImages;

    @JacksonXmlProperty(localName = "Glyph", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Glyph> glyphs;

    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public TextEquiv getTextEquiv() {
        return textEquiv;
    }

    public void setTextEquiv(TextEquiv textEquiv) {
        this.textEquiv = textEquiv;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public List<AlternativeImage> getAlternativeImages() {
        return alternativeImages;
    }

    public void setAlternativeImages(List<AlternativeImage> alternativeImages) {
        this.alternativeImages = alternativeImages;
    }

    public List<Glyph> getGlyphs() {
        return glyphs;
    }

    public void setGlyphs(List<Glyph> glyphs) {
        this.glyphs = glyphs;
    }

    public String getPrimaryScript() {
        return primaryScript;
    }

    public void setPrimaryScript(String primaryScript) {
        this.primaryScript = primaryScript;
    }

    public String getSecondaryScript() {
        return secondaryScript;
    }

    public void setSecondaryScript(String secondaryScript) {
        this.secondaryScript = secondaryScript;
    }

    public String getReadingDirection() {
        return readingDirection;
    }

    public void setReadingDirection(String readingDirection) {
        this.readingDirection = readingDirection;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<Labels> getLabels() {
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
    }
}
