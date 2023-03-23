package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import java.util.List;
import java.util.UUID;

public abstract class Region implements IRegion {
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String id;
    @JacksonXmlProperty(isAttribute = true, localName = "custom")
    private String custom;

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String regionType;

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    @JacksonXmlProperty(isAttribute = true, localName = "orientation")
    private Double orientation;

    @JacksonXmlProperty(isAttribute = true)
    private String comments;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean continuation;

    @JacksonXmlProperty(localName = "Coords", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Coords coords;

    @JacksonXmlProperty(localName = "AlternativeImage", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<AlternativeImage> alternativeImageList;

    @JacksonXmlProperty(localName = "UserDefined", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private UserDefined userDefined;

    @JacksonXmlProperty(localName = "Roles", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Roles roles;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TextRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TextRegion> textRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ImageRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<ImageRegion> imageRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "LineDrawingRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<LineDrawingRegion> lineDrawingRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "GraphicRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<GraphicRegion> graphicRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "TableRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<TableRegion> tableRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ChartRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<ChartRegion> chartRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "SeparatorRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<SeparatorRegion> separatorRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MathsRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<MathsRegion> mathsRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "ChemRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<ChemRegion> chemRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "MusicRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<MusicRegion> musicRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "AdvertRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<AdvertRegion> advertRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "NoiseRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<NoiseRegion> noiseRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "UnknownRegion", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<UnknownRegion> unknownRegions;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "Labels", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<Labels> labelsList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (Strings.isNullOrEmpty(id)) {
            this.id = "region_" + UUID.randomUUID().toString();
        } else if (Character.isDigit(id.charAt(0))) {
            this.id = "region_" + id;
        } else {
            this.id = id;
        }
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public String getRegionType() {
        return regionType;
    }

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public void setRegionType(String regionType) {
        this.regionType = regionType;
    }

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public Double getOrientation() {
        return orientation;
    }

    // FIXME Not in spec: https://www.primaresearch.org/schema/PAGE/gts/pagecontent/2019-07-15/pagecontent.xsd
    @Deprecated
    public void setOrientation(Double orientation) {
        this.orientation = orientation;
    }

    public Coords getCoords() {
        if (coords == null) {
            coords = new Coords();
        }
        return coords;
    }

    public void setCoords(Coords coords) {
        this.coords = coords;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getContinuation() {
        return continuation;
    }

    public void setContinuation(Boolean continuation) {
        this.continuation = continuation;
    }

    public List<AlternativeImage> getAlternativeImageList() {
        return alternativeImageList;
    }

    public void setAlternativeImageList(List<AlternativeImage> alternativeImageList) {
        this.alternativeImageList = alternativeImageList;
    }

    public UserDefined getUserDefined() {
        return userDefined;
    }

    public void setUserDefined(UserDefined userDefined) {
        this.userDefined = userDefined;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public List<TextRegion> getTextRegions() {
        return textRegions;
    }

    public void setTextRegions(List<TextRegion> textRegions) {
        this.textRegions = textRegions;
    }

    public List<ImageRegion> getImageRegions() {
        return imageRegions;
    }

    public void setImageRegions(List<ImageRegion> imageRegions) {
        this.imageRegions = imageRegions;
    }

    public List<LineDrawingRegion> getLineDrawingRegions() {
        return lineDrawingRegions;
    }

    public void setLineDrawingRegions(List<LineDrawingRegion> lineDrawingRegions) {
        this.lineDrawingRegions = lineDrawingRegions;
    }

    public List<GraphicRegion> getGraphicRegions() {
        return graphicRegions;
    }

    public void setGraphicRegions(List<GraphicRegion> graphicRegions) {
        this.graphicRegions = graphicRegions;
    }

    public List<TableRegion> getTableRegions() {
        return tableRegions;
    }

    public void setTableRegions(List<TableRegion> tableRegions) {
        this.tableRegions = tableRegions;
    }

    public List<ChartRegion> getChartRegions() {
        return chartRegions;
    }

    public void setChartRegions(List<ChartRegion> chartRegions) {
        this.chartRegions = chartRegions;
    }

    public List<SeparatorRegion> getSeparatorRegions() {
        return separatorRegions;
    }

    public void setSeparatorRegions(List<SeparatorRegion> separatorRegions) {
        this.separatorRegions = separatorRegions;
    }

    public List<MathsRegion> getMathsRegions() {
        return mathsRegions;
    }

    public void setMathsRegions(List<MathsRegion> mathsRegions) {
        this.mathsRegions = mathsRegions;
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

    public List<NoiseRegion> getNoiseRegions() {
        return noiseRegions;
    }

    public void setNoiseRegions(List<NoiseRegion> noiseRegions) {
        this.noiseRegions = noiseRegions;
    }

    public List<UnknownRegion> getUnknownRegions() {
        return unknownRegions;
    }

    public void setUnknownRegions(List<UnknownRegion> unknownRegions) {
        this.unknownRegions = unknownRegions;
    }

    public List<Labels> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList(List<Labels> labelsList) {
        this.labelsList = labelsList;
    }
}
