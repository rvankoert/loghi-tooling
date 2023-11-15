package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.imageanalysiscommon.visualization.VisualizationHelper;
import nl.knaw.huc.di.images.layoutanalyzer.DocumentPage;
import nl.knaw.huc.di.images.layoutanalyzer.LayoutConfiguration;
import nl.knaw.huc.di.images.layoutanalyzer.Statistics;
import nl.knaw.huc.di.images.layoutds.models.DocumentTextBlock;
import nl.knaw.huc.di.images.layoutds.models.DocumentTextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.StringWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static nl.knaw.huc.di.images.imageanalysiscommon.StringConverter.distance;
import static org.opencv.core.CvType.*;
import static org.opencv.imgproc.Imgproc.*;

public class LayoutProc {
    private static final Logger LOG = LoggerFactory.getLogger(LayoutProc.class);

    public static final int MINIMUM_XHEIGHT = 15;
    public static final int MINIMUM_WIDTH = 5;
    public static final UnicodeToAsciiTranslitirator UNICODE_TO_ASCII_TRANSLITIRATOR = new UnicodeToAsciiTranslitirator();
    private static boolean _outputDebug = true;
    private static final int bestBinarizationThreshold = 15;
    private static final int bestBinarizationBlockSize = 51;

    public static void setOutputDebug(boolean _outputDebug) {
        LayoutProc._outputDebug = _outputDebug;
    }

    public static void removeCoco(Mat input, ConnectedComponent coco) {
        int cocoBitmapWidth = coco.getBitMap().getWidth();
        int cocoBitmapHeight = coco.getBitMap().getHeight();
        BufferedImage cocoBitMap = coco.getBitMap();
        for (int i = 0; i < cocoBitmapHeight; i++) {
            for (int j = 0; j < cocoBitmapWidth; j++) {
                if (cocoBitMap.getRGB(j, i) == Color.WHITE.getRGB()) {
                    // // TODO: 20-9-16 don't use put, there is a faster way
                    input.put(i + coco.getY(), j + coco.getX(), 0);
                }
            }
        }
    }

    public static void deSpeckle(Mat input, java.util.List<ConnectedComponent> cocos, int minimumSize) {
        for (ConnectedComponent coco : cocos) {
            if (coco.getWidth() < minimumSize && coco.getHeight() < minimumSize) {
                removeCoco(input, coco);
            }
        }
    }

    public static void deSpeckleFast(Mat input, int minimumSize, Mat labels, Mat stats, Mat centroids, int noComponents) {
        Mat mask = new Mat();
        for (int i = 0; i < noComponents; i++) {
            double height = stats.get(i, CC_STAT_HEIGHT)[0];
            double width = stats.get(i, CC_STAT_WIDTH)[0];
            if (height < minimumSize && width < minimumSize) {
                Core.inRange(labels, new Scalar(i), new Scalar(i), mask);
                input.setTo(new Scalar(0), mask);
            }
        }
//        for (CoCo coco : cocos) {
//            if (coco.getBitMap().getWidth() < minimumSize && coco.getBitMap().getHeight() < minimumSize) {
//                removeCoco(input, coco);
//            }
//        }
    }

    public static void deSpeckleSingleDimension(Mat input, List<ConnectedComponent> cocos, int minimumSize) {
        for (ConnectedComponent coco : cocos) {
            if (coco.getBitMap().getWidth() < minimumSize || coco.getBitMap().getHeight() < minimumSize) {
                removeCoco(input, coco);
            }
        }
    }

    public static void removeLargeCoCos(Mat input, List<ConnectedComponent> cocos, int minimumSize) {
        for (ConnectedComponent coco : cocos) {
            if (coco.getBitMap().getWidth() > minimumSize || coco.getBitMap().getHeight() > minimumSize) {
                removeCoco(input, coco);
            }
        }
    }

