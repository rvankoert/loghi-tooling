//package nl.knaw.huc.di.images.imageanalysiscommon.model;
//
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//
//import java.util.List;
//
//public class DocumentTextLine {
//    private Mat binaryImage;
//    private Mat image;
//    private int lineCenter;
//    private int startX;
//
//    private int startY;
//    private String text;
//
//    private List<Point> upperPoints;
//    private List<Point> baseline;
//    private List<Point> topLine;
//    private List<Point> bottomLine;
//    private int XHeight;
//
//    public DocumentTextLine() {
//    }
//
//    public DocumentTextLine(int startY, int startX, Mat binaryImage, Mat image, int lineCenter, String text) {
//        this.startX = startX;
//        this.startY = startY;
//        this.binaryImage = binaryImage;
//        this.image = image;
//        this.lineCenter = lineCenter;
//        this.text = text;
//    }
//
//    public Mat getBinaryImage() {
//        return binaryImage;
//    }
//
//    public void setBinaryImage(Mat binaryImage) {
//        this.binaryImage = binaryImage;
//    }
//
//    public Mat getImage() {
//        return image;
//    }
//
//    public void setImage(Mat image) {
//        this.image = image;
//    }
//
//    public int getStartX() {
//        return startX;
//    }
//
//    public int getStartY() {
//        return startY;
//    }
//
//    public int getLineCenter() {
//        return lineCenter;
//    }
//
//    public int getWidth() {
//        return binaryImage.width();
//    }
//
//    public int getHeight() {
//        return binaryImage.height();
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public List<Point> getUpperPoints() {
//        return upperPoints;
//    }
//
//    public void setUpperPoints(List<Point> upperPoints) {
//        this.upperPoints = upperPoints;
//    }
//
//    public int getStopX() {
//        return getStartX()+getWidth();
//    }
//
//    public int getStopY() {
//        return getStartY()+getHeight();
//    }
//
//    public List<Point> getBaseline() {
//        return baseline;
//    }
//
//    public void setBaseline(List<Point> baseline) {
//        this.baseline = baseline;
//    }
//
//    public void setTopLine(List<Point> topLine) {
//        this.topLine = topLine;
//    }
//
//    public List<Point> getTopLine() {
//        return topLine;
//    }
//
//    public void setBottomLine(List<Point> bottomLine) {
//        this.bottomLine = bottomLine;
//    }
//
//    public List<Point> getBottomLine() {
//        return bottomLine;
//    }
//
//    public void setXHeight(int xHeight) {
//        this.XHeight = xHeight;
//    }
//
//    public int getXHeight() {
//        return XHeight;
//    }
//}
//
