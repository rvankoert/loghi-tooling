package nl.knaw.huc.di.images.layoutds.models.connectedComponent;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ConnectedComponent {
    private int x;
    private int y;
    private DicoveredLabel label;
    private Color color;
    private BufferedImage bitMap;
    private Object parent=null;

    public ConnectedComponent(int x, int y, BufferedImage bitMap, Color color) {
        this.x = x;
        this.y = y;
        this.bitMap = bitMap;
        this.color = color;
    }

    public DicoveredLabel getLabel() {
        return label;
    }

    public void setLabel(DicoveredLabel label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BufferedImage getBitMap() {
        return bitMap;
    }

    public int getHeight() {
        return getBitMap().getHeight();
    }

    public int getWidth() {
        return getBitMap().getWidth();
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
}