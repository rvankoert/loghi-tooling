package nl.knaw.huc.di.images.layoutanalyzer;

public class TestData {
    private String uri;
    private Integer pages;
    private Integer columns;
    private Integer textLines;
    private Integer borderLeft;
    private Integer borderRight;
    private Integer borderTop;
    private Integer borderBottom;
    private Boolean machinePrint;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPages() {
        return pages;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setTextLines(Integer textLines) {
        this.textLines = textLines;
    }

    public Integer getTextLines() {
        return textLines;
    }

    public void setBorderLeft(Integer borderLeft) {
        this.borderLeft = borderLeft;
    }

    public Integer getBorderLeft() {
        return borderLeft;
    }

    public void setBorderRight(Integer borderRight) {
        this.borderRight = borderRight;
    }

    public Integer getBorderRight() {
        return borderRight;
    }

    public void setBorderTop(Integer borderTop) {
        this.borderTop = borderTop;
    }

    public Integer getBorderTop() {
        return borderTop;
    }

    public void setBorderBottom(Integer borderBottom) {
        this.borderBottom = borderBottom;
    }

    public Integer getBorderBottom() {
        return borderBottom;
    }

    public void setMachinePrint(Boolean machinePrint) {
        this.machinePrint = machinePrint;
    }

    public Boolean getMachinePrint() {
        return machinePrint;
    }
}