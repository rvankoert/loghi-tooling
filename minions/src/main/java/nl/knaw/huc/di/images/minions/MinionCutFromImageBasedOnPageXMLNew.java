package nl.knaw.huc.di.images.minions;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.BinaryLineStrip;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.OpenCVWrapper;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/*
    this Minion just cuts
 */
public class MinionCutFromImageBasedOnPageXMLNew extends BaseMinion implements Runnable {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Path file;
    private final String outputBase;
    private final boolean overwriteExistingPage;
    private final int minWidth;
    private final int minHeight;
    private final int minWidthToHeight;
    private final String outputType;
    private final int channels;
    private final boolean writeTextContents;
    private final Integer rescaleHeight;
    private final boolean outputBoxFile;
    private final boolean outputTxtFile;
    private final boolean recalculateTextLineContoursFromBaselines;
    private final int minimumXHeight;
    private final boolean useDiforNames;
    private final boolean writeDoneFiles;
    private final boolean ignoreDoneFiles;
    private Integer fixedXHeight;
    private Path pagePath;


    public MinionCutFromImageBasedOnPageXMLNew(Path file, Path pagePath, String outputBase, boolean overwriteExistingPage,
                                               int minWidth, int minHeight, int minWidthToHeight, String outputType,
                                               int channels, boolean writeTextContents, Integer rescaleHeight,
                                               boolean outputBoxFile, boolean outputTxtFile, boolean recalculateTextLineContoursFromBaselines,
                                               Integer fixedXHeight, int minimumXHeight, boolean useDiforNames, boolean writeDoneFiles, boolean ignoreDoneFiles) {
        this.file = file;
        this.pagePath = pagePath;
        this.outputBase = outputBase;
        this.overwriteExistingPage = overwriteExistingPage;
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.minWidthToHeight = minWidthToHeight;
        this.outputType = outputType;
        this.channels = channels;
        this.writeTextContents = writeTextContents;
        this.rescaleHeight = rescaleHeight;
        this.outputBoxFile = outputBoxFile;
        this.outputTxtFile = outputTxtFile;
        this.recalculateTextLineContoursFromBaselines = recalculateTextLineContoursFromBaselines;
        this.fixedXHeight = fixedXHeight;
        this.minimumXHeight = minimumXHeight;
        this.useDiforNames = useDiforNames;
        this.writeDoneFiles = writeDoneFiles;
        this.ignoreDoneFiles = ignoreDoneFiles;
    }

    private static Options getOptions() {
        Options options = new Options();
// -input_path /media/rutger/DIFOR1/data/republicready/385578/3116_1586_2/ -outputbase /media/rutger/DIFOR1/data/republicready/snippets/ -output_type png -channels 4 -write_text_contents
        options.addOption(Option.builder("input_path").required(true).hasArg(true)
                .desc("Directory that contains the images.").build()
        );
        options.addOption(Option.builder("outputbase").required(true).hasArg(true)
                .desc("outputbase. Base output where imagesnippets will be stored").build()
        );
        options.addOption("output_type", true, "jpg or png, default png");
        options.addOption("channels", true, "3 (jpg/png) or 4 (png), default 4");
        options.addOption("threads", true, "number of threads to use, default 1");
        options.addOption("write_text_contents", false, "default false. Use when generating snippets from ground truth");
        options.addOption("xheight", true, "fixed x-height to use. This can help when used on multiple pages that contain text of very similar height.");
        options.addOption("rescaleheight", true, "rescale height");
        options.addOption("min_width", true, "minimum width of baseline");
        options.addOption("difor_names", false, "use the name convention used in the Digital Forensics project");
        options.addOption("page_path", true, "folder that contains the page xml files, by default input_path/page will be used");
        options.addOption("no_page_update", false, "do not update existing page");
        options.addOption("write_done", true, "write done files for iamges that are processed (default true)");
        options.addOption("ignore_done", false, "ignore done files and (re)process all images");
        options.addOption("help", false, "prints this help dialog");
        return options;
    }

    public static void main(String[] args) throws Exception {
//        Core.setNumThreads(Runtime.getRuntime().availableProcessors()/4);
        int numthreads = Runtime.getRuntime().availableProcessors();
//        int numthreads = Runtime.getRuntime().availableProcessors();
        numthreads = 1;
        Path inputPath = Paths.get("/media/rutger/DIFOR1/data/1.05.14/83/");
        String outputbase = "/tmp/output/imagesnippets/";
        boolean overwriteExistingPage = true;
        int minHeight = 5;
        int minWidth = 5;
        int minWidthToHeight = 0;
        Integer rescaleHeight = null;
        String output_type = "png";
        int channels = 4;
        boolean writeTextContents = false;
        boolean outputBoxFile = true;
        boolean outputTxtFile = true;
        boolean diforNames = false;
        boolean recalculateTextLineContoursFromBaselines = true;
        boolean writeDoneFiles = true;
        boolean ignoreDoneFiles = false;
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printHelp(options, "java " + MinionCutFromImageBasedOnPageXMLNew.class.getName());
            return;
        }

