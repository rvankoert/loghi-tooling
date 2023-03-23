package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TableCellRole {
    @JacksonXmlProperty(isAttribute = true)
    private int rowIndex;
    @JacksonXmlProperty(isAttribute = true)
    private int columnIndex;
    @JacksonXmlProperty(isAttribute = true)
    private int rowSpan = 1;
    @JacksonXmlProperty(isAttribute = true)
    private int colSpan = 1;
    @JacksonXmlProperty(isAttribute = true)
    private boolean header;

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
}
