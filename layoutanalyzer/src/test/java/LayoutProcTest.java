import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

public class LayoutProcTest {
    @Test
    public void overlapTestEquals() {
        Rect first = new Rect(10, 10, 10, 10);
        Rect second = new Rect(10, 10, 10, 10);
        double result = LayoutProc.intersectOverUnion(first, second);
        Assert.assertEquals(1.0d, result, 0.0001);
    }

    @Test
    public void overlapTestHalf() {
        Rect first = new Rect(5, 10, 10, 10);
        Rect second = new Rect(10, 10, 10, 10);
        double result = LayoutProc.intersectOverUnion(first, second);
        Assert.assertEquals(1 / 3d, result, 0.0001);
    }

    @Test
    public void overlapTestQuarter() {
        Rect first = new Rect(5, 5, 10, 10);
        Rect second = new Rect(10, 10, 10, 10);
        double result = LayoutProc.intersectOverUnion(first, second);
        Assert.assertEquals(1 / 7d, result, 0.0001);
    }

    @Test
    public void overlapTestHalfReversed() {
        Rect second = new Rect(5, 10, 10, 10);
        Rect first = new Rect(10, 10, 10, 10);
        double result = LayoutProc.intersectOverUnion(first, second);
        Assert.assertEquals(1 / 3d, result, 0.0001);
    }

    @Test
    public void overlapTestNone() {
        Rect first = new Rect(0, 0, 10, 10);
        Rect second = new Rect(10, 10, 10, 10);
        double result = LayoutProc.intersectOverUnion(first, second);
        Assert.assertEquals(0, result, 0.0001);
    }

    @Test
    public void splitLinesIntoWordsSimpleTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(100, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);
        LayoutProc.splitLinesIntoWords(page);
        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        Assert.assertEquals(600d, results.get(0).x, 0.01);
    }

    @Test
    public void splitLinesIntoWordsSkewedTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(1000, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);
        LayoutProc.splitLinesIntoWords(page);
        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        Assert.assertEquals(600d - 35d, results.get(0).y, 0.01);
    }

}