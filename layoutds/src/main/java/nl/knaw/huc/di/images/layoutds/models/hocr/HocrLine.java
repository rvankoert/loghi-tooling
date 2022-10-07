package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class HocrLine {
    @JsonProperty("class")
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private String classString="ocr_line";

    private List<HocrWord> words;

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String idString;
    @JacksonXmlProperty(isAttribute = true, localName = "title")
    private String title;

    @JsonIgnore
    private String boundingBox;

    @JsonIgnore
    @JacksonXmlProperty(isAttribute = true, localName = "x_ascenders")
    private int ascenders;
    @JsonIgnore
    @JacksonXmlProperty(isAttribute = true, localName = "x_descenders")
    private int descenders;
    @JsonIgnore
    private int xsize;

    @JsonProperty("span")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<HocrWord> getWords() {
        if (words== null){
            words = new ArrayList<>();
        }
        return words;
    }

    public void setWords(List<HocrWord> words) {
        this.words = words;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    @JsonIgnore
    public int getAscenders() {
        return ascenders;
    }

    public void setAscenders(int ascenders) {
        this.ascenders = ascenders;
    }

    @JsonIgnore
    public int getDescenders() {
        return descenders;
    }

    public void setDescenders(int descenders) {
        this.descenders = descenders;
    }

    @JsonIgnore
    public int getXsize() {
        return xsize;
    }

    public void setXsize(int xsize) {
        this.xsize = xsize;
    }
}
