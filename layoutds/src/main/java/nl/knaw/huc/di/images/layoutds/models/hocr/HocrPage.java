package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HocrPage {
    @JsonProperty("class")
    @JacksonXmlProperty(isAttribute = true, localName = "class")
    private String classString ="ocr_page";

    @JsonProperty("id")
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String idString;

    @JsonProperty("title")
    @JacksonXmlProperty(isAttribute = true, localName = "title")
    private String title;

    private String image;
    private String bbox;
    private String ppageno;
    @JsonProperty("div")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<HocrCArea> hocrCAreaList;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBbox() {
        return bbox;
    }

    public void setBbox(String bbox) {
        this.bbox = bbox;
    }

    public String getPpageno() {
        return ppageno;
    }

    public void setPpageno(String ppageno) {
        this.ppageno = ppageno;
    }

    public List<HocrCArea> getHocrCAreaList() {
        if (hocrCAreaList== null){
            hocrCAreaList = new ArrayList<>();
        }
        return hocrCAreaList;
    }

    public void setHocrCAreaList(List<HocrCArea> hocrCAreaList) {
        this.hocrCAreaList = hocrCAreaList;
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
