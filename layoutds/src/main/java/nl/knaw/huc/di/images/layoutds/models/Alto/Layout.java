package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Layout {
    @JacksonXmlProperty(localName = "Page", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Page page;

    public Page getPage() {
        if (page== null){
            page = new Page();
        }
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
