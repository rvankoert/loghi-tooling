package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JacksonXmlRootElement(localName = "alto", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
public class AltoDocument {

    @JacksonXmlProperty(localName = "Description", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Description description;

    @JacksonXmlProperty(localName = "Styles", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    ArrayList<Style> styles;

    @JacksonXmlProperty(localName = "Layout", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Layout layout;

    @JacksonXmlProperty(localName = "schemaLocation")
    String schemaLocation;

    public Description getDescription() {
        if (description == null){
            description = new Description();
        }
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public ArrayList<Style> getStyles() {
        return styles;
    }

    public void setStyles(ArrayList<Style> styles) {
        this.styles = styles;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }

    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    public Layout getLayout() {
        if (layout == null) {
            layout = new Layout();
        }
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
