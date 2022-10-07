package nl.knaw.huc.di.images.layoutds.models.Alto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "Page")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page {
    @JacksonXmlProperty(isAttribute = true, localName = "ID")
    String id;
    @JacksonXmlProperty(isAttribute = true, localName = "PHYSICAL_IMG_NR")
    Float physicalImageNumber = null;
    @JacksonXmlProperty(isAttribute = true, localName = "HEIGHT")
    Integer height;
    @JacksonXmlProperty(isAttribute = true, localName = "WIDTH")
    Integer width;


    @JacksonXmlProperty(isAttribute = true, localName = "PRINTED_IMG_NR")
    String printedImgNr;

    @JacksonXmlProperty(isAttribute = true, localName = "POSITION")
    PositionType position;

    @JacksonXmlProperty(isAttribute = true, localName = "PC")
    Float pageConfidence;

    @JacksonXmlProperty(isAttribute = true, localName = "QUALITY")
    QualityType quality;

    @JacksonXmlProperty(isAttribute = true, localName = "PROCESSING")
    String processing;

    @JacksonXmlProperty(localName = "TextBlock")
    @JacksonXmlElementWrapper(useWrapping = false)
    List<TextBlock> textRegionList;


    @JacksonXmlProperty(localName = "TopMargin", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Margin topMargin;

    @JacksonXmlProperty(localName = "LeftMargin", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Margin leftMargin;

    @JacksonXmlProperty(localName = "RightMargin", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Margin rightMargin;

    @JacksonXmlProperty(localName = "BottomMargin", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    Margin bottomMargin;

    @JacksonXmlProperty(localName = "PrintSpace", namespace = "http://www.loc.gov/standards/alto/ns-v2#")
    PrintSpace printSpace;
    private String pageClass;
    @JacksonXmlProperty(isAttribute = true, localName = "ACCURACY")
    private Float accuracy;
    private String qualityDetail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getPhysicalImageNumber() {
        return physicalImageNumber;
    }

    public void setPhysicalImageNumber(Float physicalImageNumber) {
        this.physicalImageNumber = physicalImageNumber;
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


    public List<TextBlock> getTextBlockList() {
        return textRegionList;
    }

    public void setTextRegionList(List<TextBlock> textBlockList) {
        this.textRegionList = textBlockList;
    }

    public PrintSpace getPrintSpace() {
        return printSpace;
    }

    public String getPrintedImgNr() {
        return printedImgNr;
    }

    public void setPrintedImgNr(String printedImgNr) {
        this.printedImgNr = printedImgNr;
    }

    public PositionType getPosition() {
        return position;
    }

    public void setPosition(PositionType position) {
        this.position = position;
    }

    public Float getPageConfidence() {
        return pageConfidence;
    }

    public void setPageConfidence(Float pageConfidence) {
        this.pageConfidence = pageConfidence;
    }

    public List<TextBlock> getTextRegionList() {
        return textRegionList;
    }

    public Margin getTopMargin() {
        return topMargin;
    }

    public void setTopMargin(Margin topMargin) {
        this.topMargin = topMargin;
    }

    public Margin getLeftMargin() {
        return leftMargin;
    }

    public void setLeftMargin(Margin leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Margin getRightMargin() {
        return rightMargin;
    }

    public void setRightMargin(Margin rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Margin getBottomMargin() {
        return bottomMargin;
    }

    public void setBottomMargin(Margin bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void setPrintSpace(PrintSpace printSpace) {
        this.printSpace = printSpace;
    }

    public void setPageClass(String pageClass) {
        this.pageClass = pageClass;
    }

    public String getPageClass() {
        return pageClass;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setQualityDetail(String qualityDetail) {
        this.qualityDetail = qualityDetail;
    }

    public String getQualityDetail() {
        return qualityDetail;
    }

    public void setQuality(QualityType quality) {
        this.quality = quality;
    }

    public QualityType getQuality() {
        return quality;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }

    public String getProcessing() {
        return processing;
    }
}
