package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "PrintSpace")
public class PrintSpace {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;

    @JacksonXmlProperty(localName = "TextBlock", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<PrintSpaceBlock> printSpaceBlocks;

//    @JacksonXmlProperty(localName = "GraphicalElement", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
//    @JacksonXmlElementWrapper(useWrapping = false)
//    List<GraphicalElement> graphicalElements;

    @JacksonXmlProperty(isAttribute = true, localName = "HEIGHT")
    int height;
    @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
    int width;
    @JacksonXmlProperty(isAttribute = true, localName = "VPOS")
    int vpos;
    @JacksonXmlProperty(isAttribute = true, localName = "HPOS")
    int hpos;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JacksonXmlProperty(localName = "TextBlock")
    public List<PrintSpaceBlock> getPrintSpaceBlocks() {
        if (printSpaceBlocks == null) {
            printSpaceBlocks = new ArrayList<>();
        }
        return printSpaceBlocks;
    }

//    public List<GraphicalElement> getGraphicalElements() {
//        if (graphicalElements == null) {
//            graphicalElements = new ArrayList<>();
//        }
//        return graphicalElements;
//    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getVpos() {
        return vpos;
    }

    public void setVpos(int vpos) {
        this.vpos = vpos;
    }

    public int getHpos() {
        return hpos;
    }

    public void setHpos(int hpos) {
        this.hpos = hpos;
    }
}
