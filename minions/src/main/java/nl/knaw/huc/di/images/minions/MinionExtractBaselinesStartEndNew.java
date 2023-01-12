package nl.knaw.huc.di.images.minions;

import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.Baseline;
import nl.knaw.huc.di.images.layoutds.models.Page.Coords;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
This takes pageXML and an png containing baselines
 and an png containing baseline start and ending
 and extracts info about the baselines
 and add baseline/textline information to the regions in the pagexml
 */
public class MinionExtractBaselinesStartEndNew implements Runnable, AutoCloseable {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final String imageFile;
    private final String imageFileStart;
    private final String imageFileEnd;

    private int numLabelsStart;
    private int numLabelsEnd;
    private Mat baseLineMatStart;
    private Mat baseLineMatEnd;
    private Mat thresHoldedBaselinesStart;
    private Mat thresHoldedBaselinesEnd;
    private Mat statsStart;
    private Mat statsEnd;
    private Mat centroidsStart;
    private Mat centroidsEnd;
    private Mat labeledStart;
    private Mat labeledEnd;
    private Mat zeroMat;
    private Mat remainingMat;
    private Mat zeroMatThresholded;
    private final boolean removeEmptyRegions;


    private String xmlFile;
    private final String outputFile;
    private boolean asSingleRegion;
    private int numLabels;
    private Mat baseLineMat;
    private Mat thresHoldedBaselines;
    private Mat stats;
    private Mat centroids;
    private Mat labeled;
    private Mat labeledRemaining;
    private Mat statsRemaining;
    private Mat centroidsRemaining;
    private int margin;
    private int dilationUsed;
    private final int minimumHeight;

    private static final Logger LOG = LoggerFactory.getLogger(MinionExtractBaselinesStartEndNew.class);


    public MinionExtractBaselinesStartEndNew(String xmlFile,
                                             String outputFile, boolean asSingleRegion,
                                             boolean removeEmptyRegions,
                                             String imageFile,
                                             String imageFileStart,
                                             String imageFileEnd,
                                             int margin,
                                             int dilationUsed,
                                             int minimumHeight
    ) {
        this.xmlFile = xmlFile;
        this.outputFile = outputFile;
        this.asSingleRegion = asSingleRegion;
        this.removeEmptyRegions = removeEmptyRegions;
        this.imageFile = imageFile;
        this.imageFileStart = imageFileStart;
        this.imageFileEnd = imageFileEnd;
        this.margin = margin;
        this.dilationUsed = dilationUsed;
        this.minimumHeight = minimumHeight;
    }

