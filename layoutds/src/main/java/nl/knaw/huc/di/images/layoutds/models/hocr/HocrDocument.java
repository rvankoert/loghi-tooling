package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonProperty;


public class HocrDocument {

    @JsonProperty("html")
    private HocrHtml hocrHtml ;

    public HocrHtml getHocrHtml() {
        if (hocrHtml==null){
            hocrHtml = new HocrHtml();
        }
        return hocrHtml;
    }

    public void setHocrHtml(HocrHtml hocrHtml) {
        this.hocrHtml = hocrHtml;
    }
}
