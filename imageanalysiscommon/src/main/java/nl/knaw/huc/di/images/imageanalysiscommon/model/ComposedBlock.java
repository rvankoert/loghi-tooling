package nl.knaw.huc.di.images.imageanalysiscommon.model;

import org.opencv.core.Mat;

/**
 * Created by rutger on 26-4-17.
 */
public class ComposedBlock {
    public Mat get_image() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    private Mat image;
    private int xStart;
    private int yStart;
    private int width;
    private int height;

    public ComposedBlock(int y, int x, Mat image) {
        this.yStart = y;
        this.xStart = x;
        this.image = image;
        this.height = image.height();
        this.width = image.width();
    }

    public int getXStart() {
        return xStart;
    }

    public void setXStart(int xStart) {
        this.xStart = xStart;
    }

    public int getYStart() {
        return yStart;
    }

    public void setYStart(int yStart) {
        this.yStart = yStart;
    }

    public int getXStop() {
        return xStart + image.width();
    }


    public int getYStop() {
        return yStart + image.height();
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}