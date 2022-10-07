package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.Alto.OCRProcessing;
import nl.knaw.huc.di.images.layoutds.models.Alto.PositionType;
import nl.knaw.huc.di.images.layoutds.models.Alto.QualityType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentPage {

    private final Date created;
    private final Integer height;
    private final Integer width;
    private ArrayList<DocumentTextBlock> documentTextBlocks;
    private ArrayList<DocumentPhoto> documentPhotos;
    private ArrayList<DocumentDrawing> documentDrawings;
    private ArrayList<DocumentMeta> documentMetas;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String idString;
    private String creator;

    private String altoSchemaLocation;
    private String fileName;
    private OCRProcessing ocrProcessing;
    private Integer printSpaceHeight;
    private Integer printSpaceHpos;
    private Integer printSpaceVpos;
    private Integer printSpaceWidth;
    private String printSpaceId;
    private PositionType position;
    private Float pageConfidence;
    private String leftMarginId;
    private String rightMarginId;
    private String topMarginId;
    private String bottomMarginId;
    private Integer topMarginWidth;
    private Integer topMarginHpos;
    private Integer topMarginVpos;
    private Integer bottomMarginWidth;
    private Integer bottomMarginHpos;
    private Integer bottomMarginVpos;
    private Integer leftMarginHeight;
    private Integer leftMarginHpos;
    private Integer leftMarginVpos;
    private Integer rightMarginHpos;
    private Integer rightMarginHeight;
    private Integer rightMarginVpos;
    private String printedImgNumber;
    private String processing;
    private QualityType quality;
    private String qualityDetail;
    private Float physicalImageNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Integer leftMargin;
    private Integer rightMargin;
    private Integer topMargin;
    private Integer bottomMargin;

    public DocumentPage(Date created, Integer height, Integer width) {
        if (created != null) {
            this.created = (Date) created.clone();
        } else {
            this.created = null;
        }
        this.height = height;
        this.width = width;
    }

    public Date getCreated() {
        if (created != null) {
            return (Date) created.clone();
        }
        return null;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }

    public List<DocumentTextBlock> getDocumentTextBlocks() {
        if (documentTextBlocks==null){
            documentTextBlocks = new ArrayList<>();
        }
        return documentTextBlocks;
    }

    public void setDocumentTextBlocks(ArrayList<DocumentTextBlock> documentTextBlocks) {
        this.documentTextBlocks = documentTextBlocks;
    }

    public List<DocumentPhoto> getDocumentPhotos() {
        if (documentPhotos==null){
            documentPhotos = new ArrayList<>();
        }
        return documentPhotos;
    }

    public void setDocumentPhotos(ArrayList<DocumentPhoto> documentPhotos) {
        this.documentPhotos = documentPhotos;
    }

    public List<DocumentDrawing> getDocumentDrawings() {
        if (documentDrawings==null){
            documentDrawings = new ArrayList<>();
        }
        return documentDrawings;
    }

    public void setDocumentDrawings(ArrayList<DocumentDrawing> documentDrawings) {
        this.documentDrawings = documentDrawings;
    }

    public Integer getLeftMarginWidth() {
        return leftMargin;
    }

    public void setLeftMarginWidth(Integer leftMargin) {
        this.leftMargin = leftMargin;
    }

    public Integer getRightMarginWidth() {
        return rightMargin;
    }

    public void setRightMarginWidth(Integer rightMargin) {
        this.rightMargin = rightMargin;
    }

    public Integer getTopMarginHeight() {
        return topMargin;
    }

    public void setTopMarginHeight(Integer topMargin) {
        this.topMargin = topMargin;
    }

    public Integer getBottomMarginHeight() {
        return bottomMargin;
    }

    public void setBottomMarginHeight(Integer bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getIdString() {
        return idString;
    }

    public List<DocumentMeta> getDocumentMetas() {
        if (documentMetas==null){
            documentMetas = new ArrayList<>();
        }
        return documentMetas;
    }

    public void addMeta(DocumentMeta documentMeta) {
        getDocumentMetas().add(documentMeta);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setAltoSchemaLocation(String altoSchemaLocation) {
        this.altoSchemaLocation = altoSchemaLocation;
    }

    public String getAltoSchemaLocation() {
        return altoSchemaLocation;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setOcrProcessing(OCRProcessing ocrProcessing) {
        this.ocrProcessing = ocrProcessing;
    }

    public OCRProcessing getOcrProcessing() {
        return ocrProcessing;
    }

    public void setPrintSpaceHeight(Integer printSpaceHeight) {
        this.printSpaceHeight = printSpaceHeight;
    }

    public Integer getPrintSpaceHeight() {
        return printSpaceHeight;
    }

    public void setPrintSpaceHpos(Integer printSpaceHpos) {
        this.printSpaceHpos = printSpaceHpos;
    }

    public Integer getPrintSpaceHpos() {
        return printSpaceHpos;
    }

    public void setPrintSpaceVpos(Integer printSpaceVpos) {
        this.printSpaceVpos = printSpaceVpos;
    }

    public Integer getPrintSpaceVpos() {
        return printSpaceVpos;
    }

    public void setPrintSpaceWidth(Integer printSpaceWidth) {
        this.printSpaceWidth = printSpaceWidth;
    }

    public Integer getPrintSpaceWidth() {
        return printSpaceWidth;
    }

    public void setPrintSpaceId(String printSpaceId) {
        this.printSpaceId = printSpaceId;
    }

    public String getPrintSpaceId() {
        return printSpaceId;
    }

    public void setPosition(PositionType position) {
        this.position = position;
    }

    public PositionType getPosition() {
        return position;
    }

    public void setPageConfidence(Float pageConfidence) {
        this.pageConfidence = pageConfidence;
    }

    public Float getPageConfidence() {
        return pageConfidence;
    }

    public void setLeftMarginId(String leftMarginId) {
        this.leftMarginId = leftMarginId;
    }

    public String getLeftMarginId() {
        return leftMarginId;
    }

    public void setRightMarginId(String rightMarginId) {
        this.rightMarginId = rightMarginId;
    }

    public String getRightMarginId() {
        return rightMarginId;
    }

    public void setTopMarginId(String topMarginId) {
        this.topMarginId = topMarginId;
    }

    public String getTopMarginId() {
        return topMarginId;
    }

    public void setBottomMarginId(String bottomMarginId) {
        this.bottomMarginId = bottomMarginId;
    }

    public String getBottomMarginId() {
        return bottomMarginId;
    }

    public void setTopMarginWidth(int topMarginWidth) {
        this.topMarginWidth = topMarginWidth;
    }

    public Integer getTopMarginWidth() {
        return topMarginWidth;
    }

    public void setTopMarginHpos(Integer topMarginHpos) {
        this.topMarginHpos = topMarginHpos;
    }

    public Integer getTopMarginHpos() {
        return topMarginHpos;
    }

    public void setTopMarginVpos(Integer topMarginVpos) {
        this.topMarginVpos = topMarginVpos;
    }

    public Integer getTopMarginVpos() {
        return topMarginVpos;
    }

    public void setBottomMarginWidth(Integer bottomMarginWidth) {
        this.bottomMarginWidth = bottomMarginWidth;
    }

    public Integer getBottomMarginWidth() {
        return bottomMarginWidth;
    }

    public void setBottomMarginHpos(Integer bottomMarginHpos) {
        this.bottomMarginHpos = bottomMarginHpos;
    }

    public Integer getBottomMarginHpos() {
        return bottomMarginHpos;
    }

    public void setBottomMarginVpos(Integer bottomMarginVpos) {
        this.bottomMarginVpos = bottomMarginVpos;
    }

    public Integer getBottomMarginVpos() {
        return bottomMarginVpos;
    }

    public void setLeftMarginHeight(Integer leftMarginHeight) {
        this.leftMarginHeight = leftMarginHeight;
    }

    public Integer getLeftMarginHeight() {
        return leftMarginHeight;
    }

    public void setLeftMarginHpos(Integer leftMarginHpos) {
        this.leftMarginHpos = leftMarginHpos;
    }

    public Integer getLeftMarginHpos() {
        return leftMarginHpos;
    }

    public void setLeftMarginVpos(Integer leftMarginVpos) {
        this.leftMarginVpos = leftMarginVpos;
    }

    public Integer getLeftMarginVpos() {
        return leftMarginVpos;
    }

    public void setRightMarginHpos(Integer rightMarginHpos) {
        this.rightMarginHpos = rightMarginHpos;
    }

    public Integer getRightMarginHpos() {
        return rightMarginHpos;
    }

    public void setRightMarginHeight(Integer rightMarginHeight) {
        this.rightMarginHeight = rightMarginHeight;
    }

    public Integer getRightMarginHeight() {
        return rightMarginHeight;
    }

    public void setRightMarginVpos(Integer rightMarginVpos) {
        this.rightMarginVpos = rightMarginVpos;
    }

    public Integer getRightMarginVpos() {
        return rightMarginVpos;
    }

    public void setPrintedImgNumber(String printedImgNumber) {
        this.printedImgNumber = printedImgNumber;
    }

    public String getPrintedImgNumber() {
        return printedImgNumber;
    }

    public void setProcessing(String processing) {
        this.processing = processing;
    }

    public String getProcessing() {
        return processing;
    }

    public void setQuality(QualityType quality) {
        this.quality = quality;
    }

    public QualityType getQuality() {
        return quality;
    }

    public void setQualityDetail(String qualityDetail) {
        this.qualityDetail = qualityDetail;
    }

    public String getQualityDetail() {
        return qualityDetail;
    }

    public void setPhysicalImageNumber(Float physicalImageNumber) {
        this.physicalImageNumber = physicalImageNumber;
    }

    public Float getPhysicalImageNumber() {
        return physicalImageNumber;
    }
}
