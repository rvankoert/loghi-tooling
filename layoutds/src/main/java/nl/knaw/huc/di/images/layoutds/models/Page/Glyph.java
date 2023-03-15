package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Glyph {
    @JacksonXmlProperty(localName = "AlternativeImage", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<AlternativeImage> alternativeImages;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;

    @JacksonXmlProperty(localName = "Graphemes", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Graphemes graphemes;

    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TextEquiv> textEquiv;

    @JacksonXmlProperty(localName = "TextStyle", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextStyle textStyle;

    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labels;

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean ligature;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean symbol;

    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String script;

    // TODO make enum with values: printed, typewritten, handwritten-cursive, handwritten-printscript, medieval-manuscript, other
    @JacksonXmlProperty(isAttribute = true)
    private String production;

    @JacksonXmlProperty(isAttribute = true)
    private String custom;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    public List<AlternativeImage> getAlternativeImages() {
        return alternativeImages;
    }

    public void setAlternativeImages(List<AlternativeImage> alternativeImages) {
        this.alternativeImages = alternativeImages;
    }

    public Coords getCoords() {
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public Graphemes getGraphemes() {
        return graphemes;
    }

    public void setGraphemes(Graphemes graphemes) {
        this.graphemes = graphemes;
    }

    public List<TextEquiv> getTextEquiv() {
        return textEquiv;
    }

    public void setTextEquiv(List<TextEquiv> textEquiv) {
        this.textEquiv = textEquiv;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public List<Labels> getLabels() {
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getLigature() {
        return ligature;
    }

    public void setLigature(Boolean ligature) {
        this.ligature = ligature;
    }

    public Boolean getSymbol() {
        return symbol;
    }

    public void setSymbol(Boolean symbol) {
        this.symbol = symbol;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
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
