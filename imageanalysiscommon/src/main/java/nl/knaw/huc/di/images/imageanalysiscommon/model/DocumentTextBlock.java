//package nl.knaw.huc.di.images.imageanalysiscommon.model;
//
//import org.opencv.core.Mat;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DocumentTextBlock {
//    private double confidence;
//    private Mat binaryImage;
//    private Mat image;
//    private int xStart;
//    private int yStart;
//    private String text;
//
//    private List<DocumentTextLine> textLines;
//
//    public DocumentTextBlock(int y, int x, Mat binaryImage, Mat image, String text) {
//        this.yStart = y;
//        this.xStart = x;
//        this.binaryImage = binaryImage;
//        this.image = image;
//        this.text = text;
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
//    public Mat getBinaryImage() {
//        return binaryImage;
//    }
//
//    public void setBinaryImage(Mat image) {
//        this.binaryImage = image;
//    }
//
//
//    public int getXStart() {
//        return xStart;
//    }
//
//    public void setXStart(int xStart) {
//        this.xStart = xStart;
//    }
//
//    public int getYStart() {
//        return yStart;
//    }
//
//    public void setYStart(int yStart) {
//        this.yStart = yStart;
//    }
//
//    public int getXStop() {
//        return xStart + binaryImage.width();
//    }
//
//
//    public int getYStop() {
//        return yStart + binaryImage.height();
//    }
//
//
//    public int getWidth() {
//
//        return getBinaryImage().width();
//    }
//
//    public int getHeight() {
//        return getBinaryImage().height();
//    }
//
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public List<DocumentTextLine> getTextLines() {
//        if (textLines == null) {
//            textLines = new ArrayList<>();
//        }
//        return textLines;
//    }
//
//    public void setTextLines(List<DocumentTextLine> textLines) {
//        this.textLines = textLines;
//    }
//
//    public double getConfidence() {
//        return confidence;
//    }
//
//    public void setConfidence(double confidence) {
//        this.confidence = confidence;
//    }
//}
