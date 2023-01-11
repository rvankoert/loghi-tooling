package nl.knaw.huc.di.images.imageanalysiscommon;

import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class StringConverterTest {
    @Test
    public void calculateLengthOfHorizontalBaseLine() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(1000, 100));

        final double length = StringConverter.calculateBaselineLength(points);
        Assert.assertEquals(900d, length, 0.01);
    }

    @Test
    public void calculateLengthOfVerticalBaseLine() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(100, 1000));

        final double length = StringConverter.calculateBaselineLength(points);
        Assert.assertEquals(900d, length, 0.01);
    }

    @Test
    public void calculateLengthOfDiagonalBaseLine() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(400, 500));

        final double length = StringConverter.calculateBaselineLength(points);
        Assert.assertEquals(500d, length, 0.01);
    }

    @Test
    public void calculateLengthOfCrookedBaseLine() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(400, 500));
        points.add(new Point(700, 100));
        points.add(new Point(1000, 500));
        points.add(new Point(1300, 100));

        final double length = StringConverter.calculateBaselineLength(points);
        Assert.assertEquals(2000d, length, 0.01);
    }

}