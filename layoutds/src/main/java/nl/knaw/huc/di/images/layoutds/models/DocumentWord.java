package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.Alto.FontStylesType;
import nl.knaw.huc.di.images.layoutds.models.Alto.SubsType;

public class DocumentWord {
    private String boundingBox;
    private String idString;
    private String title;
    private String content;
    private Integer xStart;
    private Integer yStart;
    private Integer xStop;
    private Integer yStop;
    private int height;
    private int width;
    private Float wordConfidence;
    private String CC;
    private String type;
    private FontStylesType style;
    private String styleRefs;
    private String subsContent;
    private SubsType subsType;

    public String getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(String boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public void setTitle(String title) {
        if (title!=null) {
            String[] splitted = title.split(";");
            for (String aSplitted : splitted) {
                String cleaned = aSplitted.trim();
                String[] subSplitted = cleaned.split(" ");
                if (subSplitted[0].equals("bbox")) {
                    this.setXStart(Integer.parseInt(subSplitted[1]));
                    this.setYStart(Integer.parseInt(subSplitted[2]));
                    int xstop = Integer.parseInt(subSplitted[3]);
                    int ystop = Integer.parseInt(subSplitted[4]);
                    this.setHeight(ystop - yStart);
                    this.setWidth(xstop - xStart);
                }
            }
        }
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

//    public Integer getXStop() {
//        return xStop;
//    }
//
//    public void setXStop(Integer xStop) {
//        this.xStop = xStop;
//    }
//
//    public Integer getYStop() {
//        return yStop;
//    }
//
//    public void setYStop(Integer yStop) {
//        this.yStop = yStop;
//    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setWordConfidence(Float wordConfidence) {
        this.wordConfidence = wordConfidence;
    }

    public Float getWordConfidence() {
        return wordConfidence;
    }

    public void setCC(String cc) {
        this.CC = cc;
    }

    public String getCC() {
        return CC;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStyle(FontStylesType style) {
        this.style = style;
    }

    public FontStylesType getStyle() {
        return style;
    }

    public void setStyleRefs(String styleRefs) {
        this.styleRefs = styleRefs;
    }

    public String getStyleRefs() {
        return styleRefs;
    }

    public String getSubsContent() {
        return subsContent;
    }

    public void setSubsContent(String subsContent) {
        this.subsContent = subsContent;
    }

    public SubsType getSubsType() {
        return subsType;
    }

    public void setSubsType(SubsType subsType) {
        this.subsType = subsType;
    }
}
