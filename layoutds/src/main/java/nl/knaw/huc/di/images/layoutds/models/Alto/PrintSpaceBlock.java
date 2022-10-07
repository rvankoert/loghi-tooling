package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrintSpaceBlock {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;

    @JacksonXmlProperty(isAttribute = true, localName = "HEIGHT")
    int height;
    @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
    int width;

    @JacksonXmlProperty(isAttribute = true, localName = "language")
    String language;

    @JacksonXmlProperty(isAttribute = true, localName = "STYLEREFS")
    String styleRefs;


    @JacksonXmlProperty(isAttribute = true, localName = "VPOS")
    Integer vpos;
    @JacksonXmlProperty(isAttribute = true, localName = "HPOS")
    Integer hpos;

    @JacksonXmlProperty(isAttribute = true, localName = "ROTATION")
    Float rotation;

    @JacksonXmlProperty(localName = "Shape", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Shape shape;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextLine")//, namespace = "http://www.loc.gov/standards/alto/ns-v2#")
            List<TextLine> textLines;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<PrintSpaceBlock> blocks;
    private String idNext;
    private Boolean CS;
    private String type;

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public void setTextLines(List<TextLine> textLines) {
        this.textLines = textLines;
    }

    public List<TextLine> getTextLines() {
        return textLines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getVpos() {
        return vpos;
    }

    public void setVpos(Integer vpos) {
        this.vpos = vpos;
    }

    public Integer getHpos() {
        return hpos;
    }

    public void setHpos(Integer hpos) {
        this.hpos = hpos;
    }

    public void setStyleRefs(String styleRefs) {
        this.styleRefs = styleRefs;
    }

    public String getStyleRefs() {
        return styleRefs;
    }

    public List<PrintSpaceBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<PrintSpaceBlock> blocks) {
        this.blocks = blocks;
    }

    public void setRotation(Float rotation) {
        this.rotation = rotation;
    }

    public Float getRotation() {
        return rotation;
    }

    public void setIdNext(String idNext) {
        this.idNext = idNext;
    }

    public String getIdNext() {
        return idNext;
    }

    public void setCS(Boolean cs) {
        this.CS = cs;
    }

    public Boolean getCS() {
        return CS;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
