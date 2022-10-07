package nl.knaw.huc.di.images.layoutds.models.Page;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Point {
    @JacksonXmlProperty(isAttribute = true, localName = "x")
    private int x;
    @JacksonXmlProperty(isAttribute = true, localName = "y")
    private int y;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
