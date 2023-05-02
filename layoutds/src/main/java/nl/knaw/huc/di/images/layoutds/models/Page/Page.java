package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

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
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "primaryLanguage")
    private String primaryLanguage;
    // FIXME make enum with values of ISO 639.x 2016-07-14
    @JacksonXmlProperty(isAttribute = true, localName = "secondaryLanguage")
    private String secondaryLanguage;

    @JacksonXmlProperty(localName = "AlternativeImage", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<AlternativeImage> alternativeImages = new ArrayList<>();

    @JacksonXmlProperty(localName = "ReadingOrder", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private ReadingOrder readingOrder;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextRegion", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TextRegion> textRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ImageRegion", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<ImageRegion> imageRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TableRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TableRegion> tableRegions;

    @JacksonXmlProperty(localName = "PrintSpace", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private PrintSpace printSpace;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "GraphicRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<GraphicRegion> graphicRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "LineDrawingRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<LineDrawingRegion> lineDrawingRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ChartRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<ChartRegion> chartRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "NoiseRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<NoiseRegion> noiseRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MathRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<MathsRegion> mathsRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "FrameRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<FrameRegion> frameRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SeparatorRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<SeparatorRegion> separatorRegions;

    // FIXME make enum with values: front-cover, back-cover, title, table-of-contents, index, content, blank, other
    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String pageType;

    @JacksonXmlProperty(localName = "Border", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Border border;

    @JacksonXmlProperty(localName = "Unknown", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    // Not in the spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    Unknown unknown;
    // Not in the spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    private List<Unknown> unknowns;

    @JacksonXmlProperty(localName = "Layers" , namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Layers layers;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Relations", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Relations> relationsList = new ArrayList<>();

    @JacksonXmlProperty(localName = "TextStyle", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TextStyle textStyle;

    @JacksonXmlProperty(localName = "UserDefined", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace="http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labels = new ArrayList<>();

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MapRegion")
    private List<MapRegion> mapRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ChemRegion")
    private List<ChemRegion> chemRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MusicRegion")
    private List<MusicRegion> musicRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AdvertRegion")
    private List<AdvertRegion> advertRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UnknownRegion")
    private List<UnknownRegion> unknownRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CustomRegion")
    private List<CustomRegion> customRegions;

    @JacksonXmlProperty(isAttribute = true)
    private Double imageXResolution;

    @JacksonXmlProperty(isAttribute = true)
    private Double imageYResolution;

    // FIXME make enum with values: PPI, PPCM, other
    @JacksonXmlProperty(isAttribute = true)
    private String imageResolutionUnit;

    @JacksonXmlProperty(isAttribute = true)
    private String custom;

    @JacksonXmlProperty(isAttribute = true)
    private Double orientation;

    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String primaryScript;

    // FIXME make enum with value of iso15924 2016-07-14
    @JacksonXmlProperty(isAttribute = true)
    private String secondaryScript;

    // FIXME make enum with values: left-to-right, right-to-left, top-to-bottom, bottom-to-top
    @JacksonXmlProperty(isAttribute = true)
    private String readingDirection;

    // FIXME make enum with values: left-to-right, right-to-left, top-to-bottom, bottom-to-top
    @JacksonXmlProperty(isAttribute = true)
    private String textLineReadingOrder;

    // FIXME find way to check on min value 0 and max value 1
    @JacksonXmlProperty(isAttribute = true, localName = "conf")
    private Double conf;
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

    // Not in the spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public List<Unknown> getUnknowns() {
        if (unknowns == null) {
            unknowns = new ArrayList<>();
        }
        return unknowns;
    }

    // Not in the spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public void setUnknowns(List<Unknown> unknowns) {
        this.unknowns = unknowns;
    }

    public void setPrimaryLanguage(String language) {
        this.primaryLanguage = language;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public List<AlternativeImage> getAlternativeImages() {
        return alternativeImages;
    }

    public void setAlternativeImages(List<AlternativeImage> alternativeImages) {
        this.alternativeImages = alternativeImages;
    }

    public void addAlternativeImage(AlternativeImage alternativeImage) {
        this.alternativeImages.add(alternativeImage);
    }

    public Layers getLayers() {
        return layers;
    }

    public void setLayers(Layers layers) {
        this.layers = layers;
    }

    public List<Relations> getRelationsList() {
        return relationsList;
    }

    public void setRelationsList(List<Relations> relationsList) {
        this.relationsList = relationsList;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public List<Labels> getLabels() {
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
    }

    public List<MapRegion> getMapRegions() {
        return mapRegions;
    }

    public void setMapRegions(List<MapRegion> mapRegions) {
        this.mapRegions = mapRegions;
    }

    public List<ChemRegion> getChemRegions() {
        return chemRegions;
    }

    public void setChemRegions(List<ChemRegion> chemRegions) {
        this.chemRegions = chemRegions;
    }

    public List<MusicRegion> getMusicRegions() {
        return musicRegions;
    }

    public void setMusicRegions(List<MusicRegion> musicRegions) {
        this.musicRegions = musicRegions;
    }

    public List<AdvertRegion> getAdvertRegions() {
        return advertRegions;
    }

    public void setAdvertRegions(List<AdvertRegion> advertRegions) {
        this.advertRegions = advertRegions;
    }

    public List<UnknownRegion> getUnknownRegions() {
        return unknownRegions;
    }

    public void setUnknownRegions(List<UnknownRegion> unknownRegions) {
        this.unknownRegions = unknownRegions;
    }

    public List<CustomRegion> getCustomRegions() {
        return customRegions;
    }

    public void setCustomRegions(List<CustomRegion> customRegions) {
        this.customRegions = customRegions;
    }

    public Double getImageXResolution() {
        return imageXResolution;
    }

    public void setImageXResolution(Double imageXResolution) {
        this.imageXResolution = imageXResolution;
    }

    public Double getImageYResolution() {
        return imageYResolution;
    }

    public void setImageYResolution(Double imageYResolution) {
        this.imageYResolution = imageYResolution;
    }

    public String getImageResolutionUnit() {
        return imageResolutionUnit;
    }

    public void setImageResolutionUnit(String imageResolutionUnit) {
        this.imageResolutionUnit = imageResolutionUnit;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public Double getOrientation() {
        return orientation;
    }

    public void setOrientation(Double orientation) {
        this.orientation = orientation;
    }

    public String getSecondaryLanguage() {
        return secondaryLanguage;
    }

    public void setSecondaryLanguage(String secondaryLanguage) {
        this.secondaryLanguage = secondaryLanguage;
    }

    public String getPrimaryScript() {
        return primaryScript;
    }

    public void setPrimaryScript(String primaryScript) {
        this.primaryScript = primaryScript;
    }

    public String getSecondaryScript() {
        return secondaryScript;
    }

    public void setSecondaryScript(String secondaryScript) {
        this.secondaryScript = secondaryScript;
    }

    public String getReadingDirection() {
        return readingDirection;
    }

    public void setReadingDirection(String readingDirection) {
        this.readingDirection = readingDirection;
    }

    public String getTextLineReadingOrder() {
        return textLineReadingOrder;
    }

    public void setTextLineReadingOrder(String textLineReadingOrder) {
        this.textLineReadingOrder = textLineReadingOrder;
    }

    public Double getConf() {
        return conf;
    }

    public void setConf(Double conf) {
        this.conf = conf;
    }

    public void addTextRegion(TextRegion textRegion) {

    }

    public void addRelations(Relations relations) {
        this.relationsList.add(relations);
    }

    public void addLabels(Labels labels) {
        this.labels.add(labels);
    }


//    public List<Region> getRegions() {
//        if (this.regions == null){
//            this.regions =new ArrayList<>();
//        }
//        return this.regions;
//    }
}
