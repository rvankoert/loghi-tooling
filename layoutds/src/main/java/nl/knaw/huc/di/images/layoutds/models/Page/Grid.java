package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Grid {
    @JacksonXmlProperty(localName = "GridPoints", namespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")
    private List<GridPoints> gridPointsList;

    public List<GridPoints> getGridPointsList() {
        return gridPointsList;
    }

    public void setGridPointsList(List<GridPoints> gridPointsList) {
        this.gridPointsList = gridPointsList;
    }
}
