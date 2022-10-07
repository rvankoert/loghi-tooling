package nl.knaw.huc.di.images.layoutds.models.hocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement(localName = "html")
public class HocrHtml {
    @JsonProperty("head")
    private HocrHead hocrHead;
    @JsonProperty("body")
    private HocrBody hocrBody;

    public HocrHead getHocrHead() {
        if (hocrHead==null){
            hocrHead= new HocrHead();
        }
        return hocrHead;
    }

    public void setHocrHead(HocrHead hocrHead) {
        this.hocrHead = hocrHead;
    }

    public HocrBody getHocrBody() {
        if (hocrBody== null){
            hocrBody = new HocrBody();
        }
        return hocrBody;
    }

    public void setHocrBody(HocrBody hocrBody) {
        this.hocrBody = hocrBody;
    }
}
