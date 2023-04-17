package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadingOrder {
    @JacksonXmlProperty(localName = "OrderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private OrderedGroup orderedGroup;

    @JacksonXmlProperty(localName = "UnorderedGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UnorderedGroup unorderedGroup;

    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    private Double confidence;

    public OrderedGroup getOrderedGroup() {
        return orderedGroup;
    }

    public void setOrderedGroup(OrderedGroup orderedGroup) {
        this.orderedGroup = orderedGroup;
    }

    public UnorderedGroup getUnorderedGroup() {
        return unorderedGroup;
    }

    public void setUnorderedGroup(UnorderedGroup unorderedGroup) {
        this.unorderedGroup = unorderedGroup;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }
}
