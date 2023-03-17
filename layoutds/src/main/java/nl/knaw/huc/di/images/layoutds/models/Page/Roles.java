package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Roles {
    @JacksonXmlProperty(localName = "TableCellRole", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private TableCellRole tableCellRole;

    public TableCellRole getTableCellRole() {
        return tableCellRole;
    }

    public void setTableCellRole(TableCellRole tableCellRole) {
        this.tableCellRole = tableCellRole;
    }
}
