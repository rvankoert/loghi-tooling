package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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

    @JacksonXmlProperty(isAttribute = true, localName = "language")
    private String language;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;

    @JacksonXmlProperty(localName = "TextEquiv", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextEquiv textEquiv;

    @JacksonXmlProperty(localName = "TextStyle", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextStyle textStyle;


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
}
