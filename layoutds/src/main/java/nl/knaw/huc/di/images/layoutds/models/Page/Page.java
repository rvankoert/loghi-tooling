package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class Page {
    @JacksonXmlProperty(isAttribute = true, localName = "imageFilename")
    private String imageFilename;
    @JacksonXmlProperty(isAttribute = true, localName = "imageWidth")
    private Integer imageWidth;
    @JacksonXmlProperty(isAttribute = true, localName = "imageHeight")
    private Integer imageHeight;
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;


    @JacksonXmlProperty(localName = "ReadingOrder", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private ReadingOrder readingOrder;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextRegion", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<TextRegion> textRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ImageRegion", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<ImageRegion> imageRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TableRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<TableRegion> tableRegions;

    @JacksonXmlProperty(localName = "PrintSpace", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    PrintSpace printSpace;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "GraphicRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<GraphicRegion> graphicRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "LineDrawingRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<LineDrawingRegion> lineDrawingRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ChartRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<ChartRegion> chartRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "NoiseRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<NoiseRegion> noiseRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MathRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<MathsRegion> mathsRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "FrameRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<FrameRegion> frameRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SeparatorRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    private List<SeparatorRegion> separatorRegions;


    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String pageType;

    @JacksonXmlProperty(localName = "Border", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    Border border;

    @JacksonXmlProperty(localName = "Unknown", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")
    Unknown unknown;
    private List<Unknown> unknowns;



//    private List<Region> regions;

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public ReadingOrder getReadingOrder() {
        return readingOrder;
    }

    public void setReadingOrder(ReadingOrder readingOrder) {
        this.readingOrder = readingOrder;
    }

    public List<TextRegion> getTextRegions() {
        if (textRegions==null){
            textRegions = new ArrayList<>();
        }
        return textRegions;
    }

    public void setTextRegions(List<TextRegion> textRegions) {
        this.textRegions = textRegions;
    }

    public List<ImageRegion> getImageRegions() {
        if (imageRegions==null){
            imageRegions = new ArrayList<>();
        }
        return imageRegions;
    }

    public void setImageRegions(List<ImageRegion> imageRegions) {
        this.imageRegions = imageRegions;
    }

    public List<TableRegion> getTableRegions() {
        if (tableRegions==null){
            tableRegions = new ArrayList<>();
        }
        return tableRegions;
    }

    public void setTableRegions(List<TableRegion> tableRegions) {
        this.tableRegions = tableRegions;
    }

    public PrintSpace getPrintSpace() {
        return printSpace;
    }

    public void setPrintSpace(PrintSpace printSpace) {
        this.printSpace = printSpace;
    }

    public List<GraphicRegion> getGraphicRegions() {
        if (graphicRegions == null) {
            graphicRegions = new ArrayList<>();
        }
        return graphicRegions;
    }

    public void setGraphicRegions(List<GraphicRegion> graphicRegions) {
        this.graphicRegions = graphicRegions;
    }

    public void setLineDrawingRegions(List<LineDrawingRegion> lineDrawingRegions) {
        this.lineDrawingRegions = lineDrawingRegions;
    }

    public List<LineDrawingRegion> getLineDrawingRegions() {
        if (lineDrawingRegions == null) {
            lineDrawingRegions = new ArrayList<>();
        }
        return lineDrawingRegions;
    }

    public void setChartRegions(List<ChartRegion> chartRegions) {
        this.chartRegions = chartRegions;
    }

    public List<ChartRegion> getChartRegions() {
        if (chartRegions == null) {
            chartRegions = new ArrayList<>();
        }
        return chartRegions;
    }

    public void setNoiseRegions(List<NoiseRegion> noiseRegions) {
        this.noiseRegions = noiseRegions;
    }

    public List<NoiseRegion> getNoiseRegions() {
        if (noiseRegions == null) {
            noiseRegions = new ArrayList<>();
        }
        return noiseRegions;
    }

    public void setMathsRegions(List<MathsRegion> mathsRegions) {
        this.mathsRegions = mathsRegions;
    }

    public List<MathsRegion> getMathsRegions() {
        if (mathsRegions == null) {
            mathsRegions = new ArrayList<>();
        }
        return mathsRegions;
    }

    public void setFrameRegions(List<FrameRegion> frameRegions) {
        this.frameRegions = frameRegions;
    }

    public List<FrameRegion> getFrameRegions() {
        if (frameRegions == null) {
            frameRegions = new ArrayList<>();
        }
        return frameRegions;
    }

    public List<SeparatorRegion> getSeparatorRegions() {
        if (separatorRegions == null) {
            separatorRegions = new ArrayList<>();
        }
        return separatorRegions;
    }

    public void setSeparatorRegions(List<SeparatorRegion> separatorRegions) {
        this.separatorRegions = separatorRegions;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public List<Unknown> getUnknowns() {
        if (unknowns == null) {
            unknowns = new ArrayList<>();
        }
        return unknowns;
    }

    public void setUnknowns(List<Unknown> unknowns) {
        this.unknowns = unknowns;
    }

    public void setPrimaryLanguage(String language) {
        this.primaryLanguage = language;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

//    public List<Region> getRegions() {
//        if (this.regions == null){
//            this.regions =new ArrayList<>();
//        }
//        return this.regions;
//    }
}