    private static List<Integer> horizontalProfileByte(Mat binaryImage, int startY, int stopY, int startX, int stopX, int thickness) {
        if (binaryImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> horizontals = new ArrayList<>();

        for (int y = 0; y < startY; y++) {
            horizontals.add(y, 0);
        }
        for (int y = startY; y < stopY; y++) {
            int length = 0;
            int currentLength = 0;
            for (int x = startX; x < stopX; x++) {
                if (data[y * width + x] != 0) {
                    currentLength++;
                } else {
                    currentLength = 0;
                }
                if (thickness == 0 || currentLength < thickness) {
                    length += data[y * width + x] & 0xFF;
                }
            }
            horizontals.add(y, length / 255);
        }
        return horizontals;
    }

    public static List<Integer> horizontalProfileByte(Mat binaryImage, int startY, int stopY) {
        if (binaryImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        int width = binaryImage.width();
        ArrayList<Integer> verticals = new ArrayList<>();

        for (int y = startY; y < stopY; y++) {
            verticals.add(y - startY, Core.countNonZero(binaryImage.submat(y, y + 1, 0, width)));
        }

        return verticals;
    }

    public static List<Integer> horizontalProfileInt(Mat binaryImage, int startY, int stopY) {
        if (binaryImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        int[] data = new int[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> verticals = new ArrayList<>();

        for (int y = startY; y < stopY; y++) {
            int length = 0;
            for (int x = 0; x < width; x++) {
                length += data[y * width + x] & 0xFF;
            }
            verticals.add(y, length);
        }

        return verticals;
    }

    public static List<Integer> horizontalProfileByte(Mat binaryImage) {
        if (binaryImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> horizontals = new ArrayList<>();

        for (int y = 0; y < binaryImage.height(); y++) {
            int length = 0;
            for (int x = 0; x < width; x++) {
                length += data[y * width + x] & 0xFF;
            }
            horizontals.add(y, length / 255);
        }

        return horizontals;
    }

    public static List<Integer> verticalProfile(Mat binaryImage, int startY, int stopY) {
        if (binaryImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> verticals = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            int length = 0;
            for (int y = startY; y < stopY; y++) {
                length += data[y * width + x] & 0xFF;
            }
            verticals.add(x, length / 255);
        }

        return verticals;
    }

    public static List<Integer> verticalProfileInt(Mat verticalImage, int startY, int stopY) {
        if (verticalImage.channels() != 1) {
            LOG.error("invalid input, image is not binary/grayscale");
        }
        long size = verticalImage.total() * verticalImage.channels();
        int width = verticalImage.width();
        int[] data = new int[(int) size];
        verticalImage.get(0, 0, data);
        ArrayList<Integer> verticals = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            int length = 0;
            for (int y = startY; y < stopY; y++) {
                length += data[y * width + x];//& 0xFF;
            }
            verticals.add(x, length / 255);
        }

        return verticals;
    }

    public static Mat horizontalRunlengthByte(Mat input, int targetcolor) {
        int size = (int) input.total() * input.channels();
        int width = input.width();
        int height = input.height();
        byte[] data = new byte[size];
        input.get(0, 0, data);
        Mat destination = Mat.zeros(height, width, CvType.CV_8UC1);
        for (int y = 0; y < height; y++) {
            int length = 0;
            for (int x = 0; x < width; x++) {
                if ((data[y * width + x] & 0xFF) == targetcolor) {
                    length++;
                }

                // end of line || new empty pixel
                if (length > 0 && x == width - 1 || length > 0 && (data[y * width + x] & 0xFF) != targetcolor) {
                    VisualizationHelper.drawHorizontalLine(destination, x - (length - 1), y, length, true);
                    length = 0;
                }
            }
        }
        return destination;
    }

    public static Mat horizontalRunlengthInt(Mat input, int targetcolor) {
        int size = (int) input.total() * input.channels();
        int width = input.width();
        int height = input.height();
        byte[] data = new byte[size];
        input.get(0, 0, data);
        Mat destination = Mat.zeros(height, width, CvType.CV_32S);
        for (int y = 0; y < height; y++) {
            int length = 0;
            for (int x = 0; x < width; x++) {
                if ((data[y * width + x] & 0xFF) == targetcolor) {
                    length++;
                }

                // end of line || new empty pixel
                if (length > 0 && x == width - 1 || length > 0 && (data[y * width + x] & 0xFF) != targetcolor) {
                    VisualizationHelper.drawHorizontalLine(destination, x - (length - 1), y, length, false);
                    length = 0;
                }
            }
        }
        return destination;
    }

    public static Mat verticalRunlengthInt(Mat binaryImage, int targetColor) {
        int size = (int) binaryImage.total() * binaryImage.channels();
        int imageWidth = binaryImage.width();
        int imageHeight = binaryImage.height();
        byte[] data = new byte[size];
        binaryImage.get(0, 0, data);
        Mat destination = Mat.zeros(imageHeight, imageWidth, CvType.CV_32S);

        for (int x = 0; x < imageWidth; x++) {
            int length = 0;
            for (int y = 0; y < imageHeight; y++) {
                int value = data[y * imageWidth + x] & 0xFF;
                if (value == targetColor) {
                    length++;
                }

                // end of line || new empty pixel
                if (length > 0 && y == imageHeight - 1 || length > 0 && value != targetColor) {
                    VisualizationHelper.drawVerticalLine(destination, x, y - (length - 1), length, false);
                    length = 0;
                }
            }
        }
        return destination;
    }

    private static int getBottomMargin(List<Double> profile, int from) {
        double bestValue;
        int bottomMargin = profile.size() - 1;
        bestValue = 0;
        for (int i = from; i < profile.size(); i++) {
            if (profile.get(i) > bestValue) {
                bestValue = profile.get(i);
                bottomMargin = i;
            }
        }
        return bottomMargin;
    }

    public static int getBottomMargin(List<Double> horizontalProfile, boolean isDoublePage) {
        double bestValue;
        int bottomMargin = horizontalProfile.size() - 1;
        bestValue = 0;
        int checkFrom = horizontalProfile.size() / 2;
        if (isDoublePage) {
            checkFrom = horizontalProfile.size() * 2 / 3;
        }
        for (int i = checkFrom; i < horizontalProfile.size(); i++) {
            if (horizontalProfile.get(i) > bestValue) {
                bestValue = horizontalProfile.get(i);
                bottomMargin = i;
            }
        }
        return bottomMargin;
    }

    private static int getTopMargin(List<Double> horizontalProfile, int to) {
        int topMargin = 0;
        double bestValue = 0;
        for (int i = 0; i < to; i++) {
            if (horizontalProfile.get(i) >= bestValue) {
                bestValue = horizontalProfile.get(i);
                topMargin = i;
            }
        }
        return topMargin;
    }

    public static int getTopMargin(List<Double> horizontalProfile, boolean isDoublepage) {
        int topMargin = 0;
        double bestValue = 0;
        int checkUpto = horizontalProfile.size() / 2;
        if (isDoublepage) {
            checkUpto = horizontalProfile.size() / 3;
        }
        for (int i = 0; i < checkUpto; i++) {
            if (horizontalProfile.get(i) >= bestValue) {
                bestValue = horizontalProfile.get(i);
                topMargin = i;
            }
        }
        return topMargin;
    }

    private static ArrayList<org.opencv.core.Point> droplet(Mat binaryImage, int xStart, int xStop, int yStart, int upperboundary, int lowerboundary) {

        int x = xStart;
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);

        Droplet droplet = new Droplet(data, width, xStart, xStop, yStart, upperboundary, lowerboundary);

        while (x < xStop) {
            x = droplet.getX();
            droplet.move();
        }
        return droplet.getPointList();
    }

    private static List<Integer> getInkThicknessList(Mat binaryImage, int startY, int stopY, int startX, int stopX) {
        long size = binaryImage.total() * binaryImage.channels();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> thicknessList = new ArrayList<>();
        for (int thickness = 0; thickness < 50; thickness++) {
            thicknessList.add(0);
        }
        for (int y = startY; y < stopY; y++) {
            int thickness = 0;
            boolean lastPixelOn = false;
            for (int x = startX; x < stopX; x++) {
                if (data[y * binaryImage.width() + x] != 0) {
                    if (lastPixelOn) {
                        thickness++;
                    }
                    lastPixelOn = true;
                } else {
                    if (thickness > 0 && thickness < 50) {
                        thicknessList.set(thickness, thicknessList.get(thickness) + 1);
                    }
                    lastPixelOn = false;
                    thickness = 0;
                }
            }
        }
        return thicknessList;
    }

    public static int getInkThickness(Mat despeckledImage, int startY, int stopY, int startX, int stopX) {
        List<Integer> thicknessList = getInkThicknessList(despeckledImage, startY, stopY, startX, stopX);
        int bestThickness = 0;
        int bestThicknessCount = 0;
        for (int thickness = 0; thickness < 50; thickness++) {
            int thicknessCount = thicknessList.get(thickness);
            if (thicknessCount > bestThicknessCount) {
                bestThickness = thickness;
                bestThicknessCount = thicknessCount;
            }
        }
        LOG.error("best thickness: " + bestThickness);
        return bestThickness;
    }

    public static int getXHeight(java.util.List<ConnectedComponent> cocos, int target) {
        ArrayList<Integer> widthList = new ArrayList<>();
        ArrayList<Integer> heightList = new ArrayList<>();
        ArrayList<Integer> mixedList = new ArrayList<>();
        for (int i = 0; i < 250; i++) {
            widthList.add(0);
            heightList.add(0);
            mixedList.add(0);
        }
        for (ConnectedComponent coco : cocos) {
            int width = coco.getBitMap().getWidth();
            int height = coco.getBitMap().getHeight();
            int mixed = (int) Math.sqrt(width * height);
            if (width < 250) {
                widthList.set(width, widthList.get(width) + 1);
            }
            if (height < 250) {
                heightList.set(height, heightList.get(height) + 1);
            }
            if (mixed < 250) {
                mixedList.set(mixed, mixedList.get(mixed) + 1);
            }
        }
        if (_outputDebug) {
            LOG.error("    Width");
            for (int i = 0; i < 250; i++) {
                LOG.error(String.format(" %d %d", i, widthList.get(i)));
            }
            LOG.error("    Height");
        }
        int xHeight = 0;
        int bestScore = 0;
        for (int i = (int) (target * 0.8); i < 250; i++) {
            int heightCount = heightList.get(i);
            if (heightCount > bestScore) {
                bestScore = heightCount;
                xHeight = i;
            }
            if (_outputDebug) {
                LOG.error(String.format(" %d %d", i, heightList.get(i)));
            }
        }
        if (_outputDebug) {
            LOG.debug("xheight   " + xHeight);
            LOG.debug("    Mixed");
            for (int i = 0; i < 250; i++) {
                LOG.debug(String.format(" %s   %s%n", i, mixedList.get(i)));
            }
        }

        return xHeight;
    }

    public static boolean isRotated(Mat image) {
        //TODO detect Orientation
//        Mat scaled = new Mat();
//        Mat dst = new Mat();
//
//        Size size = new Size(1000, 1000);
//        Imgproc.resize(image, scaled, size);
//        DocumentPage documentPage = new DocumentPage(scaled);
//
//        Core.transpose(scaled, dst);
//        Core.flip(dst, dst, 0);
//        long binaryImageSquared = getSumSquaredProfile(scaled);
//        long rotateImageSquared = getSumSquaredProfile(dst);
//        Highgui.imwrite("/scratch/images/rotate-90.png",dst);
//        if (rotateImageSquared> binaryImageSquared){
//            return true;
//        }
        return false;
    }

    private static Point closestPoint(List<Point> points, Point seed) {
        Point closest = null;
        double distance = Double.MAX_VALUE;
        for (Point point : points) {
            if (closest == null) {
                closest = point;
                continue;
            }
            double currentDistance = StringConverter.distance(seed, point);
            if (currentDistance < distance) {
                closest = point;
                distance = currentDistance;

            }
        }
        return closest;
    }

    public static double interlineMedian(List<TextLine> textLines, double minValue) {
        double interlineDistance = interlineMedian(textLines);
        return Math.max(interlineDistance, minValue);
    }

    public static double interlineMedian(List<TextLine> textLines) {
        ArrayList<Double> distances = new ArrayList<>();
        for (TextLine textLine : textLines) {
            List<Point> allPoints = getAllPoints(textLines);
            ArrayList<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
            allPoints.removeAll(points);
            for (Point point : points) {
                Point closestPoint = closestPoint(allPoints, point);
                if (closestPoint == null) {
                    continue;
                }
                double distance = distance(closestPoint, point);
                distances.add(distance);
            }
        }
        Statistics statistics = new Statistics(distances);
        return statistics.median();
    }

    private static List<Point> getAllPoints(List<TextLine> textLines) {
        ArrayList<Point> allPoints = new ArrayList<>();
        for (TextLine textLine : textLines) {
            ArrayList<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
            allPoints.addAll(points);
        }
        return allPoints;
    }

    public static void fixPoints(List<Point> points, int maxX, int maxY) {
        for (Point point : points) {
            fixPoint(point, maxX, maxY);
        }
    }


//    private static void extractTextLines(Mat binaryImage, DocumentPage documentPage, ArrayList<DocumentTextBlock> textBlocks) throws Exception {
//        Tess4JTest tess4JTest = new Tess4JTest();
//        Mat horizontalTextProfiles = binaryImage.clone();
//        LayoutConfiguration configuration = new LayoutConfiguration(documentPage.getImage());
//
//        int counter = 0;
//        try {
//            if (documentPage.isMachinePrint() && configuration.doTesseract()) {
//                String fullText = tess4JTest.doOCR4(ImageConversionHelper.matToBufferedImage(documentPage.getImage()));
//                documentPage.setFullText(fullText);
////                String fullTextSparse = tess4JTest.doOCR4sparse(ImageConversionHelper.matToBufferedImage(documentPage.getBinaryImage()));
////                documentPage.setFullTextSparse(fullTextSparse);
//            }
//        } catch (TesseractException e) {
//            e.printStackTrace();
//        }
//        for (DocumentTextBlock textBlock : textBlocks) {
//            if (documentPage.isMachinePrint() && configuration.doTesseract()) {
//                try {
//                    BufferedImage subBufferedImage = ImageConversionHelper.matToBufferedImage(documentPage.getImage().submat(textBlock.getYStart(), textBlock.getYStart() + textBlock.getHeight(), textBlock.getXStart(), textBlock.getXStart() + textBlock.getWidth()));
//                    String documentTextBlockText = tess4JTest.doOCR4(subBufferedImage);
//                    textBlock.setText(documentTextBlockText);
//
//                    //tess4JTest.getWords(subBufferedImage);
//                } catch (TesseractException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            counter++;
//            ArrayList<DocumentTextLine> textLines = new ArrayList<>();
//            int inkThickness = LayoutProc.getInkThickness(textBlock.getBinaryImage(), 0, textBlock.getHeight() - 1, 0, textBlock.getWidth() - 1);
//            System.err.println("best guess ink thickness: " + inkThickness);
//
//            List<Integer> horizontalProfile = LayoutProc.horizontalProfileByte(textBlock.getBinaryImage(), 0, textBlock.getHeight(), 0, textBlock.getWidth(), inkThickness * 3);
//            List<Double> smoothedHorizontalProfile;
//            int smoothFactorHorizontalProfile;
//            if (documentPage.isMachinePrint()) {
//                smoothFactorHorizontalProfile = documentPage.getxHeight() / 2;
//            } else {
//                smoothFactorHorizontalProfile = binaryImage.width() / 200;
//            }
//            smoothedHorizontalProfile = smoothList(horizontalProfile, smoothFactorHorizontalProfile);
//
//            for (int i = 0; i < smoothedHorizontalProfile.size(); i++) {
//                Double d = smoothedHorizontalProfile.get(i);
//                for (int j = 0; j < d; j++) {
//                    horizontalTextProfiles.put(i + documentPage.getTopMargin(), textBlock.getXStart() + j, 127);
//                }
//            }
//
//            if (_outputDebug) {
//                Imgcodecs.imwrite(String.format("/scratch/images/horizontalTextProfile-%s.png", counter), horizontalTextProfiles);
//            }
//
//
//            horizontalTextProfiles.release();
//            double average = 0;
//            for (Double item : smoothedHorizontalProfile) {
//                average += item;
//            }
//            average /= smoothedHorizontalProfile.size();
//
//            int valleyStart = 0;
//            int peak = 0;
//            int valleyStop;
//
//            if (_outputDebug) {
//                if (documentPage.isMachinePrint()) {
//                    System.out.println("Looks like machine print...");
//                } else {
//                    System.out.println("Does not appear to be machine print, so not doing tess");
//                }
//            }
//            int margin = documentPage.getHeight() / 100;
//            if (documentPage.isMachinePrint()) {
//                margin /= 2;
//            }
//
//            ArrayList<Integer> linecrossings = lineCrossingsHorizontal(binaryImage, 0, documentPage.getHeight(), textBlock.getXStart(), textBlock.getXStart() + textBlock.getWidth());
//            List<Double> linecrossingsSmoothed = smoothList(linecrossings, 10);
//
////            Statistics lineCrossingStats = new Statistics(linecrossingsSmoothed, 0, linecrossingsSmoothed.size());
//
//            // TODO: incorporate ink crossings in line confidence detection
////            for (int i = 0; i < documentPage.getHeight(); i++) {
////                System.out.println("crossing:   "+ i + "  : "+ linecrossingsSmoothed.get(i));
////            }
////
////            System.out.println("mean " + lineCrossingStats .getMean());
////            System.out.println("median" + lineCrossingStats .median());
////            System.out.println("min " + lineCrossingStats .getMinimum());
////            System.out.println("max " + lineCrossingStats .getMaximum());
//
//            for (int i = 0; i < smoothedHorizontalProfile.size(); i++) {
//                double lowest = Double.MAX_VALUE;
//                double highest = 0;
//
//                int marginCount = 0;
//                // find low point, margin lower, since valleys can be close together
//                while (i < smoothedHorizontalProfile.size() && (smoothedHorizontalProfile.get(i) < lowest || marginCount < margin / 2)) {
//                    if (smoothedHorizontalProfile.get(i) < lowest) {
//                        lowest = smoothedHorizontalProfile.get(i);
//                        valleyStart = i;
//                        marginCount = 0;
//                    }
//                    marginCount++;
//                    i++;
//
//                }
//
//                i = valleyStart;
//                marginCount = 0;
//                // find peak, margin a bit higher since peaks are wide apart
//                while (i < smoothedHorizontalProfile.size() && (smoothedHorizontalProfile.get(i) >= highest || marginCount < margin * 2)) {
//                    if (smoothedHorizontalProfile.get(i) >= highest) {
//                        highest = smoothedHorizontalProfile.get(i);
//                        peak = i;
//                        lowest = Double.MAX_VALUE;
//                        marginCount = 0;
//                    }
//                    i++;
//                    marginCount++;
//                }
//
//                i = peak;
//                marginCount = 0;
//                // find lowest again
//                while (i < smoothedHorizontalProfile.size() && (smoothedHorizontalProfile.get(i) < lowest || marginCount < margin / 2)) {
//                    if (smoothedHorizontalProfile.get(i) < lowest) {
//                        lowest = smoothedHorizontalProfile.get(i);
//                        marginCount = 0;
//                    }
//                    marginCount++;
//                    i++;
//                }
//
//                int minThreshold = (int) average;
//                if (minThreshold < inkThickness * 3) {
//                    minThreshold = inkThickness * 3;
//                }
//                if (lowest < 10) {//very clean document
//                    minThreshold /= 3;
//                }
//                if (highest > minThreshold) {
//                    valleyStop = i;
//                    valleyStart += documentPage.getTopMargin();
//                    if (textLines.size() > 0) {
//                        valleyStart = (valleyStart + textLines.get(textLines.size() - 1).getLineCenter()) / 2;
//                    }
//                    peak += documentPage.getTopMargin();
//                    valleyStop += documentPage.getTopMargin();
//                    Mat textLineBinaryImage = binaryImage.submat(valleyStart, valleyStop, textBlock.getXStart(), textBlock.getXStart() + textBlock.getWidth()).clone();
//                    Mat textLineImage = documentPage.getImage().submat(valleyStart, valleyStop, textBlock.getXStart(), textBlock.getXStart() + textBlock.getWidth()).clone();
//                    String text = "";
//                    if (documentPage.isMachinePrint() && configuration.doTesseract()) {
//                        try {
//                            text = tess4JTest.doOCRLine(ImageConversionHelper.matToBufferedImage(documentPage.getImage().submat(valleyStart, valleyStop, textBlock.getXStart(), textBlock.getXStart() + textBlock.getWidth())));
//                        } catch (TesseractException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    DocumentTextLine textLine = new DocumentTextLine(valleyStart, textBlock.getXStart(), textLineBinaryImage.height(), textLineBinaryImage.width(), peak, text);
//                    textLines.add(textLine);
//
////                    ArrayList<Point> pointList = droplet(binaryImage, textLine.getStartX(), textLine.getStartX() + textLine.getWidth(), textLine.getStartY(), upperboundary,lowerboundary);
////                    textLine.setUpperPoints(pointList);
//
//                    if (_outputDebug) {
//                        System.out.println("local maximum: " + i);
//                    }
//
//                    if (configuration.getOutputTextLineImages()) {
//                        Imgcodecs.imwrite("/scratch/images/textlines/textLineImage-" + textBlock.getXStart() + "-" + peak + ".png", textLineBinaryImage);
//                        System.err.println("writing " + "/scratch/images/textlines/textLineImage-" + textBlock.getXStart() + "-" + peak + ".png");
//                    }
//
//                }
//            }
//
//            doDroplets(binaryImage, textBlock, textLines);
//            if (textLines.size() > 0) {
//                if (textBlock.getDocumentParagraphs().size() == 0) {
//                    textBlock.getDocumentParagraphs().add(new DocumentParagraph());
//                }
//                textBlock.getDocumentParagraphs().get(0).setDocumentTextLines(textLines);
//            }
//            getTextBlockLineStats(textBlock);
//        }
//    }

    private static Point fixPoint(Point point, int maxX, int maxY) {
        if (point.x < 0) {
            LOG.error("point x coordinate smaller zero. Setting to zero");
            point.x = 0;
        }
        if (point.y < 0) {
            LOG.error("point y coordinate smaller zero. Setting to zero");
            point.y = 0;
        }
        if (point.x > maxX) {
            LOG.error("point x coordinate larger than width. Setting to max");
            point.x = maxX;
        }
        if (point.y > maxY) {
            LOG.error("point x coordinate larger than height. Setting to max");
            point.y = maxY;
        }
        return point;
    }

    public static void recalculateTextLinesFromBaselines(PcGts page) {
//        Mat baselineImage = Mat.zeros(image.size(), CV_8U);
//        Mat grayImage = new Mat();
//        Mat binaryImage = new Mat();
//        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, LayoutProc.getBestThreshold(grayImage));//15);
        List<TextLine> textlines = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            textlines.addAll(textRegion.getTextLines());
        }
        double interlineDistance = interlineMedian(textlines);

        for (TextLine textLine : textlines) {
            ArrayList<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
            List<Point> allPoints = getAllPoints(textlines);
            allPoints.removeAll(points);
            Point previousPoint = null;
            ArrayList<Point> textLinePoints = new ArrayList<>();
            ArrayList<Point> bottomPoints = new ArrayList<>();
            for (Point point : points) {
                Point closestPoint = closestPoint(allPoints, point);
                if (closestPoint == null) {
                    continue;
                }
//                Imgproc.line(image, closestPoint, point, new Scalar(0, 255, 0), 10);
                if (previousPoint == null) {
                    previousPoint = point;
                    textLinePoints.add(fixPoint(new Point(point.x - interlineDistance / 2, point.y), page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                    textLinePoints.add(fixPoint(new Point(point.x, point.y - interlineDistance * 0.9), page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                    bottomPoints.add(fixPoint(new Point(previousPoint.x, previousPoint.y + interlineDistance / 4), page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                    continue;
                }
//                Imgproc.line(image, previousPoint, point, new Scalar(255, 0, 0), 10);
//                Imgproc.line(image, new Point(previousPoint.x, previousPoint.y - interlineDistance / 2), new Point(point.x, point.y - interlineDistance / 2), new Scalar(0, 0, 255), 5);
//                Imgproc.line(image, new Point(previousPoint.x, previousPoint.y + interlineDistance / 4), new Point(point.x, point.y + interlineDistance / 4), new Scalar(0, 255, 255), 5);
                textLinePoints.add(fixPoint(new Point(point.x, point.y - interlineDistance * 0.9), page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                bottomPoints.add(fixPoint(new Point(point.x, point.y + interlineDistance / 4), page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                previousPoint = point;
            }
            textLinePoints.addAll(Lists.reverse(bottomPoints));
            textLine.getCoords().setPoints(StringConverter.pointToString(textLinePoints));
        }
//        Imgcodecs.imwrite("/tmp/baselines.png", image);
//        Imgcodecs.imwrite("/tmp/binary.png", binaryImage);
    }

    private static Statistics getTextBlockLineStats(DocumentTextBlock textBlock) {
        DocumentTextLine lastLine = null;
        ArrayList<Double> diffs = new ArrayList<>();
        ArrayList<Double> heights = new ArrayList<>();
        for (DocumentTextLine textLine : textBlock.getDocumentParagraphs().get(0).getDocumentTextLines()) {
            heights.add((double) textLine.getHeight());
            if (lastLine != null) {
                Double diff = (double) textLine.getLineCenter() - lastLine.getLineCenter();
                diffs.add(diff);
                LOG.info("distance between lines: " + diff);
            }
            lastLine = textLine;
        }
        Statistics stats = null;
        // if distance between lines increases, it might be a paragaph border
        if (diffs.size() > 0) {
            stats = new Statistics(diffs, 0, diffs.size());
            LOG.info("mean " + stats.getMean());
            LOG.info("median" + stats.median());
            LOG.info("min " + stats.getMinimum());
            LOG.info("max " + stats.getMaximum());
        }

        if (heights.size() > 0) {
            stats = new Statistics(heights, 0, heights.size());
            LOG.info("heights mean " + stats.getMean());
            LOG.info("heights median" + stats.median());
            LOG.info("heights min " + stats.getMinimum());
            LOG.info("heights max " + stats.getMaximum());
        }

        return stats;
    }

    private static void doDroplets(Mat binaryImage, DocumentTextBlock textBlock, ArrayList<DocumentTextLine> documentTextLines) {
        for (int i = 0; i < documentTextLines.size(); i++) {

            DocumentTextLine documentTextLine = documentTextLines.get(i);
            int upperboundary;
            if (i == 0) {
                upperboundary = textBlock.getYStart();
            } else {
                DocumentTextLine previousLine = documentTextLines.get(i - 1);
                upperboundary = previousLine.getLineCenter();
            }

            ArrayList<org.opencv.core.Point> pointList = droplet(binaryImage,
                    documentTextLine.getXStart(),
                    documentTextLine.getXStart() + documentTextLine.getWidth(),
                    documentTextLine.getYStart(),
                    upperboundary,
                    documentTextLine.getLineCenter());
            documentTextLine.setUpperPoints(pointList);

        }
    }

    private static long getSumSquaredProfile(Mat testMat) {
        List<Integer> horizontalProfile = LayoutProc.horizontalProfileByte(testMat);
        long sum = 0;
        for (Integer aHorizontalProfile : horizontalProfile) sum += Math.pow(aHorizontalProfile, 2);
        return sum;
    }

    private static Mat getGaborImageVertical(Mat grayImage) {
        Mat resultImage = Mat.zeros(grayImage.rows(), grayImage.cols(), CV_8UC1);

        double sigma = 1;
        double lambd = 8;
        double gamma = 0.02;
        Size size = new Size(33, 33);
        Mat kernel = Imgproc.getGaborKernel(size, sigma, 0, lambd, gamma);//, 3.1415/4,CV_32F);
        Imgproc.filter2D(grayImage, resultImage, CV_8UC1, kernel);
        Imgproc.threshold(resultImage, resultImage, 127, 255, Imgproc.THRESH_OTSU);
        kernel.release();
        return resultImage;
    }

    private static ArrayList<Double> getVerticalNoiseProfile(DocumentPage documentPage) {
        double sigma = 1;
        double lambd = 8;
        double gamma = 0.02;
        Size size = new Size(15, 15);

        Mat kernel = Imgproc.getGaborKernel(size, sigma, 0, lambd, gamma);//, 3.1415/4,CV_32F);
        Mat tmpImage = new Mat();

        org.opencv.core.Point anchor = new org.opencv.core.Point(kernel.cols() - kernel.cols() / 2.0 - 1, kernel.rows() - kernel.rows() / 2.0 - 1);
//        int borderMode = BORDER_CONSTANT;
//        cv::flip(kernel, kernel, -1);
//        cv::filter2D(source, dest, img.depth(), kernel , anchor, 0, borderMode);

        Imgproc.filter2D(documentPage.getBinaryImage(), tmpImage, CV_8UC1, kernel, anchor, 0);

        List<Integer> verticalProfile0 = LayoutProc.verticalProfile(tmpImage, 0, tmpImage.height());
        List<Double> verticalProfileSmooth0 = LayoutProc.smoothList(verticalProfile0, tmpImage.width() / 50);
        tmpImage.release();

        kernel = Imgproc.getGaborKernel(size, sigma, 90, lambd, gamma);//, 3.1415/4,CV_32F);
        tmpImage = new Mat();

        Imgproc.filter2D(documentPage.getBinaryImage(), tmpImage, CV_8UC1, kernel, anchor, 0);
        kernel.release();

        List<Integer> verticalProfile90 = LayoutProc.verticalProfile(tmpImage, 0, tmpImage.height());
        List<Double> verticalProfileSmooth90 = LayoutProc.smoothList(verticalProfile90, tmpImage.width() / 50);


        Mat cannyImage = new Mat();
        Imgproc.Canny(documentPage.getGrayImage(), cannyImage, 300, 600, 5, true);
        List<Integer> cannyProfileVertical = LayoutProc.verticalProfile(cannyImage, 0, cannyImage.height());

        List<Double> smoothCannyProfileVertical = LayoutProc.smoothList(cannyProfileVertical, tmpImage.width() / 50);


        ArrayList<Double> verticalProfileSmoothDivided = new ArrayList<>();
        for (int i = 0; i < verticalProfileSmooth90.size(); i++) {
            if (verticalProfileSmooth90.get(i) > 0) {
                verticalProfileSmoothDivided.add(smoothCannyProfileVertical.get(i) * verticalProfileSmooth0.get(i) / verticalProfileSmooth90.get(i));
            } else {
                verticalProfileSmoothDivided.add(0d);
            }
        }

        VisualizationHelper.drawVerticalInkProfileDouble(tmpImage, verticalProfileSmoothDivided);

        Imgcodecs.imwrite("/scratch/out-gaborresult-120div90.png", tmpImage);
        tmpImage.release();
        cannyImage.release();
        return verticalProfileSmoothDivided;
    }

    public static void getMargins(DocumentPage documentPage) {
        if (LayoutConfiguration.getGlobal().documentEdgeIsBorder()) { // if precut image
            documentPage.setLeftMargin(0);
            documentPage.setRightMargin(documentPage.getWidth() - 1);
            documentPage.setTopMargin(0);
            documentPage.setBottomMargin(documentPage.getHeight() - 1);
            return;
        }

        Mat binaryOtsu = documentPage.getBinaryOtsu();

//        Imgproc.dilate(binaryOtsu, binaryOtsu, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
//        Imgproc.dilate(binaryOtsu, binaryOtsu, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
//        Imgproc.dilate(binaryOtsu, binaryOtsu, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));

        List<Integer> otsuProfileVertical = documentPage.getOtsuProfileVertical();
        List<Integer> otsuProfileHorizontal = documentPage.getOtsuProfileHorizontal();
        ArrayList<Double> verticalNoiseProfile = getVerticalNoiseProfile(documentPage);
        Statistics stats = new Statistics(verticalNoiseProfile, 0, verticalNoiseProfile.size());
        List<Double> otsuProfileVerticalSmoothed = LayoutProc.smoothList(otsuProfileVertical, otsuProfileVertical.size() / 50);
        VisualizationHelper.drawVerticalInkProfileDouble(binaryOtsu, otsuProfileVerticalSmoothed);
        Statistics otsuStats = new Statistics(otsuProfileVerticalSmoothed, 0, otsuProfileVerticalSmoothed.size());

        if (_outputDebug) {
            LOG.debug("writing /scratch/images/out-binaryOtsu.png");
            Imgcodecs.imwrite("/scratch/images/out-binaryOtsu.png", binaryOtsu);
        }
        Mat cannyImage = new Mat();
        Imgproc.Canny(documentPage.getGrayImage(), cannyImage, 300, 600, 5, true);
        List<Integer> cannyProfileVertical = LayoutProc.verticalProfile(cannyImage, 0, cannyImage.height());
        List<Integer> cannyProfileHorizontal = LayoutProc.horizontalProfileByte(cannyImage, 0, cannyImage.height());

        List<Double> smoothCannyProfileVertical = LayoutProc.smoothList(cannyProfileVertical, 3);


        Mat binaryImage = documentPage.getBinaryImage();
        // find margins based on projection profiles of the ink
        Mat horizontalImage = horizontalRunlengthByte(binaryImage, 255);
//        Mat verticalImage = verticalRunlengthInt(binaryImage, 255);
//        ArrayList<Integer> horizontalProfile = LayoutProc.horizontalProfileByte(horizontalImage, 0, binaryImage.height(), 0, binaryImage.width(), 0);
        List<Integer> horizontalProfile = LayoutProc.horizontalProfileByte(horizontalImage, 0, binaryImage.height());
        horizontalImage.release();
        List<Integer> verticalProfile = LayoutProc.verticalProfile(binaryImage, 0, binaryImage.height());

        List<Double> smoothedHorizontalProfile = smoothList(horizontalProfile, LayoutConfiguration.getGlobal().getSmoothFactor());
        List<Double> smoothedVerticalProfile = smoothList(verticalProfile, LayoutConfiguration.getGlobal().getSmoothFactor());

        for (int i = 0; i < smoothedVerticalProfile.size() - 1; i++) {
            if (smoothedVerticalProfile.get(i) >= 1) {
                smoothCannyProfileVertical.set(i, smoothCannyProfileVertical.get(i) / smoothedVerticalProfile.get(i));
            }
        }
//        smoothCannyProfileVertical = LayoutProc.smoothListDouble(smoothCannyProfileVertical,13);
        VisualizationHelper.drawVerticalInkProfile(binaryImage, smoothCannyProfileVertical);
        if (_outputDebug) {
            LOG.debug("writing /scratch/images/out-marginImageTest.png");
            Imgcodecs.imwrite("/scratch/images/out-marginImageTest.png", binaryImage);
        }


        // top margin
        int topMargin;
        int i = 0;
        int minProfile = Integer.MAX_VALUE;
//        if (!document.topStartsWithPaper()) {
        while (i < otsuProfileHorizontal.size() && (otsuProfileHorizontal.get(i) <= minProfile || otsuProfileHorizontal.get(i) > documentPage.getHeight() / 2)) {
            minProfile = otsuProfileHorizontal.get(i);
            i++;
        }
        if (i < cannyProfileHorizontal.size()) {
            minProfile = cannyProfileHorizontal.get(i);
        }
        while (i < cannyProfileHorizontal.size() && cannyProfileHorizontal.get(i) >= minProfile) {
            minProfile = cannyProfileHorizontal.get(i);
            i++;
        }

        while (i < cannyProfileHorizontal.size() && cannyProfileHorizontal.get(i) < minProfile) {
            minProfile = cannyProfileHorizontal.get(i);
            i++;
        }
//        }
        topMargin = i;
//        if (document.topStartsWithPaper()) {
//            topMargin = LayoutProc.getTopMargin(smoothedHorizontalProfile, false);
//        }
        if (topMargin > documentPage.getHeight() / 2) {
            topMargin = LayoutProc.getTopMargin(smoothedHorizontalProfile, (int) (0.2 * documentPage.getHeight()));
        }


        // bottom margin
        i = otsuProfileHorizontal.size() - 1;
        minProfile = Integer.MAX_VALUE;
        boolean bottomStartsWithPaper = otsuProfileHorizontal.get(otsuProfileHorizontal.size() - 1) < documentPage.getWidth() / 2;

        if (!bottomStartsWithPaper) {
            // remove dark border
            while (i > 0 && (otsuProfileHorizontal.get(i) <= minProfile || otsuProfileHorizontal.get(i) > documentPage.getWidth() / 2)) {
                minProfile = otsuProfileHorizontal.get(i);
                i--;
            }
            if (i > 0) {
                minProfile = cannyProfileHorizontal.get(i);
            }
            while (i > 0 && cannyProfileHorizontal.get(i) >= minProfile) {
                minProfile = cannyProfileHorizontal.get(i);
                i--;
            }

            while (i > 0 && cannyProfileHorizontal.get(i) < minProfile) {
                minProfile = cannyProfileHorizontal.get(i);
                i--;
            }
        }
        int bottomMargin = i;
//            bottomMargin = LayoutProc.getBottomMargin(smoothedHorizontalProfile, (int) (0.9 * document.getHeight()));

        if (bottomStartsWithPaper) {
            bottomMargin = LayoutProc.getBottomMargin(smoothedHorizontalProfile, (int) (0.9 * documentPage.getHeight()));
        }
        if (bottomMargin < documentPage.getHeight() / 2) {
            bottomMargin = LayoutProc.getBottomMargin(smoothedHorizontalProfile, (int) (0.8 * documentPage.getHeight()));
        }


        // todo: we need different analysis techniques for different document scan types
        // eg: high quality scans including dark background vs low-res binary scans of pages where more or less of the borders are cropped

        // left margin
        ArrayList<Double> chancePaper = new ArrayList<>();
//        ArrayList<Double> chanceLeft = new ArrayList<>();
//        ArrayList<Double> chanceRight = new ArrayList<>();
        double maxVertical = 1;
        double maxVerticalLeft = 1;
        double maxVerticalRight = 1;
        for (i = 0; i < cannyProfileVertical.size(); i++) {
            if (cannyProfileVertical.get(i) > maxVertical) {
                maxVertical = cannyProfileVertical.get(i);
            }
            if (i < cannyProfileVertical.size() / 2 && cannyProfileVertical.get(i) > maxVerticalLeft) {
                maxVerticalLeft = cannyProfileVertical.get(i);
            }
            if (i >= cannyProfileVertical.size() / 2 && cannyProfileVertical.get(i) > maxVerticalRight) {
                maxVerticalRight = cannyProfileVertical.get(i);
            }
        }
        for (i = 0; i < cannyProfileVertical.size(); i++) {
            if (i < cannyProfileVertical.size() / 2) {
                chancePaper.add((binaryImage.height() * cannyProfileVertical.get(i).doubleValue()) / maxVerticalLeft);
            } else {
                chancePaper.add((binaryImage.height() * cannyProfileVertical.get(i).doubleValue()) / maxVerticalRight);
            }
        }

//        for (i =0; i<cannyProfileVertical.size();i++){
//            if (i < cannyProfileVertical.size()/2){
//                chanceLeft.add(cannyProfileVertical.get(i).doubleValue()/maxVertical);
//            }else{
//                chanceLeft.add(cannyProfileVertical.get(i).doubleValue()/maxVertical);
//            }
//        }


        i = 0;
        minProfile = Integer.MAX_VALUE;
        boolean leftBorderRemoved = false;
        int leftMargin = i;
        if (documentPage.leftStartsWithPaper()) {
            //could this be a single page with borders already removed?
            for (int counter = 0; counter < cannyProfileHorizontal.size() / 10; counter++) {
                if (cannyProfileHorizontal.get(counter) == 0) {
                    leftBorderRemoved = true;
                    break;
                }
            }
        } else {
            while (i < otsuProfileVertical.size() && (otsuProfileVertical.get(i) <= minProfile || otsuProfileVertical.get(i) > documentPage.getHeight() / 2)) {
                minProfile = otsuProfileVertical.get(i);
                i++;
            }

            VisualizationHelper.drawVerticalInkProfileDoubleGray(cannyImage, chancePaper);
//            VisualizationHelper.drawVerticalInkProfile(cannyImage, cannyProfileVertical, smoothCannyProfileVertical);
//            VisualizationHelper.drawVerticalInkProfileInteger(cannyImage, cannyProfileVertical);
            if (_outputDebug) {
                LOG.debug("writing /scratch/images/out-cannyImage.png");
                Imgcodecs.imwrite("/scratch/images/out-cannyImage.png", cannyImage);
            }
            if (i < smoothCannyProfileVertical.size()) {
                minProfile = smoothCannyProfileVertical.get(i).intValue();
            }
            while (i < smoothCannyProfileVertical.size() && smoothCannyProfileVertical.get(i) >= minProfile) {
                minProfile = smoothCannyProfileVertical.get(i).intValue();
                i++;
            }

            while (i < smoothCannyProfileVertical.size() && smoothCannyProfileVertical.get(i) < minProfile) {
                minProfile = smoothCannyProfileVertical.get(i).intValue();
                i++;
            }
            leftMargin = i;
        }
        cannyImage.release();
        if (!leftBorderRemoved) {
            // we try to fix left margin if it is an open book with a lot of visible page edges
            int whitespace = i;
            while (i < smoothCannyProfileVertical.size() && verticalNoiseProfile.get(i) < stats.getMean() * 1.5) {
                i++;
            }
            whitespace = i - whitespace;
            int noise = i;
            while (i < smoothCannyProfileVertical.size() && verticalNoiseProfile.get(i) > stats.getMean() * 1.5 && otsuProfileVerticalSmoothed.get(i) > otsuStats.getMean()) {
                i++;
            }
            noise = i - noise;
            int maxMargin = documentPage.getWidth() / 3;
            if (documentPage.isDoublePage()) {
                maxMargin /= 2;
            }
            if (noise > documentPage.getWidth() / 50 && i <= maxMargin) {
                leftMargin = i;
            }
            if (documentPage.leftStartsWithPaper() || leftMargin > maxMargin) {
                leftMargin = LayoutProc.getTopMargin(smoothedVerticalProfile, (int) (0.2 * documentPage.getWidth()));
            }
            // end fixing
        }

        if (documentPage.isDoublePage()) {
            if (leftMargin > documentPage.getWidth() / 4) {
                leftMargin = documentPage.getWidth() / 4 - 1;
            }
        } else {
            if (leftMargin > documentPage.getWidth() / 2) {
                leftMargin = documentPage.getWidth() / 2 - 1;
            }
        }

        // right margin
        i = otsuProfileVertical.size() - 1;
        minProfile = Integer.MAX_VALUE;

        if (!documentPage.rightStartsWithPaper()) {
            // remove dark border
            while (i > 0 && (otsuProfileVertical.get(i) <= minProfile || otsuProfileVertical.get(i) > documentPage.getHeight() / 2)) {
                minProfile = otsuProfileVertical.get(i);
                i--;
            }
            if (i > 0) {
                minProfile = cannyProfileVertical.get(i);
            }
            while (i > 0 && cannyProfileVertical.get(i) >= minProfile) {
                minProfile = cannyProfileVertical.get(i);
                i--;
            }

            while (i > 0 && cannyProfileVertical.get(i) < minProfile) {
                minProfile = cannyProfileVertical.get(i);
                i--;
            }
        }

        int rightMargin = i;
        if (documentPage.rightStartsWithPaper()) {
            rightMargin = LayoutProc.getBottomMargin(smoothedVerticalProfile, (int) (0.9 * documentPage.getWidth()));
        }

        if (documentPage.isDoublePage()) {
            if (rightMargin < documentPage.getWidth() * 3 / 4) {
                rightMargin = documentPage.getWidth() * 3 / 4 + 1;
            }
        } else {
            if (rightMargin < documentPage.getWidth() / 2) {
                rightMargin = documentPage.getWidth() / 2 + 1;
            }
        }

        documentPage.setTopMargin(topMargin);
        documentPage.setBottomMargin(bottomMargin);
        documentPage.setLeftMargin(leftMargin);
        documentPage.setRightMargin(rightMargin);
    }

    static List<Double> smoothListDouble(List<Double> profile, int smoothFactor) {
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < profile.size(); i++) {
            int counter = 0;
            result.add(i, 0d);
            for (int j = -smoothFactor; j <= smoothFactor; j++) {
                if (i + j >= 0 && i + j < profile.size()) {
                    result.set(i, result.get(i) + profile.get(i + j));
                    counter++;
                }
            }
            result.set(i, result.get(i) / counter);
        }
        return result;
    }

    public static List<Double> smoothList(List<Integer> profile, int smoothFactor) {
        ArrayList<Double> result = new ArrayList<>();
        for (int i = 0; i < profile.size(); i++) {
            int counter = 0;
            result.add(i, 0d);
            for (int j = -smoothFactor; j <= smoothFactor; j++) {
                if (i + j >= 0 && i + j < profile.size()) {
                    result.set(i, result.get(i) + profile.get(i + j));
                    counter++;
                }
            }
            result.set(i, result.get(i) / counter);
        }
        return result;
    }

    private static boolean mergeCoCoColor(ConnectedComponent line, ConnectedComponent segment) {
        int startY = line.getY();
        int startX = line.getX();

        int stopY = line.getY() + line.getBitMap().getHeight();
        int stopX = line.getX() + line.getBitMap().getWidth();

        int segmentHeight = segment.getBitMap().getHeight();
        int segmentWidth = segment.getBitMap().getWidth();
        BufferedImage lineBitMap = line.getBitMap();
        BufferedImage segmentBitMap = segment.getBitMap();

        for (int i = startY; i < stopY; i++) {
            for (int j = startX; j < stopX; j++) {
                if (j - segment.getX() >= 0 && i - segment.getY() >= 0 &&
                        j - segment.getX() < segmentWidth && i - segment.getY() < segmentHeight &&
                        lineBitMap.getRGB(j - startX, i - startY) != 0 &&
                        segmentBitMap.getRGB(j - segment.getX(), i - segment.getY()) != 0) {
                    if (segment.getColor() == line.getColor()) {
                        return false;
                    } else {
                        segment.setColor(line.getColor());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void mergeCoCoColors(java.util.List<ConnectedComponent> lineCoCos, java.util.List<ConnectedComponent> segments, int minSize, int maxHeight) {
        //TODO what to do what cocos that are connected to two different line segments?
        for (ConnectedComponent lineCoco : lineCoCos) {
            int lineCoCoHeight = lineCoco.getBitMap().getHeight();
            if (lineCoCoHeight > maxHeight ||
                    lineCoCoHeight < minSize ||
                    lineCoco.getBitMap().getWidth() < minSize) {
                continue;
            }
            for (ConnectedComponent segment : segments) {
                if (segment.getBitMap().getHeight() > maxHeight ||
                        segment.getBitMap().getHeight() < minSize ||
                        segment.getBitMap().getWidth() < minSize) {
                    continue;
                }
                if (segment.getX() + segment.getBitMap().getWidth() < lineCoco.getX()) {
                    continue;
                }
                if (lineCoco.getX() + lineCoco.getBitMap().getWidth() < segment.getX()) {
                    continue;
                }
                if (segment.getY() + segment.getBitMap().getHeight() < lineCoco.getY()) {
                    continue;
                }
                if (lineCoco.getY() + lineCoco.getBitMap().getHeight() < segment.getY()) {
                    continue;
                }

                mergeCoCoColor(lineCoco, segment);
            }
        }

    }

//    public static List<DocumentTextBlock> getDocumentTextBlocks(LayoutConfiguration configuration, DocumentPage documentPage, List<Double> verticalProfileSmoothWhitespace,
//                                                                List<Double> verticalProfileSmooth, List<Double> smoothCannyProfileVertical,
//                                                                int leftMargin, int rightMargin, int start, int stop) throws Exception {
//        Mat binary = documentPage.getDespeckledImage();
//        ArrayList<DocumentTextBlock> textBlocks = new ArrayList<>();
//
//
//        Statistics whitespaceStats = new Statistics(verticalProfileSmoothWhitespace, start, stop);
//        double averageWhitespace = whitespaceStats.getMean();
//        Statistics stats = new Statistics(verticalProfileSmooth, start, stop);
//
//        double mean = stats.getMean();
//        double median = stats.median();
//        double lowerThreshold = mean + median;
//        lowerThreshold /= 2;
//
//        Statistics cannyStats = new Statistics(smoothCannyProfileVertical, start, stop);
//
////        if (median < lowerThreshold){
////            lowerThreshold = median;
////        }
//
////        Double minimum = stats.getMinimum();
//
//        // Move to the right
//        double confidence = 1;
//        for (int i = leftMargin; i < rightMargin; i++) {
//            // if you pass a threshold value
//            double localConfidence = getLocalConfidence(i, verticalProfileSmoothWhitespace,
//                    averageWhitespace, verticalProfileSmooth,
//                    stats, smoothCannyProfileVertical, cannyStats);
//
//            System.out.println("conf: " + i + ":" + localConfidence);
//            if (localConfidence > 0 ||
//                    verticalProfileSmooth.get(i) > lowerThreshold ||
//                    (verticalProfileSmoothWhitespace.get(i) < averageWhitespace && verticalProfileSmooth.get(i) > lowerThreshold / 2)) {
////if (localConfidence > 1.5){
//                int j = i;
//                double realXStartValue = Double.MAX_VALUE;
//                // find actual
//                while (j > documentPage.getLeftMargin() && verticalProfileSmooth.get(j) <= realXStartValue && verticalProfileSmooth.get(j) > 10) {
//                    realXStartValue = verticalProfileSmooth.get(j);
//                    j--;
//                }
//
//                confidence -= (i - j) / (double) documentPage.getWidth(); // if moving around a lot decrease confidence
//                // discard if too small and close to leftmargin
////                if (j - leftMargin < configuration.getTextBlockMinWidth()) {
////                    continue;
////                }
//
//                int xStart = j;
//                // compensate for smoothing
//                xStart -= new LayoutConfiguration(documentPage.getImage()).getSmoothFactor();
//                if (xStart < leftMargin) {
//                    xStart = leftMargin;
//                }
//
//                // while within textblock
//                while ((verticalProfileSmooth.get(i) > lowerThreshold
//                        || (verticalProfileSmoothWhitespace.get(i) < averageWhitespace && verticalProfileSmooth.get(i) > lowerThreshold / 2)
//                        || averageWhitespace < documentPage.getHeight() // pages that are completely filled have low average whitespace
//                )
//                        && i < verticalProfileSmooth.size() - 1
//                        && i < rightMargin) {
//                    if (verticalProfileSmooth.get(i) < lowerThreshold || verticalProfileSmoothWhitespace.get(i) > averageWhitespace) {
//                        confidence -= 1 / (double) documentPage.getWidth();
//                    }
//                    i++;
//                }
//
//
//                j = i;
//                double realXStopValue = Double.MAX_VALUE;
//                while (j > leftMargin &&
//                        j < verticalProfileSmooth.size() &&
//                        verticalProfileSmooth.get(j) <= realXStopValue &&
//                        j < rightMargin) {
//                    realXStopValue = verticalProfileSmooth.get(j);
//                    j++;
//                }
//
//                System.out.println(documentPage.getTopMargin());
//                System.out.println(documentPage.getBottomMargin());
//                System.out.println(xStart);
//                System.out.println(j);
//                Mat binarySubmat = binary.submat(documentPage.getTopMargin(), documentPage.getBottomMargin(), xStart, j).clone();
//                Mat submat = documentPage.getImage().submat(documentPage.getTopMargin(), documentPage.getBottomMargin(), xStart, j).clone();
//
//                if (binarySubmat.width() > configuration.getTextBlockMinWidth()) {
//                    String text = "";
//                    try {
////                        if (_outputDebug) {
////                            System.out.println("doing a tesseract call");
////                        }
////                        text = tess4JTest.doOCR(ImageConversionHelper.matToBufferedImage(documentPage.getBinaryImage().submat(documentPage.getTopMargin(), documentPage.getBottomMargin(), xStart, j)));
//                    } catch (Exception ex) {
//                        System.out.println(ex.getMessage());
//                    }
//
//
//                    DocumentTextBlock block = new DocumentTextBlock(documentPage.getTopMargin(), xStart, binarySubmat, submat, text);
//                    block.setConfidence(confidence);
//                    System.out.println("confidence: " + confidence);
//                    textBlocks.add(block);
//                } else {
//                    System.out.println("discarded tiny textBlock: " + binarySubmat.width());
//                }
//            }
//        }
////        documentPage.setTextBlocks(textBlocks);
//        LayoutProc.extractTextLines(documentPage.getBinaryImage(), documentPage, textBlocks);
//
//        ArrayList<DocumentTextBlock> filteredBlocks = new ArrayList<>();
//        for (DocumentTextBlock textBlock : textBlocks) {
//            if (textBlock.getDocumentParagraphs().get(0).getDocumentTextLines().size() > 0) {
//                filteredBlocks.add(textBlock);
//            }
//        }
////        documentPage.setTextBlocks(filteredBlocks);
//
//        if (_outputDebug) {
//            Imgcodecs.imwrite("/scratch/images/out-dropletimage.png", documentPage.getBinaryImage());
//        }
//        return filteredBlocks;
//    }

    public static double inkRatio(Mat binaryImage, int startY, int stopY, int startX, int stopX) {
        int inkCounter = 0;
        long size = binaryImage.total() * binaryImage.channels();
        int width = binaryImage.width();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        for (int y = startY; y < stopY; y++) {
            for (int x = startX; x < stopX; x++) {
                if ((data[y * width + x] & 0xFF) == 255) {
                    inkCounter++;
                }
            }
        }
        return (double) inkCounter / ((stopY - startY) * (stopX - startX));
    }

    private static double getLocalConfidence(int i, List<Double> verticalProfileSmoothWhitespace,
                                             double averageWhitespace, List<Double> verticalProfileSmooth,
                                             Statistics stats, List<Double> smoothCannyProfileVertical, Statistics cannyStats) {
        double localConfidence = 0;
        if (verticalProfileSmooth.get(i) > 0) {
            localConfidence += (verticalProfileSmooth.get(i) - stats.getMean()) / (verticalProfileSmooth.get(i) + stats.getMean());
        }
        if (verticalProfileSmoothWhitespace.get(i) > 0) {
            localConfidence -= (verticalProfileSmoothWhitespace.get(i) - averageWhitespace) / (verticalProfileSmoothWhitespace.get(i) + averageWhitespace);
            if (verticalProfileSmoothWhitespace.get(i) / averageWhitespace > 2) {
                localConfidence -= 1;
            }
        }

        if (smoothCannyProfileVertical.get(i) > 0) {
            localConfidence += (smoothCannyProfileVertical.get(i) - cannyStats.getMean()) / (smoothCannyProfileVertical.get(i) + cannyStats.getMean());
        }
        return localConfidence;
    }

    public static Mat rotate(Mat source, double angle) {
        org.opencv.core.Point center = new org.opencv.core.Point((source.cols() - 1) / 2.0, (source.rows() - 1) / 2.0);

        Mat rotationMatrix2D = Imgproc.getRotationMatrix2D(center, angle, 1.0);
        Rect bbox = new RotatedRect(center, source.size(), angle).boundingRect();

        Mat destination = Mat.zeros(bbox.height, bbox.width, CV_8UC1);

        double[] val = rotationMatrix2D.get(0, 2);
        rotationMatrix2D.put(0, 2, val[0] + destination.width() / 2.0 - source.cols() / 2.0);

        val = rotationMatrix2D.get(1, 2);
        rotationMatrix2D.put(1, 2, val[0] + destination.height() / 2.0 - source.rows() / 2.0);

        Imgproc.warpAffine(source, destination, rotationMatrix2D, bbox.size());

        rotationMatrix2D.release();

        return destination;
    }

    public static double getDeskewAngle(Mat image) {
        //TODO: calculate correct size for destination image
        double bestAngle = 0;
        long highestHorizontalScore = 0;
        for (double angle = -5.0; angle <= 5.0; angle += 0.1) {
            Mat testMat = rotate(image, angle);

            long sum = LayoutProc.getSumSquaredProfile(testMat);

            if (sum > highestHorizontalScore) {
                highestHorizontalScore = sum;
                bestAngle = angle;
            }
            testMat.release();
        }
        return bestAngle;
    }

    private static ArrayList<Integer> lineCrossingsHorizontal(Mat binaryImage, int startY, int stopY, int startX, int stopX) {
        long size = binaryImage.total() * binaryImage.channels();
        byte[] data = new byte[(int) size];
        binaryImage.get(0, 0, data);
        ArrayList<Integer> horizontals = new ArrayList<>();

        for (int y = startY; y < stopY; y++) {
            int crossings = 0;
            boolean lastPixelOn = false;
            for (int x = startX; x < stopX; x++) {
                if (data[y * binaryImage.width() + x] != 0) {
                    if (!lastPixelOn) {
                        crossings++;
                    }
                    lastPixelOn = true;
                } else {
                    lastPixelOn = false;
                }
            }
            horizontals.add(y, crossings);
        }
        return horizontals;
    }

    public static int getBestThreshold(Mat grayScaleImage) {
        return 15; // 15 is a good default threshold, works less well when bleedthrough or other noise is present
        // 10 works ok-ish on faded carbon-copies
    }

    public static Mat darkenImage(String uri) {
        Mat image = Imgcodecs.imread(uri);
        LayoutConfiguration configuration = new LayoutConfiguration(image);
        LayoutProc.setOutputDebug(false);
        configuration.setOutputFile(false);
        configuration.setOutputDebug(false);
        LayoutConfiguration.setGlobal(configuration);

        DocumentPage documentPage = new DocumentPage(image, uri);

        Mat mask = new Mat();
        Mat maskInverse = new Mat();
        Imgproc.adaptiveThreshold(documentPage.getGrayImage(), mask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 71, 10);//15);
        Core.bitwise_not(mask, maskInverse);

        Mat binary = new Mat();
        Imgproc.cvtColor(mask, binary, Imgproc.COLOR_GRAY2BGR);

        Mat background = new Mat();
        Core.bitwise_and(binary, binary, background, maskInverse);


        Mat foreground = new Mat();
        Core.bitwise_and(image, image, foreground, mask);
        Mat result = new Mat();
        Core.add(background, foreground, result);

        Core.addWeighted(result, 0.5, image, 0.5, 0, result, image.depth());
        image.release();
        mask.release();
        maskInverse.release();
        binary.release();
        background.release();
        foreground.release();
        return result;
    }

    public static Mat darkenImage(Mat image) {
        LayoutConfiguration configuration = new LayoutConfiguration(image);
        LayoutProc.setOutputDebug(false);
        configuration.setOutputFile(false);
        configuration.setOutputDebug(false);
        LayoutConfiguration.setGlobal(configuration);

        Mat mask = new Mat();
        Mat maskInverse = new Mat();
        Mat grayImage = new Mat();
        if (image.type() != CV_8U) {
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
        } else {
            image.copyTo(grayImage);
        }

        Imgproc.adaptiveThreshold(grayImage, mask, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 71, 10);//15);
        Core.bitwise_not(mask, maskInverse);

        Mat binary = new Mat();
        Imgproc.cvtColor(mask, binary, Imgproc.COLOR_GRAY2BGR);

        Mat background = new Mat();
        Core.bitwise_and(binary, binary, background, maskInverse);


        Mat foreground = new Mat();
        Core.bitwise_and(image, image, foreground, mask);
        Mat result = new Mat();
        Core.add(background, foreground, result);

        Core.addWeighted(result, 0.5, image, 0.5, 0, result, image.depth());
        mask.release();
        maskInverse.release();
        binary.release();
        background.release();
        foreground.release();
        return result;
    }

    public static double intersectOverUnion(Mat first, Mat second) {
        Mat intersect = new Mat();
        Mat union = new Mat();
        Core.bitwise_and(first, second, intersect);
        Core.bitwise_or(first, second, union);
        double countIntersect = Core.countNonZero(intersect);
        double countUnion = Core.countNonZero(union);
        intersect.release();
        union.release();
        return countIntersect / countUnion;
    }

    public static double intersectOverUnion(Rect first, Rect second) {
        return intersect(first, second) / union(first, second);
    }

    public static double union(Rect first, Rect second) {
        double result = first.area() + second.area() - intersect(first, second);
        return result;
    }

    public static double intersect(Rect first, Rect second) {

        if (first.x > second.x + second.width || second.x > first.x + first.width) {
            return 0;
        }
        if (first.y > second.y + second.height || second.y > first.y + first.height) {
            return 0;
        }
        int leftStart = Math.max(first.x, second.x);
        int topStart = Math.max(first.y, second.y);
        int rightStop = Math.min(first.x + first.width, second.x + second.width);
        int bottomStop = Math.min(first.y + first.height, second.y + second.height);

        double result = ((rightStop - leftStart) * (bottomStop - topStart));


        return result;
    }

    public static Rect getBoundingBox(List<Point> points) {
        int xStart = Integer.MAX_VALUE;
        int xStop = Integer.MIN_VALUE;
        int yStart = Integer.MAX_VALUE;
        int yStop = Integer.MIN_VALUE;

        for (Point point : points) {
            if (point.x < xStart) {
                xStart = (int) point.x;
            }
            if (point.x > xStop) {
                xStop = (int) point.x;
            }
            if (point.y < yStart) {
                yStart = (int) point.y;
            }
            if (point.y > yStop) {
                yStop = (int) point.y;
            }
        }

        return new Rect(xStart, yStart, xStop - xStart, yStop - yStart);
    }

    public static Rect getBoundingBoxTextLines(List<TextLine> textLines) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (TextLine textLine : textLines) {
            for (Point point : StringConverter.stringToPoint(textLine.getCoords().getPoints())) {
                if (point.x < minX) {
                    minX = (int) point.x;
                }
                if (point.x > maxX) {
                    maxX = (int) point.x;
                }
                if (point.y < minY) {
                    minY = (int) point.y;
                }
                if (point.y > maxY) {
                    maxY = (int) point.y;
                }
            }
        }
        Rect rect = new Rect(minX, minY, maxX - minX, maxY - minY);
        return rect;
    }

    public static void reorderRegions(PcGts page, List<String> regionOrderList) {
        List<TextRegion> finalTextRegions = new ArrayList<>();
        List<TextRegion> tmpRegionList = new ArrayList<>();
        OrderedGroup orderedGroup = new OrderedGroup();
        List<RegionRefIndexed> refList = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            tmpRegionList.add(textRegion);
        }

        int counter = 0;
        for (String regionTypeOrder : regionOrderList) {
//            LOG.debug("regionTypeOrder: " +regionTypeOrder);
            List<TextRegion> newSortedTextRegionsBatch = new ArrayList<>();
            List<TextRegion> unsortedTextRegions = new ArrayList<>();
            for (TextRegion textRegion : tmpRegionList) {
                //custom="structure {type:paragraph;}">
                String custom = textRegion.getCustom();
                if (custom !=null){
                    String[] splitted = custom.split(":");
                    if (splitted.length>1){
                        custom = splitted[1].split(";")[0].trim();
                    }
                }
                if ((custom != null && custom.equals(regionTypeOrder))
                        || (textRegion.getRegionType() != null && textRegion.getRegionType().equals(regionTypeOrder))
                        || regionTypeOrder == null) {
//                    LOG.debug("textRegion.getRegionType(): " + textRegion.getRegionType());
                    unsortedTextRegions.add(textRegion);
                }
            }
            tmpRegionList.removeAll(unsortedTextRegions);

            while (unsortedTextRegions.size() > 0) {
                TextRegion best = null;

                // select top left
                if (newSortedTextRegionsBatch.size() == 0) {
                    best = getTopLeftRegion(unsortedTextRegions, 0, 0, best);
                    unsortedTextRegions.remove(best);
                    newSortedTextRegionsBatch.add(best);
                    counter = addRegionRefIndex(refList, counter, best);
                } else {
                    TextRegion previousRegion = newSortedTextRegionsBatch.get(newSortedTextRegionsBatch.size() - 1);
                    Rect boundingBoxOld = getBoundingBox(StringConverter.stringToPoint(previousRegion.getCoords().getPoints()));

                    double bestDistance = Double.MAX_VALUE;
                    // find region that matches bottom left with top left
                    for (TextRegion textRegion : unsortedTextRegions) {
                        Rect boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getCoords().getPoints()));
                        double currentDistance =
                                StringConverter.distance(
                                        new Point(boundingBoxOld.x, boundingBoxOld.y + boundingBoxOld.height),
                                        new Point(boundingBox.x, boundingBox.y));
                        if (best == null ||
                                currentDistance < bestDistance
                        ) {
                            best = textRegion;
                            bestDistance = currentDistance;
                        }
                    }
                    // find region that matches bottom center with top center
                    if (previousRegion.getTextLines() != null && previousRegion.getTextLines().size() > 0) {
                        boundingBoxOld = getBoundingBox(StringConverter.stringToPoint(previousRegion.getTextLines().get(previousRegion.getTextLines().size() - 1).getCoords().getPoints()));
                    }

                    for (TextRegion textRegion : unsortedTextRegions) {
                        Rect boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getCoords().getPoints()));
                        if (textRegion.getTextLines() != null && textRegion.getTextLines().size() > 0) {
                            boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getTextLines().get(0).getCoords().getPoints()));
                        }
                        double currentDistance =
                                StringConverter.distance(
                                        new Point(boundingBoxOld.x + boundingBoxOld.width / 2, boundingBoxOld.y + boundingBoxOld.height),
                                        new Point(boundingBox.x + boundingBox.width / 2, boundingBox.y));
                        if (best == null ||
                                currentDistance < bestDistance
                        ) {
                            best = textRegion;
                            bestDistance = currentDistance;
                        }
                    }


                    unsortedTextRegions.remove(best);
                    newSortedTextRegionsBatch.add(best);
                    counter = addRegionRefIndex(refList, counter, best);
                }

            }
            finalTextRegions.addAll(newSortedTextRegionsBatch);
        }

        page.getPage().setTextRegions(finalTextRegions);
        orderedGroup.setRegionRefIndexedList(refList);
        if (refList.size() > 0) {
            ReadingOrder readingOrder = new ReadingOrder();
            readingOrder.setOrderedGroup(orderedGroup);
            page.getPage().setReadingOrder(readingOrder);
        }
    }

    private static int addRegionRefIndex(List<RegionRefIndexed> refList, int counter, TextRegion best) {
        RegionRefIndexed regionRefIndexed = new RegionRefIndexed();
        regionRefIndexed.setIndex(counter);
        regionRefIndexed.setRegionRef(best.getId());
        refList.add(regionRefIndexed);
        counter++;
        return counter;
    }
    private static TextRegion getTopLeftRegion(List<TextRegion> textRegions, int x, int x1, TextRegion best) {
        double bestDistance = Double.MAX_VALUE;
        for (TextRegion textRegion : textRegions) {
            Rect boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getCoords().getPoints()));
            double currentDistance =
                    StringConverter.distance(
                            new Point(x, x1),
                            new Point(boundingBox.x, boundingBox.y));
            if (best == null ||
                    currentDistance < bestDistance
            ) {
                best = textRegion;
                bestDistance = currentDistance;
            }
        }
        return best;
    }

    public static void reorderRegionsOld2(PcGts page) {
        List<TextRegion> newTextRegions = new ArrayList<>();
        List<TextRegion> textRegions = new ArrayList<>(page.getPage().getTextRegions());
//        ReadingOrder readingOrder = page.getPage().getReadingOrder();
        OrderedGroup orderedGroup = new OrderedGroup();
        List<RegionRefIndexed> refList = new ArrayList<>();
        int counter = 0;

        while (textRegions.size() > 0) {
            TextRegion best = null;
            double bestDistance = Double.MAX_VALUE;
            for (TextRegion textRegion : textRegions) {
                Rect boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getCoords().getPoints()));
                double currentDistance =
                        StringConverter.distance(
                                new Point(0, 0),
                                new Point(boundingBox.x + boundingBox.width, boundingBox.y + boundingBox.height));
                if (best == null ||
                        currentDistance < bestDistance
                ) {
                    best = textRegion;
                    bestDistance = currentDistance;
                }
            }
            textRegions.remove(best);
            newTextRegions.add(best);
            counter = addRegionRefIndex(refList, counter, best);
        }
        page.getPage().setTextRegions(newTextRegions);
        orderedGroup.setRegionRefIndexedList(refList);
        ReadingOrder readingOrder = new ReadingOrder();
        readingOrder.setOrderedGroup(orderedGroup);
        page.getPage().setReadingOrder(readingOrder);
    }

    public static void reorderRegionsOld(PcGts page) {
        List<TextRegion> newTextRegions = new ArrayList<>();
        List<TextRegion> textRegions = new ArrayList<>(page.getPage().getTextRegions());
//        ReadingOrder readingOrder = page.getPage().getReadingOrder();
        OrderedGroup orderedGroup = new OrderedGroup();
        List<RegionRefIndexed> refList = new ArrayList<>();
        int margin = 0;
        int counter = 0;

        while (textRegions.size() > 0) {
            TextRegion topLeft = null;
            int leftX = Integer.MAX_VALUE;
            int topY = Integer.MAX_VALUE;
            for (TextRegion textRegion : textRegions) {
                Rect boundingBox = getBoundingBox(StringConverter.stringToPoint(textRegion.getCoords().getPoints()));
                if (topLeft == null ||
                        boundingBox.x < (leftX)
//                        && boundingBox.y<= topY
                ) {
                    leftX = boundingBox.x;
                    topY = boundingBox.y;
                    topLeft = textRegion;
                }
            }
            textRegions.remove(topLeft);
            newTextRegions.add(topLeft);
            counter = addRegionRefIndex(refList, counter, topLeft);
        }
        page.getPage().setTextRegions(newTextRegions);
        orderedGroup.setRegionRefIndexedList(refList);
        ReadingOrder readingOrder = new ReadingOrder();
        readingOrder.setOrderedGroup(orderedGroup);
        page.getPage().setReadingOrder(readingOrder);
    }

    public static Mat calcSeamImage(Mat energyMat, double scaleDownFactor) {
        Mat energyMatTmp = new Mat();
        Imgproc.resize(energyMat, energyMatTmp, new Size(Math.ceil(energyMat.width() / scaleDownFactor), Math.ceil(energyMat.height() / scaleDownFactor)));
        Mat seamImage = new Mat();
        energyMatTmp.convertTo(seamImage, CV_64F);
        OpenCVWrapper.release(energyMatTmp);

        for (int j = 0; j < seamImage.width(); j++) {
            for (int i = 0; i < seamImage.height(); i++) {
                double lowest = Float.MAX_VALUE;
                for (int m = -1; m <= 1; m++) {
                    int y = i + m;
                    int x = j - 1;
                    if (y < 0) {
                        y = 0;
                    }
                    if (y >= seamImage.height()) {
                        y = seamImage.height() - 1;
                    }

                    if (x < 0) {
                        lowest = 0;
                    } else {
                        double value = seamImage.get(y, x)[0];
                        if (value < lowest) {
                            lowest = value;
                        }
                    }
                }
                double[] putter = new double[1];
                putter[0] = lowest + seamImage.get(i, j)[0];
                seamImage.put(i, j, putter);
            }
        }
        Imgproc.resize(seamImage, seamImage, new Size(energyMat.width(), energyMat.height()));
        return seamImage;
    }

    public static double getMeanAngle(List<Double> anglesDeg) {
        double x = 0.0;
        double y = 0.0;

        for (double angleD : anglesDeg) {
            double angleR = Math.toRadians(angleD);
            x += Math.cos(angleR);
            y += Math.sin(angleR);
        }
        double avgR = Math.atan2(y / anglesDeg.size(), x / anglesDeg.size());
        return Math.toDegrees(avgR);
    }

    public static double getMainAngle(List<Point> baseLinePoints) {
        Point lastPoint = null;
        List<Double> data = new ArrayList<>();
        for (Point point : baseLinePoints) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }
            double y = point.y - lastPoint.y;
            double x = point.x - lastPoint.x;
            double angle = Math.toDegrees(Math.atan2(y, x));
            double distance = distance(point, lastPoint);
            for (int i = 0; i < distance; i++) {
                data.add(angle);
            }
            lastPoint = point;
        }
        // This is to handle BIG CAPITALS at the start of a sentence
        //TODO: deal with rotated images
        if (data.size() > 200) {
            return getMeanAngle(data.subList(150, data.size() - 1));
//            statistics = new Statistics(data, 150, data.size() - 1);
//            return Math.toDegrees(statistics.getMean());
        }

        return getMeanAngle(data);
//        statistics = new Statistics(data);
//        double median = statistics.median();
////        double mean = statistics.getMean();
////
////        if (Math.abs(median)<Math.abs(mean)){
////            return mean;
////        }
//        return Math.toDegrees(median);
    }

    public static TextLine closestLineAbove(TextLine current, List<TextLine> textLines) {
        Rect currentRect = LayoutProc.getBoundingBox(StringConverter.stringToPoint(current.getBaseline().getPoints()));
        TextLine closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (TextLine textLine : textLines) {
            Rect otherRect = LayoutProc.getBoundingBox(StringConverter.stringToPoint(textLine.getBaseline().getPoints()));
            int distance = currentRect.y - otherRect.y;
            if (otherRect.x + otherRect.width > currentRect.x
                    && otherRect.x < currentRect.x + currentRect.width
                    && otherRect.y < currentRect.y
                    && distance < closestDistance) {
                closest = textLine;
                closestDistance = distance;
            }
        }
        return closest;
    }

    public static synchronized TextLine closestLineBelow(TextLine current, List<TextLine> textLines) {
        Rect currentRect = LayoutProc.getBoundingBox(StringConverter.stringToPoint(current.getBaseline().getPoints()));
        TextLine closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (TextLine textLine : textLines) {
            Rect otherRect = LayoutProc.getBoundingBox(StringConverter.stringToPoint(textLine.getBaseline().getPoints()));
            int distance = otherRect.y - currentRect.y;
            if (otherRect.x + otherRect.width > currentRect.x
                    && otherRect.x < currentRect.x + currentRect.width
                    && otherRect.y > currentRect.y
                    && distance < closestDistance) {
                closest = textLine;
                closestDistance = distance;
            }
        }
        return closest;
    }

    public static String convertToBoxFile(Mat binaryLineStrip, String text) {
        return convertToBoxFile(binaryLineStrip.height(), binaryLineStrip.width(), text);
    }

    public static String convertToBoxFile(int height, int width, String text) {
        int left = 0;
        int right = width;
        int top = height;
        int bottom = 0;
        StringWriter box = new StringWriter();
        for (int j = 0; j < text.length(); j++) {
            String character = text.substring(j, j + 1);
            box.append(String.format("%s %s %s %s %s 0%n", character, left, bottom, right, top));
        }
        box.append(String.format("\t %s %s %s %s 0%n", left, bottom, right, top));

        return box.toString();
    }

    private static Mat energyImage(Mat grayImage) {
        Mat combined2 = null;
        Mat grayImageInverted = null;
        Mat sobel1 = null;
        Mat sobel2 = null;
        Mat combined = null;
        Mat binary = null;
        Mat blurred = null;

        grayImageInverted = OpenCVWrapper.bitwise_not(grayImage);
        if (grayImageInverted.size().width == 0 || grayImageInverted.size().height == 0) {
            LOG.error("broken grayImageInverted");
            return null;
        }
        sobel1 = OpenCVWrapper.Sobel(grayImageInverted, -1, 1, 0, 3, 1, -15);
        sobel2 = OpenCVWrapper.Sobel(grayImageInverted, -1, 0, 1, 3, 1, -15);

        grayImageInverted = OpenCVWrapper.release(grayImageInverted);


        combined = OpenCVWrapper.addWeighted(sobel1, sobel2);
        if (combined.size().width == 0 || combined.size().height == 0) {
            LOG.error("broken combined");
            return null;
        }
        sobel1 = OpenCVWrapper.release(sobel1);
        sobel2 = OpenCVWrapper.release(sobel2);
        binary = OpenCVWrapper.adaptiveThreshold(grayImage, 21);

        combined2 = OpenCVWrapper.addWeighted(combined, binary);
        combined = OpenCVWrapper.release(combined);
        binary = OpenCVWrapper.release(binary);

        blurred = OpenCVWrapper.GaussianBlur(combined2);
        combined2 = OpenCVWrapper.release(combined2);
        return blurred;
    }

    private static void drawBaselines(List<TextLine> textlines, Mat image) {
        for (TextLine textLine : textlines) {
            ArrayList<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
            Point lastPoint = null;
            for (Point point : points) {
                if (point.x < 0) {
                    point.x = 0;
                }
                if (point.x >= image.width()) {
                    point.x = image.width() - 1;
                }
                if (point.y < 0) {
                    point.y = 0;
                }
                if (point.y >= image.height()) {
                    point.y = image.height() - 1;
                }
                if (lastPoint != null) {
                    OpenCVWrapper.line(image, lastPoint, point, new Scalar(Float.MAX_VALUE), 10);
                }
                lastPoint = point;
            }
        }
    }

    private static double getLocalInterlineDistance(double interlineDistance, Rect baselineRect, TextLine closestAbove) {
        double localInterlineDistance = interlineDistance;
        if (closestAbove != null) {
            Rect closestAboveRect = LayoutProc.getBoundingBox(StringConverter.stringToPoint(closestAbove.getBaseline().getPoints()));
            int tmpDistance = baselineRect.y - (closestAboveRect.y + closestAboveRect.height);
            if (tmpDistance > interlineDistance) {
                if (2 * interlineDistance > tmpDistance) {
                    localInterlineDistance = tmpDistance;
                } else {
                    localInterlineDistance = 2 * interlineDistance;
                }
            }
        } else {
            localInterlineDistance = 2 * interlineDistance;
        }
        return localInterlineDistance;
    }

    private static double getYStartTop(List<Point> baseLinePoints, double localInterlineDistance) {
        double min = Double.MAX_VALUE;
        for (Point point : baseLinePoints) {
            double current = point.y - localInterlineDistance;
            if (current < min) {
                min = current;
            }
        }
        if (min < 0) {
            min = 0;
        }
        return min;
    }

    private static double getYStartBottom(Mat blurred, double xHeight, List<Point> baseLinePoints) {
        double max = Double.MIN_VALUE;
        for (Point point : baseLinePoints) {
            double current = point.y + xHeight;
            if (current > max) {
                max = current;
            }
        }
        if (max >= blurred.height()) {
            max = blurred.height() - 1;
        }
        return max;
    }

    private static int getXStart(Mat blurred, int xMargin, List<Point> baseLinePoints) {
        int xStart = (int) (baseLinePoints.get(baseLinePoints.size() - 1).x) + xMargin;
        if (xStart >= blurred.width()) {
            xStart = blurred.width() - 1;
        }
        return xStart;
    }

    private static int getXStop(int xMargin, List<Point> baseLinePoints) {
        int xStop = (int) (baseLinePoints.get(0).x) - xMargin;
        if (xStop < 0) {
            xStop = 0;
        }
        return xStop;
    }

    //this function finds the path with the highest energy
    public static List<Point> findSeam(Mat seamImage) {
        List<Point> points = new ArrayList<>();
        int yStart = 0;
        int xStart = seamImage.width() - 1;
        double maxEnergy = -1;
        for (int i = 0; i < seamImage.rows(); i++) {
            double energy = seamImage.get(i, xStart)[0];
            if (energy > maxEnergy) {
                yStart = i;
                maxEnergy = energy;
            }
        }

        points.add(new Point(xStart, yStart));
        for (int j = xStart; j > 0; j--) {
            double first = seamImage.get(yStart - 1, j)[0];
            double middle = seamImage.get(yStart, j)[0];
            double last = seamImage.get(yStart + 1, j)[0];
            if (first > middle && first > last) {
                yStart--;
            } else if (last > middle) {
                yStart++;
            } else if (middle >= first && middle >= last) {

            } else {
                new Exception("error").printStackTrace();
            }
            if (yStart < 1) {
                yStart = 1;
            }
            if (yStart > seamImage.height() - 2) {
                yStart = seamImage.height() - 2;
            }
            points.add(new Point(j, yStart));
        }

        points = Lists.reverse(points);
        return points;
    }

    private static List<Point> findSeamOld(Mat seamImage, int xStart, int seamOffsetFromBaseline, int xStop, boolean preferDown,
                                           boolean preferUp, List<Point> baseLinePoints, int yOffset, int xHeight, int xOffset,
                                           int margin, int interLineDistance) {
        List<Point> baseLinePointsExpanded = StringConverter.expandPointList(baseLinePoints);
        for (Point point : baseLinePointsExpanded) {
            point.y = point.y - yOffset;
            point.x = (point.x - xOffset) - margin;
        }
        List<Point> points = new ArrayList<>();
        int yStart = (int) (baseLinePoints.get(baseLinePoints.size() - 1).y - yOffset) + seamOffsetFromBaseline;
        if (yStart >= seamImage.height()) {
            yStart = seamImage.height() - 1;
        }
        if (yStart < 0) {
            yStart = 0;
        }

        points.add(new Point(xStart, yStart));
        for (int j = xStart; j >= xStop; j--) {
            if (j >= seamImage.width()) {
                new Exception("writing outside image width5").printStackTrace();
            }
            if (j < 0) {
                new Exception("writing outside image width6").printStackTrace();
            }
            if (yStart < 1) {
                yStart = 1;
            }
            if (yStart > seamImage.height() - 2) {
                yStart = seamImage.height() - 2;
            }
            double first = seamImage.get(yStart - 1, j)[0];
            double middle = seamImage.get(yStart, j)[0];
            double last = seamImage.get(yStart + 1, j)[0];
            int flexMargin = 50;
            boolean outside = false;
//            if (preferDown || preferUp) {
//                for (Point baseLinePoint : baseLinePointsExpanded) {
//                    if ((int) (baseLinePoint.x) == j) {
//                        int currentBaselinePoint = (int) (baseLinePoint.y);
//                        int currentBottomTopBorder = currentBaselinePoint - xHeight;
//                        if (preferDown) {
//                            int targetLine = currentBottomTopBorder - yStart;
//                            if (targetLine < 0) {
//                                flexMargin = (int) Math.sqrt(Math.abs(targetLine));
////                                flexMargin = -(int) Math.sqrt(targetLine);
////                                flexMargin = 0;
//                            } else {
//                                flexMargin = -(int) Math.sqrt(targetLine);
////                                flexMargin = 0;
//                            }
//                            if (yStart >= currentBaselinePoint && yStart >= 2) {
////                                yStart--;
//                                outside = true;
//                            }
//                        }
//                        if (preferUp) {
//                            int targetLine = currentBaselinePoint - yStart + seamOffsetFromBaseline;
////                            int targetLine = seamOffsetFromBaseline - ((int) (point.y - (yOffset)) + (xHeight) / 2);//(int) point.y - (yOffset) + (interLineDistance - xHeight) / 2;
//                            if (targetLine < 0) {
////                                flexMargin = -(int) Math.sqrt(Math.abs(targetLine));
//                                flexMargin = 0;
//                            } else {
//                                flexMargin = -targetLine;
//                            }
//                            if (yStart < (baseLinePoint.y) && yStart < seamImage.height() - 2) {
////                                yStart++;
//                                outside = true;
//                            }
//
//                        }
////                        flexMargin *=flexMargin;
//                        break;
//                    }
//                }
//            }
//            if (preferUp) {
//                first += flexMargin;
//                middle += flexMargin;
//            }
//            if (preferDown) {
//                last += flexMargin;
//                middle += flexMargin;
//            }
            if (first < middle && first < last) {
                yStart--;
            } else if (last < middle) {
                yStart++;
            } else if (middle <= first && middle <= last) {

            } else {
                new Exception("error").printStackTrace();
            }
            if (yStart >= seamImage.height()) {
                yStart = seamImage.height() - 1;
            }
            if (yStart < 0) {
                yStart = 0;
            }

            if (preferUp && outside && yStart < seamImage.height() - 3) {
                yStart += 2;
            }
            if (preferDown && outside && yStart >= 2) {
                yStart -= 2;
            }
            points.add(new Point(j, yStart));
        }

        Lists.reverse(points);
        Point lastPoint = null;
        for (Point point : points) {
            if (lastPoint != null) {
                if (lastPoint.x < 0) {
                    new Exception("writing outside image width1").printStackTrace();
                }
                if (lastPoint.x >= seamImage.width()) {
                    new Exception("writing outside image width2").printStackTrace();
                }
                if (lastPoint.y < 0) {
                    new Exception("writing outside image height1").printStackTrace();
                }
                if (lastPoint.y >= seamImage.height()) {
                    new Exception("writing outside image height2").printStackTrace();
                }
                if (point.x < 0) {
                    new Exception("writing outside image width1").printStackTrace();
                }
                if (point.x >= seamImage.width()) {
                    new Exception("writing outside image width2").printStackTrace();
                }
                if (point.y < 0) {
                    new Exception("writing outside image height1").printStackTrace();
                }
                if (point.y >= seamImage.height()) {
                    new Exception("writing outside image height2").printStackTrace();
                }
            }
            lastPoint = point;
        }
        return points;
    }

    private static List<Point> findSeam(String identifier, Mat seamImage, int xStart, double seamOffsetFromBaseline, int xStop, boolean preferDown,
                                        boolean preferUp, List<Point> baseLinePoints, double yOffset, double xHeight, int xOffset,
                                        int margin, double interLineDistance) {
        List<Point> baseLinePointsExpanded = StringConverter.expandPointList(baseLinePoints);
        for (Point point : baseLinePointsExpanded) {
            point.y = point.y - yOffset;
            point.x = (point.x - xOffset) - margin;
        }
        List<Point> points = new ArrayList<>();
        int yStart = (int) ((baseLinePoints.get(baseLinePoints.size() - 1).y - yOffset) + seamOffsetFromBaseline);
        if (yStart >= seamImage.height()) {
            yStart = seamImage.height() - 1;
        }
        if (yStart < 0) {
            yStart = 0;
        }

        points.add(new Point(xStart, yStart));
        for (int j = xStart; j >= xStop; j--) {
            if (j >= seamImage.width()) {
                new Exception(identifier + " writing outside image width5: " + j + " : " + seamImage.width()).printStackTrace();
            }
            if (j < 0) {
                new Exception(identifier + "writing outside image width6: " + j).printStackTrace();
            }
            if (yStart < 1) {
                yStart = 1;
            }
            if (yStart > seamImage.height() - 2) {
                yStart = seamImage.height() - 2;
            }
            if (yStart < 1) {
                new Exception(identifier + "image too small? seamImage.height(): " + seamImage.height()).printStackTrace();
            }
            double first = seamImage.get(yStart - 1, j)[0];
            double middle = seamImage.get(yStart, j)[0];
            double last = seamImage.get(yStart + 1, j)[0];
            boolean outside = false;
            if (first < middle && first < last) {
                yStart--;
            } else if (last < middle) {
                yStart++;
            } else if (middle <= first && middle <= last) {

            } else {
                LOG.error("first: " + first);
                LOG.error("middle: " + middle);
                LOG.error("last: " + last);
                LOG.error("yStart: " + yStart);
                LOG.error("j: " + j);
                new Exception("error, we should never ever get here. Only times i got here: I had bad RAM").printStackTrace();
            }
            if (yStart >= seamImage.height()) {
                yStart = seamImage.height() - 1;
            }
            if (yStart < 0) {
                yStart = 0;
            }

            if (preferUp && outside && yStart < seamImage.height() - 3) {
                yStart += 2;
            }
            if (preferDown && outside && yStart >= 2) {
                yStart -= 2;
            }
            points.add(new Point(j, yStart));
        }

        Lists.reverse(points);
        Point lastPoint = null;
        for (Point point : points) {
            if (lastPoint != null) {
                if (lastPoint.x < 0) {
                    new Exception("writing outside image width1").printStackTrace();
                }
                if (lastPoint.x >= seamImage.width()) {
                    new Exception("writing outside image width2").printStackTrace();
                }
                if (lastPoint.y < 0) {
                    new Exception("writing outside image height1").printStackTrace();
                }
                if (lastPoint.y >= seamImage.height()) {
                    new Exception("writing outside image height2").printStackTrace();
                }
                if (point.x < 0) {
                    new Exception("writing outside image width1").printStackTrace();
                }
                if (point.x >= seamImage.width()) {
                    new Exception("writing outside image width2").printStackTrace();
                }
                if (point.y < 0) {
                    new Exception("writing outside image height1").printStackTrace();
                }
                if (point.y >= seamImage.height()) {
                    new Exception("writing outside image height2").printStackTrace();
                }
            }
            lastPoint = point;
        }
        return points;
    }

    private static synchronized void drawSeam(Mat colorizedSubmat, List<Point> topPoints) {
        Point lastPoint = null;
        for (Point point : topPoints) {
            if (point != null) {
                lastPoint = point;
                continue;
            }
            Imgproc.line(colorizedSubmat, lastPoint, point, new Scalar(255));
        }
    }

    public static void recalculateTextLineContoursFromBaselines(String identifier, Mat image, PcGts page, int minimumInterlineDistance) {
        recalculateTextLineContoursFromBaselines(identifier, image, page, 1, minimumInterlineDistance);
    }

    /**
     * @param image
     * @param page
     * @param scaleDownFactor
     */
    public static void recalculateTextLineContoursFromBaselines(String identifier, Mat image, PcGts page, double scaleDownFactor, int minimumInterlineDistance) {
        Mat grayImage = null;
//        Mat colorized = null;
        Mat blurred = null;

        grayImage = OpenCVWrapper.cvtColor(image);
//        colorized = OpenCVWrapper.zeros(grayImage.size(), CV_8UC3);

        blurred = energyImage(grayImage);

        List<TextLine> allLines = new ArrayList<>();
        try {
            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                allLines.addAll(textRegion.getTextLines());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Mat baselineImage = new Mat();
        blurred.convertTo(baselineImage, CV_64F);
        drawBaselines(allLines, baselineImage);

        int counter = 1;
        double interlineDistance = LayoutProc.interlineMedian(allLines, minimumInterlineDistance);//94;
        LOG.info(identifier + " interline distance: " + interlineDistance);

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            for (TextLine textLine : textRegion.getTextLines()) {
                double xHeightBasedOnInterline = interlineDistance / 3;
                if (xHeightBasedOnInterline < (MINIMUM_XHEIGHT)) {
                    xHeightBasedOnInterline = (MINIMUM_XHEIGHT);
                }
                int baselineThickness = (int) (xHeightBasedOnInterline / (2 * scaleDownFactor));
                long startTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                int xMargin = (int) xHeightBasedOnInterline;
                List<Point> baseLinePoints = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                if (baseLinePoints.size() <= 1) {
                    continue;
                }
                Rect baselineRect = LayoutProc.getBoundingBox(baseLinePoints);
                TextLine closestAbove = LayoutProc.closestLineAbove(textLine, allLines);
                double localInterlineDistance = getLocalInterlineDistance(interlineDistance, baselineRect, closestAbove);

                double yStartTop = getYStartTop(baseLinePoints, localInterlineDistance);
                double yStartBottom = getYStartBottom(blurred, xHeightBasedOnInterline, baseLinePoints);

                int xStart = getXStart(blurred, xMargin, baseLinePoints);
                int xStop = getXStop(xMargin, baseLinePoints);

                Rect roi = new Rect(xStop, (int) yStartTop, xStart - xStop, (int) (yStartBottom - yStartTop));
                if (roi.height <= 0 || roi.width <= 0) {
                    continue;
                }
                if (roi.height + roi.y >= blurred.height()
                        || roi.width + roi.x >= blurred.width()) {
                    continue;
                }
                Mat tmpSubmat = blurred.submat(roi);
                Mat baselineImageSubmat = baselineImage.submat(roi);
                Mat average = new Mat(tmpSubmat.size(), CV_64F, Core.mean(tmpSubmat));
                Mat tmpBinary = new Mat();
//                average.convertTo(average, CV_64F);

                Core.subtract(baselineImageSubmat, average, tmpBinary);
                baselineImageSubmat = OpenCVWrapper.release(baselineImageSubmat);
//                tmpBinary.convertTo(tmpBinary, CV_64F);
                average = OpenCVWrapper.release(average);
                Mat cloned = tmpBinary.clone();
                tmpSubmat = OpenCVWrapper.release(tmpSubmat);
                tmpBinary = OpenCVWrapper.release(tmpBinary);

                if (closestAbove != null) {
                    for (Point point : StringConverter.stringToPoint(closestAbove.getBaseline().getPoints())) {

                        int yTarget = (int) point.y - roi.y;
                        if (yTarget > 0
                                && yTarget < cloned.height()
                                && point.x - roi.x >= 0
                                && point.x - roi.x < cloned.width()) {
                            Imgproc.line(
                                    cloned,
                                    new Point(point.x - roi.x, 0),
                                    new Point(point.x - roi.x, yTarget),
                                    new Scalar(Float.MAX_VALUE),
                                    baselineThickness);
                        }
                    }
                }
                Mat seamImageTop = LayoutProc.calcSeamImage(cloned, scaleDownFactor);
                cloned = OpenCVWrapper.release(cloned);
                if (seamImageTop.height() <= 2) {
                    seamImageTop = OpenCVWrapper.release(seamImageTop);
                    continue;
                }
                List<Point> contourPoints = findSeam(
                        identifier,
                        seamImageTop,
                        seamImageTop.width() - 1,
                        -(1.5 * xHeightBasedOnInterline),
                        0,
                        true,
                        false,
                        baseLinePoints,
                        yStartTop,
                        xHeightBasedOnInterline,
                        xStop,
                        xMargin,
                        localInterlineDistance);
                seamImageTop = OpenCVWrapper.release(seamImageTop);

                for (Point point : contourPoints) {
                    point.x += roi.x;
                    point.y += roi.y;
                }


                /// bottom line
                yStartTop = baselineRect.y;
                if (yStartTop < 0) {
                    yStartTop = 0;
                }
                if (yStartTop >= blurred.height()) {
                    yStartTop = blurred.height() - 1;
                }

                yStartBottom = baselineRect.y + (baselineRect.height - 1) + interlineDistance;
                if (yStartBottom >= blurred.height()) {
                    yStartBottom = blurred.height() - 1;
                }

                if (yStartBottom - yStartTop <= 0) {
                    continue;
                }
                Rect searchArea = new Rect(xStop, (int) yStartTop, xStart - xStop, (int) (yStartBottom - yStartTop));
                Mat tmpSubmat2 = blurred.submat(searchArea);
                baselineImageSubmat = baselineImage.submat(searchArea);

//                Mat cloned2 = tmpSubmat2.clone();
                Mat average2 = new Mat(tmpSubmat2.size(), CV_8UC1, Core.mean(tmpSubmat2));
                average2.convertTo(average2, CV_64F);
                Mat tmpBinary2 = new Mat();

                Core.subtract(baselineImageSubmat, average2, tmpBinary2);
                baselineImageSubmat = OpenCVWrapper.release(baselineImageSubmat);
                tmpBinary2.convertTo(tmpBinary2, CV_64F);

                average2 = OpenCVWrapper.release(average2);
                Mat cloned2 = tmpBinary2.clone();
                tmpSubmat2 = OpenCVWrapper.release(tmpSubmat2);
                tmpBinary2 = OpenCVWrapper.release(tmpBinary2);

                List<Point> localPoints = new ArrayList<>();
                for (Point point : baseLinePoints) {
                    localPoints.add(new Point(point.x - xStop - xMargin, point.y - yStartTop));
                }
                for (Point point : baseLinePoints) {
                    localPoints.add(new Point(point.x - xStop - xMargin, point.y - yStartTop));
                }
                Point lastPoint = null;
                for (Point point : localPoints) {
                    if (lastPoint != null
                            && lastPoint.x >= 0
                            && lastPoint.x < cloned2.width()
                            && lastPoint.y >= 0
                            && lastPoint.y < cloned2.height()
                            && point.x >= 0
                            && point.x < cloned2.width()
                            && point.y >= 0
                            && point.y < cloned2.height()
                    ) {
                        Imgproc.line(cloned2, lastPoint, point, new Scalar(Float.MAX_VALUE), (int) (10 / scaleDownFactor));
                    }
                    lastPoint = point;
                }

                TextLine closestBelow = LayoutProc.closestLineBelow(textLine, allLines);
                if (closestBelow != null) {
                    for (Point point : StringConverter.stringToPoint(closestBelow.getBaseline().getPoints())) {
                        int yTarget = (int) point.y - searchArea.y;
                        if (yTarget > 0
                                && yTarget < cloned2.height()
                                && point.x - searchArea.x >= 0
                                && point.x - searchArea.x < cloned2.width()) {
                            Imgproc.line(
                                    cloned2,
                                    new Point(point.x - searchArea.x, yTarget),
                                    new Point(point.x - searchArea.x, cloned2.height() - 1),
                                    new Scalar(Float.MAX_VALUE),
                                    baselineThickness);
                        }
                    }
                }

                Mat seamImageBottom = LayoutProc.calcSeamImage(cloned2, scaleDownFactor);
                if (seamImageBottom.height() <= 2) {
                    seamImageBottom = OpenCVWrapper.release(seamImageBottom);
                    continue;
                }

                cloned2 = OpenCVWrapper.release(cloned2);

                List<Point> bottomPoints = findSeam(
                        identifier,
                        seamImageBottom,
                        searchArea.width - 1,
                        xHeightBasedOnInterline / 2,
                        0,
                        false,
                        true,
                        baseLinePoints,
                        yStartTop,
                        xHeightBasedOnInterline,
                        xStop,
                        xMargin,
                        interlineDistance);

                seamImageBottom = OpenCVWrapper.release(seamImageBottom);

                for (Point point : bottomPoints) {
                    point.x += searchArea.x;
                    point.y += searchArea.y;
                }

                contourPoints = Lists.reverse(contourPoints);

//                List<Double> distances = new ArrayList<>();
//                for (Point point : contourPoints){
//                    Point closest = closestPoint(bottomPoints, point);
//                    double distance = distance(closest, point);
//                    distances.add(distance);
//                }
//                Statistics statistics = new Statistics(distances);
////                xHeight = statistics.getMean();
//                xHeight = statistics.getMinimum()/3;
////                xHeight = statistics.getMaximum();
////                xHeight = statistics.median();
////                xHeight = statistics.median();
                contourPoints.addAll(bottomPoints);

                for (Point point : contourPoints) {
                    if (point.x < 0) {
                        new Exception("point.x<0").printStackTrace();
                    }
                    if (point.y < 0) {
                        new Exception("point.y<0").printStackTrace();
                    }
//                    if (point.x >= colorized.width()) {
//                        new Exception("point.x>=colorized.width()").printStackTrace();
//                    }
//                    if (point.y >= colorized.height()) {
//                        new Exception("point.y>=colorized.height()").printStackTrace();
//                    }
                }
                List<Point> newPoints = StringConverter.simplifyPolygon(contourPoints);
                textLine.getCoords().setPoints(StringConverter.pointToString(StringConverter.simplifyPolygon(newPoints, 5)));
                if (textLine.getTextStyle() == null) {
                    textLine.setTextStyle(new TextStyle());
                }
                textLine.getTextStyle().setxHeight((int) xHeightBasedOnInterline);

                MatOfPoint sourceMat = new MatOfPoint();
                sourceMat.fromList(contourPoints);
                List<MatOfPoint> finalPoints = new ArrayList<>();
                finalPoints.add(sourceMat);
                OpenCVWrapper.release(sourceMat);
                counter++;
            }
        }
        LOG.info(identifier + " textlines: " + (counter));
        LOG.info(identifier + " average textline took: " + (stopwatch.elapsed(TimeUnit.MILLISECONDS) / counter));

////            StringTools.writeFile(file.toAbsolutePath().toString() + ".done", "");
////            Imgcodecs.imwrite("/tmp/input-colorized.png", colorized);
////            String pageXmlString = PageUtils.convertPcGtsToString(page);
////            StringTools.writeFile(inputXmlFile, pageXmlString);
//        colorized = OpenCVWrapper.release(colorized);
        blurred = OpenCVWrapper.release(blurred);
        grayImage = OpenCVWrapper.release(grayImage);
        baselineImage = OpenCVWrapper.release(baselineImage);

    }

//    private static RotatedRect getRotatedRect(List<Point> baseLinePoints) {
//        MatOfPoint2f sourceMat = new MatOfPoint2f();
//        sourceMat.fromList(baseLinePoints);
//
//        RotatedRect rect = Imgproc.minAreaRect(sourceMat);
//        sourceMat.release();
//        return rect;
//    }

    private static void shrinkPoints(List<Point> points, double shrinkFactor) {
        for (Point point : points) {
            point.x = point.x / shrinkFactor;
            point.y = point.y / shrinkFactor;
        }
    }

    private static RotatedRect getRotatedRect(List<Point> baseLinePoints) {
        MatOfPoint2f sourceMat = new MatOfPoint2f();
        sourceMat.fromList(baseLinePoints);

        RotatedRect rect = Imgproc.minAreaRect(sourceMat);
        OpenCVWrapper.release(sourceMat);
        return rect;
    }

    private static List<Point> warpPoints(List<Point> points, Mat perspectiveMat) {
        MatOfPoint2f matOfPoint = new MatOfPoint2f();
        matOfPoint.fromList(points);
        MatOfPoint2f matOfPointResult = new MatOfPoint2f();
        if (matOfPoint.channels() + 1 != perspectiveMat.cols()) {
            new Exception("matOfPoint.channels()+1 != perspectiveMat.cols()").printStackTrace();
        }
        Core.perspectiveTransform(matOfPoint, matOfPointResult, perspectiveMat);
        List<Point> returnPoints = matOfPointResult.toList();
        OpenCVWrapper.release(matOfPoint);
        OpenCVWrapper.release(matOfPointResult);
        return returnPoints;
    }

    private static MatOfPoint2f getDestinationMat(RotatedRect rotatedRect, double radians, Point point1a, Point point2a, Point point3a, Point point4a) {
        MatOfPoint2f dst;
        radians = -radians;
        double x = point1a.x - rotatedRect.center.x;
        double y = point1a.y - rotatedRect.center.y;
        Point point1b = new Point(x * Math.cos(radians) - y * Math.sin(radians), x * Math.sin(radians) + y * Math.cos(radians));
        point1b.x += rotatedRect.center.x;
        point1b.y += rotatedRect.center.y;
        x = point2a.x - rotatedRect.center.x;
        y = point2a.y - rotatedRect.center.y;
        Point point2b = new Point(x * Math.cos(radians) - y * Math.sin(radians), x * Math.sin(radians) + y * Math.cos(radians));
        point2b.x += rotatedRect.center.x;
        point2b.y += rotatedRect.center.y;
        x = point3a.x - rotatedRect.center.x;
        y = point3a.y - rotatedRect.center.y;
        Point point3b = new Point(x * Math.cos(radians) - y * Math.sin(radians), x * Math.sin(radians) + y * Math.cos(radians));
        point3b.x += rotatedRect.center.x;
        point3b.y += rotatedRect.center.y;
        x = point4a.x - rotatedRect.center.x;
        y = point4a.y - rotatedRect.center.y;
        Point point4b = new Point(x * Math.cos(radians) - y * Math.sin(radians), x * Math.sin(radians) + y * Math.cos(radians));
        point4b.x += rotatedRect.center.x;
        point4b.y += rotatedRect.center.y;
        dst = new MatOfPoint2f();
        dst.fromArray(point1b, point2b, point3b, point4b);
        return dst;
    }

    private static Mat getBaselineMat(Size size, List<Point> points, double xHeight) {
        Mat baselineMat = Mat.zeros(size, CV_8UC1);
        for (Point point : points) {
            Point topPoint = new Point(point.x, point.y - xHeight);
            Point bottomPoint = new Point(point.x, point.y);
            if (topPoint.x <= 0) {
                topPoint.x = 0;
            }
            if (bottomPoint.x <= 0) {
                bottomPoint.x = 0;
            }
            if (topPoint.y > size.height - 1) {
                topPoint.y = size.height - 1;
            }
            if (topPoint.x > size.width - 1) {
                topPoint.x = size.width - 1;
            }

            Imgproc.line(baselineMat, topPoint, bottomPoint, new Scalar(255), 1);
        }
        return baselineMat;
    }

    //    private static Mat extractRotatedRect(Mat image,RotatedRect rotatedRect,  List<Point> points, int above, int below, double mainAngle){
//        Mat rotated = rotate(image.submat(rotatedRect.boundingRect()), mainAngle);
//        rotatedRect.points();
//        return rotated;
//    }
    /*
    Gets a text line from an image based on the baseline and contours. Text line is rotated to its main horizontal axis(straightened).
     */
    public static Mat getBinaryLineStripOld(Mat image, List<Point> contourPoints, List<Point> baseLinePoints, double xHeight, boolean includeMask) {
        Mat finalOutput = null;
        Mat rotationMat = null;
        Mat mask = null;
        Mat baseLineMat = null;
        Mat perspectiveMat = null;
        MatOfPoint sourceMat = null;
        MatOfPoint2f src = null;
        MatOfPoint2f dst = null;
        Mat deskewedImage = null;
        Mat deskewedSubmat = null;
        List<Point> expandedBaseline = StringConverter.expandPointList(baseLinePoints);
        Rect baseLineBox = LayoutProc.getBoundingBox(baseLinePoints);

        // if just one point: ignore
        if (baseLinePoints.size() < 2) {
            return null;
        }
        // if too small just ignore
        if (baseLineBox.width < 10) {
            return null;
        }
        // FIXME RUTGERCHECK: this also includes vertical and upside down lines.
        if (baseLinePoints.get(0).x > baseLinePoints.get(baseLinePoints.size() - 1).x) {
            return null;
        }

        if (baseLineBox.y < 0
                || baseLineBox.y + baseLineBox.height > image.height()
                || baseLineBox.x < 0
                || baseLineBox.x + baseLineBox.width > image.width()
        ) {
            LOG.error("error baselineBox outside image");
            return null;
        }
        RotatedRect rotatedRect = getRotatedRect(baseLinePoints);
        double mainAngle = LayoutProc.getMainAngle(baseLinePoints);
        double angle = rotatedRect.angle;
        angle = mainAngle;
        while (angle < -45) {
            angle += 90;
        }
        while (angle > 45) {
            angle -= 90;
        }
        rotationMat = Imgproc.getRotationMatrix2D(rotatedRect.center, angle, 1);

        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.ceil(image.width() * cos + image.height() * sin);
        int newHeight = (int) Math.ceil(image.width() * sin + image.height() * cos);

        deskewedImage = OpenCVWrapper.warpAffine(image, rotationMat, new Size(newWidth, newHeight));
        OpenCVWrapper.release(rotationMat);

        Rect cuttingRect = rotatedRect.boundingRect();
        cuttingRect.y = (int) (rotatedRect.center.y - 2 * xHeight);
        cuttingRect.height = (int) (3 * xHeight);
        if (cuttingRect.x + cuttingRect.width > deskewedImage.width()) {
            cuttingRect.width = deskewedImage.width() - cuttingRect.x;
        }
        if (cuttingRect.y < 0
                || cuttingRect.y + cuttingRect.height >= deskewedImage.height() - 1
                || cuttingRect.x < 0
                || cuttingRect.x + cuttingRect.width >= deskewedImage.width() - 1) {
            //do nothing
            // TODO: fixme. Due to rotation x or y might fall outside the image.
            LOG.error("dropping out this line due to going outside the box after rotation");
        } else {
            Mat tmpSubmat = null;
            try {
                tmpSubmat = deskewedImage.submat(cuttingRect);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            deskewedSubmat = tmpSubmat.clone();
            OpenCVWrapper.release(tmpSubmat);
        }
        OpenCVWrapper.release(deskewedImage);

        if (deskewedSubmat != null) {
            Point point1a = new Point(0, 0);
            Point point2a = new Point(image.width() - 1, 0);
            Point point3a = new Point(0, image.height() - 1);
            Point point4a = new Point(image.width() - 1, image.height() - 1);
            src = new MatOfPoint2f();
            src.fromArray(point1a, point2a, point3a, point4a);
            dst = getDestinationMat(rotatedRect, radians, point1a, point2a, point3a, point4a);

            perspectiveMat = Imgproc.getPerspectiveTransform(src, dst);
            expandedBaseline = warpPoints(expandedBaseline, perspectiveMat);
            for (Point point : expandedBaseline) {
                point.x -= cuttingRect.x;
                point.y -= cuttingRect.y;
            }
            Point offset = new Point(cuttingRect.x, cuttingRect.y);
            baseLineMat = getBaselineMat(cuttingRect.size(), expandedBaseline, xHeight);
//            contourPoints = new ArrayList<>();
//            contourPoints.add(new Point(0,0));
//            contourPoints.add(new Point(image.width(), image.height()));
            ArrayList<Point> clonedPoints = new ArrayList<>();
            List<Point> tmpPoints = warpPoints(contourPoints, perspectiveMat);
            perspectiveMat = OpenCVWrapper.release(perspectiveMat);
            for (Point point : tmpPoints) {
                clonedPoints.add(new Point(point.x - cuttingRect.x, point.y - cuttingRect.y));
            }

            sourceMat = new MatOfPoint();
            sourceMat.fromList(clonedPoints);
            List<MatOfPoint> finalPoints = new ArrayList<>();
            finalPoints.add(sourceMat);
            mask = new Mat(cuttingRect.size(), CV_8UC1, new Scalar(0));
            Scalar color = new Scalar(127);
            Imgproc.fillPoly(mask, finalPoints, color);
            for (int i = 1; i < expandedBaseline.size(); i++) {
                Imgproc.line(mask, expandedBaseline.get(i - 1), expandedBaseline.get(i), new Scalar(255), (int) xHeight / 3);
            }
            int size = deskewedSubmat.height() / 2;
            if (size % 2 == 0) {
                size++;
            }
            if (size >= deskewedSubmat.width()) {
                size = deskewedSubmat.width() / 2;
                if (size % 2 == 0) {
                    size++;
                }
            }
            if (size > 1) {

                List<Mat> splittedImage = new ArrayList<>();
                Core.split(deskewedSubmat, splittedImage);
                deskewedSubmat = OpenCVWrapper.release(deskewedSubmat);
                List<Mat> toMerge = null;
                if (includeMask) {
                    toMerge = Arrays.asList(splittedImage.get(0), splittedImage.get(1), splittedImage.get(2), mask);//, mask);
                } else {
                    toMerge = Arrays.asList(splittedImage.get(0), splittedImage.get(1), splittedImage.get(2));//, mask);
                }
                if (mask.channels() != 1) {
                    new Exception(" mask.channels() != 1").printStackTrace();
                }
                if (mask.type() != CV_8UC1) {
                    new Exception(" mask.type() != CV_8UC1").printStackTrace();
                }

                try {
                    finalOutput = OpenCVWrapper.merge(toMerge);
                    OpenCVWrapper.release(splittedImage.get(0));
                    OpenCVWrapper.release(splittedImage.get(1));
                    OpenCVWrapper.release(splittedImage.get(2));
                    mask = OpenCVWrapper.release(mask);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOG.error("toMerge.size() " + toMerge.size());
                    LOG.error("deskewSubmat.size() " + deskewedSubmat.size());
                    LOG.error("mask.size() " + mask.size());
                    new Exception("here").printStackTrace();
                }
            }
        }

        perspectiveMat = OpenCVWrapper.release(perspectiveMat);
        deskewedImage = OpenCVWrapper.release(deskewedImage);
        deskewedSubmat = OpenCVWrapper.release(deskewedSubmat);
        rotationMat = OpenCVWrapper.release(rotationMat);
        mask = OpenCVWrapper.release(mask);
        baseLineMat = OpenCVWrapper.release(baseLineMat);
        if (sourceMat != null) {
            sourceMat.release();
        }
        if (src != null) {
            src.release();
        }
        if (dst != null) {
            dst.release();
        }
        return finalOutput;
    }

    public static Mat convertToBinaryImage(Mat grayImage) {
        Mat binaryImage = Mat.zeros(new Size(grayImage.height(), grayImage.width()), CvType.CV_8UC1);
        Imgproc.adaptiveThreshold(grayImage, binaryImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, bestBinarizationBlockSize, bestBinarizationThreshold);
        return binaryImage;
    }

    public static Boolean rotatedRectContainsPoint(RotatedRect rect, Point point) {
        // rotate around rectangle center by -rectAngle
        double sin = Math.sin(-rect.angle);
        double cos = Math.cos(-rect.angle);

        // set origin to rect center
        Point newPoint = new Point(point.x - rect.center.x, point.y - rect.center.y);
        // rotate
        newPoint = new Point(newPoint.x * cos - newPoint.y * sin, newPoint.x * sin + newPoint.y * cos);
        // put origin back
        newPoint = new Point(newPoint.x + rect.center.x, newPoint.y + rect.center.y);

        // check if our transformed point is in the rectangle, which is no longer
        // rotated relative to the point

        return newPoint.x >= rect.center.x - rect.size.width / 2
                && newPoint.x <= rect.center.x + rect.size.width / 2
                && newPoint.y >= rect.center.y - rect.size.height / 2 && newPoint.y <= rect.center.y + rect.size.height / 2;
    }


    private static Point rotatePoint(Point point, Point oldCenter, Point newCenter, double rotation) {
        Point result = new Point(point.x - newCenter.x, point.y - newCenter.y);
        result = new Point(result.x * Math.cos(rotation) - result.y * Math.sin(rotation), result.x * Math.sin(rotation) + result.y * Math.cos(rotation));
        result = new Point(result.x + oldCenter.x, result.y + oldCenter.y);
        return result;
    }

    /*
Gets a text line from an image based on the baseline and contours. Text line is rotated to its main horizontal axis(straightened).
 */
    public static BinaryLineStrip getBinaryLineStrip(String identifier, Mat image, List<Point> contourPoints, List<Point> baseLinePoints,
                                                     Integer xHeight, boolean includeMask, int minWidth, String textLineId,
                                                     double aboveMultiplier, double belowMultiplier, double besideMultiplier) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Mat finalOutput = null;
        Mat finalFinalOutput = null;
//        Mat rotationMat = null;
//        Mat baseLineMat = null;
//        Mat perspectiveMat = null;
        MatOfPoint2f src = null;
        MatOfPoint2f dst = null;
        Mat deskewedSubmat = null;
        fixPoints(baseLinePoints, image.width(), image.height());
        List<Point> expandedBaseline = StringConverter.expandPointList(baseLinePoints);
        Rect baseLineBox = LayoutProc.getBoundingBox(baseLinePoints);

        // if just one point: ignore
        if (baseLinePoints.size() < 2) {
            LOG.debug(identifier + ": just one point: ignore");
            return null;
        }
        // if too small just ignore
        double baselineLength = StringConverter.calculateBaselineLength(baseLinePoints);
        if (baselineLength < minWidth) {
            LOG.debug(identifier + ": too small just ignore");
            return null;
        }
        // FIXME RUTGERCHECK: this also includes vertical and upside down lines.
//        if (baseLinePoints.get(0).x > baseLinePoints.get(baseLinePoints.size() - 1).x) {
//            return null;
//        }

        if (baseLineBox.y < 0
                || baseLineBox.y + baseLineBox.height > image.height()
                || baseLineBox.x < 0
                || baseLineBox.x + baseLineBox.width > image.width()
        ) {
            LOG.error(identifier + ": error baselineBox outside image");
            return null;
        }
        double mainAngle = LayoutProc.getMainAngle(baseLinePoints);
        double radians = Math.toRadians(mainAngle);

        double above = aboveMultiplier * xHeight;
        double below = belowMultiplier * xHeight;
        double beside = besideMultiplier * xHeight;

        List<Point> newPoints = new ArrayList<>();
        double tmpAngle = -radians;
        for (Point point : baseLinePoints) {
            // add point above baseline point
            double x = 0 * Math.cos(tmpAngle) - above * Math.sin(tmpAngle);
            double y = 0 * Math.sin(tmpAngle) + above * Math.cos(tmpAngle);
            Point newPoint = new Point(point.x - x, point.y - y);
            newPoints.add(newPoint);
            // add point below baseline point
            x = 0 * Math.cos(tmpAngle) + below * Math.sin(tmpAngle);
            y = 0 * Math.sin(tmpAngle) - below * Math.cos(tmpAngle);
            newPoint = new Point(point.x - x, point.y - y);
            newPoints.add(newPoint);
            // add point besides baseline point. Really should be only necessary for for and last point
            x = beside * Math.cos(tmpAngle);
            y = beside * Math.sin(tmpAngle);
            newPoint = new Point(point.x - x, point.y - y);
            newPoints.add(newPoint);
            x = -beside * Math.cos(tmpAngle);
            y = -beside * Math.sin(tmpAngle);
            newPoint = new Point(point.x - x, point.y - y);
            newPoints.add(newPoint);
        }
        Point pivotPoint = getPivotPoint(baseLinePoints);
//        Point rotatedTopPoint = findRotatedTopPoint(newPoints, mainAngle);
//        Point rotatedBottomPoint = findRotatedBottomPoint(newPoints, mainAngle);
//        Point rotatedLeftMostPoint = findRotatedLeftMostPoint(newPoints, mainAngle);
//        Point rotatedRightMostPoint = findRotatedRightMostPoint(newPoints, mainAngle );

        RotatedRect rotatedRect = new RotatedRect(pivotPoint, new Size(100, 100), mainAngle);
        Mat src_pts = new Mat();
        Imgproc.boxPoints(rotatedRect, src_pts);

        double width = rotatedRect.size.width;
        double height = rotatedRect.size.height;
//        getDestinationMat(rotatedRect, -mainAngle, )
        MatOfPoint2f dst_pts = new MatOfPoint2f();
        dst_pts.fromArray(
                new Point(pivotPoint.x - 50, pivotPoint.y + 50),
                new Point(pivotPoint.x - 50, pivotPoint.y - 50),
                new Point(pivotPoint.x + 50, pivotPoint.y - 50),
                new Point(pivotPoint.x - 50, pivotPoint.y + 50)
        );

        Mat perspectiveMat = getPerspectiveTransform(src_pts, dst_pts);
        List<Point> warpedNewPoints = new ArrayList<>();// = warpPoints(newPoints, perspectiveMat);
        for (Point point : newPoints) {
            Point result = rotatePoint(point, pivotPoint, pivotPoint, -radians);
            warpedNewPoints.add(result);
        }


        MatOfPoint warpedNewPointsMat = new MatOfPoint();
        warpedNewPointsMat.fromList(warpedNewPoints);
        Rect boundingRect = boundingRect(warpedNewPointsMat);

        RotatedRect rotatedRectFull = new RotatedRect(pivotPoint, boundingRect.size(), mainAngle);

//        Rect cuttingRect = rotatedRectFull.boundingRect();

        src_pts = new Mat();
        Imgproc.boxPoints(rotatedRectFull, src_pts);

        width = rotatedRectFull.size.width;
        height = rotatedRectFull.size.height;

        dst_pts = new MatOfPoint2f();
        dst_pts.fromArray(
                new Point(0, height - 1),
                new Point(0, 0),
                new Point(width - 1, 0),
                new Point(width - 1, height - 1)
        );

        Mat perspectiveMatTest = Imgproc.getPerspectiveTransform(src_pts, dst_pts);
        deskewedSubmat = new Mat();
        Imgproc.warpPerspective(image, deskewedSubmat, perspectiveMatTest, boundingRect.size());
        List<Point> warpedContourPoints = warpPoints(contourPoints, perspectiveMatTest);

//        if (write ){
//            Imgcodecs.imwrite("/tmp/rotated.png",deskewedSubmat);
//        }

        if (deskewedSubmat == null) {
            LOG.error(identifier + ": deskewedSubmat is null");
        }
        if (deskewedSubmat != null) {

            Mat mask = getMask(deskewedSubmat, warpedContourPoints);

            expandedBaseline = warpPoints(expandedBaseline, perspectiveMatTest);


            Mat grayImage = new Mat();
            Imgproc.cvtColor(deskewedSubmat, grayImage, Imgproc.COLOR_BGR2GRAY);
            Mat grayImageInverted = OpenCVWrapper.bitwise_not(grayImage);
            grayImage.release();
//            Mat binary = convertToBinaryImage(grayImage);
//            Mat tmpSubmat = blurred.submat(roi);
            Mat average = new Mat(grayImageInverted.size(), CV_8UC1, Core.mean(grayImageInverted));
            Mat tmpGrayBackgroundSubtracted = new Mat();
            Core.subtract(grayImageInverted, average, tmpGrayBackgroundSubtracted);
            average.release();
            grayImageInverted.release();
            Mat maskedBinary = new Mat();
//            Mat tmpBinary = new Mat();
//            tmpBinary = convertToBinaryImage(tmpGrayBackgroundSubtracted);

            tmpGrayBackgroundSubtracted.copyTo(maskedBinary, mask);
//            tmpBinary.release();
//            Imgcodecs.imwrite("/tmp/binary.png", tmpBinary);
            List<Integer> horizontalProfile = horizontalProfileByte(maskedBinary);
            maskedBinary.release();
            tmpGrayBackgroundSubtracted.release();
            List<Double> horizontalProfileDouble = smoothList(horizontalProfile, LayoutProc.MINIMUM_XHEIGHT / 2);

            Point lowestBaselinePoint = getLowestPoint(expandedBaseline);
            Point highestBaselinePoint = getHighestPoint(expandedBaseline);
            double medianY = getMedianY(expandedBaseline);
            xHeight = calculateXHeightViaProjection(textLineId, horizontalProfileDouble, lowestBaselinePoint, highestBaselinePoint);
            xHeight += calculateXHeightViaMask(textLineId, mask);
            xHeight /= 2;
            if (xHeight == null) {
                return null;
            }

//            baseLineMat = getBaselineMat(cuttingRect.size(), expandedBaseline, xHeight);
            perspectiveMat.release();


//            for (int i = 1; i < expandedBaseline.size(); i++) {
//                Imgproc.line(mask, expandedBaseline.get(i - 1), expandedBaseline.get(i), new Scalar(255), (int) xHeight / 3);
//            }
            int size = deskewedSubmat.height() / 2;
            if (size % 2 == 0) {
                size++;
            }
            if (size >= deskewedSubmat.width()) {
                size = deskewedSubmat.width() / 2;
                if (size % 2 == 0) {
                    size++;
                }
            }
            if (size > 1) {

                List<Mat> splittedImage = new ArrayList<>();
                Core.split(deskewedSubmat, splittedImage);
                deskewedSubmat = OpenCVWrapper.release(deskewedSubmat);
                List<Mat> toMerge = null;
                if (includeMask) {
                    toMerge = Arrays.asList(splittedImage.get(0), splittedImage.get(1), splittedImage.get(2), mask);//, mask);
                } else {
                    toMerge = Arrays.asList(splittedImage.get(0), splittedImage.get(1), splittedImage.get(2));//, mask);
                }
                if (mask.channels() != 1) {
                    new Exception(" mask.channels() != 1").printStackTrace();
                }
                if (mask.type() != CV_8UC1) {
                    new Exception(" mask.type() != CV_8UC1").printStackTrace();
                }

                try {
                    finalOutput = OpenCVWrapper.merge(toMerge);
                    int rowStart = (int) medianY - 2 * xHeight;
                    if (rowStart < 0) {
                        rowStart = 0;
                    }
                    int rowEnd = (int) medianY + 1 * xHeight;
                    if (rowEnd >= finalOutput.height()) {
                        rowEnd = finalOutput.height() - 1;
                    }
                    finalFinalOutput = finalOutput
                            .submat(rowStart,
                                    rowEnd,
                                    0,
                                    finalOutput.width() - 1)
                            .clone();
                    OpenCVWrapper.release(finalOutput);
                    OpenCVWrapper.release(splittedImage.get(0));
                    OpenCVWrapper.release(splittedImage.get(1));
                    OpenCVWrapper.release(splittedImage.get(2));
                    OpenCVWrapper.release(mask);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LOG.error(identifier + ": toMerge.size() " + toMerge.size());
                    LOG.error(identifier + ": deskewSubmat.size() " + deskewedSubmat.size());
                    LOG.error(identifier + ": mask.size() " + mask.size());
                    new Exception("here").printStackTrace();
                }
            }
        }

//        perspectiveMat = OpenCVWrapper.release(perspectiveMat);
//        deskewedImage = OpenCVWrapper.release(deskewedImage);
        deskewedSubmat = OpenCVWrapper.release(deskewedSubmat);
//        rotationMat = OpenCVWrapper.release(rotationMat);
//        mask = OpenCVWrapper.release(mask);
//        baseLineMat = OpenCVWrapper.release(baseLineMat);
        if (src != null) {
            src.release();
        }
        if (dst != null) {
            dst.release();
        }
        BinaryLineStrip binaryLineStrip = new BinaryLineStrip();
        binaryLineStrip.setLineStrip(finalFinalOutput);
        binaryLineStrip.setxHeight(xHeight);
        return binaryLineStrip;
    }

    private static Integer calculateXHeightViaMask(String textLineId, Mat mask) {
        int sum = 0;
        int columns = 0;
        for (int i = 0; i < mask.width(); i++) {
            int counter = 0;
            for (int j = 0; j < mask.height(); j++) {
                double value = mask.get(j, i)[0];
                if (value > 1) {
                    counter++;
                }
            }
            if (counter > 0) {
                columns++;
                sum += counter;
            }
        }
        return (int) ((sum / columns) * 0.5);
    }

    private static Integer calculateXHeightViaProjection(String textLineId, List<Double> horizontalProfileDouble, Point lowestBaselinePoint, Point highestBaselinePoint) {
        Integer start = null;
        Integer end = null;
        Integer xHeight;
        Double averageBaselineLineProfileScore = null;
        Double maxScore = Double.MIN_VALUE;
        Double minScore = Double.MAX_VALUE;
        int startY = (int) (lowestBaselinePoint.y + highestBaselinePoint.y) / 2;
        // Start from the bottom and work our way up
        for (int i = startY; i > 0; i--) {
            if (averageBaselineLineProfileScore == null) {
                if (i >= horizontalProfileDouble.size()) {
                    LOG.error("going out of bounds");
                }
                averageBaselineLineProfileScore = horizontalProfileDouble.get(i);
            }
            // go up while it increases, with Minimum xheight as minimum to go up
            if (maxScore == Double.MIN_VALUE || horizontalProfileDouble.get(i) >= maxScore || startY - i < LayoutProc.MINIMUM_XHEIGHT) {
                if (horizontalProfileDouble.get(i) > maxScore) {
                    maxScore = horizontalProfileDouble.get(i);
                }
                if (horizontalProfileDouble.get(i) < minScore) {
                    minScore = horizontalProfileDouble.get(i);
                }
                if (end == null) {
                    end = i;
                }
                start = i;
            } else {
                break;
            }
//                Imgproc.line(deskewedSubmat, new Point(0,i), new Point(horizontalProfileDouble.get(i), i), new Scalar(255,0,0));
        }
        // continue from peak
//            if (start == null) {
//                Imgcodecs.imwrite("/tmp/test.png", deskewedSubmat);
//            }
        // TODO: fixme this is a workaround for lines that are rotated a bit too much right now
        // these are now falsely excluded
        if (start == null) {
            LOG.error("dropping line: " + textLineId);
            return null;
        }
        for (int i = start; i > 0; i--) {
            if (end - i <= LayoutProc.MINIMUM_XHEIGHT || horizontalProfileDouble.get(i) >= (minScore + maxScore) / 2) {
                start = i;
            } else {
                break;
            }
//                Imgproc.line(deskewedSubmat, new Point(0,i), new Point(horizontalProfileDouble.get(i), i), new Scalar(0,255,0));
        }
        Integer zeroLine = null;
        for (int i = start; i > 0; i--) {
            if (horizontalProfileDouble.get(i) < 0.1) {
                zeroLine = i;
                break;
            }
        }
        xHeight = end - start;
//        if (zeroLine!=null && (double)(end - zeroLine) / (double)xHeight < 1.5){
//            xHeight = (int) (xHeight/ 1.5);
//        }
        return xHeight;
    }

    private static Point findRotatedTopPoint(List<Point> newPoints, double mainAngle) {
        double radians = Math.toRadians(mainAngle);
        double yZero = Double.MAX_VALUE;
        Point rotatedTopPoint = null;
        for (Point point : newPoints) {
            Point xIntersect = new Point(0, point.y * Math.cos(radians) + point.x * Math.sin(radians));
            if (yZero > xIntersect.y) {
                rotatedTopPoint = point;
                yZero = xIntersect.y;
            }
        }
        return rotatedTopPoint;
    }

    private static Point findRotatedBottomPoint(List<Point> newPoints, double mainAngle) {
        double radians = Math.toRadians(mainAngle);
        double yZero = -Double.MAX_VALUE;
        Point rotatedBottomPoint = null;
        for (Point point : newPoints) {
            Point xIntersect = new Point(0, point.y * Math.cos(radians) + point.x * Math.sin(radians));
            if (yZero < xIntersect.y) {
                rotatedBottomPoint = point;
                yZero = xIntersect.y;
            }
        }
        return rotatedBottomPoint;
    }

    private static Point findRotatedLeftMostPoint(List<Point> newPoints, double mainAngle) {
        double radians = Math.toRadians(mainAngle);
        double xZero = Double.MAX_VALUE;
        Point rotatedLeftMostPoint = null;
        for (Point point : newPoints) {
            Point yAxis = new Point(point.x + Math.tan(radians) * point.y, 0);
            if (xZero > yAxis.x) {
                rotatedLeftMostPoint = point;
                xZero = yAxis.x;
            }
        }
        return rotatedLeftMostPoint;
    }

    private static Point findRotatedRightMostPoint(List<Point> newPoints, double mainAngle) {
        double radians = Math.toRadians(mainAngle);
        double xZero = -Double.MAX_VALUE;
        Point rotatedRightMostPoint = null;
        for (Point point : newPoints) {
            Point yAxis = new Point(point.x + Math.tan(radians) * point.y, 0);
            if (xZero < yAxis.x) {
                rotatedRightMostPoint = point;
                xZero = yAxis.x;
            }
        }
        return rotatedRightMostPoint;
    }

    private static Point getPivotPoint(List<Point> newPoints) {
        Point highest = getHighestPoint(newPoints);
        Point lowest = getLowestPoint(newPoints);
        Point leftMost = getLeftMostPoint(newPoints);
        Point rightMost = getRightMostPoint(newPoints);

        return new Point((leftMost.x + rightMost.x) / 2., (highest.y + lowest.y) / 2.);

    }

    private static Mat getMask(Mat deskewedSubmat, List<Point> warpedContourPoints) {
        MatOfPoint sourceMat = new MatOfPoint();
        sourceMat.fromList(warpedContourPoints);
        List<MatOfPoint> finalPoints = new ArrayList<>();
        finalPoints.add(sourceMat);
        Mat mask = new Mat(deskewedSubmat.size(), CV_8UC1, new Scalar(1));
        Scalar color = new Scalar(255);
        Imgproc.fillPoly(mask, finalPoints, color);
        sourceMat.release();
        return mask;
    }

    private static double getMedianY(List<Point> expandedBaseline) {

        List<Double> yPoints = new ArrayList<>();
        for (Point point : expandedBaseline) {
            yPoints.add(point.y);
        }

        return new Statistics(yPoints).median();
    }

    private static Point getHighestPoint(List<Point> newPoints) {
        Double y = null;
        Point bestPoint = null;
        for (Point point : newPoints) {
            if (y == null || point.y < y) {
                y = point.y;
                bestPoint = point;
            }
        }
        return bestPoint;
    }

    private static Point getLowestPoint(List<Point> newPoints) {
        Double y = null;
        Point bestPoint = null;
        for (Point point : newPoints) {
            if (y == null || point.y > y) {
                y = point.y;
                bestPoint = point;
            }
        }
        return bestPoint;
    }

    private static Point getLeftMostPoint(List<Point> newPoints) {
        Double x = null;
        Point bestPoint = null;
        for (Point point : newPoints) {
            if (x == null || point.x < x) {
                x = point.x;
                bestPoint = point;
            }
        }
        return bestPoint;
    }

    private static Point getRightMostPoint(List<Point> newPoints) {
        Double x = null;
        Point bestPoint = null;
        for (Point point : newPoints) {
            if (x == null || point.x > x) {
                x = point.x;
                bestPoint = point;
            }
        }
        return bestPoint;
    }

    public static void splitLinesIntoWords(PcGts page) {
        final Integer maxY = page.getPage().getImageHeight();
        final Integer maxX = page.getPage().getImageWidth();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            for (TextLine textLine : textRegion.getTextLines()) {
                TextEquiv textEquiv = textLine.getTextEquiv();
                if (textEquiv != null) {
                    String text = textEquiv.getUnicode();
                    if (Strings.isNullOrEmpty(text)) {
                        text = textEquiv.getPlainText();
                    }
                    if (!Strings.isNullOrEmpty(text) && text.trim().length() > 0) {

                        List<Point> baselinePoints = StringConverter.expandPointList(StringConverter.stringToPoint(textLine.getBaseline().getPoints()));
                        if (baselinePoints.isEmpty()) {
                            LOG.error("Textline with id '" + textLine.getId() + "' has no (valid) baseline.");
                            LOG.error("words: " + text);
                            LOG.error("baselinepoints: " + textLine.getBaseline().getPoints());
                            continue;
                        }

                        textLine.setWords(new ArrayList<>());


                        final double baselineLength = StringConverter.calculateBaselineLength(baselinePoints);
                        double charWidth =0d;
                        String[] splitted = text.split(" ");
                        boolean skipInitialSpace= true;
                        for (final String wordString : splitted) {
                            if (Strings.isNullOrEmpty(wordString)) {
                                continue;
                            }
                            charWidth += wordString.length();
                            if (skipInitialSpace){
                                skipInitialSpace=false;
                                continue;
                            }else{
                                // add a space
                                charWidth++;
                            }
                        }
                        charWidth = baselineLength / charWidth;
                        int nextBaseLinePointIndex = 0;
                        Point firstBaselinePoint = baselinePoints.get(nextBaseLinePointIndex++);
                        Point nextBaselinePoint = firstBaselinePoint;
                        double startX = firstBaselinePoint.x;
                        double startY = firstBaselinePoint.y;
                        // FIXME see TI-541
                        final int magicValueForYHigherThanWord = 35;
                        final int magicValueForYLowerThanWord = 10;

                        for (final String wordString : splitted) {
                            if (Strings.isNullOrEmpty(wordString)) {
                                continue;
                            }
                            Word word = new Word();
                            word.setTextEquiv(new TextEquiv(null, UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii(wordString), wordString));
                            Coords wordCoords = new Coords();
                            List<Point> wordPoints = new ArrayList<>();
                            List<Point> lowerPoints = new ArrayList<>();

                            double charsToAddToBox = wordString.length();
                            while (charsToAddToBox > 0) {
                                final Point startPointOfWord = new Point(startX, startY);
                                boolean isPointBeyond = distance(startPointOfWord, baselinePoints.get(0)) >= distance(nextBaselinePoint, baselinePoints.get(0));
                                boolean moreBaseLinesAvailable = nextBaseLinePointIndex < baselinePoints.size();

                                while (isPointBeyond && moreBaseLinesAvailable) {
                                    nextBaselinePoint = baselinePoints.get(nextBaseLinePointIndex++);
                                    isPointBeyond = distance(startPointOfWord, baselinePoints.get(0)) >= distance(nextBaselinePoint, baselinePoints.get(0));
                                    moreBaseLinesAvailable = nextBaseLinePointIndex < baselinePoints.size();
                                }

                                if (isPointBeyond) {
                                    // If no more points are available and still (parts of) characters need to be in a box, there probably is a rounding problem.
                                    break;
                                }

                                final double distance = distance(new Point(startX, startY), nextBaselinePoint);
                                final double charDistance = (distance / charWidth); // ignore parts of characters
                                final double distanceHorizontal = StringConverter.distanceHorizontal(startPointOfWord, nextBaselinePoint);
                                final double cos = distanceHorizontal / distance;
                                final double distanceVertical = StringConverter.distanceVertical(startPointOfWord, nextBaselinePoint);
                                final double sin = distanceVertical / distance;

                                final double compensatedSpaceAboveBaselineY = magicValueForYHigherThanWord * cos;
                                final double compensatedSpaceBelowBaselineY = magicValueForYLowerThanWord * cos;
                                final double compensatedSpaceAboveBaselineX = magicValueForYHigherThanWord * sin;
                                final double compensatedSpaceBelowBaselineX = magicValueForYLowerThanWord * sin;

                                if (wordPoints.isEmpty()) {
                                    wordPoints.add(new Point(Math.min(maxX, Math.max(0, startX + compensatedSpaceAboveBaselineX)), Math.max(0, startY - compensatedSpaceAboveBaselineY)));
                                    lowerPoints.add(new Point(Math.min(maxX, Math.max(0, startX - compensatedSpaceBelowBaselineX)), Math.min(maxY, startY + compensatedSpaceBelowBaselineY)));
                                }

                                if (charDistance > charsToAddToBox) {
                                    final double wordLength = charsToAddToBox * charWidth;
                                    final double distanceHorizontalWord = wordLength * cos;
                                    final double distanceVerticalWord = wordLength * sin;

                                    startX += distanceHorizontalWord;
                                    startY += distanceVerticalWord;
                                    wordPoints.add(new Point(Math.min(maxX, Math.max(0, startX + compensatedSpaceAboveBaselineX)), Math.max(0, startY - compensatedSpaceAboveBaselineY)));
                                    lowerPoints.add(new Point(Math.min(maxX, Math.max(0, startX - compensatedSpaceBelowBaselineX)), Math.min(maxY, startY + compensatedSpaceBelowBaselineY)));

                                    startX += (charWidth * cos); // add space
                                    startY += (charWidth * sin); // add space
                                    charsToAddToBox = 0;

                                } else if (charDistance <= charsToAddToBox) {
                                    startX = nextBaselinePoint.x;
                                    startY = nextBaselinePoint.y;
                                    wordPoints.add(new Point(Math.min(maxX, Math.max(0, startX + compensatedSpaceAboveBaselineX)), Math.max(0, startY - compensatedSpaceAboveBaselineY)));
                                    lowerPoints.add(new Point(Math.min(maxX, Math.max(0, startX - compensatedSpaceBelowBaselineX)), Math.min(maxY, startY + compensatedSpaceBelowBaselineY)));
                                    charsToAddToBox -= charDistance;
                                }
                            }

                            // FIXME hack to make sure the points are in the right order
                            wordPoints.sort(Comparator.comparingDouble(point -> point.x));
                            lowerPoints.sort(Comparator.comparingDouble(point -> point.x));
                            Collections.reverse(lowerPoints);
                            wordPoints.addAll(lowerPoints);
                            wordCoords.setPoints(StringConverter.pointToString(wordPoints));
                            word.setCoords(wordCoords);

                            if (StringUtils.isBlank(wordCoords.getPoints())) {
                                LOG.error("Word '" + wordString + "' of line '" + text + "' has no coords. Word will be ignored.");
                                continue;
                            }

                            textLine.getWords().add(word);
                        }
                    }
                }
            }
        }

        page.getMetadata().setLastChange(new Date());
        if (page.getMetadata().getMetadataItems() == null) {
            page.getMetadata().setMetadataItems(new ArrayList<>());
        }
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("word-splitting");
        metadataItem.setValue("loghi-htr-tooling");

        page.getMetadata().getMetadataItems().add(metadataItem);
    }

    public static double getDistance(Point last, Point first) {
        return Math.sqrt(Math.pow(last.x - first.x, 2) + Math.pow(last.y - first.y, 2));
    }

    public static double getLength(List<Point> points) {
        if (points.size() < 2) {
            return 0;
        }
        double length = 0;
        Point lastPoint = null;
        for (Point point : points) {
            if (lastPoint == null) {
                lastPoint = point;
                continue;
            }
            length += getDistance(lastPoint, point);
            lastPoint = point;
        }
        return length;
    }

}