package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HocrCArea {
    @JsonProperty("class")
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private String classString="ocr_carea";


    @JsonProperty("id")
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String idString;

    @JsonProperty("title")
    @JacksonXmlProperty(isAttribute = true, localName = "title")
    private String title;
//    @JsonProperty("id")
//    private String id;
    @JsonProperty("lang")
    private String lang;
    private List<HocrParagraph > paragraphList;


    @JsonProperty("p")
    @JacksonXmlElementWrapper(useWrapping = false)
    public List<HocrParagraph> getParagraphList() {
        if (paragraphList == null){
            paragraphList = new ArrayList<>();
        }
        return paragraphList;
    }

    public void setParagraphList(List<HocrParagraph> paragraphList) {
        this.paragraphList = paragraphList;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getIdString() {
        return idString;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
