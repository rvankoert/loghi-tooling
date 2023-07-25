package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.OpenCVWrapper;
import nl.knaw.huc.di.images.layoutds.models.LaypaConfig;
import nl.knaw.huc.di.images.layoutds.models.P2PaLAConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    private final String outputFile;
    private final P2PaLAConfig p2palaconfig;
    private final LaypaConfig laypaConfig;
    private final String identifier;
    private final Supplier<PcGts> pageSupplier;
    private final Supplier<Mat> baselineImageSupplier;
    private boolean asSingleRegion;
    private int margin;
    private boolean invertImage;
    private final Consumer<String> errorLog;
    private int threshold;
    private List<String> reorderRegionsList;

    public MinionExtractBaselines(String identifier, Supplier<PcGts> pageSupplier, String outputFile,
                                  boolean asSingleRegion, P2PaLAConfig p2palaconfig, LaypaConfig laypaConfig,
                                  Supplier<Mat> baselineImageSupplier, int margin, boolean invertImage, int threshold,
                                  List<String> reorderRegionsList)
    {
        this(identifier, pageSupplier, outputFile, asSingleRegion, p2palaconfig, laypaConfig, baselineImageSupplier,
                margin, invertImage, error -> {}, threshold, reorderRegionsList);
    }

    public MinionExtractBaselines(String identifier, Supplier<PcGts> pageSupplier, String outputFile,
                                  boolean asSingleRegion, P2PaLAConfig p2palaconfig, LaypaConfig laypaConfig,
                                  Supplier<Mat> baselineImageSupplier, int margin,
                                  boolean invertImage, Consumer<String> errorLog,
                                  int threshold, List<String> reorderRegionsList) {
        this.identifier = identifier;
        this.pageSupplier = pageSupplier;
        this.baselineImageSupplier = baselineImageSupplier;
        this.outputFile = outputFile;
        this.asSingleRegion = asSingleRegion;
        this.margin = margin;
        this.invertImage = invertImage;
        this.p2palaconfig = p2palaconfig;
        this.laypaConfig = laypaConfig;
        this.errorLog = errorLog;
        this.threshold = threshold;
        this.reorderRegionsList = reorderRegionsList;
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


    public static PcGts mergeTextLines(PcGts page, List<TextLine> newTextLines, boolean addLinesWithoutRegion,
                                       boolean asSingleRegion, String identifier, boolean removeEmptyRegions,
                                       int margin, boolean clearExistingLines) {
        final List<TextLine> oldTextLines = page.getPage().getTextRegions().stream().flatMap(region -> region.getTextLines().stream()).collect(Collectors.toList());
        final Map<String, String> newLinesToOldLines = BaselinesMapper.mapNewLinesToOldLines(newTextLines, oldTextLines, new Size(page.getPage().getImageWidth(), page.getPage().getImageHeight()));

        for (TextLine newTextLine : newTextLines) {
            if (newLinesToOldLines.containsKey(newTextLine.getId())) {
                final String oldTextLineId = newLinesToOldLines.get(newTextLine.getId());
                final Optional<TextLine> oldTextLine = oldTextLines.stream().filter(oldLine -> oldLine.getId().equals(oldTextLineId)).findAny();
                if (oldTextLine.isPresent()) {
                    newTextLine.setId(oldTextLineId);
                }
            }
        }


        LOG.info("textlines to match: " + newTextLines.size() + " " + identifier);
        if (!asSingleRegion) {
            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                textRegion.setTextLines(new ArrayList<>());
            }

            for (float percentage = 0.51f; percentage >= 0.01f; percentage -= 0.05) {
                if (newTextLines.isEmpty()) {
                    break;
                }
                for (TextRegion textRegion : page.getPage().getTextRegions()) {
                    newTextLines = PageUtils.attachTextLines(textRegion, newTextLines, percentage, 0);
                }
            }
        } else {
            page.getPage().setTextRegions(new ArrayList<>());
            if (clearExistingLines){
                for (TextRegion textRegion : page.getPage().getTextRegions()) {
                    textRegion.setTextLines(new ArrayList<>());
                }
            }
            if (newTextLines.size() > 0) {
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
                    newRegion.setTextLines(newTextLines);
                    page.getPage().getTextRegions().add(newRegion);
                }
            }
        }
        if (newTextLines.size() > 0) {
            LOG.info("textlines remaining: " + newTextLines.size() + " " + identifier);
            for (TextLine textLine: newTextLines){
                final TextRegion newRegion = new TextRegion();
                newRegion.setId(UUID.randomUUID().toString());
                newRegion.setCoords(textLine.getCoords());
                newRegion.setTextLines(new ArrayList<>());
                newRegion.getTextLines().add(textLine);
                page.getPage().getTextRegions().add(newRegion);
            }
        }

        List<TextRegion> goodRegions = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