        if (cmd.hasOption("help")) {
            printHelp(options, "java " + MinionCutFromImageBasedOnPageXMLNew.class.getName());
            return;
        }

        if (cmd.hasOption("input_path")) {
            inputPath = Paths.get(cmd.getOptionValue("input_path"));
        }
        if (cmd.hasOption("outputbase")) {
            outputbase = cmd.getOptionValue("outputbase");
        }
        if (cmd.hasOption("output_type")) {
            output_type = cmd.getOptionValue("output_type");
        }
        if (cmd.hasOption("channels")) {
            channels = Integer.parseInt(cmd.getOptionValue("channels"));
        }
        if (cmd.hasOption("write_text_contents")) {
            writeTextContents = true;
        }
        Integer fixedXHeight = null;
        if (cmd.hasOption("xheight")) {
            fixedXHeight = Integer.parseInt(cmd.getOptionValue("xheight"));
        }
        int minimumXHeight = LayoutProc.MINIMUM_XHEIGHT;

        if (cmd.hasOption("threads")) {
            numthreads = Integer.parseInt(cmd.getOptionValue("threads"));
        }
        if (cmd.hasOption("rescaleheight")) {
            rescaleHeight = Integer.parseInt(cmd.getOptionValue("rescaleheight"));
        }

        if (cmd.hasOption("min_width")) {
            minWidth = Integer.parseInt(cmd.getOptionValue("min_width"));
        }

        final Path pagePath;
        if (cmd.hasOption("page_path")) {
            pagePath = Paths.get(cmd.getOptionValue("page_path"));
        } else {
            pagePath = inputPath.resolve("page");
        }

        if (cmd.hasOption("no_page_update")) {
            overwriteExistingPage = false;
        }

        if (cmd.hasOption("write_done")) {
             writeDoneFiles = "true".equals(cmd.getOptionValue("write_done"));
        }

        ignoreDoneFiles = cmd.hasOption("ignore_done");

        diforNames = cmd.hasOption("difor_names");

        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            Runnable worker = new MinionCutFromImageBasedOnPageXMLNew(file, pagePath, outputbase, overwriteExistingPage,
                    minWidth, minHeight, minWidthToHeight, output_type, channels, writeTextContents, rescaleHeight,
                    outputBoxFile, outputTxtFile, recalculateTextLineContoursFromBaselines, fixedXHeight, minimumXHeight,
                    diforNames, writeDoneFiles, ignoreDoneFiles);
            executor.execute(worker);
        }


        executor.shutdown();
        executor.awaitTermination(60L, TimeUnit.MINUTES);
    }

    private boolean hasImageExtension(Path file){
        String lowercase = file.toString().toLowerCase();
        return lowercase.endsWith(".jpg")
                || lowercase.endsWith(".jpeg")
                || lowercase.endsWith(".png")
                || lowercase.endsWith(".tif")
                || lowercase.endsWith(".tiff");
    }
    private void runFile(Path file, Path pagePath) throws IOException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        if (hasImageExtension(file)) {
            Mat image;
            System.out.println(file);
            String inputFile = file.toAbsolutePath().toString();
            String fileNameWithoutExtension = FilenameUtils.removeExtension(file.getFileName().toString());
            String inputXmlFile = pagePath.resolve(fileNameWithoutExtension + ".xml").toFile().getAbsolutePath();
            Path inputXmlFilePath = Paths.get(inputXmlFile);
            image = OpenCVWrapper.imread(inputFile);
//            System.out.println("including imread took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (image.size().width == 0 || image.size().height == 0) {
                System.err.println("broken image");
                return;
            }
            if (!new File(outputBase).exists()) {
                new File(outputBase).mkdir();
            }
            File balancedOutputBase = new File (outputBase, fileNameWithoutExtension);
            if (!balancedOutputBase.exists()) {
                balancedOutputBase.mkdir();
            }

            String pageXml = StringTools.readFile(inputXmlFilePath);
            if (Strings.isNullOrEmpty(pageXml)) {
                System.err.println(inputXmlFilePath + " is empty");
                return;
            }
            PcGts page;
            try {
                page = PageUtils.readPageFromString(pageXml);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println(inputXmlFilePath + " does not appear to be a valid PageXml file");
                return;
            }

//            System.out.println("including readPageFromString took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
            final Stopwatch recalc = Stopwatch.createStarted();
            // resize image
            final double shrinkFactor = 4;

            if (recalculateTextLineContoursFromBaselines) {
                LayoutProc.recalculateTextLineContoursFromBaselines(file.toString(), image, page, shrinkFactor);
            }
            System.out.println("recalc: " + recalc.stop());
