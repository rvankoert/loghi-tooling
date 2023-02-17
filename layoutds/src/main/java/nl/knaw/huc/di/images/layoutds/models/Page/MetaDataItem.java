//package nl.knaw.huc.di.images.layoutds.models.Page;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
//import org.joda.time.DateTime;
//
//import java.util.List;
//
//@JacksonXmlRootElement(localName = "MetadataItem")
//@JsonInclude(JsonInclude.Include.NON_EMPTY)
//public class MetaDataItem {
//    @JacksonXmlElementWrapper(useWrapping = false)
//    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
//    List<Label> labels;
//
//    @JacksonXmlProperty(isAttribute = true, localName = "type")
//    private String type;
//    @JacksonXmlProperty(isAttribute = true, localName = "name")
//    private String name;
//    @JacksonXmlProperty(isAttribute = true, localName = "value")
//    private String value;
//    @JacksonXmlProperty(isAttribute = true, localName = "date")
//    private DateTime date;
//
//}
