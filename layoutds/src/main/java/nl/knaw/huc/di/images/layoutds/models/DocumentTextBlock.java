package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DocumentTextBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double confidence;
    @Transient
    private Mat binaryImage;
    @Transient
    private Mat image;
    private Integer xStart;
    private Integer yStart;
    private String text;
    private int width;
    private int height;
    private String type;
    @Transient
    private TextEquiv textEquiv;
    private ArrayList<DocumentTextBlock> documentTextBlocks;
    private String idString;
    private String title;
    private String polygon;
    private String styleRefs;
    private Float rotation;

    public DocumentTextBlock(int y, int x, Mat binaryImage, Mat image, String text) {
        this.xStart = x;
        this.yStart = y;
        this.height = image.height();
        this.width = image.width();
        this.binaryImage = binaryImage;
        this.image = image;
        this.text = text;
    }

    public DocumentTextBlock() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private ArrayList<DocumentParagraph> documentParagraphs;

    public ArrayList<DocumentParagraph> getDocumentParagraphs() {
        if (documentParagraphs == null) {
            documentParagraphs = new ArrayList<>();
        }
        return documentParagraphs;
    }

    public void setDocumentParagraphs(ArrayList<DocumentParagraph> documentParagraphs) {
        this.documentParagraphs = documentParagraphs;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Mat getBinaryImage() {
        return binaryImage;
    }

    public void setBinaryImage(Mat binaryImage) {
        this.binaryImage = binaryImage;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public Integer getXStart() {
        return xStart;
    }

    public void setXStart(Integer xStart) {
        this.xStart = xStart;
    }

    public Integer getYStart() {
        return yStart;
    }

    public void setYStart(Integer yStart) {
        this.yStart = yStart;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setType(String type) {

        this.type = type;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getType() {
        return type;
    }

    public void setTextEquiv(TextEquiv textEquiv) {
        this.textEquiv = textEquiv;
    }

    public TextEquiv getTextEquiv() {
        return textEquiv;
    }

    public ArrayList<DocumentTextBlock> getDocumentTextBlocks() {
        return documentTextBlocks;
    }

    public void setDocumentTextBlocks(ArrayList<DocumentTextBlock> documentTextBlocks) {
        this.documentTextBlocks = documentTextBlocks;
    }

    public List<DocumentParagraph> getDocumentParagraphsRecursive() {
        List<DocumentParagraph> documentParagraphs = new ArrayList<>();
        for (DocumentTextBlock documentTextBlock : getDocumentTextBlocks()) {
            documentParagraphs.addAll(documentTextBlock.getDocumentParagraphsRecursive());
        }
        documentParagraphs.addAll(getDocumentParagraphs());
        return documentParagraphs;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPolygon(String polygon) {
        this.polygon = polygon;
    }

    public String getPolygon() {
        return polygon;
    }

    public Rect getBoundingBox() {
        Point leftTop = new Point(getXStart(), getYStart());
        Point rightBottom = new Point(getXStart() + getWidth(), getYStart() + getHeight());

        return new Rect(leftTop, rightBottom);
    }

    public void setStyleRefs(String styleRefs) {
        this.styleRefs = styleRefs;
    }

    public String getStyleRefs() {
        return styleRefs;
    }

    public void setRotation(Float rotation) {
        this.rotation = rotation;
    }

    public Float getRotation() {
        return rotation;
    }
}
