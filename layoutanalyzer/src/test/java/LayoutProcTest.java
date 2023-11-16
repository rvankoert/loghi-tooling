import com.google.common.collect.Ordering;
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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1500);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1500);
        page.getPage().setImageWidth(2000);

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
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "de E van alles claerser versaken dan hebbe niet"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("714,1379 764,1385 814,1382 864,1385 914,1385 964,1385 1014,1383 1064,1383 1114,1383 1164,1382 1214,1382 1264,1382 1314,1382 1364,1382 1414,1380 1464,1380 1514,1379 1564,1379 1614,1379 1664,1379 1714,1379 1764,1379 1814,1379 1864,1379 1914,1379 1964,1377 2014,1375 2064,1375 2114,1372 2164,1371 2214,1367 2264,1363 2314,1361 2364,1356 2414,1353 2464,1350 2514,1347 2564,1345 2568,1345");
        textRegion.getTextLines().add(textLine);

        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);
        final long emptyCoordsCount = page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().stream()
                .map(Word::getCoords).map(Coords::getPoints).filter(StringUtils::isBlank).count();
        assertThat(emptyCoordsCount, is(0L));
    }

    @Test
    public void splitLinesSupportsSentencesWithDoubleSpaces(){
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "de  E van  alles  claerser  versaken  dan  hebbe  niet"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("714,1379 764,1385 814,1382 864,1385 914,1385 964,1385 1014,1383 1064,1383 1114,1383 1164,1382 1214,1382 1264,1382 1314,1382 1364,1382 1414,1380 1464,1380 1514,1379 1564,1379 1614,1379 1664,1379 1714,1379 1764,1379 1814,1379 1864,1379 1914,1379 1964,1377 2014,1375 2064,1375 2114,1372 2164,1371 2214,1367 2264,1363 2314,1361 2364,1356 2414,1353 2464,1350 2514,1347 2564,1345 2568,1345");
        textRegion.getTextLines().add(textLine);

        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);
        final long emptyCoordsCount = page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().size();
        assertThat(emptyCoordsCount, is(9L));
    }

    @Test
    public void splitLinesSupportsSingleCharacterLastWords(){
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "6, Lammen „ 10. „ \" 20, -, -"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("1216,1964 1266,1854 1366,1851 1466,1851 1616,1857 1666,1863 1716,1853 1766,1845 1816,1854 1866,1867 1916,1870 1966,1881 2066,1884 2166,1894 2216,1889 2266,1887 2316,1892 2366,1900 2416,1894 2466,1894 2516,1885 2566,1893 2666,1898 2716,1896 2766,1892 2866,1891 2916,1895 2966,1902 3016,1905 3061,2047");
        textRegion.getTextLines().add(textLine);

        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);
        final long emptyCoordsCount = page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().size();
        assertThat(emptyCoordsCount, is(9L));
    }

    @Test
    public void splitLinesIntoWordsCoordsXValuesShouldBeInOrder() {
        final TextLine textLine = new TextLine();
        textLine.setTextEquiv(new TextEquiv(null, "SeHoUT er WETLIODSEREN"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("2904,899 2954,896 3004,897 3054,896 3104,897 3154,897 3204,898 3254,899 3304,901 3354,902 3404,904 3454,904 3504,903 3554,901 3604,905 3654,908 3704,908 3754,910 3804,911 3854,912 3904,913 3954,914 4004,914 4054,914 4104,914 4154,917 4204,918 4254,919 4304,921 4354,922 4404,921 4454,924 4504,925 4554,926 4604,926 4654,927 4661,932");

        final TextRegion textRegion = new TextRegion();
        textRegion.getTextLines().add(textLine);
        final PcGts pcGts = new PcGts();
        pcGts.getPage().setImageHeight(1000);
        pcGts.getPage().setImageWidth(2000);
        pcGts.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(pcGts);

        assertPointsAreInOrder(pcGts.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0));
        assertPointsAreInOrder(pcGts.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1));
        assertPointsAreInOrder(pcGts.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(2));
    }

    private void assertPointsAreInOrder(Word word) {
        final ArrayList<Point> points = StringConverter.stringToPoint(word.getCoords().getPoints());
        final List<Double> topXPoints = points.subList(0, points.size() / 2).stream().map(point -> point.x).collect(Collectors.toList());
        final List<Double> bottomXPoints = points.subList(points.size() / 2, points.size()).stream().map(point -> point.x).collect(Collectors.toList());

        assertThat(Ordering.natural().isOrdered(topXPoints), is(true));
        assertThat(Ordering.natural().reverse().isOrdered(bottomXPoints), is(true));
    }

    @Test
    public void  splitLinesIntoWordsCoordsXValuesAreNotNegativeTopDown() {
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(0, 100));
        baseLinePoints.add(new Point(0, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1Results.stream().map(point -> point.x).allMatch(x -> x >= 0), is(true));
        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point.x).allMatch(x -> x >= 0), is(true));

    }

    @Test
    public void  splitLinesIntoWordsCoordsXValuesAreNotNegativeBottomUp() {
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(0, 1000));
        baseLinePoints.add(new Point(0, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1Results.stream().map(point -> point.x).allMatch(x -> x >= 0), is(true));
        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point.x).allMatch(x -> x >= 0), is(true));

    }

    @Test
    public void splitLinesIntoWordsCoordsXValuesADoNotExceedThePageWidthTopDown() {
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        final int imageWidth = 500;
        page.getPage().setImageWidth(imageWidth);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(490, 100));
        baseLinePoints.add(new Point(490, 1000));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1Results.stream().map(point -> point.x).allMatch(x -> x <= imageWidth), is(true));
        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point.x).allMatch(x -> x <= imageWidth), is(true));
    }

    @Test
    public void splitLinesIntoWordsCoordsXValuesADoNotExceedThePageWidthBottomUp() {
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        final int imageWidth = 500;
        page.getPage().setImageWidth(imageWidth);

        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> baseLinePoints = new ArrayList<>();
        baseLinePoints.add(new Point(495, 1000));
        baseLinePoints.add(new Point(495, 100));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(baseLinePoints));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1Results.stream().map(point -> point.x).allMatch(x -> x <= imageWidth), is(true));
        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point.x).allMatch(x -> x <= imageWidth), is(true));
    }

    @Test
    public void splitLinesIntoWordsCoordsYValuesAreNotNegative() {
        PcGts page = new PcGts();
        page.getPage().setImageHeight(1000);
        page.getPage().setImageWidth(2000);
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 10));
        points.add(new Point(1000, 10));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1results.stream().map(point -> point .y).allMatch(y -> y >= 0), is(true));

        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point .y).allMatch(y -> y >= 0), is(true));
    }

    @Test
    public void splitLinesIntoWordsCoordsYValuesDoNotExceedThePageHeight() {
        PcGts page = new PcGts();
        final int imageHeight = 100;
        page.getPage().setImageHeight(imageHeight);
        page.getPage().setImageWidth(1500);
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();
        List<Point> points = new ArrayList<>();
        points.add(new Point(100, 95));
        points.add(new Point(1000, 95));
        textLine.setTextEquiv(new TextEquiv(null, "test asdf"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints(StringConverter.pointToString(points));
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

        ArrayList<Point> word1results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(0).getCoords().getPoints());
        assertThat(word1results.stream().map(point -> point .y).allMatch(y -> y <= imageHeight), is(true));

        ArrayList<Point> word2Results = StringConverter.stringToPoint(page.getPage().getTextRegions().get(0).getTextLines().get(0).getWords().get(1).getCoords().getPoints());
        assertThat(word2Results.stream().map(point -> point .y).allMatch(y -> y <= imageHeight), is(true));
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


    @Test
    public void splitLinesIntoWordsProdFailPointsStringNotValid() {
        PcGts page = new PcGts();
        final int imageHeight = 10000;
        page.getPage().setImageHeight(imageHeight);
        page.getPage().setImageWidth(10000);
        TextRegion textRegion = new TextRegion();
        TextLine textLine = new TextLine();

        textLine.setTextEquiv(new TextEquiv(null, "medtd it  16 D. 8 bottel „ 390, 32. \" -"));
        textLine.setBaseline(new Baseline());
        textLine.getBaseline().setPoints("1271,2000 1321,1630 1371,1615 1421,1602 1471,1641 1521,1670 1571,1654 1621,1649 1671,1649 1721,1655 1771,1676 1871,1668 1921,1675 1971,1687 2021,1692 2071,1687 2121,1670 2171,1684 2221,1700 2271,1693 2321,1699 2371,1695 2471,1672 2521,1668 2571,1645 2621,1628 2671,1547 2721,1589 2771,1578 2821,1581 2871,1588 2971,1707 3021,1711 3071,1724 3107,1943");
        textRegion.getTextLines().add(textLine);
        page.getPage().getTextRegions().add(textRegion);

        LayoutProc.splitLinesIntoWords(page);

    }
}