    private void extractAndMergeBaseLinesNew(
            String xmlPath, String outputFile, int margin
    ) throws IOException {
        String transkribusPageXml = StringTools.readFile(xmlPath);
        boolean addLinesWithoutRegion = true;
        PcGts page = PageUtils.readPageFromString(transkribusPageXml);
        int pointDistance = 1;
        List<List<Point>> baselines = new ArrayList<>();
        // Start -> follow -> end".
        for (int labelNumber = 1; labelNumber < numLabelsStart; labelNumber++) {
            Point startPoint = getStartPoint(labelNumber, dilationUsed);
            Rect rect = null;
            for (int startCounter = (int) startPoint.x; startCounter < (int) startPoint.x + 50 && startCounter < labeled.width() - 1; startCounter++) {
                int pixelValueTarget = (int) labeled.get((int) startPoint.y, startCounter)[0];
                if (pixelValueTarget != 0) {
                    rect = new Rect((int) stats.get(pixelValueTarget, Imgproc.CC_STAT_LEFT)[0],
                            (int) stats.get(pixelValueTarget, Imgproc.CC_STAT_TOP)[0],
                            (int) stats.get(pixelValueTarget, Imgproc.CC_STAT_WIDTH)[0],
                            (int) stats.get(pixelValueTarget, Imgproc.CC_STAT_HEIGHT)[0]);
                    Mat submat = labeled.submat(rect);
                    Mat submatEnd = labeledEnd.submat(rect);
                    Point offset = new Point(rect.x, rect.y);
                    List<Point> baseline = new ArrayList<>();
                    Point point = null;
                    int pixelCounter = -1;
                    boolean endPixelsFound = false;
                    boolean mergedLineDetected = false;
                    for (int i = startCounter - rect.x; i < submat.width(); i++) {
                        boolean mergedLineDetectedStep1 = false;
                        double sum = 0;
                        int counter = 0;
                        for (int j = 0; j < submat.height(); j++) {
                            int pixelValue = (int) submat.get(j, i)[0];
                            if (pixelValue == pixelValueTarget) {
                                sum += j;
                                counter++;
                                if (mergedLineDetectedStep1) {
                                    mergedLineDetected = true;
                                }
                                zeroMat.put(rect.y + j, rect.x + i, 0);
                            } else {
                                if (counter > 0) {
                                    mergedLineDetectedStep1 = true;
                                }
                            }
                        }
                        if (counter > 1) {
                            sum /= counter;
                            if (pixelCounter > 50 && (int) submatEnd.get((int) sum, i)[0] != 0) {
//                                System.out.println("reached end of textline");
                                endPixelsFound = true;
                            } else if (endPixelsFound) {
                                break;
                            }

                        }
                        if (counter < minimumHeight) {
                            continue;
                        }

                        pixelCounter++;

                        point = new Point(i + offset.x, sum + offset.y);
                        if (pixelCounter % pointDistance == 0) {
                            baseline.add(point);
                        }
                    }
                    if (pixelCounter % pointDistance != 0) {
                        baseline.add(point);
                    }
                    if (mergedLineDetected) {
                        LOG.info("mergedLineDetected: " + xmlFile);
                    }
                    submat.release();
                    submatEnd.release();
                    if (baseline.size() > 2) {
                        baseline = StringConverter.simplifyPolygon(baseline, 3);
                    }

                    baselines.add(baseline);
                    break;
                }
            }
        }


        List<TextLine> newTextLines = new ArrayList<TextLine>();
        for (List<Point> baselinePoints : baselines) {
            if (baselinePoints.size() < 2) {
                continue;
            }
            TextLine textLine = new TextLine();
            Coords coords = new Coords();
            List<Point> coordPoints = new ArrayList<>();
            coordPoints.add(new Point(0, 0));
            coordPoints.add(new Point(1, 0));
            coordPoints.add(new Point(1, 1));
            coordPoints.add(new Point(0, 1));
            coords.setPoints(StringConverter.pointToString(coordPoints));
            textLine.setCoords(coords);
            Baseline baseline = new Baseline();
            baseline.setPoints(StringConverter.pointToString(baselinePoints));
            textLine.setBaseline(baseline);
            textLine.setId(UUID.randomUUID().toString());
            newTextLines.add(textLine);
        }

        Imgproc.threshold(zeroMat, zeroMatThresholded, 0, 255, Imgproc.THRESH_BINARY);
        thresHoldedBaselines.copyTo(remainingMat, zeroMatThresholded);
        zeroMatThresholded.release();

        int numLabelsRemaining = Imgproc.connectedComponentsWithStats(remainingMat, labeledRemaining, statsRemaining, centroidsRemaining, 8, CvType.CV_32S);
        baselines.clear();
// Search from right to left
        for (int labelNumber = 1; labelNumber < numLabelsEnd; labelNumber++) {
            Point endPoint = getEndPoint(labelNumber);
            Rect rect = null;
            for (int endCounter = (int) endPoint.x; endCounter > (int) endPoint.x - 50 && endCounter > 0; endCounter--) {
                int pixelValueTarget = (int) labeledRemaining.get((int) endPoint.y, endCounter)[0];
                if (pixelValueTarget != 0) {
                    rect = new Rect((int) statsRemaining.get(pixelValueTarget, Imgproc.CC_STAT_LEFT)[0],
                            (int) statsRemaining.get(pixelValueTarget, Imgproc.CC_STAT_TOP)[0],
                            (int) statsRemaining.get(pixelValueTarget, Imgproc.CC_STAT_WIDTH)[0],
                            (int) statsRemaining.get(pixelValueTarget, Imgproc.CC_STAT_HEIGHT)[0]);
                    Mat submat = labeledRemaining.submat(rect);
//                    Mat submatRemaining = labeledRemaining.submat(rect);
                    Point offset = new Point(rect.x, rect.y);
                    List<Point> baseline = new ArrayList<>();

                    Point point = null;
                    int pixelCounter = -1;

                    for (int i = endCounter - rect.x; i > 0; i--) {
                        double sum = 0;
                        int counter = 0;

                        for (int j = 0; j < submat.height(); j++) {
                            int pixelValue = (int) submat.get(j, i)[0];
                            if (pixelValue == pixelValueTarget) {
                                sum += j;
                                counter++;
                                labeledRemaining.put(rect.y + j, rect.x + i, 0);
                                zeroMat.put(rect.y + j, rect.x + i, 0);
                            }
                        }
                        if (counter > 1) {
                            sum /= counter;
                        }

                        if (counter < minimumHeight) {
                            continue;
                        }

                        pixelCounter++;

                        point = new Point(i + offset.x, sum + offset.y);
                        if (pixelCounter % pointDistance == 0) {
                            baseline.add(point);
                        }
                    }
                    if (pixelCounter % pointDistance != 0) {
                        baseline.add(point);
                    }

                    submat.release();
//                    submatRemaining.release();
                    baseline = Lists.reverse(baseline);
                    if (baseline.size() > 2) {
                        baseline = StringConverter.simplifyPolygon(baseline, 2);
                    }
                    baselines.add(baseline);
                    break;
                }
            }
        }

        for (List<Point> baselinePoints : baselines) {
            if (baselinePoints.size() < 2) {
                continue;
            }
            TextLine textLine = new TextLine();
            Coords coords = new Coords();
            List<Point> coordPoints = new ArrayList<>();
            coordPoints.add(new Point(0, 0));
            coordPoints.add(new Point(1, 0));
            coordPoints.add(new Point(1, 1));
            coordPoints.add(new Point(0, 1));
            coords.setPoints(StringConverter.pointToString(coordPoints));
            textLine.setCoords(coords);
            Baseline baseline = new Baseline();
            baseline.setPoints(StringConverter.pointToString(baselinePoints));
            textLine.setBaseline(baseline);
            textLine.setId(UUID.randomUUID().toString());
            newTextLines.add(textLine);
        }

/// new code

        Imgproc.threshold(zeroMat, zeroMatThresholded, 0, 1, Imgproc.THRESH_BINARY);
        remainingMat.release();
        this.remainingMat = Mat.zeros(this.thresHoldedBaselines.size(), thresHoldedBaselines.type());
        thresHoldedBaselines.copyTo(remainingMat, zeroMatThresholded);
//        Imgcodecs.imwrite("/tmp/thresHoldedBaselines.png", thresHoldedBaselines);
//        Imgcodecs.imwrite("/tmp/zeroMat.png", zeroMat);
//        Imgcodecs.imwrite("/tmp/zeroMatThresholded.png", zeroMatThresholded);
//        Imgcodecs.imwrite("/tmp/remain.png", remainingMat);
        zeroMatThresholded.release();

        baselines.clear();
// remaining without valid start or end
        numLabelsRemaining = Imgproc.connectedComponentsWithStats(remainingMat, labeledRemaining, statsRemaining, centroidsRemaining, 8, CvType.CV_32S);
        for (int labelNumber = 1; labelNumber < numLabelsRemaining; labelNumber++) {
            Rect rect = null;
            rect = new Rect((int) statsRemaining.get(labelNumber, Imgproc.CC_STAT_LEFT)[0],
                    (int) statsRemaining.get(labelNumber, Imgproc.CC_STAT_TOP)[0],
                    (int) statsRemaining.get(labelNumber, Imgproc.CC_STAT_WIDTH)[0],
                    (int) statsRemaining.get(labelNumber, Imgproc.CC_STAT_HEIGHT)[0]);
            Mat submat = labeledRemaining.submat(rect);
            Point offset = new Point(rect.x, rect.y);
            List<Point> baseline = new ArrayList<>();

            Point point = null;
            int pixelCounter = -1;

            for (int i = 0; i < rect.width; i++) {
                double sum = 0;
                int counter = 0;

                for (int j = 0; j < submat.height(); j++) {
                    int pixelValue = (int) submat.get(j, i)[0];
                    if (pixelValue == labelNumber
                            && (int) labeledEnd.get(rect.y + j, rect.x + i)[0] == 0
                            && (int) labeledStart.get(rect.y + j, rect.x + i)[0] == 0) {
                        sum += j;
                        counter++;
                        labeledRemaining.put(rect.y + j, rect.x + i, 0);
//                        zeroMat.put(rect.y + j, rect.x + i, 0);
                    }
                }
                if (counter > 1) {
                    sum /= counter;
                }

                if (counter < minimumHeight) {
                    continue;
                }

                pixelCounter++;

                point = new Point(i + offset.x, sum + offset.y);
                if (pixelCounter % pointDistance == 0) {
                    baseline.add(point);
                }
            }
            if (pixelCounter % pointDistance != 0) {
                baseline.add(point);
            }

            submat.release();
            if (baseline.size() < 50) {
                continue;
            }
            if (baseline.size() > 2) {
                baseline = StringConverter.simplifyPolygon(baseline, 2);
            }
            baselines.add(baseline);
        }

        for (List<Point> baselinePoints : baselines) {
            if (baselinePoints.size() < 2) {
                continue;
            }
            TextLine textLine = new TextLine();
            Coords coords = new Coords();
            List<Point> coordPoints = new ArrayList<>();
            coordPoints.add(new Point(0, 0));
            coordPoints.add(new Point(1, 0));
            coordPoints.add(new Point(1, 1));
            coordPoints.add(new Point(0, 1));
            coords.setPoints(StringConverter.pointToString(coordPoints));
            textLine.setCoords(coords);
            Baseline baseline = new Baseline();
            baseline.setPoints(StringConverter.pointToString(baselinePoints));
            textLine.setBaseline(baseline);
            textLine.setId(UUID.randomUUID().toString());
            newTextLines.add(textLine);
        }
/// end new code

        // find duplicate lines
        List<TextLine> linesToRemove = new ArrayList<>();
        for (int i = 0; i < newTextLines.size(); i++) {
            for (int j = i + 1; j < newTextLines.size(); j++) {
                TextLine firstLine = newTextLines.get(i);
                TextLine secondLine = newTextLines.get(j);
                ArrayList<Point> firstPoints = StringConverter.stringToPoint(firstLine.getBaseline().getPoints());
                ArrayList<Point> secondPoints = StringConverter.stringToPoint(secondLine.getBaseline().getPoints());

                Point firstLastPoints = firstPoints.get(firstPoints.size() - 1);
                Point secondLastPoints = secondPoints.get(secondPoints.size() - 1);

                if (firstLastPoints.x == secondLastPoints.x && firstLastPoints.y == secondLastPoints.y) {
                    if (firstPoints.get(0).x > secondPoints.get(0).x) {
                        linesToRemove.add(firstLine);
                    } else {
                        linesToRemove.add(secondLine);
                    }
                    continue;
                }
                Point firstPoint = firstPoints.get(0);
                Point secondPoint = secondPoints.get(0);

                if (firstPoint.x == secondPoint.x && firstPoint.y == secondPoint.y) {
                    linesToRemove.add(secondLine);
                    continue;
                }
            }
        }
        newTextLines.removeAll(linesToRemove);
        labeledRemaining.release();
        statsRemaining.release();
        centroidsRemaining.release();
        String newPageXml = MinionExtractBaselines.mergeTextLines(page, newTextLines, addLinesWithoutRegion, asSingleRegion, xmlPath, removeEmptyRegions, margin);
        page = PageUtils.readPageFromString(newPageXml);
        LayoutProc.recalculateTextLinesFromBaselines(page);
        newPageXml = PageUtils.convertPcGtsToString(page);
//        Imgcodecs.imwrite("/tmp/thresHoldedBaselines.png", thresHoldedBaselines);
//        Imgcodecs.imwrite("/tmp/thresHoldedBaselinesStart.png", thresHoldedBaselinesStart);
//        Imgcodecs.imwrite("/tmp/thresHoldedBaselinesEnd.png", thresHoldedBaselinesEnd);
//        Mat remainingMat = new Mat();
//        Imgproc.threshold(zeroMat,zeroMat, 0, 255, Imgproc.THRESH_BINARY);
//        thresHoldedBaselines.copyTo(remainingMat, zeroMat);
//        Imgcodecs.imwrite("/tmp/zeroMat.png", zeroMat);
//        Imgcodecs.imwrite("/tmp/remainingMat.png", remainingMat);
        StringTools.writeFile(outputFile, newPageXml);

    }

