package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class GraphemeGroup extends GraphemeBase{
    @JacksonXmlProperty(localName = "Grapheme", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Grapheme> graphemeList;
    @JacksonXmlProperty(localName = "NonPrintingChar", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<NonPrintingChar> nonPrintingCharList;

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
}
