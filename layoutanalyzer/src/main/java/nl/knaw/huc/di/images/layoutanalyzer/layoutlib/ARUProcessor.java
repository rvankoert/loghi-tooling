//package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;
//
//import com.google.common.base.Stopwatch;
//import nl.knaw.huc.di.images.icotesseract.Tess4JTest;
//import nl.knaw.huc.di.images.imageanalysiscommon.connectedComponent.ConnectedComponentProc;
//import nl.knaw.huc.di.images.imageanalysiscommon.imageConversion.ImageConversionHelper;
//import nl.knaw.huc.di.images.imageanalysiscommon.visualization.VisualizationHelper;
//import nl.knaw.huc.di.images.layoutanalyzer.Statistics;
//import nl.knaw.huc.di.images.layoutds.models.*;
//import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
//import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
//import nl.knaw.huc.di.images.layoutds.models.connectedComponent.DicoveredLabel;
//import nl.knaw.huc.di.images.stringtools.StringTools;
//import org.opencv.core.Point;
//import org.opencv.core.*;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//import java.util.List;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//import static org.opencv.core.CvType.CV_32FC3;
//
//public class ARUProcessor {
//
//    private static Tess4JTest _tess4jTest = null;
//
//    private static ArrayList<Point> getTopLine(byte[] bytes, int width, List<Point> baseline, int minMarginAbove) {
//        System.out.println("getTopLine");
//        ArrayList<Point> topline = new ArrayList<>();
//        for (Point point : baseline) {
//            int x = (int) point.x;
//            int y = (int) point.y;
//
//            int marginAbove = 0;
//            int i = y - minMarginAbove;
//            if (i < 0) {
//                i = 0;
//            }
//            while (i > 0) {
//                if (bytes[i * width + x] > 0.0) {
//                    i--;
//                } else {
//                    break;
//                }
//            }
//            while (i > 0) {
//                if (bytes[i * width + x] > 0.0) {
//                    break;
//                }
//                marginAbove = y - i;
//                i--;
//            }
//            if (marginAbove < minMarginAbove) {
//                marginAbove = minMarginAbove;
//            }
//            Point topPoint = new Point(x, y - marginAbove);
//            topline.add(topPoint);
//        }
//        System.out.println("getTopLine done");
//        return topline;
//    }
//
//    private static ArrayList<Point> getBottomLine(Mat binaryBaseLines, List<Point> baseline) {
//        int minMarginBelow = Integer.MIN_VALUE;
//        ArrayList<Point> bottomline = new ArrayList<>();
//        for (Point point : baseline) {
//            int marginBelow = 0;
//            int i = (int) point.y + 5;
//            if (i >= binaryBaseLines.height()) {
//                i = binaryBaseLines.height() - 1;
//            }
//            while (i < binaryBaseLines.height() && binaryBaseLines.get(i, (int) point.x)[0] > 0.0) {
//                i++;
//            }
//            while (i < binaryBaseLines.height()) {
//                if (binaryBaseLines.get(i, (int) point.x)[0] > 0.0) {
//                    break;
//                }
//                marginBelow = i - (int) point.y;
//                i++;
//            }
//
//            if (marginBelow > minMarginBelow) {
//                minMarginBelow = marginBelow;
//            }
//            Point bottomPoint = new Point(point.x, point.y + marginBelow);
//            bottomline.add(bottomPoint);
//        }
//        return bottomline;
//
//    }
//
//    private static double getMedianHeight(Mat baseLineMat, Mat inputOrigMatBinary, int smoothfactor, List<Point> baseline, int stride, int range, int multiplier) throws Exception {
//        System.out.println("getting median height. stride:" + stride + " range:" + range + " multiplier:" + multiplier);
//        if (stride <= 0) {
//            throw new Exception("stride should be >= 1");
//        }
//
//        ArrayList<Double> heights = new ArrayList<>();
//        for (int i = 0; i < baseline.size() - 1; i += stride) {
//
//            int startY = (int) Math.round(baseline.get(i).y) - range / 2;
//            if (startY < 0) {
//                startY = 0;
//            }
//            int stopY = (int) Math.round(baseline.get(i).y) + range / 2;
//            if (stopY >= baseLineMat.height()) {
//                stopY = baseLineMat.height() - 1;
//            }
//            int xStart = (int) Math.round(baseline.get(i).x);
//            int xStop = (int) (baseline.get(i).x) + range * multiplier;
//
//            if (xStop >= baseLineMat.width()) {
//                xStop = baseLineMat.width() - 1;
//            }
//
//            if (xStart >= xStop) {
//                continue;
//            }
//            System.out.println("baseLineMat.height() " + baseLineMat.height() + " xStart " + xStart + " xStop " + xStop);
//            List<Integer> profile = LayoutProc.horizontalProfileByte(inputOrigMatBinary.submat(0, baseLineMat.height(), xStart, xStop), startY, stopY);
//            List<Double> profileSmooth = LayoutProc.smoothList(profile, smoothfactor);
//
//            int valleyAbove;
//            int valleyBelow;
//            int j = profileSmooth.size() / 2;
//            double lastValue = Double.MIN_VALUE;
//            while (j < profileSmooth.size() && profileSmooth.get(j) >= lastValue) {
//                lastValue = profileSmooth.get(j);
//                j++;
//            }
//            while (j < profileSmooth.size() && profileSmooth.get(j) < lastValue) {
//                lastValue = profileSmooth.get(j);
//                j++;
//            }
//            valleyBelow = j;
//
//            j = profileSmooth.size() / 2;
//            lastValue = Double.MIN_VALUE;
//            while (j > 0 && profileSmooth.get(j) >= lastValue) {
//                lastValue = profileSmooth.get(j);
//                j--;
//            }
//            while (j > 0 && profileSmooth.get(j) < lastValue) {
//                lastValue = profileSmooth.get(j);
//                j--;
//            }
//            valleyAbove = j;
//            int height = valleyBelow - valleyAbove;
//            heights.add((double) height);
//        }
//        return new Statistics(heights, 0, heights.size()).median();
//    }
//
//    private static ArrayList<Point> getBaseLine(ConnectedComponent lineCoco, int stride) {
//
//        int leftX = lineCoco.getX();
//        int rightX = lineCoco.getX() + lineCoco.getWidth() - 1;
//
//        ArrayList<Point> baseline = new ArrayList<>();
//        Mat image = ImageConversionHelper.bufferedImageToBinaryMat(lineCoco.getBitMap());
//
//        for (int i = 0; i < image.width(); i += stride) {
//            int leftYTop = 0;
//            int leftYBottom = image.height() - 1;
//            for (int j = 0; j < image.height(); j++) {
//                double value = image.get(j, i)[0];
//                if (value > 0.0) {
//                    leftYTop = j;
//                    break;
//                }
//            }
//            for (int j = image.height() - 1; j > 0; j--) {
//                double value = image.get(j, i)[0];
//                if (value > 0.0) {
//                    leftYBottom = j;
//                    break;
//                }
//            }
//            int leftY = (leftYBottom + leftYTop) / 2;
//            leftY += lineCoco.getY();
//
//            Point left = new Point(leftX + i, leftY);
//
//            baseline.add(left);
//        }
//
//
//        int rightYTop = 0;
//        int rightYBottom = image.height() - 1;
//        for (int i = 0; i < image.height(); i++) {
//            double value = image.get(i, image.width() - 1)[0];
//            if (value > 0.0) {
//                rightYTop = i;
//                break;
//            }
//        }
//
//        for (int i = image.height() - 1; i > 0; i--) {
//            double value = image.get(i, image.width() - 1)[0];
//            if (value > 0.0) {
//                rightYBottom = i;
//                break;
//            }
//        }
//        int rightY = (rightYBottom + rightYTop) / 2;
//        rightY += lineCoco.getY();
//        Point right = new Point(rightX, rightY);
//        image.release();
//        baseline.add(right);
//        return smoothLine(baseline);
//    }
//
//    private static ArrayList<LineDescriptor> getLineDescriptors(Mat binaryBaseLineMat, Mat inputOrigMatBinary) throws Exception {
//        return getLineDescriptors(binaryBaseLineMat, inputOrigMatBinary, 1);
//    }
//
//    public static List<ArrayList<Point>> getBaseLines(Mat binaryBaseLineMat) throws Exception {
//        return getBaseLines(binaryBaseLineMat, 10, 1);
//    }
//
//    private static List<ArrayList<Point>> getBaseLines(Mat binaryBaseLineMat, int minimumWidth, int stride) throws Exception {
//        ConnectedComponentProc coCoProc = new ConnectedComponentProc();
//        System.out.println("extracting cocos baselines");
//        BufferedImage binImageBaseLines = ImageConversionHelper.matToBufferedImage(binaryBaseLineMat);
//        List<ConnectedComponent> cocoBaselines = coCoProc.process(binImageBaseLines, false);
//        System.out.println("extracting cocos baselinesdone");
//        List<ConnectedComponent> cleanBaseLineCocos = new ArrayList<>();
//        List<ArrayList<Point>> baseLines = new ArrayList<>();
//        for (ConnectedComponent baseline : cocoBaselines) {
//            if (baseline.getWidth() > minimumWidth) {
//                cleanBaseLineCocos.add(baseline);
//                ArrayList<Point> baseLine = getBaseLine(baseline, stride);
//                baseLines.add(baseLine);
//            }
//        }
//
//        return baseLines;
//    }
//
//    private static ArrayList<LineDescriptor> getLineDescriptors(Mat binaryBaseLineMat, Mat inputOrigMatBinary, int stride) throws Exception {
//        ConnectedComponentProc coCoProc = new ConnectedComponentProc();
//        BufferedImage binImageBaseLines = ImageConversionHelper.matToBufferedImage(binaryBaseLineMat);
//        System.out.println("extracting cocos linedescriptors");
//        List<ConnectedComponent> cocos = coCoProc.process(ImageConversionHelper.matToBufferedImage(inputOrigMatBinary), false);
//        System.out.println("extracting cocos linedescriptors done");
//
//        ArrayList<LineDescriptor> lineDescriptors = new ArrayList<>();
//        List<ArrayList<Point>> cleanBaseLineCocos = getBaseLines(binaryBaseLineMat);
//
//        int height = binaryBaseLineMat.height();
//        int width = binaryBaseLineMat.width();
//        byte[] bytes = new byte[height * width];
//        binaryBaseLineMat.get(0, 0, bytes);
//
//        for (ArrayList<Point> baseLine : cleanBaseLineCocos) {
//            LineDescriptor lineDescriptor = new LineDescriptor();
////            ArrayList<Point> baseLine = getBaseLine(baselineCoco, stride);
//
//            ArrayList<Point> topline = getTopLine(bytes, width, baseLine, Integer.MAX_VALUE);
//
//            ArrayList<Point> bottomline = getBottomLine(binaryBaseLineMat, baseLine);
//
////            lineDescriptor.setLineCoco(baselineCoco);
//
//            ArrayList<Point> compressedBaseLine = compressLine(baseLine);
//
//            lineDescriptor.setCompressedBaseline(compressedBaseLine);
//            lineDescriptor.setTopLine(topline);
//            lineDescriptor.setBottomLine(bottomline);
//
//
//            List<Double> topDiff = new ArrayList<>();
//            List<Double> bottomDiff = new ArrayList<>();
//            for (int i = 0; i < baseLine.size(); i++) {
//                topDiff.add(baseLine.get(i).y - topline.get(i).y);
//                bottomDiff.add(bottomline.get(i).y - baseLine.get(i).y);
//            }
//            Statistics topStatistics = new Statistics(topDiff, 0, baseLine.size() - 1);
//            Statistics bottomStatistics = new Statistics(bottomDiff, 0, baseLine.size() - 1);
//            double topMedian = topStatistics.median();
//            double bottomMedian = bottomStatistics.median();
//
//            int range = (int) (Math.min(topMedian, bottomMedian) * 1.5);
//            if (range <= 20) {
//                range = (int) (Math.max(topMedian, bottomMedian) * 1.5);
//            }
//            if (range < 20) {
//                range = 20;
//            }
//            double medianHeight = getMedianHeight(binaryBaseLineMat, inputOrigMatBinary, range / 10, baseLine, range / 2, (int) (range * 1.5), 5);
//
//            lineDescriptor.setMedianHeight((int) medianHeight);
//
//            int minMarginBelow = getMarginBelow(lineDescriptor.getBaseline(), height, width, bytes);
//            int minMarginAbove = getMarginAbove(lineDescriptor.getBaseline(), width, bytes);
//            lineDescriptor.setMinMarginBelow(minMarginBelow);
//            lineDescriptor.setMinMarginAbove(minMarginAbove);
//            lineDescriptors.add(lineDescriptor);
//        }
//
//        System.out.println("determine xheight");
//        System.out.println("cocos: " + cocos.size());
//
//        // determine xheight
//        List<ConnectedComponent> explainedCocos = new ArrayList<>();
//        for (LineDescriptor lineDescriptor : lineDescriptors) {
//            System.out.println("determine xheight for linedescriptor");
//            int lineCocoWidth = lineDescriptor.getWidth();
//            for (ConnectedComponent inputCoco : cocos) {
//                int inputCocoWidth = inputCoco.getWidth();
//                int inputCocoHeight = inputCoco.getHeight();
//                int inputCocoX = inputCoco.getX();
//                int inputCocoY = inputCoco.getY();
//                if (inputCocoY < lineDescriptor.getCentery() && //above or crossing baseline
//                        (inputCocoX + inputCocoWidth / 2) > lineDescriptor.getLeftX() &&   // center above baseline
//                        (inputCocoX + inputCocoWidth / 2) < lineDescriptor.getRightX()// center above baseline
//                ) {
//                    int minMarginAbove = lineDescriptor.getMinMarginAbove();
//                    int minMarginBelow = lineDescriptor.getMinMarginBelow();
//                    boolean ok = false;
//                    for (int i = 0; i < lineDescriptor.getTopLine().size() - 1; i++) {
//                        Point topPoint = lineDescriptor.getTopLine().get(i);
//                        Point nextTopPoint = lineDescriptor.getTopLine().get(i + 1);
//                        if (inputCocoX + inputCocoWidth / 2.0 >= topPoint.x && inputCocoX + inputCocoWidth / 2.0 < nextTopPoint.x) {
//                            if (inputCocoY + inputCocoHeight / 2.0 > topPoint.y + minMarginAbove / 2.0) {
//                                ok = true;
//                            } else {
//                                ok = false;
//                                break;
//                            }
//                        }
//                    }
//                    if (ok) {
//                        for (int i = 0; i < lineDescriptor.getBaseline().size() - 1; i++) {
//                            Point baselinePoint = lineDescriptor.getBaseline().get(i);
//                            Point nextBaselinePoint = lineDescriptor.getBaseline().get(i + 1);
//                            if (inputCocoX + inputCocoWidth / 2.0 >= baselinePoint.x && inputCocoX + inputCocoWidth / 2.0 < nextBaselinePoint.x) {
//                                if (inputCocoY + inputCocoHeight / 2.0 > baselinePoint.y + minMarginBelow / 2.0) {
//                                    ok = false;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    if (ok) {
//                        if (inputCoco.getParent() == null) {
//                            explainedCocos.add(inputCoco);
//                            inputCoco.setParent(lineDescriptor);
//                        }
//                    }
//                }
//            }
//            List<Double> heights = new ArrayList<>();
//            for (ConnectedComponent coco : explainedCocos) {
//                if (coco.getHeight() >= 10) { // if it's less than 10 it is not readable anyway
//                    heights.add((double) coco.getHeight());
//                }
//            }
//            if (heights.size() > 1) {
//                Statistics heightStats = new Statistics(heights, 0, heights.size() - 1);
//                lineDescriptor.setXHeight((int) heightStats.median());
//                ArrayList<Point> topline = getTopLine(bytes, width, lineDescriptor.getBaseline(), lineDescriptor.getXHeight());
//                lineDescriptor.setTopLine(topline);
//            }
//        }
//        System.out.println("returning linedescriptors");
//        return lineDescriptors;
//    }
//
//    public static ArrayList<Point> compressLine(ArrayList<Point> line) {
//        ArrayList<Point> returnPoints = new ArrayList<>();
//
//        for (int i = 0; i < line.size(); i++) {
//            if (i == 0) {
//                returnPoints.add(line.get(i));
//            } else if (i == line.size() - 1) {
//                returnPoints.add(line.get(i));
//            } else if (i % 20 == 0) {
//                returnPoints.add(line.get(i));
//            }
//        }
//        return returnPoints;
//
//    }
//
//    private static int getMarginBelow(List<Point> baseline, int height, int width, byte[] bytes) {
//        System.out.println("getMarginBelow");
//
//        int minMarginBelow = Integer.MIN_VALUE;
//        for (Point point : baseline) {
//            int x = (int) point.x;
//            int y = (int) point.y;
//            int marginBelow = 0;
//            int i = y + 5;
//            if (i >= height) {
//                i = height - 1;
//            }
//            while (i < height && bytes[width * i + x] > 0.0) {
//                i++;
//            }
//            while (i < height) {
//                if (bytes[width * i + x] > 0.0) {
//                    break;
//                }
//                marginBelow = i - y;
//                i++;
//            }
//
//            if (marginBelow > minMarginBelow) {
//                minMarginBelow = marginBelow;
//            }
//        }
//        System.out.println("getMarginBelow done");
//        return minMarginBelow;
//    }
//
//    private static int getMarginAbove(List<Point> baseline, int width, byte[] bytes) {
//        System.out.println("getMarginAbove ");
//
//        int minMarginAbove = Integer.MAX_VALUE;
//        for (Point point : baseline) {
//            int x = (int) point.x;
//            int y = (int) point.y;
//            int marginAbove = 0;
//            int i = y - 5;
//            if (i < 0) {
//                i = 0;
//            }
//            while (i > 0) {
//                if (bytes[width * i + x] > 0.0) {
//                    i--;
//                } else {
//                    break;
//                }
//            }
//            while (i > 0) {
//                if (bytes[width * i + x] > 0.0) {
//                    break;
//                }
//                marginAbove = y - i;
//                i--;
//            }
//            if (marginAbove < minMarginAbove) {
//                minMarginAbove = marginAbove;
//            }
//        }
//        System.out.println("getMarginAbove Done ");
//        return minMarginAbove;
//    }
//
//    private static void drawBaseLine(Mat colorized, LineDescriptor lineDescriptor) {
//        Point start = lineDescriptor.getBaseline().get(0);
//        for (int i = 1; i < lineDescriptor.getBaseline().size(); i++) {
//            Point stop = lineDescriptor.getBaseline().get(i);
//            Imgproc.line(colorized, start, stop, new Scalar(127, 127, 127));
//            start = stop;
//        }
//    }
//
//    private static void drawBaseLines(Mat colorized, List<LineDescriptor> lines) {
//        for (LineDescriptor lineDescriptor : lines) {
//            drawBaseLine(colorized, lineDescriptor);
//        }
//    }
//
//
//    private static double getSlope(ArrayList<Point> line) {
//        double dx = line.get(line.size() - 1).x - line.get(0).x;
//        double dy = line.get(line.size() - 1).y - line.get(0).y;
//        double theta = Math.atan2(dy, dx);
//        return theta * 180 / Math.PI;
//    }
//
//
////    public static ArrayList<LineDescriptor> extractTextLinesNew2(Mat baseLineMat, Mat inputOrigMat, int documentImageId, String baseOutput, String tag, String uri, int minWidth, int minHeight, boolean writeOut, boolean useXHeight, Mat colorized)  {
////        // take the area between two baselines.
////        // label pixels as belonging to line A
////        // label pixels as belonging to line B
////        // label overlap as overlap
////        // cut overlap via cutting path
////
////        return null;
////    }
////
////    public static ArrayList<LineDescriptor> extractTextLinesNew3(Mat baseLineMat, Mat inputOrigMat, int documentImageId, String baseOutput, String tag, String uri, int minWidth, int minHeight, boolean writeOut, boolean useXHeight, Mat colorized)  {
////        // create mountains/valleys based on baselines and inkdistribution
////        // cut least energy paths
////        return null;
////    }
//
//    public static ArrayList<LineDescriptor> extractTextLinesNew(Mat baseLineMat, Mat inputOrigMat, int documentImageId, String baseOutput, String tag, String uri, int minWidth, int minHeight, boolean writeOut, boolean useXHeight, Mat colorized) throws Exception {
//        ArrayList<LineDescriptor> linedescriptors = extractTextLines(baseLineMat, inputOrigMat, documentImageId, baseOutput, tag, uri, minWidth, minHeight, writeOut, useXHeight, colorized);
//
//        Mat blurred = new Mat();
//        Imgproc.GaussianBlur(inputOrigMat, blurred, new Size(11, 11), 101);
//        Imgcodecs.imwrite("/tmp/blurred.png", blurred);
//
//        for (LineDescriptor lineDescriptor : linedescriptors) {
//            ArrayList<Point> baseline = lineDescriptor.getBaseline();
//            System.out.println("Slope: " + getSlope(baseline));
//
//            ArrayList<Point> cuttingPathGuider = new ArrayList<>();
////            cuttingPathGuider = lineDescriptor.getBaseline()-lineDescriptor.getXHeight() - lineAbove.baseline()
//
//            int i = 0;
//            for (Point point : lineDescriptor.getBaseline()) {
//                // get orientation
//                double x = point.x;
//                double y = ((point.y - lineDescriptor.getXHeight()) + lineDescriptor.getTopLine().get(i).y) / 2;//-lineDescriptor.getXHeight();
//                if (y < 0) {
//                    y = 0;
//                }
//                cuttingPathGuider.add(new Point(x, y));
//
//                i++;
//            }
//
//            DropletNew droplet = new DropletNew(blurred, cuttingPathGuider);
//            ArrayList<Point> cuttingList = droplet.getPointList();
//            Point start = cuttingPathGuider.get(0);
//            for (int j = 1; j < cuttingPathGuider.size(); j++) {
//                Point stop = cuttingPathGuider.get(j);
//                Imgproc.line(inputOrigMat, start, stop, new Scalar(127));
//                start = stop;
//            }
//            start = cuttingList.get(0);
//            for (int j = 1; j < cuttingList.size(); j++) {
//                Point stop = cuttingList.get(j);
//                Imgproc.line(inputOrigMat, start, stop, new Scalar(255));
//                start = stop;
//            }
//        }
//        return linedescriptors;
//    }
//
//    public static ArrayList<LineDescriptor> extractTextLines(Mat baseLineMat, Mat inputOrigMat, int documentImageId, String baseOutput, String tag, String uri, int minWidth, int minHeight, boolean writeOut, boolean useXHeight, Mat colorized) throws Exception {
//        Mat inputOrigMatBinary = new Mat();
//        Imgproc.resize(baseLineMat, baseLineMat, new Size(inputOrigMat.width(), inputOrigMat.height()));
//        Imgproc.adaptiveThreshold(inputOrigMat, inputOrigMatBinary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 155, 15);//15);
//
//        Mat binaryBaseLineMat = new Mat();
//        Core.bitwise_not(baseLineMat, baseLineMat);
//        Imgproc.threshold(baseLineMat, binaryBaseLineMat, 0.95 * 255, 255, Imgproc.THRESH_BINARY_INV);
//
//        StringBuilder allFiles = new StringBuilder();
//        ArrayList<LineDescriptor> lines = getLineDescriptors(binaryBaseLineMat, inputOrigMatBinary, 1);
//        if (colorized != null) {
//            drawBaseLines(colorized, lines);
//        }
//        ConnectedComponentProc coCoProc = new ConnectedComponentProc();
//        System.out.println("extracting cocos");
//
//        List<ConnectedComponent> cocos = coCoProc.process(ImageConversionHelper.matToBufferedImage(inputOrigMatBinary), false);
//        System.out.println("extracting cocos done");
//
//        ArrayList<ConnectedComponent> explainedCocos = new ArrayList<>();
//        for (int lineNo = lines.size() - 1; lineNo >= 0; lineNo--) {
//            System.out.println("processing line " + lineNo + "/" + lines.size());
//            LineDescriptor lineDescriptor = lines.get(lineNo);
//            if (lineDescriptor.getWidth() < minWidth || lineDescriptor.getMedianHeight() < minHeight) {
//                lines.remove(lineDescriptor);
//                continue;
//            }
//            int margin;
//            if (useXHeight) {
//                margin = lineDescriptor.getXHeight() * 2;
//            } else {
//                margin = lineDescriptor.getMedianHeight();
//            }
//
//            if (writeOut) {
//                String filename = extractTextLine(inputOrigMat, lineDescriptor.getBaseline(), margin, documentImageId, baseOutput);
//                allFiles.append(filename).append(" ").append(tag).append(" ").append(0).append(" ").append(uri).append("\n");
//            }
//
//            for (ConnectedComponent inputCoco : cocos) {
//                int inputCocoWidth = inputCoco.getWidth();
//                int inputCocoHeight = inputCoco.getHeight();
//                int inputCocoX = inputCoco.getX();
//                int inputCocoY = inputCoco.getY();
//                if (inputCocoY < lineDescriptor.getCentery() && //above or crossing baseline
//                        (inputCocoX + inputCocoWidth / 2) > lineDescriptor.getLeftX() &&   // center above baseline
//                        (inputCocoX + inputCocoWidth / 2) < lineDescriptor.getRightX()// center above baseline
//                ) {
//                    boolean ok = false;
//                    for (int i = 0; i < lineDescriptor.getTopLine().size() - 1; i++) {
//                        Point topPoint = lineDescriptor.getTopLine().get(i);
//                        Point nextTopPoint = lineDescriptor.getTopLine().get(i + 1);
//                        if (inputCocoX + inputCocoWidth / 2.0 >= topPoint.x && inputCocoX + inputCocoWidth / 2.0 < nextTopPoint.x) {
////                            System.out.println ("topline: " +lineDescriptor.getTopLine().size());
////                            System.out.println ("baseline: " +lineDescriptor.getBaseline().size());
//                            if (inputCocoY + inputCocoHeight / 2.0 > lineDescriptor.getBaseline().get(i).y - margin / 2.0) {
//                                ok = true;
//                            } else {
//                                ok = false;
//                                break;
//                            }
//                        }
//                    }
//                    if (ok) {
//                        for (int i = 0; i < lineDescriptor.getBaseline().size() - 1; i++) {
//                            Point baselinePoint = lineDescriptor.getBaseline().get(i);
//                            Point nextBaselinePoint = lineDescriptor.getBaseline().get(i + 1);
//                            if (inputCocoX + inputCocoWidth / 2.0 >= baselinePoint.x && inputCocoX + inputCocoWidth / 2.0 < nextBaselinePoint.x) {
//                                if (inputCocoY + inputCocoHeight / 2.0 > baselinePoint.y + margin / 2.0) {
//                                    ok = false;
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    if (ok) {
//                        if (inputCoco.getParent() == null) {
//                            explainedCocos.add(inputCoco);
//                            inputCoco.setParent(lineDescriptor);
////                            inputCoco.setColor(lineCoco.getColor());
//                            if (colorized != null) {
//                                VisualizationHelper.colorize(colorized, inputCoco);
//                            }
//                        } else {
//                            if (colorized != null) {
//                                inputCoco.setColor(Color.white);
//                                VisualizationHelper.colorize(colorized, inputCoco);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (writeOut) {
//            StringTools.writeFile(baseOutput + documentImageId + "/files-new.txt", allFiles.toString());
//        }
//
//        cocos.removeAll(explainedCocos);
//
//        Stopwatch totalTime = Stopwatch.createStarted();
//        if (colorized != null) {
//            colorizeRemainingCoCosWhite(colorized, cocos);
//            Imgcodecs.imwrite("/tmp/colorized.png", colorized);
//        }
//        System.out.println("creating colored stuff: " + totalTime.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");
//        binaryBaseLineMat.release();
//
//        return lines;
//    }
//
//    private static boolean isHeader(LineDescriptor lineDescriptor) {
//        if (lineDescriptor.getLinesAbove().size() > 0) {
//            return false;
//        }
//        if (lineDescriptor.getLinesAboveRelaxed().size() > 0) {
//            return false;
//        }
//        if (lineDescriptor.getLinesBelow().size() > 0) {
//            ArrayList<LineDescriptor> linesBelow = lineDescriptor.getLinesBelow();
//            linesBelow.sort(Comparator.comparing(LineDescriptor::getCentery));
//            return !(overlap(lineDescriptor, linesBelow.get(0)) > 0.9);
//        }
//        return false;
//    }
//
//    private static boolean isFooter(LineDescriptor lineDescriptor) {
//        if (lineDescriptor.getLinesBelow().size() > 0) {
//            return false;
//        }
//        if (lineDescriptor.getLinesBelowRelaxed().size() > 0) {
//            return false;
//        }
//        if (lineDescriptor.getLinesAbove().size() > 0) {
//            ArrayList<LineDescriptor> lineAbove = lineDescriptor.getLinesAbove();
//            lineAbove.sort(Comparator.comparing(LineDescriptor::getCentery));
//            Collections.reverse(lineAbove);
//            return !(overlap(lineDescriptor, lineAbove.get(0)) > 0.9);
//        }
//        return false;
//    }
//
//
//    private static void colorizeRemainingCoCosWhite(Mat colorized, List<ConnectedComponent> cocos) {
//        for (ConnectedComponent inputCoco : cocos) {
//            inputCoco.setColor(Color.white);
//            VisualizationHelper.colorize(colorized, inputCoco);
//        }
//    }
//
//    private static String extractTextLine(Mat input, List<Point> baseline, int margin, int documentImageId, String outputDir) {
//
//        int width = (int) (1 + Math.round(baseline.get(baseline.size() - 1).x) - Math.round(baseline.get(0).x));
//        Mat output = Mat.zeros(margin, width, input.type());
//        for (int i = 0; i < baseline.size() - 1; i++) {
//            Point base = baseline.get(i);
//            int rowStart = (int) Math.round(base.y - 0.75 * margin);
//            if (rowStart < 0) {
//                rowStart = 0;
//            }
//            int rowStop = (int) Math.round(base.y + 0.25 * margin);
//            if (rowStop >= input.height()) {
//                rowStop = input.height() - 1;
//            }
//            Mat old = input.submat(rowStart, rowStop, (int) Math.round(base.x), (int) Math.round(base.x) + 1);
//            Mat target = output.submat(0, margin, i, i + 1);
//            old.copyTo(target);
//        }
//
//
//        double yStart = Double.MAX_VALUE;
//        for (Point top : baseline) {
//            if (top.y < yStart) {
//                yStart = top.y;
//            }
//        }
//        yStart -= 0.75 * margin;
//        if (yStart < 0) {
//            yStart = 0;
//        }
//        double yStop = Double.MIN_VALUE;
//        for (Point top : baseline) {
//            if (top.y > yStop) {
//                yStop = top.y;
//            }
//        }
//        yStop += 0.25 * margin;
//
//        if (yStop >= input.height()) {
//            yStop = input.height() - 1;
//        }
//        int xStart = (int) baseline.get(0).x;
//        int xStop = (int) baseline.get(baseline.size() - 1).x;
//        String filename = outputDir +
//                documentImageId +
//                "/textline-" +
//                documentImageId + "-" +
//                xStart + "-" +
//                (int) yStart + "-" +
//                xStop + "-" +
//                (int) yStop + "-" +
//                ".png";
//        Imgcodecs.imwrite(filename, output);
//        output.release();
//        return filename;
//    }
//
//    private static double overlap(LineDescriptor top, LineDescriptor bottom) {
//        double left = Math.max(top.getLeftX(), bottom.getLeftX());
//        double right = Math.min(top.getRightX(), bottom.getRightX());
//
//        return (2.0 * (right - left)) / (top.getWidth() + bottom.getWidth());
//    }
//
//    private static double overlapSingle(LineDescriptor top, LineDescriptor bottom) {
//        double left = Math.max(top.getLeftX(), bottom.getLeftX());
//        double right = Math.min(top.getRightX(), bottom.getRightX());
//
//        return (right - left) / Math.min(top.getWidth(), bottom.getWidth());
//    }
//
//    private static void connectLines(List<LineDescriptor> lines) {
//        lines.sort(Comparator.comparing(LineDescriptor::getCentery));
//        int counter = 0;
//        for (LineDescriptor lineDescriptor : lines) {
//            counter++;
//            for (int i = counter - 2; i >= 0; i--) {
//                if (lineDescriptor.getCenterX() > lines.get(i).getLeftX() &&
//                        lineDescriptor.getCenterX() < lines.get(i).getRightX()) {
//                    lineDescriptor.addLineAbove(lines.get(i));
//                    if (!lines.get(i).getLinesBelow().contains(lineDescriptor)) {
//                        lines.get(i).addLineBelow(lineDescriptor);
//                    }
//                }
//            }
//
//            for (int i = counter; i < lines.size(); i++) {
//                if (lineDescriptor.getCenterX() > lines.get(i).getLeftX() &&
//                        lineDescriptor.getCenterX() < lines.get(i).getRightX()) {
//                    lineDescriptor.addLineBelow(lines.get(i));
//                    if (!lines.get(i).getLinesAbove().contains(lineDescriptor)) {
//                        lines.get(i).addLineAbove(lineDescriptor);
//                    }
//                }
//            }
//
//            for (int i = counter - 2; i >= 0; i--) {
//                if (lineDescriptor.getRightX() > lines.get(i).getLeftX() &&
//                        lineDescriptor.getLeftX() < lines.get(i).getRightX()) {
//                    lineDescriptor.addLineAboveRelaxed(lines.get(i));
//                    if (!lines.get(i).getLinesBelowRelaxed().contains(lineDescriptor)) {
//                        lines.get(i).addLineBelowRelaxed(lineDescriptor);
//                    }
//                }
//            }
//
//            for (int i = counter; i < lines.size(); i++) {
//                if (lineDescriptor.getRightX() > lines.get(i).getLeftX() &&
//                        lineDescriptor.getLeftX() < lines.get(i).getRightX()) {
//                    lineDescriptor.addLineBelowRelaxed(lines.get(i));
//                    if (!lines.get(i).getLinesAboveRelaxed().contains(lineDescriptor)) {
//                        lines.get(i).addLineAboveRelaxed(lineDescriptor);
//                    }
//                }
//            }
//        }
//    }
//
//
//    private static void labelLineDescriptors(List<LineDescriptor> lines) {
//        for (LineDescriptor lineDescriptor : lines) {
//            if (isHeader(lineDescriptor)) {
//                lineDescriptor.setDiscoveredLabel(DicoveredLabel.PageHeader);
//            }
//
//            if (isFooter(lineDescriptor)) {
//                lineDescriptor.setDiscoveredLabel(DicoveredLabel.PageFooter);
//            }
//
//            if (lineDescriptor.getDiscoveredLabel() == DicoveredLabel.None) {
//                lineDescriptor.setDiscoveredLabel(DicoveredLabel.TextLine);
//            }
//        }
//    }
//
//    private static Double interlineDistance(ArrayList<LineDescriptor> inputLines) {
//        ArrayList<LineDescriptor> lines = (ArrayList<LineDescriptor>) inputLines.clone();
//        lines.sort(Comparator.comparing(LineDescriptor::getCentery));
//        ArrayList<Double> interlineDistances = new ArrayList<>();
//        for (LineDescriptor lineDescriptor : lines) {
//            Double distance = null;
//            for (LineDescriptor subline : lines) {
//                if (subline.getCentery() > lineDescriptor.getCentery() &&
//                        subline.getRightX() > lineDescriptor.getLeftX() &&
//                        subline.getLeftX() < lineDescriptor.getRightX()
//                ) {
//                    if (distance == null || subline.getCentery() - lineDescriptor.getCentery() < distance) {
//                        distance = subline.getCentery() - lineDescriptor.getCentery();
//                    }
//                }
//            }
//            if (distance != null) {
//                interlineDistances.add(distance);
//            }
//        }
//        if (interlineDistances.size() == 0) {
//            return 0.0;
//        }
//        return new Statistics(interlineDistances, 0, interlineDistances.size()).median();
//    }
//
//    public static ArrayList<DocumentTextBlock> getColumns(Mat binaryMat, Mat inputOrigMat, ArrayList<LineDescriptor> lines, Mat colorized, boolean doOcr) throws Exception {
//        ArrayList<DocumentTextBlock> documentTextBlocks = new ArrayList<>();
//        double interlineDistance = interlineDistance(lines);
//        System.out.println("interlineDistance: " + interlineDistance);
//        ArrayList<DocumentTextBlock> columns = new ArrayList<>();
//
//        connectLines(lines);
//        labelLineDescriptors(lines);
//
//        List<LineDescriptor> removedLines = removeHeadersAndFooters(lines, true);
//        for (LineDescriptor lineDescriptor : removedLines) {
//            if (doOcr && lineDescriptor.getBoundingBox().height > 0 && lineDescriptor.getBoundingBox().width > 0) {
//                System.out.printf("%s %s %s %s%n", lineDescriptor.getBoundingBox().x, lineDescriptor.getBoundingBox().y, lineDescriptor.getBoundingBox().width, lineDescriptor.getBoundingBox().height);
//                Rect boundingBox = lineDescriptor.getBoundingBox();
//                if (boundingBox.y + boundingBox.height > inputOrigMat.height()) {
//                    boundingBox.height = inputOrigMat.height() - boundingBox.y;
//                }
//                if (boundingBox.x + boundingBox.width > inputOrigMat.width()) {
//                    boundingBox.width = inputOrigMat.width() - boundingBox.x;
//                }
//                String text = getTess4JTest().doOCRLine(ImageConversionHelper.matToBufferedImage(inputOrigMat.submat(boundingBox))).trim();
//                lineDescriptor.setText(text);
//            }
//        }
//
//
//        DocumentTextBlock headersAndFooters = new DocumentTextBlock();
//        headersAndFooters.setType("headerAndFooter");
//        DocumentParagraph headersAndFootersParagraph = new DocumentParagraph();
//        headersAndFooters.getDocumentParagraphs().add(headersAndFootersParagraph);
//
//        if (removedLines.size() > 0) {
//
//            for (LineDescriptor lineDescriptor : removedLines) {
//                DocumentTextLine documentTextLine = lineDescriptorToDocumentTextLine(lineDescriptor);
//                headersAndFootersParagraph.getDocumentTextLines().add(documentTextLine);
//            }
//
//        }
//        for (LineDescriptor lineDescriptor : lines) {
//            if (lineDescriptor.getLinesAbove().size() == 0 && lineDescriptor.getLinesBelow().size() == 0) {
//                DocumentTextLine documentTextLine = lineDescriptorToDocumentTextLine(lineDescriptor);
//                headersAndFootersParagraph.getDocumentTextLines().add(documentTextLine);
//            }
//        }
//        if (headersAndFooters.getDocumentParagraphs().get(0).getDocumentTextLines().size() > 0) {
//            columns.add(headersAndFooters);
//        }
//
//
//        while (lines.size() > 0) {
//            LineDescriptor targetLine = lines.get(0);
//            ArrayList<LineDescriptor> cluster = new ArrayList<>();
//            cluster.add(targetLine);
//            ArrayList<LineDescriptor> lineToDo = new ArrayList<>();
//            lineToDo.add(targetLine);
//
//            while (lineToDo.size() > 0) {
//                targetLine = lineToDo.get(0);
//                ArrayList<LineDescriptor> combinedLines = new ArrayList<>();
//
//                combinedLines.addAll(targetLine.getLinesBelowRelaxed());
//                combinedLines.addAll(targetLine.getLinesAboveRelaxed());
//
//                for (LineDescriptor lineDescriptor : combinedLines) {
//                    if (targetLine != lineDescriptor && overlapSingle(targetLine, lineDescriptor) > 0.5 &&
//                            Math.abs(targetLine.getCentery() - lineDescriptor.getCentery()) < binaryMat.height() / 10.0 &&
//                            !cluster.contains(lineDescriptor)
//                            && Math.abs(targetLine.getCentery() - lineDescriptor.getCentery()) < 1.5 * interlineDistance
//                    ) {
//
//                        cluster.add(lineDescriptor);
//                        lineToDo.add(lineDescriptor);
//                    }
//                }
//
//                lineToDo.remove(targetLine);
//            }
//            if (cluster.size() >= 2) {
//                DocumentTextBlock documentTextBlock = getDocumentTextBlock(inputOrigMat, binaryMat, cluster, doOcr);
//                documentTextBlocks.add(documentTextBlock);
//            }
//            lines.removeAll(cluster);
//        }
//
//
//        ArrayList<DocumentTextBlock> columnMaterial = (ArrayList<DocumentTextBlock>) documentTextBlocks.clone();
//
//        while (columnMaterial.size() > 0) {
//            DocumentTextBlock base = columnMaterial.get(0);
//            columnMaterial.remove(base);
//            DocumentTextBlock column = new DocumentTextBlock();
//            columns.add(column);
//            column.getDocumentTextBlocks().add(base);
//            ArrayList<DocumentTextBlock> appended = new ArrayList<>();
//
//            for (int i = columnMaterial.size() - 1; i >= 0; i--) {
//                DocumentTextBlock partial = columnMaterial.get(i);
//                if (partial != base &&
//                        ((partial.getXStart() + partial.getWidth() / 2) > base.getXStart() &&
//                                (partial.getXStart() + partial.getWidth() / 2) < base.getXStart() + base.getWidth())
//                ) {
//                    column.getDocumentTextBlocks().add(partial);
//                    columnMaterial.remove(partial);
//                    appended.add(partial);
//                }
//            }
//
//            for (int i = 0; i < appended.size(); i++) {
//                base = appended.get(i);
//                for (int j = columnMaterial.size() - 1; j >= 0; j--) {
//                    DocumentTextBlock partial = columnMaterial.get(j);
//                    if (partial != base &&
//                            (partial.getXStart() + partial.getWidth() / 2) > base.getXStart() &&
//                            (partial.getXStart() + partial.getWidth() / 2) < base.getXStart() + base.getWidth()
//                    ) {
//                        column.getDocumentTextBlocks().add(partial);
//                        columnMaterial.remove(partial);
//                        appended.add(partial);
//                    }
//                }
//            }
//        }
//
//        for (DocumentTextBlock column : columns) {
//            column.setXStart(Integer.MAX_VALUE);
//            column.setYStart(Integer.MAX_VALUE);
//            for (DocumentTextBlock partial : column.getDocumentTextBlocks()) {
//                if (partial.getXStart() < column.getXStart()) {
//                    column.setXStart(partial.getXStart());
//                }
//                if (partial.getYStart() < column.getYStart()) {
//                    column.setYStart(partial.getYStart());
//                }
//                if (partial.getXStart() + partial.getWidth() > column.getXStart() + column.getWidth()) {
//                    column.setWidth(partial.getXStart() + partial.getWidth() - column.getXStart());
//                }
//                if (partial.getYStart() + partial.getHeight() > column.getYStart() + column.getHeight()) {
//                    column.setHeight(partial.getYStart() + partial.getHeight() - column.getYStart());
//                }
//            }
//
//            if (column.getDocumentParagraphs().size() > 0) {
//                for (DocumentTextLine documentTextLine : column.getDocumentParagraphs().get(0).getDocumentTextLines()) {
//                    if (documentTextLine.getXStart() < column.getXStart()) {
//                        column.setXStart(documentTextLine.getXStart());
//                    }
//                    if (documentTextLine.getYStart() < column.getYStart()) {
//                        column.setYStart(documentTextLine.getYStart());
//                    }
//                    if (documentTextLine.getXStart() + documentTextLine.getWidth() > column.getXStart() + column.getWidth()) {
//                        column.setWidth(documentTextLine.getXStart() + documentTextLine.getWidth() - column.getXStart());
//                    }
//                    if (documentTextLine.getYStart() + documentTextLine.getHeight() > column.getYStart() + column.getHeight()) {
//                        column.setHeight(documentTextLine.getYStart() + documentTextLine.getHeight() - column.getYStart());
//                    }
//                }
//            }
//        }
//
//        ArrayList<DocumentTextBlock> headerBlocks = new ArrayList<>();
//        for (DocumentTextBlock documentTextBlock : documentTextBlocks) {
//            for (LineDescriptor lineDescriptor : removedLines) {
//                if (lineDescriptor.getDiscoveredLabel() == DicoveredLabel.PageHeader) {
//                    if (documentTextBlock.getXStart() + documentTextBlock.getWidth() > lineDescriptor.getLeftX() &&
//                            documentTextBlock.getXStart() < lineDescriptor.getRightX()
//                    ) {
//                        TextEquiv textEquiv = new TextEquiv(null, lineDescriptor.getText());
//                        DocumentTextBlock headerBlock = new DocumentTextBlock();
//                        headerBlock.setType("heading");
//                        headerBlock.setTextEquiv(textEquiv);
//                        headerBlocks.add(headerBlock);
//                    }
//                }
//            }
//        }
//
//        if (colorized != null) {
//            drawLineDescriptorBoxes(lines, colorized);
//            drawLineDescriptorBoxes(removedLines, colorized);
//
//            for (DocumentTextBlock column : columns) {
//                VisualizationHelper.colorize(colorized, column, new Scalar(0, 0, 255), 5);
//                System.out.println("column: " + column.getYStart() + " - " + column.getXStart() + " - ");
//                for (DocumentTextBlock textBlock : column.getDocumentTextBlocks()) {
//                    VisualizationHelper.colorize(colorized, textBlock);
//                    for (DocumentParagraph paragraph : textBlock.getDocumentParagraphs())
//                        for (DocumentTextLine documentTextLine : paragraph.getDocumentTextLines()) {
//                            VisualizationHelper.colorize(colorized, documentTextLine);
//                        }
//                }
//            }
//            Imgcodecs.imwrite("/tmp/colorized2.png", colorized);
//        }
//        documentTextBlocks.sort(Comparator.comparing(DocumentTextBlock::getXStart));
//
////        headerBlocks.addAll(documentTextBlocks);
//
//        return columns;
//    }
//
//    private static List<LineDescriptor> removeHeadersAndFooters(List<LineDescriptor> lines, boolean doChildren) {
//        ArrayList<LineDescriptor> removedLines = new ArrayList<>();
//        for (int i = lines.size() - 1; i >= 0; i--) {
//            LineDescriptor lineDescriptor = lines.get(i);
//            if (lineDescriptor.getDiscoveredLabel() == DicoveredLabel.PageHeader || lineDescriptor.getDiscoveredLabel() == DicoveredLabel.PageFooter) {
//                removedLines.add(lineDescriptor);
//                lines.remove(lineDescriptor);
//                continue;
//            }
//            if (doChildren) {
//                removeHeadersAndFooters(lineDescriptor.getLinesBelow(), false);
//                removeHeadersAndFooters(lineDescriptor.getLinesAbove(), false);
//                removeHeadersAndFooters(lineDescriptor.getLinesBelowRelaxed(), false);
//                removeHeadersAndFooters(lineDescriptor.getLinesAboveRelaxed(), false);
//            }
//        }
//        return removedLines;
//    }
//
//    public static ArrayList<DocumentTextBlock> getTextBlocksOld(Mat inputOrigMat, boolean doOcr, Mat colorized, List<LineDescriptor> lines) throws Exception {
//
//        ArrayList<DocumentTextBlock> documentTextBlocks = new ArrayList<>();
//        connectLines(lines);
//
//        List<LineDescriptor> parentLines = new ArrayList<>();
//        for (LineDescriptor lineDescriptor : lines) {
//            if (lineDescriptor.getLinesAbove().size() == 0) {
//                parentLines.add(lineDescriptor);
//            }
//        }
//
//        List<LineDescriptor> childLines = new ArrayList<>();
//        for (LineDescriptor lineDescriptor : lines) {
//            if (lineDescriptor.getLinesBelow().size() == 0) {
//                childLines.add(lineDescriptor);
//            }
//        }
//
//        System.out.println(parentLines.size());
//        System.out.println(childLines.size());
//
//        Mat binaryMat = Mat.zeros(new Size(inputOrigMat.height(), inputOrigMat.width()), CvType.CV_8UC1);
//        Imgproc.adaptiveThreshold(inputOrigMat, binaryMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 101, 15);
//
//        List<LineDescriptor> linesToIgnore = new ArrayList<>();
//
//        int teller = 0;
//        while (parentLines.size() > 0) {
//            linesToIgnore.add(parentLines.get(0));
//            parentLines.get(0).setDiscoveredLabel(DicoveredLabel.PageHeader);
//            if (parentLines.get(0).getLinesBelow().size() > 0) {
//                List<LineDescriptor> cluster = cluster(parentLines.get(0).getLinesBelow().get(0), linesToIgnore);
//                parentLines.removeAll(cluster);
//                if (cluster.size() > 10) {
//                    DocumentTextBlock textBlock = getDocumentTextBlock(inputOrigMat, binaryMat, cluster, doOcr);
//                    documentTextBlocks.add(textBlock);
//                    teller++;
//                } else {
//                    if (cluster.size() == 1) {
//                        cluster.get(0).setDiscoveredLabel(DicoveredLabel.PageHeader);
//                    }
//                }
//            } else {
//                parentLines.remove(parentLines.get(0));
//            }
//            System.out.println("teller: " + teller);
//        }
//        if (colorized != null) {
//            drawLineDescriptorBoxes(lines, colorized);
//
//            for (DocumentTextBlock textBlock : documentTextBlocks) {
//                VisualizationHelper.colorize(colorized, textBlock);
//                for (DocumentParagraph paragraph : textBlock.getDocumentParagraphs())
//                    for (DocumentTextLine documentTextLine : paragraph.getDocumentTextLines()) {
//                        VisualizationHelper.colorize(colorized, documentTextLine);
//                    }
//            }
//            Imgcodecs.imwrite("/tmp/colorized2.png", colorized);
//        }
//        documentTextBlocks.sort(Comparator.comparing(DocumentTextBlock::getXStart));
//
//        return documentTextBlocks;
//    }
//
//    public static DocumentPage getDocumentPage(Mat baseLineMat, Mat inputOrigMat,
//                                               int documentImageId, String baseOutput,
//                                               String tag, String uri,
//                                               int minWidth, int minHeight,
//                                               boolean writeOut, boolean doOcr,
//                                               Mat colorized) throws Exception {
//
//        System.out.println("extracting textlines");
//        ArrayList<LineDescriptor> lines = ARUProcessor.extractTextLines(baseLineMat, inputOrigMat, documentImageId, baseOutput, tag, uri, minWidth, minHeight, writeOut, false, colorized);
//        //        ArrayList<DocumentTextBlock> documentTextBlocks = getTextBlocks(inputOrigMat, doOcr, colorized, lines);
//        System.out.println("extracting documentTextBlocks ");
//        ArrayList<DocumentTextBlock> documentTextBlocks = getColumns(baseLineMat, inputOrigMat, lines, colorized, doOcr);
//        System.out.println("extracting documentTextBlocks done");
//
//        DocumentPage documentPage = new DocumentPage(new Date(), inputOrigMat.height(), inputOrigMat.width());
//        documentPage.setDocumentTextBlocks(documentTextBlocks);
//
//        return documentPage;
//    }
//
//
//    public static Path exportTrainedData() throws IOException {
//        java.nio.file.Path path = FileSystems.getDefault().getPath("/tmp/tesseract/republic-2019-12-13/");
//        if (!path.toFile().exists() && !path.toFile().mkdirs()) {
//            System.err.println("could not create path");
//        }
//
//        if (!FileSystems.getDefault().getPath(path.toAbsolutePath() + "/nld.traineddata").toFile().exists()) {
//            InputStream initialStream = ARUProcessor.class.getClassLoader().getResourceAsStream("tesseract/republic-2019-12-13/nld.traineddata");
//
//            File targetFile = new File(path.toAbsolutePath() + "/nld.traineddata");
//            OutputStream outStream = new FileOutputStream(targetFile);
//
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = initialStream.read(buffer)) != -1) {
//                outStream.write(buffer, 0, bytesRead);
//            }
//
//            outStream.flush();
//            outStream.close();
//        }
//        return path;
//    }
//
//    private static Tess4JTest getTess4JTest() throws IOException {
//        if (_tess4jTest == null) {
//            Path path = exportTrainedData();
//
//            _tess4jTest = new Tess4JTest(path.toAbsolutePath().toString(), Tess4JTest.PageSegmentationMode.PSM_RAW_LINE, "nld");
//        }
//        return _tess4jTest;
//    }
//
//    private static DocumentTextBlock getDocumentTextBlock(Mat inputOrigMat, Mat binaryMat, List<LineDescriptor> cluster, boolean doOcr) throws Exception {
//        double left = Double.MAX_VALUE;
//        double right = Double.MIN_VALUE;
//        double top = Double.MAX_VALUE;
//        double bottom = Double.MIN_VALUE;
//        for (LineDescriptor lineDescriptor : cluster) {
//            if (lineDescriptor.getLinesAbove().size() == 0) {//&& lineDescriptor.getWidth() < binaryMat.width() / 10) { // ignore titles/headers
//                lineDescriptor.setDiscoveredLabel(DicoveredLabel.Title);
////                continue;
//            }
//            if (lineDescriptor.getLeftX() < left) {
//                left = lineDescriptor.getLeftX();
//            }
//            if (lineDescriptor.getRightX() > right) {
//                right = lineDescriptor.getRightX();
//            }
//            if (lineDescriptor.getCentery() - lineDescriptor.getXHeight() < top) {
//                top = lineDescriptor.getCentery() - lineDescriptor.getXHeight();
//            }
//            if (lineDescriptor.getCentery() + lineDescriptor.getXHeight() > bottom) {
//                bottom = lineDescriptor.getCentery() + lineDescriptor.getXHeight();
//            }
//            if (top < 0) {
//                top = 0;
//            }
//            if (bottom > inputOrigMat.height()) {
//                bottom = inputOrigMat.height();
//            }
//        }
//
//        DocumentTextBlock documentTextBlock = new DocumentTextBlock((int) top, (int) left, binaryMat.submat((int) top, (int) bottom, (int) left, (int) right), inputOrigMat.submat((int) top, (int) bottom, (int) left, (int) right), null);
//        documentTextBlock.setType("column");
//        for (LineDescriptor lineDescriptor : cluster) {
//            Rect boundingBox = lineDescriptor.getBoundingBox();
//            if (boundingBox.x + boundingBox.width > inputOrigMat.width()) {
//                boundingBox.width = (inputOrigMat.width() - 1) - boundingBox.x;
//            }
//            if (boundingBox.y + boundingBox.height > inputOrigMat.height()) {
//                boundingBox.height = (inputOrigMat.height() - 1) - boundingBox.y;
//            }
//
//            if (doOcr && boundingBox.height > 0) {
//                String text = getTess4JTest().doOCRLine(ImageConversionHelper.matToBufferedImage(inputOrigMat.submat(lineDescriptor.getBoundingBox()))).trim();
//                lineDescriptor.setText(text);
//            }
//            lineDescriptor.setDiscoveredLabel(DicoveredLabel.TextLine);
//
//
//            DocumentTextLine documentTextLine = lineDescriptorToDocumentTextLine(lineDescriptor);
//            if (documentTextBlock.getDocumentParagraphs().size() == 0) {
//                documentTextBlock.getDocumentParagraphs().add(new DocumentParagraph());
//            }
//            documentTextBlock.getDocumentParagraphs().get(0).getDocumentTextLines().add(documentTextLine);
//        }
//        documentTextBlock.getDocumentParagraphs().get(0).getDocumentTextLines().sort(Comparator.comparing(DocumentTextLine::getYStart));
//        StringBuilder stringBuilder = new StringBuilder();
//        for (DocumentTextLine documentTextLine : documentTextBlock.getDocumentParagraphs().get(0).getDocumentTextLines()) {
//            stringBuilder.append(documentTextLine.getText()).append("\n");
//        }
//        TextEquiv textEquiv = new TextEquiv(null, stringBuilder.toString().trim());
//        documentTextBlock.setTextEquiv(textEquiv);
//        return documentTextBlock;
//    }
//
//    private static DocumentTextLine lineDescriptorToDocumentTextLine(LineDescriptor lineDescriptor) {
//        DocumentTextLine documentTextLine = new DocumentTextLine(
//                lineDescriptor.getBoundingBox().y,
//                lineDescriptor.getBoundingBox().x,
//                lineDescriptor.getBoundingBox().height,
//                lineDescriptor.getBoundingBox().width,
//                lineDescriptor.getBoundingBox().y + lineDescriptor.getBoundingBox().height / 2,
//                lineDescriptor.getText());
//
//        documentTextLine.setCompressedBaseLine(lineDescriptor.getCompressedBaseline());
//        documentTextLine.setXHeight(lineDescriptor.getXHeight());
//        return documentTextLine;
//    }
//
//
//    private static List<LineDescriptor> cluster(LineDescriptor lineDescriptor, List<LineDescriptor> linesToIgnore) {
//        List<LineDescriptor> cluster = new ArrayList<>();
//        cluster.add(lineDescriptor);
//        cluster.addAll(lineDescriptor.getLinesAbove());
//        linesToIgnore.addAll(lineDescriptor.getLinesAbove());
//        cluster.addAll(lineDescriptor.getLinesBelow());
//        for (int i = 0; i < cluster.size(); i++) {
//            LineDescriptor toCheck = cluster.get(i);
//            if (!linesToIgnore.contains(toCheck)) {
//                linesToIgnore.add(toCheck);
//                for (LineDescriptor itemToAdd : cluster(toCheck, linesToIgnore)) {
//                    if (!cluster.contains(itemToAdd)) {
//                        cluster.add(itemToAdd);
//                    }
//                }
//            }
//        }
//
//        return cluster;
//    }
//
//    private static void drawLineDescriptorBoxes(List<LineDescriptor> lines, Mat colorized) {
//        for (LineDescriptor lineDescriptor : lines) {
//            Rect rectangle = new Rect(new Point(lineDescriptor.getLeftX(), lineDescriptor.getCentery() - lineDescriptor.getXHeight() * 1.75),
//                    new Point(lineDescriptor.getRightX(), lineDescriptor.getCentery() + lineDescriptor.getXHeight() * 0.75));
//
//            Imgproc.rectangle(colorized, rectangle, new Scalar(127, 127, 127), 2);
//        }
//    }
//
//    public static int getMedianXHeight(Mat baseLineMat, Mat inputOrigMat, int documentImageId, String baseOutput, String tag, String uri, int minWidth, int minHeight, boolean writeOut) throws Exception {
//        Mat colorized = Mat.zeros(inputOrigMat.size(), CV_32FC3);
//
//        List<LineDescriptor> lines = extractTextLines(baseLineMat, inputOrigMat, documentImageId, baseOutput, tag, uri, minWidth, minHeight, writeOut, true, colorized);
//        lines.sort(Comparator.comparing(LineDescriptor::getXHeight));
//
//        return lines.get(lines.size() / 2).getXHeight();
//    }
//
//    public static ArrayList<Point> smoothLine(ArrayList<Point> line) {
//        int smoothFactor = 20;
//        if (line.size() < 2 * smoothFactor) {
//            return line;
//        }
//        List<Double> yCoordinates = new ArrayList<>();
//        ArrayList<Point> smoothLine = new ArrayList<>();
//        for (Point point : line) {
//            yCoordinates.add(point.y);
//        }
//        List<Double> result = LayoutProc.smoothListDouble(yCoordinates, smoothFactor);
//        int counter = 0;
//        for (Double number : result) {
//            smoothLine.add(new Point(line.get(counter).x, number));
//            counter++;
//        }
//        return smoothLine;
//    }
//}
