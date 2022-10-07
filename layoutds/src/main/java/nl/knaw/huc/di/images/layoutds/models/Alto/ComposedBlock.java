package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "ComposedBlock")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ComposedBlock extends PrintSpaceBlock {
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
