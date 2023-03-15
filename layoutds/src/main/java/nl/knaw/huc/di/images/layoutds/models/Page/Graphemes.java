package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Graphemes {
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Grapheme", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Grapheme> graphemeList;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "NonPrintingChar", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<NonPrintingChar> nonPrintingCharList;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "GraphemeGroup", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<GraphemeGroup> graphemeGroupList;


    public List<Grapheme> getGraphemeList() {
        return graphemeList;
    }

    public void setGraphemeList(List<Grapheme> graphemeList) {
        this.graphemeList = graphemeList;
    }

    public List<NonPrintingChar> getNonPrintingCharList() {
        return nonPrintingCharList;
    }

    public void setNonPrintingCharList(List<NonPrintingChar> nonPrintingCharList) {
        this.nonPrintingCharList = nonPrintingCharList;
    }

    public List<GraphemeGroup> getGraphemeGroupList() {
        return graphemeGroupList;
    }

    public void setGraphemeGroupList(List<GraphemeGroup> graphemeGroupList) {
        this.graphemeGroupList = graphemeGroupList;
    }
}
