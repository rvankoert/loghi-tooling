package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

import java.util.List;
import java.util.UUID;

@JacksonXmlRootElement(localName = "TextLine")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TextLine {
    public static String TEXTLINE_PREFIX="line_";
    public TextLine() {
        this.setId(null);
    }

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "custom")
    private String custom;
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;
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
    @JacksonXmlProperty(isAttribute = true)
    private Integer index;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;
    @JacksonXmlProperty(localName = "Baseline", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Baseline baseline;
    // FIXME should be list
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlProperty(localName = "TextStyle", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextStyle textStyle;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Word", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Word> words;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AlternativeImage", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<AlternativeImage> alternativeImages;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labels;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (Strings.isNullOrEmpty(id)) {
            this.id = TEXTLINE_PREFIX + UUID.randomUUID().toString();
        } else if (Character.isDigit(id.charAt(0))) {
            this.id = TEXTLINE_PREFIX + id;
        } else {
            this.id = id;
        }
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public Coords getCoords() {
        if (this.coords == null) {
            this.coords = new Coords();
        }
        return this.coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public Baseline getBaseline() {
        if (baseline == null) {
            baseline = new Baseline();
        }
        return baseline;
    }

    public void setBaseline(Baseline baseline) {
        this.baseline = baseline;
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

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public List<AlternativeImage> getAlternativeImages() {
        return alternativeImages;
    }

    public void setAlternativeImages(List<AlternativeImage> alternativeImages) {
        this.alternativeImages = alternativeImages;
    }

    public List<Labels> getLabels() {
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
