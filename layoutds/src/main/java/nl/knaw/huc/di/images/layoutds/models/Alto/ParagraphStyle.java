//package nl.knaw.huc.di.images.layoutds.models.Alto;
//
//import com.fasterxml.jackson.annotation.JsonSubTypes;
//import com.fasterxml.jackson.annotation.JsonTypeInfo;
//import com.fasterxml.jackson.annotation.JsonTypeName;
//import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
//
//import javax.persistence.Entity;
//
//@Entity
//@JsonTypeName("ParagraphStyle")
//public class ParagraphStyle implements  Style {
//    @JacksonXmlProperty(isAttribute = true, localName = "ID", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
//    String id;
//
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getAlign() {
//        return align;
//    }
//
//    public void setAlign(String align) {
//        this.align = align;
//    }
//}
