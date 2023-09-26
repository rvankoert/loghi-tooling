package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class MinionRecalculateReadingOrderNew implements Runnable, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MinionRecalculateReadingOrderNew.class);

    public static final UnicodeToAsciiTranslitirator UNICODE_TO_ASCII_TRANSLITIRATOR = new UnicodeToAsciiTranslitirator();
    private final double interlineClusteringMultiplier;
    private final String identifier;
    private final PcGts page;
    private final Consumer<PcGts> pageSaver;
    private final boolean cleanBorders;
    private final int borderMargin;
    private final boolean asSingleRegion;
    private final double dubiousSizeWidthMultiplier;
    private final Double dubiousSizeWidth;

    private List<String> readingOrderList;

    public MinionRecalculateReadingOrderNew(String identifier, PcGts page, Consumer<PcGts> pageSaver,
                                            boolean cleanBorders, int borderMargin, boolean asSingleRegion,
                                            double interlineClusteringMultiplier, double dubiousSizeWidthMultiplier,
                                            Double dubiousSizeWidth, List<String> readingOrderList) {
        this.identifier = identifier;
        this.page = page;
        this.pageSaver = pageSaver;
        this.cleanBorders = cleanBorders;
        this.borderMargin = borderMargin;
        this.asSingleRegion= asSingleRegion;
        this.interlineClusteringMultiplier = interlineClusteringMultiplier;
        this.dubiousSizeWidthMultiplier = dubiousSizeWidthMultiplier;
        this.dubiousSizeWidth = dubiousSizeWidth;
        if (readingOrderList==null || readingOrderList.size()==0){
            readingOrderList = new ArrayList<>();
            readingOrderList.add(null);
        }
        this.readingOrderList =readingOrderList;
    }

    private static Options getOptions() {
        Options options = new Options();

        options.addOption(Option.builder("input_dir").required(true).hasArg(true)
                .desc("directory of the page files that should be processed").build()
        );
        options.addOption("threads", true, "threads to use, default 2");
        options.addOption("clean_borders", false, "when true removes the small baselines, that are visible on the piece of the adjacent that is visible in the scan (default value is false)");
        options.addOption("border_margin", true, "border_margin, default 200");
        options.addOption("help", false, "prints this help dialog");
        options.addOption("as_single_region", false, "as single region");
        options.addOption("dubious_size_width", true, "the minimum length in pixels the baseline must have to be a valid baseline connected to the side of the iamge, default 5% of the image width");
        options.addOption("dubious_size_width_multiplier", true, "calculate the dubious_size_width, when this property is used the dubious_size_width is used, default 0.05");
        options.addOption("interline_clustering_multiplier", true,  "helps to calculate the maximum cluster distance between two lines, default 1.5");
        options.addOption("reading_order_list", true, "reading_order_list");
        options.addOption("use_2013_namespace", "set PageXML namespace to 2013, to avoid causing problems with Transkribus");

        return options;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }

    public static void main(String[] args) throws IOException, ParseException {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException ex ) {
            printHelp(options, "java " + MinionRecalculateReadingOrderNew.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java " + MinionRecalculateReadingOrderNew.class.getName());
            return;
        }

        String inputDir = "/media/rutger/HDI0002/difor-data-hannah-divide8/page/";
        if (commandLine.hasOption("input_dir")) {
            inputDir = commandLine.getOptionValue("input_dir");
            LOG.info("input_dir: " + inputDir);
        }
        int numthreads = 2;
        if (commandLine.hasOption("threads")) {
            numthreads = Integer.parseInt(commandLine.getOptionValue("threads"));
        }
        boolean cleanBorders = commandLine.hasOption("clean_borders");
        int borderMargin = 200;
        if (commandLine.hasOption("border_margin")) {
            borderMargin = Integer.parseInt(commandLine.getOptionValue("border_margin"));
        }

        boolean asSingleRegion = commandLine.hasOption("as_single_region");

        double interlineClusteringMultiplier = 1.5;
        if (commandLine.hasOption("interline_clustering_multiplier")) {
            interlineClusteringMultiplier = Double.parseDouble(commandLine.getOptionValue("interline_clustering_multiplier"));
        }

        double dubiousSizeWidthMultiplier = 0.05;
        if (commandLine.hasOption("dubious_size_width_multiplier")) {
            dubiousSizeWidthMultiplier = Double.parseDouble(commandLine.getOptionValue("dubious_size_width_multiplier"));
        }

        Double dubiousSizeWidth = null;
        if (commandLine.hasOption("dubious_size_width")) {
            dubiousSizeWidth = Double.parseDouble(commandLine.getOptionValue("dubious_size_width"));
        }

        List<String> readingOrderList = new ArrayList<>();
        if (commandLine.hasOption("reading_order_list")) {
            readingOrderList.addAll(Arrays.asList(commandLine.getOptionValue("reading_order_list").split(",")));
        }

        String namespace = commandLine.hasOption("use_2013_namespace") ? PageUtils.NAMESPACE2013: PageUtils.NAMESPACE2019;

        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(Paths.get(inputDir));
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            if (!file.toFile().isFile()) {
                continue;
            }
            if (file.getFileName().toString().endsWith(".xml")) {
                LOG.info(file.toAbsolutePath().toString());
                final String pageFile = file.toAbsolutePath().toString();
                String pcGtsString = StringTools.loadStringFromFile(pageFile);
                PcGts page = PageUtils.readPageFromString(pcGtsString);

                Consumer<PcGts> pageSaver = newPage -> {
                    try {
                        PageUtils.writePageToFile(newPage, namespace,  Paths.get(pageFile));
                    } catch (IOException e) {
                        LOG.error("Could not save updated page", e);
                    }
                };



                Runnable worker = new MinionRecalculateReadingOrderNew(pageFile, page, pageSaver, cleanBorders,
                        borderMargin, asSingleRegion, interlineClusteringMultiplier, dubiousSizeWidthMultiplier,
                        dubiousSizeWidth, readingOrderList);
                executor.execute(worker);//calling execute method of ExecutorService
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {

        }
    }

    @Override
    public void close() throws Exception {
    }

//    public static PcGts runPage(DocumentImage documentImage, boolean cleanBorders, int borderMargin) {
//        DocumentOCRResult documentOCRResult = MinionCutFromImageBasedOnPageXML.getLatestGroundtruth(documentImage.getDocumentOCRResults());
//        PcGts currentPage = PageUtils.readPageFromString(documentOCRResult.getResult());
//        return runPage(currentPage, cleanBorders, borderMargin);
//    }

    public PcGts runPage(String id, PcGts page, boolean cleanBorders, int borderMargin, boolean asSingleRegion,
                         List<String> readingOrderList) {
        // Minimal length of baseline that is connected to the border of the image
        double dubiousSizeWidth = this.dubiousSizeWidth != null ? this.dubiousSizeWidth : page.getPage().getImageWidth() * dubiousSizeWidthMultiplier;
        List<TextLine> allLines = new ArrayList<>();
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            allLines.addAll(textRegion.getTextLines());
        }
        double interlinemedian = LayoutProc.interlineMedian(allLines);
        LOG.info(id + " interlinemedian: " + interlinemedian);
        if (interlinemedian < 10) {
            interlinemedian = 10;
        }

        List<TextLine> linesToRemove = new ArrayList<>();
        page.getPage().getTextRegions().clear();
        if (cleanBorders) {
            for (TextLine textLine : allLines) {
                List<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                Point textLineStart = points.get(0);
                Point textLineEnd = points.get(points.size() - 1);
//                final String dubious_line_at_border = "dubious line at border";
                if (Math.abs(textLineEnd.x - page.getPage().getImageWidth()) < borderMargin) {
//                    textLine.setTextEquiv(new TextEquiv(null, UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii("border"), "border"));
                    if (StringConverter.distance(textLineStart, textLineEnd) < dubiousSizeWidth) {
//                        textLine.setTextEquiv(new TextEquiv(null, UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii(dubious_line_at_border), dubious_line_at_border));
                        linesToRemove.add(textLine);
                    }
                }
                if (textLineStart.x < borderMargin) {
//                    textLine.setTextEquiv(new TextEquiv(null, UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii("border"), "border"));
                    if (StringConverter.distance(textLineStart, textLineEnd) < dubiousSizeWidth) {
//                        textLine.setTextEquiv(new TextEquiv(null, UNICODE_TO_ASCII_TRANSLITIRATOR.toAscii(dubious_line_at_border), dubious_line_at_border));
                        linesToRemove.add(textLine);
                    }
                }

            }
            allLines.removeAll(linesToRemove);
        }

        List<TextLine> removedLines = new ArrayList<>();
        for (TextLine textLine : allLines) {
            if (removedLines.contains(textLine)) {
                continue;
            }

            List<TextLine> cluster = new ArrayList<>();
            cluster.add(textLine);
            removedLines.add(textLine);

            boolean newLinesAdded = true;
            while (newLinesAdded) {
                newLinesAdded = false;
                for (TextLine mainTextLine : cluster) {
                    List<Point> mainPoints = StringConverter.stringToPoint(mainTextLine.getBaseline().getPoints());
                    double mainTextLineOrientation = LayoutProc.getMainAngle(mainPoints);
                    Point mainTextLineStart = mainPoints.get(0);
                    Point mainTextLineEnd = mainPoints.get(mainPoints.size() - 1);

                    for (TextLine subTextLine : allLines) {
                        if (removedLines.contains(subTextLine)) {
                            continue;
                        }
                        double subTextLineOrientation = LayoutProc.getMainAngle(StringConverter.stringToPoint(subTextLine.getBaseline().getPoints()));
//                if subTextLine same orientation
//                if (LayoutProc.distance())
//                && closeby
                        List<Point> subPoints = StringConverter.stringToPoint(subTextLine.getBaseline().getPoints());
                        Point subTextLineStart = subPoints.get(0);
                        Point subTextLineEnd = subPoints.get(subPoints.size() - 1);

                        //add to cluster if starts are close together
                        double maxDistance = interlinemedian * interlineClusteringMultiplier;
                        if (StringConverter.distance(mainTextLineStart, subTextLineStart) < maxDistance) {
                            cluster.add(subTextLine);
                            removedLines.add(subTextLine);
                            newLinesAdded = true;
                        } else {
                            //add to cluster if centers are vertically close together
                            double horizontalSubPointX = ((subTextLineStart.x + subTextLineEnd.x) / 2);
                            double averageSubPointY = ((subTextLineStart.y + subTextLineEnd.y) / 2);
                            double mainTextLineY = ((mainTextLineStart.y + mainTextLineEnd.y) / 2);
                            if (horizontalSubPointX > mainTextLineStart.x && horizontalSubPointX < mainTextLineEnd.x) {
                                // and y-distance is less than interlineClusteringMultiplier * interline
                                if (Math.abs(averageSubPointY - mainTextLineY) < maxDistance) {
                                    cluster.add(subTextLine);
                                    removedLines.add(subTextLine);
                                    newLinesAdded = true;
                                }
                            }
                        }
                    }
                    if (newLinesAdded) {
                        break;
                    }
                }
            }

//            List<TextLine> finalTextlines = new ArrayList<>();


            TextRegion textRegion = new TextRegion();
            textRegion.setId(UUID.randomUUID().toString());
            Coords coords = new Coords();
            textRegion.setCustom("structure {type:Text;}");
            Rect regionPoints = LayoutProc.getBoundingBoxTextLines(cluster);
            coords.setPoints(StringConverter.pointToString(regionPoints));
            textRegion.setCoords(coords);

            cluster.sort(Comparator.comparing(textLine1 ->
                    StringConverter.distance(new Point(regionPoints.x, regionPoints.y),
                            new Point(regionPoints.x + (StringConverter.stringToPoint(textLine1.getBaseline().getPoints()).get(0).x - regionPoints.x) / 10, StringConverter.stringToPoint(textLine1.getBaseline().getPoints()).get(0).y))));
            int counter = 0;
            for (TextLine textLine1 : cluster) {
                textLine1.setCustom("readingOrder {index:" + counter + ";}");
                counter++;
            }
            textRegion.setTextLines(cluster);


            page.getPage().getTextRegions().add(textRegion);
        }

        LayoutProc.reorderRegions(page, readingOrderList);

        page.getMetadata().setLastChange(new Date());

        if (page.getMetadata().getMetadataItems() == null) {
            page.getMetadata().setMetadataItems(new ArrayList<>());
        }
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("reading-order");
        metadataItem.setValue("loghi-htr-tooling");

        page.getMetadata().getMetadataItems().add(metadataItem);

        return page;
    }

    @Override
    public void run() {
        try {
            PcGts newPage = runPage(identifier, page, cleanBorders, borderMargin, asSingleRegion, readingOrderList);
            pageSaver.accept(newPage);
        } finally {
            try {
                this.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
