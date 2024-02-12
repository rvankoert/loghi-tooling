package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;
import org.hibernate.engine.jdbc.batch.spi.Batch;

import java.util.ArrayList;
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
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private Coords coords;
    @JacksonXmlProperty(localName = "Baseline", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private Baseline baseline;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Word", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<Word> words;
    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlProperty(localName = "TextStyle", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private TextStyle textStyle;

    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private UserDefined userDefined;


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

    public void addUserAttributeToUserDefined(UserAttribute userAttribute) {
        if (this.userDefined == null) {
            this.userDefined = new UserDefined();
        }
        if (this.userDefined.getUserAttributes() == null) {
            this.userDefined.setUserAttributes(new ArrayList<>());
        }
        this.userDefined.getUserAttributes().add(userAttribute);
    }

    public UserDefined getUserDefined(){return userDefined;}

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
}
