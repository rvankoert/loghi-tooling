package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TableRegion extends Region {

    @JacksonXmlProperty(isAttribute = true, localName = "rows")
    private Integer rows;
    @JacksonXmlProperty(isAttribute = true, localName = "columns")
    private Integer columns;
    // TODO make enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true, localName = "lineColour")
    private String lineColour;
    @JacksonXmlProperty(isAttribute = true, localName = "lineSeparators")
    private String lineSeparators;
    @JacksonXmlProperty(isAttribute = true, localName = "embText")
    private Boolean embText;
    // TODO make enum with values: black, blue, brown, cyan, green, gray, indigo, magenta, orange, pink, red, turquoise, violet, white, yellow, other
    @JacksonXmlProperty(isAttribute = true)
    private String bgColour;

    @JacksonXmlProperty(localName = "Grid", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private Grid grid;

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getRows() {
        return rows;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setLineColour(String lineColour) {
        this.lineColour = lineColour;
    }

    public String getLineColour() {
        return lineColour;
    }

    public void setLineSeparators(String lineSeparators) {
        this.lineSeparators = lineSeparators;
    }

    public String getLineSeparators() {
        return lineSeparators;
    }

    public void setEmbText(Boolean embText) {
        this.embText = embText;
    }

    public Boolean getEmbText() {
        return embText;
    }

    public void setBgColour(String bgColour) {
        this.bgColour = bgColour;
    }

    public String getBgColour() {
        return bgColour;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }
}
