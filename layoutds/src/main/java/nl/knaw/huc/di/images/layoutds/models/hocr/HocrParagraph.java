package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class HocrParagraph {
    @JsonProperty("class")
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private String classString="ocr_par";

    @JsonProperty("id")
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;

    @JsonProperty("lang")
    @JacksonXmlProperty(isAttribute = true, localName = "lang")
    private String lang;

    @JsonProperty("title")
    @JacksonXmlProperty(isAttribute = true, localName = "title")
    private String title;

    private List<HocrLine> textLines;

    public String getClassString() {
        return classString;
    }

    public void setClassString(String classString) {
        this.classString = classString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("span")
    public List<HocrLine> getTextLines() {
        if (textLines == null){
            textLines = new ArrayList<>();
        }
        return textLines;
    }

    public void setTextLines(List<HocrLine> textLines) {
        this.textLines = textLines;
    }
}
