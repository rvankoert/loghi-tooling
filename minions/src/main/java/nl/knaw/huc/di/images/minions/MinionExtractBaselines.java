package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 and extracts info about the baselines
 and add baseline/textline information to the regions in the pagexml
 */
public class MinionExtractBaselines implements Runnable, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MinionExtractBaselines.class);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final String imageFile;
    private final String outputFile;
    private String xmlFile;
    private boolean asSingleRegion;
    private int margin;


    public MinionExtractBaselines(String xmlFile, String outputFile, boolean asSingleRegion, String imageFile, int margin) {
        this.xmlFile = xmlFile;
        this.outputFile = outputFile;
        this.asSingleRegion = asSingleRegion;
        this.imageFile = imageFile;
        this.margin = margin;
    }

    private static List<Point> extractBaseline(Mat baselineMat, int label, Point offset, int minimumHeight, String xmlFile) {
        List<Point> baseline = new ArrayList<>();
        int i;
        Point point = null;
        int pixelCounter = -1;
        boolean mergedLineDetected = false;
        for (i = 0; i < baselineMat.width(); i++) {
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
            LOG.info("mergedLineDetected: " + xmlFile);
        }
        return baseline;
    }

    public static List<TextLine> extractBaselines(boolean cleanup, int minimumHeight, int minimumWidth, int numLabels, Mat stats, Mat labeled, String identifier) {
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

    public static String mergeTextLines(PcGts page, List<TextLine> textLines, boolean addLinesWithoutRegion, boolean asSingleRegion, String xmlFile, boolean removeEmptyRegions, int margin) throws JsonProcessingException {
        System.err.println("textlines to match: " + textLines.size() + " " + xmlFile);
        if (!asSingleRegion) {
            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                textRegion.setTextLines(new ArrayList<>());
                textLines = PageUtils.attachTextLines(textRegion, textLines, 0.51f, 0);
            }
            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                textLines = PageUtils.attachTextLines(textRegion, textLines, 0.01f, margin);
            }
        } else {
            page.getPage().setTextRegions(new ArrayList<>());

            if (textLines.size() > 0) {
                if (addLinesWithoutRegion) {
                    TextRegion newRegion = new TextRegion();
                    newRegion.setId(UUID.randomUUID().toString());
                    Coords coords = new Coords();
                    List<Point> coordPoints = new ArrayList<>();
                    coordPoints.add(new Point(0, 0));
                    coordPoints.add(new Point(page.getPage().getImageWidth() - 1, 0));
                    coordPoints.add(new Point(page.getPage().getImageWidth() - 1, page.getPage().getImageHeight() - 1));
                    coordPoints.add(new Point(0, page.getPage().getImageHeight() - 1));
                    coords.setPoints(StringConverter.pointToString(coordPoints));
                    newRegion.setCoords(coords);
                    newRegion.setTextLines(textLines);
                    page.getPage().getTextRegions().add(newRegion);
                }
            }
        }
        if (textLines.size() > 0) {
            System.err.println("textlines remaining: " + textLines.size() + " " + xmlFile);
        }

        List<TextRegion> goodRegions = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