    private Point getStartPoint(int labelNumber, int dilationUsed) {
        int pixelCounter = 0;
        int totalPixelsOn = 0;
        Rect rect = new Rect((int) statsStart.get(labelNumber, Imgproc.CC_STAT_LEFT)[0],
                (int) statsStart.get(labelNumber, Imgproc.CC_STAT_TOP)[0],
                (int) statsStart.get(labelNumber, Imgproc.CC_STAT_WIDTH)[0],
                (int) statsStart.get(labelNumber, Imgproc.CC_STAT_HEIGHT)[0]);
        Mat submat = labeledStart.submat(rect);
        for (int counter = 0; counter < rect.height; counter++) {
            int pixelValue = (int) submat.get(counter, submat.width() - 1)[0];
            if (pixelValue == labelNumber) {
                pixelCounter += counter;
                totalPixelsOn++;
            }
        }
        submat.release();
        Point startPoint = new Point(rect.x + dilationUsed, rect.y + (pixelCounter / totalPixelsOn));
        return startPoint;
    }

    private Point getEndPoint(int labelNumber) {
        int pixelCounter = 0;
        int totalPixelsOn = 0;
        Rect rect = new Rect((int) statsEnd.get(labelNumber, Imgproc.CC_STAT_LEFT)[0],
                (int) statsEnd.get(labelNumber, Imgproc.CC_STAT_TOP)[0],
                (int) statsEnd.get(labelNumber, Imgproc.CC_STAT_WIDTH)[0],
                (int) statsEnd.get(labelNumber, Imgproc.CC_STAT_HEIGHT)[0]);
        Mat submat = labeledEnd.submat(rect);
        for (int counter = 0; counter < rect.height; counter++) {
            int pixelValue = (int) submat.get(counter, 0)[0];
            if (pixelValue == labelNumber) {
                pixelCounter += counter;
                totalPixelsOn++;
            }
        }
        submat.release();
        Point endPoint = new Point(rect.x + (rect.width / 2), rect.y + (pixelCounter / totalPixelsOn));
        return endPoint;
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("input_path_png").required(true).hasArg(true)
                .desc("Folder that contains the images with the baselines and their start and end.").build()
        );
        options.addOption(Option.builder("input_path_png_start").required(true).hasArg(true)
                .desc("Folder with the images that contain the start points of the baselines").build()
        );
        options.addOption(Option.builder("input_path_png_end").required(true).hasArg(true)
                .desc("Folder with the images that contain the end points of the baselines").build()
        );
        options.addOption(Option.builder("input_path_pagexml").required(true).hasArg(true)
                .desc("Folder that contains the PAGE xml that has to be updated").build()
        );
        options.addOption(Option.builder("output_path_pagexml").required(true).hasArg(true)
                .desc("The folder where the updated page has to be saved.").build()
        );
        options.addOption("as_single_region", false, "All text lines should be included in one text region. (default: false)");
        options.addOption("remove_empty_regions", false, "Remove empty regions from page. (default: false)");
        options.addOption("margin", true, "The amount of pixels the baseline can be outside of its TextRegion to be included (default: 50)");
        options.addOption("dilation", true, "Compensate for the dilation used in pixels (default: 5)");
        options.addOption("minimum_height", true, "Minimum height of a text line in pixels (default: 5)");

