package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.ArrayList;
import java.util.List;

public class HocrHead {
    @JsonProperty("title")
    private String title;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JsonProperty("meta")
    private List<HocrMeta> hocrMetaList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HocrMeta> getHocrMetaList() {
        if (hocrMetaList == null) {
            hocrMetaList = new ArrayList<>();
        }
        return hocrMetaList;
    }

    public void addMeta(HocrMeta hocrMeta) {
        getHocrMetaList().add(hocrMeta);
    }

}
