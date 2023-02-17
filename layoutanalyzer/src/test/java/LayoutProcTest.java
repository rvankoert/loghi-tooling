import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        assertThat(results, contains(
                point().withX(135).withY(600),
                point().withX(135).withY(1000),
                point().withX(90).withY(1000),
                point().withX(90).withY(600)
        ));

    }

    @Test
    public void splitLinesIntoWordsHorizontalTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 100));
        points.add(new Point(1000, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        Assert.assertEquals(600d, results.get(0).x, 0.01);
        final Optional<Double> biggestXValue = results.stream().map(point -> point.x).sorted(Comparator.reverseOrder()).collect(Collectors.toList()).stream().findFirst();
        Assert.assertEquals(Double.valueOf(1000), biggestXValue.get());
    }

    @Test
    public void splitLinesIntoWordsSkewedDownwardsTest() {
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
        assertThat(results, contains(
                point().withX(625).withY(575),
                point().withX(1025).withY(975),
                point().withX(993).withY(1007),
                point().withX(593).withY(607)
        ));
    }

    @Test
    public void splitLinesIntoWordsSkewedUpwardsTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 1000));
        points.add(new Point(1000, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(results, contains(
                point().withX(575).withY(475),
                point().withX(975).withY(75),
                point().withX(1007).withY(107),
                point().withX(607).withY(507)
        ));
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

        List<Point> resultsWord1 = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(resultsWord1, contains(
                point().withX(125).withY(75),
                point().withX(225).withY(175),
                point().withX(470).withY(119),
                point().withX(478).withY(164),
                point().withX(193).withY(207),
                point().withX(93).withY(107)
        ));

        List<Point> resultsWord2 = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(resultsWord2, contains(
               point().withX(577).withY(102),
               point().withX(997).withY(65),
               point().withX(1001).withY(110),
               point().withX(581).withY(147)
        ));
    }

    @Test
    public void splitLinesIntoWordsRealDataTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        textLine.getBaseline().setPoints("457,543 507,542 557,542 607,539 657,539 707,536 757,536 807,533 857,531 907,530 957,530 1007,527 1057,527 1077,522");
        textLine.setTextEquiv(new TextEquiv(null, "Januarij 1864."));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

    }

    @Test
    public void splitLinesIntoWordsIgnoresTextLinesWithoutBaseline() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine1 = new TextLine();
        textLine1.getBaseline().setPoints("");
        textLine1.setTextEquiv(new TextEquiv(null, "W:J:"));
        textRegion.getTextLines().add(textLine1);
        TextLine textLine2 = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(100, 100));
        baseLinePoints.add(new Point(200, 200));
        baseLinePoints.add(new Point(500, 150));
        baseLinePoints.add(new Point(1000, 100));
        textLine2.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine2.setBaseline(new Baseline());
        textLine2.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine2);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        assertThat(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords(), is(nullValue()));
        assertThat(page.getPage().getTextRegions().get(0).getTextLines().get(1).getWords(), hasSize(2));

    }

    @Test
    public void splitLinesIntoWordsIgnoresTextLinesWithoutText() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine1 = new TextLine();
        final ArrayList<Point> baseline1Points = new ArrayList<>();
        baseline1Points.add(new Point(100, 50));
        baseline1Points.add(new Point(1000, 50));
        textLine1.getBaseline().setPoints(StringConverter.pointToString(baseline1Points));
        textLine1.setTextEquiv(new TextEquiv(null, ""));
        textRegion.getTextLines().add(textLine1);
        TextLine textLine2 = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(100, 100));
        baseLinePoints.add(new Point(200, 200));
        baseLinePoints.add(new Point(500, 150));
        baseLinePoints.add(new Point(1000, 100));
        textLine2.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine2.setBaseline(new Baseline());
        textLine2.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine2);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        assertThat(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords(), is(nullValue()));
        assertThat(page.getPage().getTextRegions().get(0).getTextLines().get(1).getWords(), hasSize(2));

    }

    @Test
    public void splitLinesIntoWordsHalfCircleTest() {
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("117,1009 117,930 140,833 171,757 195,716 255,642 394,531 538,465 603,444");
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        List<Point> resultsWord1 = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(resultsWord1, contains(
                point().withX(82).withY(1009),
                point().withX(82).withY(937),
                point().withX(127).withY(937),
                point().withX(127).withY(1009)
        ));

        List<Point> resultsWord2 = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(7).getCoords().getPoints());
        assertThat(resultsWord2, contains(
                point().withX(520).withY(435),
                point().withX(523).withY(433),
                point().withX(592).withY(411),
                point().withX(606).withY(454),
                point().withX(542).withY(474),
                point().withX(538).withY(476)

        ));
    }

    @Test
    public void splitLinesIntoWordsDoesNotAddWordsWithoutCoords(){
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();

        TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "de  E van alles claerser versaken dan hebbe niet"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("714,1379 764,1385 814,1382 864,1385 914,1385 964,1385 1014,1383 1064,1383 1114,1383 1164,1382 1214,1382 1264,1382 1314,1382 1364,1382 1414,1380 1464,1380 1514,1379 1564,1379 1614,1379 1664,1379 1714,1379 1764,1379 1814,1379 1864,1379 1914,1379 1964,1377 2014,1375 2064,1375 2114,1372 2164,1371 2214,1367 2264,1363 2314,1361 2364,1356 2414,1353 2464,1350 2514,1347 2564,1345 2568,1345");
        textRegion.getTextLines().add(textLine);

        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);
        final long emptyCoordsCount = page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().stream()
                .map(Word::getCoords).map(Coords::getPoints).filter(StringUtils::isBlank).count();
        assertThat(emptyCoordsCount, is(0L));
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
            this.x = Math.round(x);
            return this;
        }

        PointMatcher withY(double y) {
            this.y = Math.round(y);
            return this;
        }
    }

    static PointMatcher point() {
        return new PointMatcher();
    }

}
