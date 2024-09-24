package nl.knaw.huc.di.images.minions;

import com.google.common.base.Stopwatch;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.OpenCVWrapper;
import nl.knaw.huc.di.images.layoutds.models.Page.Baseline;
import nl.knaw.huc.di.images.layoutds.models.Page.Coords;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BaselinesMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BaselinesMapper.class);

    public static final float SCALE = 0.25f;
    public static final double MIN_LIMIT_ACCEPT = 0.50;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static Map<String, String> mapNewLinesToOldLines(List<TextLine> newTextLines, List<TextLine> oldTextLines, Size size) {
        final Stopwatch started = Stopwatch.createStarted();

        final HashMap<String, List<String>> possibleNewOldMappings = new HashMap<>();
        final HashMap<String, List<String>> possibleOldNewMappings = new HashMap<>();
        final double scaledWidth = size.width * SCALE;
        final double scaledHeight = size.height * SCALE;
        final Size scaledSize = new Size(scaledWidth, scaledHeight);

        for (TextLine newTextLine : newTextLines) {
            Mat newLineImage = Mat.zeros(scaledSize, CvType.CV_8UC1); // Make sure initialize with zeroes, weird things happen with initialized with new Mat()
            writeBaseLineToMat(newLineImage, newTextLine.getBaseline(), SCALE);

            for (TextLine oldTextLine : oldTextLines) {
                Mat oldLineImage = Mat.zeros(scaledSize, CvType.CV_8UC1); // Make sure initialize with zeroes, weird things happen with initialized with new Mat()
                writeBaseLineToMat(oldLineImage, oldTextLine.getBaseline(), SCALE);

                final double intersectOverUnion = LayoutProc.intersectOverUnion(newLineImage, oldLineImage);


                if (intersectOverUnion > MIN_LIMIT_ACCEPT) {
                    final String newTextLineId = newTextLine.getId();
                    if (!possibleNewOldMappings.containsKey(newTextLineId)) {
                        possibleNewOldMappings.put(newTextLineId, new ArrayList<>());
                    }
                    final String oldTextLineId = oldTextLine.getId();
                    if (!possibleOldNewMappings.containsKey(oldTextLineId)) {
                        possibleOldNewMappings.put(oldTextLineId, new ArrayList<>());
                    }
                    possibleNewOldMappings.get(newTextLineId).add(oldTextLineId);
                    possibleOldNewMappings.get(oldTextLineId).add(newTextLineId);
                }

                oldLineImage = OpenCVWrapper.release(oldLineImage);


            }
            newLineImage.release();
        }

        final Map<String, String> idMapping = possibleNewOldMappings.entrySet().stream()
                .filter(entry -> entry.getValue().size() == 1)
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().get(0)))
                .filter(entry -> possibleOldNewMappings.get(entry.getValue()).size() == 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        LOG.info("Mapping lines took: " + started.stop());

        return idMapping;
    }


    private static void writeBaseLineToMat(Mat image, Baseline baseline, float scale) {
        Point beginPoint = null;
        Point endPoint = null;
        Scalar color = new Scalar(255);
        int thickness = Math.max((int) (10 * scale), 1);
        for (Point point : StringConverter.stringToPoint(baseline.getPoints())) {
            endPoint = new Point(point.x * scale, point.y * scale);
            if (beginPoint != null && endPoint != null) {
                Imgproc.line(image, beginPoint, endPoint, color, thickness);
            }

            beginPoint = endPoint;

        }

    }

    private static List<TextLine> extractBaselines(boolean cleanup, int minimumHeight, int minimumWidth, int numLabels, Mat stats, Mat labeled, String identifier) {
        List<TextLine> allTextLines = extractBaselines(numLabels, stats, labeled, identifier, minimumHeight);
        if (!cleanup) {
            return allTextLines;
        }
        List<TextLine> textLines = new ArrayList<>();
        for (TextLine textLine : allTextLines) {
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            for (Point point : StringConverter.stringToPoint(textLine.getBaseline().getPoints())) {
                if (point.x < minX) {
                    minX = (int) point.x;
                }
                if (point.x > maxX) {
                    maxX = (int) point.x;
                }
            }
            int width = 0;
            if (maxX > 0) {
                width = maxX - minX;
            }
            if (width >= minimumWidth) {
                textLines.add(textLine);
            }
        }
        return textLines;
    }

    private static List<TextLine> extractBaselines(int numLabels, Mat stats, Mat labeled, String identifier, int minimumHeight) {
        List<TextLine> textLines = new ArrayList<>();
        for (int i = 1; i < numLabels; i++) {
            Rect rect = new Rect((int) stats.get(i, Imgproc.CC_STAT_LEFT)[0],
                    (int) stats.get(i, Imgproc.CC_STAT_TOP)[0],
                    (int) stats.get(i, Imgproc.CC_STAT_WIDTH)[0],
                    (int) stats.get(i, Imgproc.CC_STAT_HEIGHT)[0]);
            Mat submat = labeled.submat(rect);
            List<Point> baselinePoints = extractBaseline(submat, i, new Point(rect.x, rect.y), minimumHeight, identifier);
            if (baselinePoints.size() < 2) {
                continue;
            }
            TextLine textLine = new TextLine();
            Coords coords = new Coords();
            List<Point> coordPoints = new ArrayList<>();
            coordPoints.add(new Point(rect.x, rect.y));
            coordPoints.add(new Point(rect.x + rect.width, rect.y));
            coordPoints.add(new Point(rect.x + rect.width, rect.y + rect.height));
            coordPoints.add(new Point(rect.x, rect.y + rect.height));
            coords.setPoints(StringConverter.pointToString(coordPoints));
            textLine.setCoords(coords);
            Baseline baseline = new Baseline();
            baseline.setPoints(StringConverter.pointToString(baselinePoints));
            textLine.setBaseline(baseline);
            textLine.setId(UUID.randomUUID().toString());
            textLines.add(textLine);
        }
        return textLines;
    }

    private static List<Point> extractBaseline(Mat baselineMat, int label, Point offset, int minimumHeight, String imageFile) {
        List<Point> baseline = new ArrayList<>();
        Point point = null;
        int pixelCounter = -1;
        boolean mergedLineDetected = false;
        for (int i = 0; i < baselineMat.width(); i++) {
            boolean mergedLineDetectedStep1 = false;
            double sum = 0;
            int counter = 0;
            for (int j = 0; j < baselineMat.height(); j++) {
                int pixelValue = (int) baselineMat.get(j, i)[0];
                if (pixelValue == label) {
                    sum += j;
                    counter++;
                    if (mergedLineDetectedStep1) {
                        mergedLineDetected = true;
                    }
                } else {
                    if (counter > 0) {
                        mergedLineDetectedStep1 = true;
                    }
                }
            }
            if (counter < minimumHeight) {
                continue;
            }
            pixelCounter++;
            if (counter > 1) {
                sum /= counter;
            }

            point = new Point(i + offset.x, sum + offset.y);
            if (pixelCounter % 50 == 0) {
                baseline.add(point);
            }
        }
        if (pixelCounter % 50 != 0) {
            baseline.add(point);
        }
        if (mergedLineDetected) {
            System.out.println("lines detected for: " + imageFile);
        }
        return baseline;
    }
}