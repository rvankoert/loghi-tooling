package nl.knaw.huc.di.images.layoutds.models;

import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
import nl.knaw.huc.di.images.layoutds.models.connectedComponent.DicoveredLabel;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;

public class LineDescriptor {

    private ArrayList<Point> baseline;
    private ConnectedComponent lineCoco;
    private ArrayList<Point> topLine;
    private ArrayList<Point> bottomLine;
    private int medianHeight;
    private int minMarginBelow;
    private int minMarginAbove;
    private ArrayList<LineDescriptor> linesAbove;
    private ArrayList<LineDescriptor> linesBelow;

    private ArrayList<LineDescriptor> linesAboveRelaxed;
    private ArrayList<LineDescriptor> linesBelowRelaxed;

    private int xHeight;
    private Rect boundingBox = null;
    private DicoveredLabel discoveredLabel;
    private String text;
    private ArrayList<Point> compressedBaseline;

    public ArrayList<Point> getBaseline() {
        if (baseline == null) {
            if (compressedBaseline.size() > 1) {
                ArrayList<Point> returnLine = new ArrayList<>();
                Point first = null;
                for (Point point : compressedBaseline) {
                    if (first == null) {
                        first = point;
                        continue;
                    }
                    for (int i = (int) first.x; i < point.x; i++) {
                        double yBase = first.y;
                        double diff = point.y - first.y;
                        double newY = yBase + ((i - first.x) * diff) / (point.x - first.x);
                        Point p = new Point(i, newY);
                        returnLine.add(p);
                    }
                }
                returnLine.add(compressedBaseline.get(compressedBaseline.size() - 1));
                baseline = returnLine;
            } else {
                baseline = compressedBaseline;
            }
        }
        return baseline;
    }

//    public CoCo getLineCoco() {
//        return lineCoco;
//    }
//
//    public void setLineCoco(CoCo lineCoco) {
//        this.lineCoco = lineCoco;
//    }

    public ArrayList<Point> getTopLine() {
        return topLine;
    }

    public void setTopLine(ArrayList<Point> topLine) {
        this.topLine = topLine;
    }

    public ArrayList<Point> getBottomLine() {
        return bottomLine;
    }

    public void setBottomLine(ArrayList<Point> bottomLine) {
        this.bottomLine = bottomLine;
    }

    public int getMedianHeight() {
        return medianHeight;
    }

    public void setMedianHeight(int medianHeight) {
        this.medianHeight = medianHeight;
    }

    public int getMinMarginBelow() {
        return minMarginBelow;
    }

    public void setMinMarginBelow(int minMarginBelow) {
        this.minMarginBelow = minMarginBelow;
    }

    public int getMinMarginAbove() {
        return minMarginAbove;
    }

    public void setMinMarginAbove(int minMarginAbove) {
        this.minMarginAbove = minMarginAbove;
    }

    public int getWidth() {
        return (int) (baseline.get(baseline.size() - 1).x - baseline.get(0).x);
    }

    public double getLeftX() {
        return baseline.get(0).x;
    }

    public double getCenterX() {
        return baseline.get(baseline.size() / 2).x;
    }

    public double getCentery() {
        return baseline.get(baseline.size() / 2).y;
    }

    public double getRightX() {
        return baseline.get(baseline.size() - 1).x;
    }

    public ArrayList<LineDescriptor> getLinesAbove() {
        if (linesAbove == null) {
            linesAbove = new ArrayList<>();
        }
        return linesAbove;
    }

    public ArrayList<LineDescriptor> getLinesBelow() {
        if (linesBelow == null) {
            linesBelow = new ArrayList<>();
        }
        return linesBelow;
    }

    public int getXHeight() {
        return xHeight;
    }

    public void setXHeight(int xHeight) {
        this.xHeight = xHeight;
    }

    public void addLineBelow(LineDescriptor lineDescriptor) {
        if (!getLinesBelow().contains(lineDescriptor)) {
            getLinesBelow().add(lineDescriptor);
        }
    }

    public void addLineAbove(LineDescriptor lineDescriptor) {
        if (!getLinesAbove().contains(lineDescriptor)) {
            getLinesAbove().add(lineDescriptor);
        }

    }

    public ArrayList<LineDescriptor> getLinesAboveRelaxed() {
        if (linesAboveRelaxed == null) {
            linesAboveRelaxed = new ArrayList<>();
        }
        return linesAboveRelaxed;
    }

    public ArrayList<LineDescriptor> getLinesBelowRelaxed() {
        if (linesBelowRelaxed == null) {
            linesBelowRelaxed = new ArrayList<>();
        }
        return linesBelowRelaxed;
    }

    public void addLineBelowRelaxed(LineDescriptor lineDescriptor) {
        if (!getLinesBelowRelaxed().contains(lineDescriptor)) {
            getLinesBelowRelaxed().add(lineDescriptor);
        }

    }

    public void addLineAboveRelaxed(LineDescriptor lineDescriptor) {
        if (!getLinesAboveRelaxed().contains(lineDescriptor)) {
            getLinesAboveRelaxed().add(lineDescriptor);
        }
    }


    public Rect getBoundingBox() {
        if (boundingBox == null) {
            boundingBox = new Rect();
            boundingBox.x = Integer.MAX_VALUE;
            boundingBox.y = Integer.MAX_VALUE;

            for (Point point : baseline) {
                if (point.x < boundingBox.x) {
                    boundingBox.x = (int) point.x;
                }
                if (point.y - getXHeight() * 1.75 < boundingBox.y) {
                    boundingBox.y = (int) (point.y - getXHeight() * 1.75);
                }
                if (boundingBox.x < 0) {
                    boundingBox.x = 0;
                }
                if (boundingBox.y < 0) {
                    boundingBox.y = 0;
                }
                if (point.x > boundingBox.x + boundingBox.width) {
                    boundingBox.width = (int) (point.x - boundingBox.x);
                }
                if (point.y + getXHeight() * 0.75 > boundingBox.y + boundingBox.height) {
                    boundingBox.height = (int) (point.y + getXHeight() * 0.75) - boundingBox.y;
                }
            }
        }
        return boundingBox;
    }

    public void setDiscoveredLabel(DicoveredLabel discoveredLabel) {
        this.discoveredLabel = discoveredLabel;
    }

    public DicoveredLabel getDiscoveredLabel() {
        return discoveredLabel;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCompressedBaseline(ArrayList<Point> compressedBaseline) {
        this.compressedBaseline = compressedBaseline;
    }

    public ArrayList<Point> getCompressedBaseline() {
        return compressedBaseline;
    }
}
