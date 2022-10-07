package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HocrBody {
    @JsonProperty("div")
    private HocrPage hocrPage;

    public HocrPage getHocrPage() {
        if (hocrPage == null) {
            hocrPage = new HocrPage();
        }
        return hocrPage;
    }

    public void setHocrPage(HocrPage hocrPage) {
        this.hocrPage = hocrPage;
    }
}
