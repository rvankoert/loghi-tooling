package nl.knaw.huc.di.images.layoutds.models;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


@Entity
@XmlRootElement
public class DocumentTextLine implements Serializable {

    private String text;
    private String confidence;
    private String coordinates = "";
    private ArrayList<Point> baseLine;
    private String custom;
    private String primaryLanguage;
    private Boolean bold;
    private Boolean underlined;
    private Boolean superscript;
    private Boolean subscript;
    private Float fontSize;
    private Boolean italic;
    private Boolean strikethrough;
    private String fontFamily;
    private int ascenders;
    private int descenders;
    private String title;
    private int xsize;
    private String idString;
    private ArrayList<DocumentWord> words;
    private Integer xStart;
    private Integer yStart;
    private Integer xStop;
    private Integer yStop;
    private Integer lineCenter;
    private Integer height;
    private Integer width;

    @Transient
    private Mat binaryImage;
    @Transient
    private Mat image;
    private Integer XHeight;
    private ArrayList<Point> upperPoints;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer baseLineY;
    private ArrayList<Point> compressedBaseLine;
    private Boolean CS;

    public DocumentTextLine(int startY, int startX, int height, int width, int lineCenter, String text) {
        this.xStart = startX;
        this.yStart = startY;
        this.height = height;
        this.width = width;
        this.lineCenter = lineCenter;
        this.text = text;
        this.baseLine = new ArrayList<>();
        this.compressedBaseLine = new ArrayList<>();
    }

    public DocumentTextLine(Integer startY, Integer startX, Integer height, Integer width) {
        this.xStart = startX;
        this.yStart = startY;
        this.height = height;
        this.width = width;
    }


    public DocumentTextLine() {
        this.baseLine = new ArrayList<>();
        this.compressedBaseLine = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column
    private Date lastChanged;

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {

        this.lastChanged = lastChanged;
    }

    ArrayList<Point> upperBoundary;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setBaseLine(ArrayList<Point> baseLine) {
        this.baseLine = baseLine;
    }

    public ArrayList<Point> getBaseLine() {
        if (baseLine == null) {
            baseLine = new ArrayList<>();
        }
        return baseLine;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getCustom() {
        return custom;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getBold() {
        return bold;
    }

    public Rect getBoundingBox() {
        Point leftTop = new Point(getXStart(), getYStart());
        Point rightBottom = new Point(getXStart() + getWidth(), getYStart() + getHeight());

        return new Rect(leftTop, rightBottom);
    }

    public void setAscenders(int ascenders) {
        this.ascenders = ascenders;
    }

    public int getAscenders() {
        return ascenders;
    }

    public void setDescenders(int descenders) {
        this.descenders = descenders;
    }

    public int getDescenders() {
        return descenders;
    }

    public void setTitle(String title) {
        if (title != null) {
            String[] splitted = title.split(";");
            for (String aSplitted : splitted) {
                String cleaned = aSplitted.trim();
                String[] subSplitted = cleaned.split(" ");
                if (subSplitted[0].equals("bbox")) {
                    this.setXStart(Integer.parseInt(subSplitted[1]));
                    this.setYStart(Integer.parseInt(subSplitted[2]));
                    this.setXStop(Integer.parseInt(subSplitted[3]));
                    this.setYStop(Integer.parseInt(subSplitted[4]));
                }
            }
        }
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setXsize(int xsize) {
        this.xsize = xsize;
    }

    public int getXsize() {
        return xsize;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getIdString() {
        return idString;
    }

    public ArrayList<DocumentWord> getWords() {
        return words;
    }

    public void setWords(ArrayList<DocumentWord> words) {
        this.words = words;
    }

    public Integer getXStart() {
        return xStart;
    }

    private void setXStart(Integer xStart) {
        this.xStart = xStart;
    }

    public Integer getYStart() {
        return yStart;
    }

    private void setYStart(Integer yStart) {
        this.yStart = yStart;
    }

    public Integer getXStop() {
        return xStop;
    }

    private void setXStop(Integer xStop) {
        this.xStop = xStop;
    }

    public Integer getYStop() {
        return yStop;
    }

    private void setYStop(Integer yStop) {
        this.yStop = yStop;
    }

    public void setBaseline(ArrayList<Point> baseline) {
        this.baseLine = baseline;
    }

    public ArrayList<Point> getBaseline() {
        return baseLine;
    }

    public void setXHeight(Integer xHeight) {
        this.XHeight = xHeight;
    }

    public Integer getXHeight() {
        return XHeight;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }


    public int getLineCenter() {
        return lineCenter;
    }

    public void setUpperPoints(ArrayList<Point> upperPoints) {
        this.upperPoints = upperPoints;
    }

    public ArrayList<Point> getUpperPoints() {
        return upperPoints;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public Mat getBinaryImage() {
        return binaryImage;
    }

    public void setBinaryImage(Mat binaryImage) {
        this.binaryImage = binaryImage;
    }

    public void setBaseLineY(Integer baseLineY) {
        this.baseLineY = baseLineY;
    }

    public Integer getBaseLineY() {
        return baseLineY;
    }

    public ArrayList<Point> getCompressedBaseLine() {
        return compressedBaseLine;
    }

    public void setCompressedBaseLine(ArrayList<Point> compressedBaseLine) {
        this.compressedBaseLine = compressedBaseLine;
    }

    public void setCS(Boolean cs) {
        this.CS = cs;
    }

    public Boolean getCS() {
        return CS;
    }


    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }

    public void setSuperscript(Boolean superscript) {
        this.superscript = superscript;
    }

    public void setSubscript(Boolean subscript) {
        this.subscript = subscript;
    }

    public Float getFontSize() {
        return fontSize;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public Boolean getItalic() {
        return italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public Boolean getStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }
}
