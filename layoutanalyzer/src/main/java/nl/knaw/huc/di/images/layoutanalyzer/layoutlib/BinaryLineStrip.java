package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import org.opencv.core.Mat;

public class BinaryLineStrip {
    Mat lineStrip;
    Integer xHeight;

    public Mat getLineStrip() {
        return lineStrip;
    }

    public void setLineStrip(Mat lineStrip) {
        this.lineStrip = lineStrip;
    }

    public Integer getxHeight() {
        return xHeight;
    }

    public void setxHeight(Integer xHeight) {
        this.xHeight = xHeight;
    }
}