        return options;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }


    //example parameters
    // -input_path_png /scratch/randomprint/results/prod/page/ -input_path_png_start /scratch/randomprint-start/results/prod/page/ -input_path_png_end /scratch/randomprint-end/results/prod/page/ -input_path_pagexml /scratch/randomprint/results/prod/page/ -output_path_pagexml /scratch/randomprint/results/prod/page/
    public static void main(String[] args) throws Exception {
        int numthreads = (Runtime.getRuntime().availableProcessors() / 2) + 1;
        int maxCount = -1;

        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java " + MinionExtractBaselinesStartEndNew.class.getName());
            return;
        }

        if (cmd.hasOption("help")) {
            printHelp(options, "java " + MinionExtractBaselinesStartEndNew.class.getName());
            return;
        }

        String inputPathPng = "/scratch/randomprint/results/prod/page/";
        String inputPathPngStart = "/scratch/randomprint-start/results/prod/page/";
        String inputPathPngEnd = "/scratch/randomprint-end/results/prod/page/";
        String inputPathPageXml = "/scratch/randomprint/results/prod/page/";
        String outputPathPageXml = "/scratch/randomprint/results/prod/page/";
        boolean asSingleRegion = false;
        boolean removeEmptyRegions = false;
        int margin = 50;
        int dilationUsed = 5;
        int minimumHeight = 5;
        if (cmd.hasOption("input_path_png")) {
            inputPathPng = cmd.getOptionValue("input_path_png");
            System.out.println("input_path_png: " + inputPathPng);
        }
        if (cmd.hasOption("input_path_png_start")) {
            inputPathPngStart = cmd.getOptionValue("input_path_png_start");
            System.out.println("input_path_png_start: " + inputPathPngStart);
        }
        if (cmd.hasOption("input_path_png_end")) {
            inputPathPngEnd = cmd.getOptionValue("input_path_png_end");
            System.out.println("input_path_png_end: " + inputPathPngEnd);
        }
        if (cmd.hasOption("input_path_pagexml")) {
            inputPathPageXml = cmd.getOptionValue("input_path_pagexml");
            System.out.println("input_path_pagexml: " + inputPathPageXml);
        }
        if (cmd.hasOption("output_path_pagexml")) {
            outputPathPageXml = cmd.getOptionValue("output_path_pagexml");
            System.out.println("output_path_pagexml: " + outputPathPageXml);
        }
        if (cmd.hasOption("as_single_region")) {
            asSingleRegion = true;
        }