//            if (textRegion.getTextLines().size() > 0 || textRegion.getCustom().contains(":Photo") || textRegion.getCustom().contains(":Drawing")) {
            if (!removeEmptyRegions || textRegion.getTextLines().size() > 0 || textRegion.getCustom().contains(":Photo") || textRegion.getCustom().contains(":Drawing") || textRegion.getCustom().contains(":separator")) {
                goodRegions.add(textRegion);
            }
        }
        page.getPage().setTextRegions(goodRegions);
        XmlMapper mapper = new XmlMapper();

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
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

    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("input_path_png").required(true).hasArg(true)
                .desc("P2PaLA baseline detection output").build()
        );

        options.addOption(Option.builder("input_path_page").required(true).hasArg(true)
                .desc("Folder of the page files, that need to be updated").build()
        );

        options.addOption(Option.builder("output_path_page").required(true).hasArg(true)
                .desc("Folder to write the updated page to").build()
        );

        options.addOption(Option.builder("as_single_region").required(false).hasArg(true)
                .desc("Are all baselines in the same region? (true / false, default is true)").build()
        );
        options.addOption("threads", true, "number of threads to use, default 4");

        options.addOption("help", false, "prints this help dialog");

        return options;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }

    public static void main(String[] args) throws Exception {
        int numthreads = 4;
        int maxCount = -1;
        int margin = 50;
//        String inputPathPng = "/home/rutger/republic/batch2all/page/";
//        String inputPathPageXml = "/home/rutger/republic/batch2all/page/";
//        String outputPathPageXml = "/home/rutger/republic/batch2all/page/";
//        String inputPathPng = "/data/work_baseline_detection-5/results/prod/page/";
//        String inputPathPageXml = "/home/rutger/republic/all/page/";
//        String outputPathPageXml = "/data/statengeneraalall3/page/";
//        String inputPathPng = "/scratch/haarlem/results/prod/page/";
//        String inputPathPageXml = "/scratch/haarlem/results/prod/page/";
//        String outputPathPageXml = "/scratch/haarlem/results/prod/page/";
        String inputPathPng = "/scratch/output/";
        String inputPathPageXml = "/data/prizepapersall/page/";
        String outputPathPageXml = "/data/prizepapersall/page/";
        boolean asSingleRegion = true;

        final Options options = getOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java " + MinionExtractBaselines.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java " + MinionExtractBaselines.class.getName());
            return;
        }


        inputPathPng = commandLine.getOptionValue("input_path_png");
        inputPathPageXml = commandLine.getOptionValue("input_path_page");
        outputPathPageXml = commandLine.getOptionValue("output_path_page");
        if (commandLine.hasOption("threads")) {
            numthreads = Integer.parseInt(commandLine.getOptionValue("threads"));
        }

        if (commandLine.hasOption("as_single_region")) {
            asSingleRegion = commandLine.getOptionValue("as_single_region").equals("true");
        }

//        if (args.length > 0) {
//            inputPathPng = args[0];
//        }
//        if (args.length > 1) {
//            inputPathPageXml = args[1];
//        }
//        if (args.length > 2) {
//            outputPathPageXml = args[2];
//        }
//        if(args.length > 3) {
//            asSingleRegion = args[3].equals("true");
//        }
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(Paths.get(inputPathPng));
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));


        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        for (Path file : files) {
            if (file.getFileName().toString().endsWith(".png")) {
                if (maxCount != 0) {
                    maxCount--;
//                    String base = FilenameUtils.removeExtension(file.toAbsolutePath().toString());
                    String baseFilename = FilenameUtils.removeExtension(file.getFileName().toString());
                    String xmlFile = inputPathPageXml + baseFilename + ".xml";
                    String imageFile = inputPathPng + baseFilename + ".png";
                    String outputFile = outputPathPageXml + baseFilename + ".xml";
                    if (Files.exists(Paths.get(xmlFile))) {
//                        System.out.println(xmlFile);

//                        Runnable worker = new MinionExtractBaselines(xmlFile, outputFile, false, numLabels, baseLineMat, thresHoldedBaselines, stats, centroids, labeled, margin);
                        Runnable worker = new MinionExtractBaselines(xmlFile, outputFile, asSingleRegion, imageFile, margin);
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

    private void extractAndMergeBaseLines(
            String xmlPath, String outputFile, int margin
    ) throws IOException {
        String transkribusPageXml = StringTools.readFile(xmlPath);
        boolean addLinesWithoutRegion = true;
        boolean cleanup = true;
        int minimumWidth = 15;
        int minimumHeight = 3;
        Mat baseLineMat = Imgcodecs.imread(imageFile, Imgcodecs.IMREAD_GRAYSCALE);
        Mat thresHoldedBaselines = new Mat(baseLineMat.size(), CvType.CV_32S);
        Imgproc.threshold(baseLineMat, thresHoldedBaselines, 0, 255, Imgproc.THRESH_BINARY_INV);
        Mat stats = new Mat();
        Mat centroids = new Mat();
        Mat labeled = new Mat();
        int numLabels = Imgproc.connectedComponentsWithStats(thresHoldedBaselines, labeled, stats, centroids, 8, CvType.CV_32S);


        PcGts page = PageUtils.readPageFromString(transkribusPageXml);
        List<TextLine> textLines = extractBaselines(cleanup, minimumHeight, minimumWidth, numLabels, stats, labeled, xmlPath);

        String newPageXml = mergeTextLines(page, textLines, addLinesWithoutRegion, this.asSingleRegion, xmlPath, false, margin);
        StringTools.writeFile(outputFile, newPageXml);
        baseLineMat.release();
        thresHoldedBaselines.release();
        stats.release();
        centroids.release();
        labeled.release();
    }

    @Override
    public void run() {
        try {
            System.out.println(this.imageFile);
            extractAndMergeBaseLines(xmlFile, outputFile, margin);
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
    }
}