//            if (textRegion.getTextLines().size() > 0 || textRegion.getCustom().contains(":Photo") || textRegion.getCustom().contains(":Drawing")) {
            if (!removeEmptyRegions || textRegion.getTextLines().size() > 0 || textRegion.getCustom().contains(":Photo") || textRegion.getCustom().contains(":Drawing") || textRegion.getCustom().contains(":separator")) {
                goodRegions.add(textRegion);
            }
        }
        page.getPage().setTextRegions(goodRegions);

        return page;
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

        options.addOption(Option.builder("as_single_region").required(false).hasArg(false)
                .desc("Are all baselines in the same region? default is false)").build()
        );
        options.addOption(Option.builder("p2palaconfig").required(false).hasArg(true)
                .desc("Path to P2PaLAConfig used").build()
        );
        options.addOption(Option.builder("laypaconfig").required(false).hasArg(true)
                .desc("Path to laypaconfig used").build()
        );
        options.addOption(Option.builder("margin").required(false).hasArg(true)
                .desc("the margin in pxels used when determining if a text line is part of a text region, default 50")
                .build()
        );
        options.addOption("threads", true, "number of threads to use, default 4");

        options.addOption("help", false, "prints this help dialog");
        options.addOption("invert_image", false, "inverts pixelmap image");
        options.addOption("threshold", true, "threshold to use for binarization of baselinemaps, default 32");
        options.addOption("region_order_list", true, "region_order_list");

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
        List<String> regionOrderList = new ArrayList<>();
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
        boolean asSingleRegion = false;
        String p2palaConfig = null;
        String laypaConfig= null;
        int threshold = 32;

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
        if (commandLine.hasOption("threshold")) {
            threshold = Integer.parseInt(commandLine.getOptionValue("threshold"));
        }
        if (commandLine.hasOption("p2palaconfig")) {
            p2palaConfig = commandLine.getOptionValue("p2palaconfig");
        }
        if (commandLine.hasOption("laypaconfig")) {
            laypaConfig = commandLine.getOptionValue("laypaconfig");
        }

        if (commandLine.hasOption("as_single_region")) {
            asSingleRegion = true;
        }
        if (commandLine.hasOption("margin")) {
           margin = Integer.parseInt(commandLine.getOptionValue("margin"));
        }
        boolean invertImage = commandLine.hasOption("invert_image");

        if (commandLine.hasOption("margin")) {
            margin = Integer.parseInt(commandLine.getOptionValue("margin"));
        }

        if (commandLine.hasOption("region_order_list")) {
            regionOrderList.addAll(Arrays.asList(commandLine.getOptionValue("region_order_list").trim().split(",")));
            regionOrderList.add(null);// default add all regions without type
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

        final P2PaLAConfig p2PaLAConfigContents = p2palaConfig != null ? readP2PaLAConfigFile(p2palaConfig) : null ;
        final LaypaConfig laypaConfigContents = laypaConfig != null ? readLaypaConfigFile(laypaConfig) : null ;


        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        for (Path file : files) {
            if (file.getFileName().toString().endsWith(".png")) {
                if (maxCount != 0) {
                    maxCount--;
//                    String base = FilenameUtils.removeExtension(file.toAbsolutePath().toString());
                    String baseFilename = FilenameUtils.removeExtension(file.getFileName().toString());
                    String xmlFile = Path.of(inputPathPageXml, baseFilename + ".xml").toFile().getAbsolutePath();
                    String baselineImageFile = Path.of(inputPathPng, baseFilename + ".png").toFile().getAbsolutePath();
                    String outputFile = Path.of(outputPathPageXml, baseFilename + ".xml").toFile().getAbsolutePath();
//                    if (Files.exists(Paths.get(xmlFile))) {
                        final Supplier<PcGts> pageSupplier = () -> {
                            try {
                                return PageUtils.readPageFromFile(Path.of(xmlFile));
                            } catch (IOException e) {
                                LOG.error("Cannot read page: " + e);
                                return null;
                            }
                        };

                    Supplier<Mat> baselineImageSupplier = () -> Imgcodecs.imread(baselineImageFile, Imgcodecs.IMREAD_GRAYSCALE);
                        Runnable worker = new MinionExtractBaselines(baselineImageFile, pageSupplier, outputFile,
                                asSingleRegion, p2PaLAConfigContents, laypaConfigContents, baselineImageSupplier,
                                margin, invertImage, threshold, regionOrderList);

                        executor.execute(worker);//calling execute method of ExecutorService
//                    }
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        LOG.info("Finished all threads");
    }

    private void extractAndMergeBaseLines(Supplier<PcGts> pageSupplier, String outputFile, int margin,
                                          P2PaLAConfig p2PaLAConfig, LaypaConfig laypaConfig, int threshold)
            throws IOException, org.json.simple.parser.ParseException {
        boolean addLinesWithoutRegion = true;
        boolean cleanup = true;
        int minimumWidth = 15;
        int minimumHeight = 3;
        Mat baseLineMat = this.baselineImageSupplier.get();
        Mat thresHoldedBaselines = new Mat();
        if (this.invertImage) {
            Imgproc.threshold(baseLineMat, thresHoldedBaselines, threshold, 255, Imgproc.THRESH_BINARY_INV);
        } else {
            Imgproc.threshold(baseLineMat, thresHoldedBaselines, threshold, 255, Imgproc.THRESH_BINARY);
        }
        baseLineMat = OpenCVWrapper.release(baseLineMat);
        Mat stats = new Mat();
        Mat centroids = new Mat();
        Mat labeled = new Mat();
        int numLabels = Imgproc.connectedComponentsWithStats(thresHoldedBaselines, labeled, stats, centroids, 8, CvType.CV_32S);
        centroids = OpenCVWrapper.release(centroids);
        LOG.info("FOUND LABELS:" + numLabels);

        PcGts page = pageSupplier.get();
        if (page == null) {
            page = PageUtils.createFromImage(thresHoldedBaselines, identifier);
        }
        thresHoldedBaselines = OpenCVWrapper.release(thresHoldedBaselines);
        List<TextLine> textLines = extractBaselines(cleanup, minimumHeight, minimumWidth, numLabels, stats, labeled, this.identifier);
        labeled = OpenCVWrapper.release(labeled);
        stats = OpenCVWrapper.release(stats);

        mergeTextLines(page, textLines, addLinesWithoutRegion, this.asSingleRegion, this.identifier,
                false, margin, true);
        if (this.reorderRegionsList.size()>0){
            LayoutProc.reorderRegions(page, this.reorderRegionsList);
        }

        if (p2PaLAConfig != null) {
            LOG.info("adding p2palaconfig info.");
            addP2PaLAInfo(page, p2PaLAConfig);
        }
        if (laypaConfig != null) {
            LOG.info("adding laypaconfig info.");
            addLaypaInfo(page, laypaConfig);
        }

        try {
            final Path outputFilePath = Paths.get(outputFile);
            final Path parent = outputFilePath.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            PageUtils.writePageToFileAtomic(page, outputFilePath);
        } catch (IOException ex) {
            errorLog.accept("Could not write '" + outputFile+"'");
            throw ex;
        }
    }

    public static P2PaLAConfig readP2PaLAConfigFile(String configFile) throws IOException, org.json.simple.parser.ParseException {
        P2PaLAConfig p2PaLAConfig = new P2PaLAConfig();
        if (Strings.isNullOrEmpty(configFile) || !Files.exists(Paths.get(configFile))) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(configFile));

        Map<String, Object> values = new HashMap<>();

        JSONObject args = (JSONObject) jsonObject.get("args");
        for (Object key : args.keySet()) {
            LOG.debug(String.valueOf(key));
            LOG.debug(String.valueOf(args.get(key)));
            if (args.get(key) != null) {
                values.put((String) key, String.valueOf(args.get(key)));
            }
        }
        p2PaLAConfig.setValues(values);

        return p2PaLAConfig;
    }

    public static P2PaLAConfig readP2PaLAConfigFile(InputStream configFile) throws IOException, org.json.simple.parser.ParseException {
        P2PaLAConfig p2PaLAConfig = new P2PaLAConfig();
        if (configFile == null) {
            return null;
        }
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(configFile));

        Map<String, Object> values = new HashMap<>();

        JSONObject args = (JSONObject) jsonObject.get("args");
        for (Object key : args.keySet()) {
            LOG.debug(String.valueOf(key));
            LOG.debug(String.valueOf(args.get(key)));
            if (args.get(key) != null) {
                values.put((String) key, String.valueOf(args.get(key)));
            }
        }
        p2PaLAConfig.setValues(values);

        return p2PaLAConfig;
    }

    public static LaypaConfig readLaypaConfigFile(String configFile) throws IOException, org.json.simple.parser.ParseException {
        LaypaConfig laypaConfig = new LaypaConfig();
        if (Strings.isNullOrEmpty(configFile) || !Files.exists(Paths.get(configFile))) {
            return null;
        }

        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(configFile);
        HashMap yamlMap = (HashMap) yaml.load(inputStream);
        for (Object key : yamlMap.keySet()) {
            System.out.println(key);
        }

        Map<String, Object> values = new HashMap<>();

        for (Object key : yamlMap.keySet()) {
            if (key != null) {
                values.put((String) key, String.valueOf(yamlMap.get(key)));
            }
        }
        laypaConfig.setValues(values);

        return laypaConfig;
    }

    public static LaypaConfig readLaypaConfigFile(InputStream configFile) throws IOException, org.json.simple.parser.ParseException {
        LaypaConfig laypaConfig = new LaypaConfig();
        if (configFile == null) {
            return null;
        }

        Yaml yaml = new Yaml();

        HashMap yamlMap = (HashMap) yaml.load(configFile);
        for (Object key : yamlMap.keySet()) {
            System.out.println(key);
        }

        Map<String, Object> values = new HashMap<>();

        for (Object key : yamlMap.keySet()) {
            if (key != null) {
                values.put((String) key, String.valueOf(yamlMap.get(key)));
            }
        }
        laypaConfig.setValues(values);

        return laypaConfig;
    }

    private void addP2PaLAInfo(PcGts page, P2PaLAConfig p2PaLAConfig) throws IOException, org.json.simple.parser.ParseException {
        ArrayList<MetadataItem> metadataItems = new ArrayList<>();
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("layout-analysis");
        metadataItem.setValue("p2pala");
        Labels labels = new Labels();
        ArrayList<Label> labelsList = new ArrayList<>();
        for (String key : p2PaLAConfig.getValues().keySet()) {
            Label label = new Label();
            label.setType(key);
            Object value = p2PaLAConfig.getValues().get(key);
            label.setValue(String.valueOf(value));
            labelsList.add(label);
        }
        labels.setLabel(labelsList);
        metadataItem.setLabels(labels);
        metadataItems.add(metadataItem);
        page.getMetadata().setMetadataItems(metadataItems);
    }

    private void addLaypaInfo(PcGts page, LaypaConfig laypaConfig) throws IOException, org.json.simple.parser.ParseException {
        ArrayList<MetadataItem> metadataItems = new ArrayList<>();
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("layout-analysis");
        metadataItem.setValue("laypa");
        Labels labels = new Labels();
        ArrayList<Label> labelsList = new ArrayList<>();
        for (String key : laypaConfig.getValues().keySet()) {
            Label label = new Label();
            label.setType(key);
            Object value = laypaConfig.getValues().get(key);
            label.setValue(String.valueOf(value));
            labelsList.add(label);
        }
        labels.setLabel(labelsList);
        metadataItem.setLabels(labels);
        metadataItems.add(metadataItem);
        page.getMetadata().setMetadataItems(metadataItems);
    }

    @Override
    public void run() {
        try {
            LOG.info(this.identifier);
            extractAndMergeBaseLines(this.pageSupplier, outputFile, margin, this.p2palaconfig, this.laypaConfig,
                    this.threshold);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
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