//        asSingleRegion = true;
        if (cmd.hasOption("remove_empty_regions")) {
            removeEmptyRegions = true;
        }
        if (cmd.hasOption("margin")) {
            margin = Integer.parseInt(cmd.getOptionValue("margin"));
        }
        if (cmd.hasOption("dilation")) {
            dilationUsed = Integer.parseInt(cmd.getOptionValue("dilation"));
        }
        if (cmd.hasOption("minimum_height")) {
            minimumHeight = Integer.parseInt(cmd.getOptionValue("minimum_height"));
        }
        System.out.println("as_single_region: " + asSingleRegion);

        DirectoryStream<Path> fileStream = Files.newDirectoryStream(Paths.get(inputPathPng));
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));


        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        for (Path file : files) {
            if (file.getFileName().toString().endsWith(".png")) {// && file.getFileName().toString().endsWith("DDD_010927620_001.png")) {
                if (maxCount != 0) {
                    maxCount--;
                    String baseFilename = FilenameUtils.removeExtension(file.getFileName().toString());
                    String xmlFile = new File(inputPathPageXml, baseFilename + ".xml").getAbsolutePath();
                    String imageFile = new File(inputPathPng, baseFilename + ".png").getAbsolutePath();
                    String imageFileStart = new File(inputPathPngStart, baseFilename + ".png").getAbsolutePath();
                    String imageFileEnd = new File(inputPathPngEnd, baseFilename + ".png").getAbsolutePath();
                    String outputFile = new File(outputPathPageXml, baseFilename + ".xml").getAbsolutePath();

                    if (Files.exists(Paths.get(xmlFile))
                            && Files.exists(Paths.get(imageFile))
                            && Files.exists(Paths.get(imageFileStart))
                            && Files.exists(Paths.get(imageFileEnd))
                    ) {
//                        System.out.println(xmlFile);

                        Runnable worker = new MinionExtractBaselinesStartEndNew(
                                xmlFile,
                                outputFile, asSingleRegion,
                                removeEmptyRegions,
                                imageFile, imageFileStart, imageFileEnd,
                                margin, dilationUsed, minimumHeight
                        );
                        executor.execute(worker);//calling execute method of ExecutorService
                    }
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        System.out.println("Finished all threads");
    }


    @Override
    public void run() {
        try {
//            extractAndMergeBaseLines(xmlFile, outputFile);
//            System.out.println("starting: "+this.imageFile);
            baseLineMat = Imgcodecs.imread(this.imageFile, Imgcodecs.IMREAD_GRAYSCALE);
            baseLineMatStart = Imgcodecs.imread(imageFileStart, Imgcodecs.IMREAD_GRAYSCALE);
            baseLineMatEnd = Imgcodecs.imread(imageFileEnd, Imgcodecs.IMREAD_GRAYSCALE);
            thresHoldedBaselines = new Mat();
            thresHoldedBaselinesStart = new Mat();
            thresHoldedBaselinesEnd = new Mat();
            Imgproc.threshold(baseLineMat, thresHoldedBaselines, 0, 255, Imgproc.THRESH_BINARY);
            Imgproc.threshold(baseLineMatStart, thresHoldedBaselinesStart, 0, 255, Imgproc.THRESH_BINARY);
            Imgproc.threshold(baseLineMatEnd, thresHoldedBaselinesEnd, 0, 255, Imgproc.THRESH_BINARY);

            stats = new Mat();
            centroids = new Mat();
            labeled = new Mat();

            statsStart = new Mat();
            centroidsStart = new Mat();
            labeledStart = new Mat();

            statsEnd = new Mat();
            centroidsEnd = new Mat();
            labeledEnd = new Mat();


//                        Imgcodecs.imwrite ("/tmp/thresHoldedBaselines.png",thresHoldedBaselines );
//                        Imgcodecs.imwrite ("/tmp/thresHoldedBaselinesStart.png",thresHoldedBaselinesStart );
//                        Imgcodecs.imwrite ("/tmp/thresHoldedBaselinesEnd.png",thresHoldedBaselinesEnd );


            numLabels = Imgproc.connectedComponentsWithStats(thresHoldedBaselines, labeled, stats, centroids);
            numLabelsStart = Imgproc.connectedComponentsWithStats(thresHoldedBaselinesStart, labeledStart, statsStart, centroidsStart);
            numLabelsEnd = Imgproc.connectedComponentsWithStats(thresHoldedBaselinesEnd, labeledEnd, statsEnd, centroidsEnd);
            this.zeroMat = Mat.ones(this.thresHoldedBaselines.size(), thresHoldedBaselines.type());
            this.remainingMat = Mat.zeros(this.thresHoldedBaselines.size(), thresHoldedBaselines.type());
            this.zeroMatThresholded = Mat.ones(this.thresHoldedBaselines.size(), thresHoldedBaselines.type());
            this.labeledRemaining = new Mat();
            this.statsRemaining = new Mat();
            this.centroidsRemaining = new Mat();

            extractAndMergeBaseLinesNew(xmlFile, outputFile, margin);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws Exception {
        baseLineMat.release();
        baseLineMatStart.release();
        baseLineMatEnd.release();
        thresHoldedBaselines.release();
        thresHoldedBaselinesStart.release();
        thresHoldedBaselinesEnd.release();
        stats.release();
        statsStart.release();
        statsEnd.release();
        centroids.release();
        centroidsStart.release();
        centroidsEnd.release();
        labeled.release();
        labeledStart.release();
        labeledEnd.release();
        zeroMat.release();
        remainingMat.release();
        zeroMatThresholded.release();
        labeledRemaining.release();
        statsRemaining.release();
        centroidsRemaining.release();
    }
}