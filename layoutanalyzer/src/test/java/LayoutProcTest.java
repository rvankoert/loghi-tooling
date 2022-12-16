import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

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
    public void splitLinesIntoWordsVerticalBaselineTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(100, 100));
        baseLinePoints.add(new Point(100, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
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
        List<Point> baselinePoints = new ArrayList<>();
        baselinePoints.add(new Point(100, 100));
        baselinePoints.add(new Point(1000, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baselinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);
        LayoutProc.splitLinesIntoWords(page);
        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        Assert.assertEquals(600d - 35d, results.get(0).y, 0.01);
    }

    @Test
    public void splitLinesIntoWordsCrookedLineTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(100, 100));
        baseLinePoints.add(new Point(200, 200));
        baseLinePoints.add(new Point(500, 150));
        baseLinePoints.add(new Point(1000, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);
        LayoutProc.splitLinesIntoWords(page);
        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(results, Matchers.hasSize(6));
//        Assert.assertEquals(600d, results.get(0).x, 0.01);
    }

    @Test
    public void splitLinesIntoWordsPointsAreOrderedAsABox() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(100, 100));
        baseLinePoints.add(new Point(200, 200));
        baseLinePoints.add(new Point(500, 150));
        baseLinePoints.add(new Point(1000, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);
        LayoutProc.splitLinesIntoWords(page);
        List<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(results, contains(
                point().withX(100).withY(100 - 35),
                point().withX(200).withY(200 - 35),
                point().withX(500).withY(150 - 35),
                point().withX(500).withY(150 + 10),
                point().withX(200).withY(200 + 10),
                point().withX(100).withY(100 + 10)
        ));
    }

    static class PointMatcher extends TypeSafeMatcher<Point> {
        private double x;
        private double y;

        public PointMatcher() {
        }

        @Override
        protected boolean matchesSafely(Point point) {
            return point.x == this.x && point.y == this.y;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Expected x to be ").appendValue(x).appendText(" and y to be ").appendValue(y);
        }

        PointMatcher withX(double x){
            this.x = x;
            return this;
        }

        PointMatcher withY(double y) {
            this.y = y;
            return this;
        }
    }

    static PointMatcher point() {
        return new PointMatcher();
    }

}