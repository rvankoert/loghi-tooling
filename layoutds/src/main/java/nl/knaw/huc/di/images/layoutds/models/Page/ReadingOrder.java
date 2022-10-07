package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ReadingOrder {
    @JacksonXmlProperty(localName = "OrderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private OrderedGroup orderedGroup;

    public OrderedGroup getOrderedGroup() {
        return orderedGroup;
    }

    public void setOrderedGroup(OrderedGroup orderedGroup) {
        this.orderedGroup = orderedGroup;
    }
}
