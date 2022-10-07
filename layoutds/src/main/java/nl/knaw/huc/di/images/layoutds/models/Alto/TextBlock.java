package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "TextBlock")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextBlock extends PrintSpaceBlock {
}