//            System.out.println("including recalculateTextLineContoursFromBaselines took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                for (TextLine textLine : textRegion.getTextLines()) {
//                    if (textLine.getId().equals("25bffb32-0563-4dd1-b6cb-5b9bd449ecba")){
//                        System.err.println("25bffb32-0563-4dd1-b6cb-5b9bd449ecba");
//                    }
                    List<Point> contourPoints = StringConverter.stringToPoint(textLine.getCoords().getPoints());
                    if (contourPoints.size() == 0) {
                        //TODO: this should not abort the flow
                        continue;
                    }
                    List<Point> baseLinePoints = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                    Integer xHeight = null;
                    TextStyle textStyle = textLine.getTextStyle();
                    if (textStyle != null) {
                        xHeight = textStyle.getxHeight();
                    }

                    if (fixedXHeight != null) {
                        xHeight = fixedXHeight;
                    }
                    if (xHeight == null || xHeight < minimumXHeight) {
                        xHeight = minimumXHeight;
                    }
                    // TODO: determine xheight by histogram
                    // TODO: determin xheight by CoCo (printed/printlike only)
                    // TODO determine xheight by moving entire baseline up and counting binary pixels
                    // TODO: determine xheight by smearing
                    boolean includeMask = this.channels == 4;
//                    System.out.println("before    getBinaryLineStrip took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
                    long before = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                    if (textLine.getId().startsWith("f1eda6a5")) {
                        System.err.println("debug");
                    }
                    String lineStripId = inputXmlFilePath.getFileName().toString() + "-" + textLine.getId();
                    BinaryLineStrip binaryLineStrip = LayoutProc.getBinaryLineStrip(file.toString(), image, contourPoints,
                            baseLinePoints, xHeight, includeMask, minWidth, lineStripId, 4, 3, 2);
//                    System.out.println("getBinaryLineStrip took: " + (stopwatch.elapsed(TimeUnit.MILLISECONDS)-before));
                    Mat lineStrip = null;
                    if (binaryLineStrip != null && binaryLineStrip.getLineStrip() != null) {
                        lineStrip = binaryLineStrip.getLineStrip();
                        xHeight = binaryLineStrip.getxHeight();
                        if (textLine.getTextStyle() == null) {
                            textLine.setTextStyle(new TextStyle());
                        }
                        textLine.getTextStyle().setxHeight(xHeight);
                        if (lineStrip.width() >= minWidth
                                && lineStrip.height() > minHeight
                                && (lineStrip.width() / lineStrip.height()) >= minWidthToHeight) {
//                            String randomUUIDString = inputXmlFilePath.getFileName().toString() + "-" + textRegion.getId()+"-"+textLine.getId();
                            if (rescaleHeight != null) {
                                Mat binaryLineStripNew = new Mat();
                                double targetHeight = rescaleHeight;
                                double heightScale = targetHeight / (double) (lineStrip.height());
                                double newWidth = heightScale * lineStrip.width();
                                if (newWidth < 32) {
                                    newWidth = 32;
                                }
                                Imgproc.resize(lineStrip, binaryLineStripNew, new Size(newWidth, targetHeight));
                                OpenCVWrapper.release(lineStrip);
                                lineStrip = binaryLineStripNew;
                            }
                            if (writeTextContents) {
                                String textValue = "";
                                TextEquiv textEquiv = textLine.getTextEquiv();
                                if (textEquiv != null) {
                                    textValue = textEquiv.getPlainText();
                                    if (!Strings.isNullOrEmpty(textEquiv.getUnicode())) {
                                        textValue = textEquiv.getUnicode();
                                    }
                                    if (Strings.isNullOrEmpty(textValue)) {
                                        System.err.println("empty line... continuing");
                                        continue;
                                    }
                                }
                                if (lineStrip.width() > minWidth
                                        && lineStrip.height() > minHeight
                                        && (lineStrip.width() / lineStrip.height()) >= minWidthToHeight) {
                                    if (outputTxtFile) {
                                        StringTools.writeFile(new File(balancedOutputBase, lineStripId + ".txt").getAbsolutePath(), textValue);
                                    }
                                    if (outputBoxFile) {
                                        String boxValue = LayoutProc.convertToBoxFile(lineStrip.height(), lineStrip.width(), StringTools.makeNew(textValue));
                                        StringTools.writeFile(new File(balancedOutputBase, lineStripId + ".box").getAbsolutePath(), boxValue);
                                    }
                                }
                            }
                            if (this.useDiforNames) {
                                final String filename = new File(balancedOutputBase, "textline_" + fileNameWithoutExtension + "_" + textLine.getId() + "." + this.outputType).getAbsolutePath();
                                System.out.println("save snippet: " + filename);
                                Imgcodecs.imwrite(filename, lineStrip);
                            } else {
                                Imgcodecs.imwrite(new File(balancedOutputBase, lineStripId + "." + this.outputType).getAbsolutePath(), lineStrip);
                            }
                        }
                    }
                    OpenCVWrapper.release(lineStrip);
                }
            }
            if (overwriteExistingPage) {
                String pageXmlString = PageUtils.convertPcGtsToString(page);
                StringTools.writeFile(inputXmlFile, pageXmlString);
            }

            OpenCVWrapper.release(image);
            System.out.println("Single image took: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
            if (this.writeDoneFiles) {
                StringTools.writeFile(file + ".done", "");
            }
        }
    }

    @Override
    public void run() {
        try {
            if (this.ignoreDoneFiles || !Files.exists(Paths.get(this.file + ".done"))) {
                this.runFile(this.file, this.pagePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
