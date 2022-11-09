package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Strings;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.imageanalysiscommon.connectedComponent.ConnectedComponentProc;
import nl.knaw.huc.di.images.imageanalysiscommon.imageConversion.ImageConversionHelper;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

public class MinionGeneratePageImages {

    public static final String FONT_PATH = "font_path";
    public static final String TEXT_PATH = "text_path";
    public static final String OUTPUT_PATH = "output_path";
    public static final String CHANCE_ITALIC = "chance_italic";
    public static final String CHANCE_BOLD = "chance_bold";
    public static final String CHANCE_UNDERLINE = "chance_underline";
    public static final String CHANCE_UPPERCASE = "chance_uppercase";
    public static final String CHANCE_LINE = "chance_line";
    public static final String MIN_FONT_SIZE = "min_font_size";
    public static final String MAX_FONT_SIZE = "max_font_size";
    public static final String MAX_TEXT_LENGTH = "max_text_length";
    public static final String MULTIPLY = "multiply";
    public static final String MAX_FILES = "max_files";
    static double chanceUpperCase = 0.2d;
    static int maxTextLength = 150;
    private static String largeText = "";
    private static Random random = null;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static List<String> readFonts(String pathString) throws Exception {
        Path path = Paths.get(pathString);
        ArrayList<String> fonts = new ArrayList<>();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
            for (Path file : files) {
                if (file.getFileName().toString().endsWith(".ttf")) {
                    try {
                        GraphicsEnvironment ge =
                                GraphicsEnvironment.getLocalGraphicsEnvironment();
                        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(file.toAbsolutePath().toString()));
                        ge.registerFont(font);
                        BufferedImage img = generateTextLine("bleet", new Color(255, 255, 255), new Color(0, 0, 0), 250, 250, new Font(font.getName(), Font.PLAIN, 40));

                        Mat result = new Mat();
                        Mat input = ImageConversionHelper.bufferedImageToMat(img);
                        Mat grayImage = new Mat();
                        if (input.type() != CV_8U) {
                            Imgproc.cvtColor(input, grayImage, Imgproc.COLOR_BGR2GRAY);
                        } else {
                            input.copyTo(grayImage);
                        }

                        Imgproc.threshold(grayImage, result, 10, 255, THRESH_BINARY);

                        img = ImageConversionHelper.matToBufferedImage(result);
                        ConnectedComponentProc coCoProc = new ConnectedComponentProc();
                        List<ConnectedComponent> cocos = coCoProc.process(img, false);
//                        if (cocos.size() == 1) {
////                            ImageIO.write(img, "png", new File("/tmp/font.png"));
                        fonts.add(font.getName());
//                        }
                    } catch (IOException | FontFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        return fonts;
    }

    private static ArrayList<String> getAllUsableFonts() {
        ArrayList<String> returnFonts = new ArrayList<>();
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String fontName : fonts) {
            Font font = new Font(fontName, Font.PLAIN, 32);
            if (font.canDisplay('ſ')
                    && font.canDisplay('ﬅ')
                    && font.canDisplay('ﬄ')
                    && font.canDisplay('ﬃ')
                    && font.canDisplay('ﬂ')
                    && font.canDisplay('æ')
                    && font.canDisplay('Æ')
                    && font.canDisplay('œ')
                    && !fontName.equals("Adobe Blank")
            ) {
                System.out.println(fontName);
                returnFonts.add(fontName);
            }

        }
        return returnFonts;
    }

    private static boolean canDisplay(Font font, String text) {
        for (char character : text.toCharArray()) {
            if (!font.canDisplay(character)) {
                return false;
            }
        }
        return true;
    }

    private static Options getOptions() {
        Options options = new Options();
// -input_path /media/rutger/DIFOR1/data/republicready/385578/3116_1586_2/ -outputbase /media/rutger/DIFOR1/data/republicready/snippets/ -output_type png -channels 4 -write_text_contents
        options.addOption(Option.builder(TEXT_PATH).required(true).hasArg(true)
                .desc("path with plain text files that are used to generated page and images.").build()
        );
        options.addOption(Option.builder(OUTPUT_PATH).required(true).hasArg(true)
                .desc("Base output where imagesnippets will be stored").build()
        );
        options.addOption(Option.builder(FONT_PATH).required(true).hasArg(true).desc("Path of the fonts to use")
                .build()
        );
        options.addOption("make_old", false, "make_old, uses old style chars such as long s (default: false)");
        options.addOption("add_salt_and_pepper", false, "Adds salt and pepper noise");
        options.addOption("random_augment", false, "randomly augment the images");
        options.addOption("underline", false, "underline the generated text");
        options.addOption("help", false, "prints this help dialog");
        options.addOption(CHANCE_ITALIC, false, "chance that lines that should be italic (default: 0.2)");
        options.addOption(CHANCE_BOLD, false, "chance that lines that should be bold (default: 0.2)");
        options.addOption(CHANCE_UNDERLINE, false, "chance that lines that should be underline (default: 0.2)");
        options.addOption(CHANCE_UPPERCASE, false, "chance that lines that should be uppercase (default: 0.2)");
        options.addOption(CHANCE_LINE, false, "chance that lines that should be underline (default: 0.2)");
        options.addOption(MIN_FONT_SIZE, true, "Minimal font size (default: 36)");
        options.addOption(MAX_FONT_SIZE, true, "Maximum font size (default: 96)");
        options.addOption(MAX_TEXT_LENGTH, true, "Maximum number of chararacters of the text (default: 150)");
        options.addOption(MULTIPLY, true, "The amount of times a piece of text should be used. (default: 1)");
        options.addOption(MAX_FILES, true, "The maximum number of generated files (default: 500000");


        return options;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }

    public static void main(String[] args) throws Exception {
        String outputpath = "/scratch/synthetic/";
        outputpath = "/media/rutger/HDI0002/synthetic/";

        String textPath = "/home/rutger/data/text/";
        textPath = "/media/rutger/HDI0002/toLaptop/text/";

        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java " + MinionGeneratePageImages.class.getName());
            return;
        }

        if (cmd.hasOption("help")) {
            printHelp(options, "java " + MinionGeneratePageImages.class.getName());
            return;
        }

        if (cmd.hasOption(TEXT_PATH)) {
            textPath = cmd.getOptionValue(TEXT_PATH);
        }
        if (cmd.hasOption(OUTPUT_PATH)) {
            outputpath = cmd.getOptionValue(OUTPUT_PATH);
        }
        List<String> fonts;
        if (cmd.hasOption(FONT_PATH)) {
            fonts = readFonts(cmd.getOptionValue(FONT_PATH));
        } else {
            fonts = readFonts("/home/rutger/Downloads/toLaptop/fonts/usable/");
        }

        boolean random_augment = cmd.hasOption("random_augment");
        boolean add_salt_and_pepper = cmd.hasOption("add_salt_and_pepper");
        boolean makeOld = cmd.hasOption("make_old");
        boolean underline = cmd.hasOption("underline");
        double chanceItalic = 0.2d;
        double chanceBold = 0.2d;
        double chanceUnderline = 0.2d;
        double chanceLine = 0.2d;
        int fontMinSize = 36;
        int fontMaxSize = 96;
        int multiply = 1;
        int maxFiles = 500000;

        chanceItalic = getDoubleValue(cmd, chanceItalic, CHANCE_ITALIC);
        chanceBold = getDoubleValue(cmd, chanceBold, CHANCE_BOLD);
        chanceUnderline = getDoubleValue(cmd, chanceUnderline, CHANCE_UNDERLINE);
        chanceUpperCase = getDoubleValue(cmd, chanceUpperCase, CHANCE_UPPERCASE);
        chanceLine = getDoubleValue(cmd, chanceLine, CHANCE_LINE);
        fontMinSize = getIntValue(cmd, fontMinSize, MIN_FONT_SIZE);
        fontMaxSize = getIntValue(cmd, fontMaxSize, MIN_FONT_SIZE);
        maxTextLength = getIntValue(cmd, maxTextLength, MAX_TEXT_LENGTH);
        multiply = getIntValue(cmd, multiply, MULTIPLY);
        maxFiles = getIntValue(cmd, maxFiles, MAX_FILES);



//        List<String> fonts = readHandwrittenFonts("/home/rutger/fonts/ttf/");
//        List<String> fonts = getAllUsableFonts();

        int counter = 0;
        StringBuilder merged = new StringBuilder();
        Path path = Paths.get(textPath);
        if (!new File(outputpath).exists()) {
            new File(outputpath).mkdirs();
        }
        PrintWriter out = new PrintWriter(outputpath + "synthetic.txt");

        try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
            for (Path file : files) {
                if (!file.toAbsolutePath().toString().endsWith(".txt")) {
                    continue;
                }
                try {
                    largeText = StringTools.readFile(file.toAbsolutePath().toString());
                } catch (Exception ex) {
                    System.out.println("error: " + file.toAbsolutePath().toString());
                    continue;
                }
                List<String> splitted = Arrays.asList(largeText.split("\n"));
                int skip = (int) (getRandom().nextDouble() * splitted.size()) - 40;
                if (skip < 0) {
                    skip = 0;
                }
                splitted = splitted.stream().skip(skip).limit(40).collect(Collectors.toList());

                for (int i = 0; i < multiply; i++) {
                    Font font = getRandomFont(fonts, fontMinSize, fontMaxSize, chanceBold, chanceItalic);
                    Font font2 = null;
                    Map<TextAttribute, Object> attributes = new HashMap<>();
                    //Tracking should be somewhere between -0.1 and 0.3
//                        double tracking = (getRandom().nextDouble() * 0.4) - 0.1;
                    double tracking = (getRandom().nextDouble() * 0.4) - 0.1;
                    attributes.put(TextAttribute.TRACKING, tracking);
                    font2 = font.deriveFont(attributes);

                    int maxWidth = 0;
                    int maxheight = 0;
                    int totalHeight = 0;
                    int spaceWidth = 0;
                    double spacing = 0.5 + getRandom().nextDouble() * 1.0;

                    for (String line : splitted) {
                        String text = line;
                        if (text.length() > maxTextLength) {
                            text = text.substring(0, maxTextLength).trim();
                        }
                        text = text.trim();
                        if (text.isEmpty()) {
                            continue;
                        }
                        if (text.contains(">>pagina-aanduiding<<")) {
                            continue;
                        }
                        if (makeOld) {
                            text = StringTools.makeOld(text);
                        }
                        if (chanceUpperCase > getRandom().nextDouble()) {
                            text = text.toUpperCase();
                        }
                        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D graphics2D = img.createGraphics();
//                    List<String> fonts = getAllUsableFonts(text);
                        if (!canDisplay(font, text)) {
                            continue;
                        }


                        graphics2D.setFont(font2);
                        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

                        FontMetrics fm = graphics2D.getFontMetrics();
                        int width = fm.stringWidth(" " + text + " ");
                        spaceWidth = fm.stringWidth(" ");
                        if (width > maxWidth) {
                            maxWidth = width;
                        }
                        int height = fm.getHeight();
                        totalHeight += height;
                        if (height > maxheight) {
                            maxheight = height;
                        }
                    }

//                    String text = aSplitted;
//                    if (text.length() > maxTextLength) {
//                        text = text.substring(0, maxTextLength).trim();
//                    }
//                    text = text.trim();
//                    if (text.isEmpty()) {
//                        continue;
//                    }
//                    if (text.contains(">>pagina-aanduiding<<")) {
//                        continue;
//                    }
//
//                    if (makeOld) {
//                        text = StringTools.makeOld(text);
//                    }
//                    if (chanceUpperCase > getRandom().nextDouble()) {
//                        text = text.toUpperCase();
//                    }
//
//                    Map<TextAttribute, Object> attributes = new HashMap<>();
//                    //Tracking should be somewhere between -0.1 and 0.3

//                    System.out.println(counter + font.getName());
                    if (maxWidth == 0) {
                        System.out.println(file);
                        System.out.println(font.getName());
                        System.out.println(counter + font2.getName());
                        continue;
                    }
                    counter++;
                    BufferedImage img = generatePageClean(splitted, totalHeight, maxWidth, maxheight, maxWidth, font2, spaceWidth, spacing, counter, outputpath);
                    //baseline is exact at position "height" and runs from spaceWidth to spaceWidth+width
                    Mat originalMat = ImageConversionHelper.bufferedImageToMat(img);
                    if (chanceLine > getRandom().nextDouble()) {
                        org.opencv.core.Point startPoint = new org.opencv.core.Point(getRandom().nextInt(originalMat.width()), getRandom().nextInt(originalMat.height()));
                        org.opencv.core.Point endPoint = new org.opencv.core.Point(getRandom().nextInt(originalMat.width()), getRandom().nextInt(originalMat.height()));
                        Scalar color = new Scalar(getRandom().nextInt(256), getRandom().nextInt(256), getRandom().nextInt(256));
                        Imgproc.line(originalMat, startPoint, endPoint, color);
                    }

                    double currentHeight = (double) originalMat.height();

//                        int rowStart = (int) (getRandom().nextDouble() * 0.2 * currentHeight);
//                        int rowEnd = (int) (0.8 * currentHeight + getRandom().nextDouble() * 0.2 * currentHeight);
                    Mat mat = originalMat;
                    // Baseline
                    // Imgproc.line(mat,new org.opencv.core.Point(spaceWidth,height), new org.opencv.core.Point(width-spaceWidth,height),new Scalar(255,0,0));
                    if (underline && getRandom().nextDouble() < chanceUnderline) {
                        double linelocation = maxheight * (1 + getRandom().nextDouble() * 0.3);
                        Imgproc.line(mat, new org.opencv.core.Point(spaceWidth, linelocation), new org.opencv.core.Point(maxWidth - spaceWidth, linelocation), new Scalar(20, 25, 23));
                    }
                    String filename = "synthetic" + String.format("%05d", counter);
                    String fullPath = outputpath + "/" + filename + ".png";
                    System.out.println(filename + " " + font.getName());
//                    ImageIO.write(img, "png", new File(fullPath));

                    if (add_salt_and_pepper) {
                        Mat saltpepperNoise = Mat.zeros(mat.rows(), mat.cols(), CV_8U);
                        Core.randu(saltpepperNoise, 0, 255);
                        Mat black = Mat.zeros(mat.rows(), mat.cols(), CV_8U);
                        Mat white = Mat.zeros(mat.rows(), mat.cols(), CV_8U);


                        int upperbound = 255 - getRandom().nextInt(55);
                        int lowerbound = getRandom().nextInt(55);
                        Imgproc.threshold(saltpepperNoise, black, upperbound, 255, THRESH_BINARY);
                        Imgproc.threshold(saltpepperNoise, white, lowerbound, 255, THRESH_BINARY_INV);


                        mat.setTo(new Scalar(200, 200, 200), white);
                        mat.setTo(new Scalar(0, 0, 0), black);
                        saltpepperNoise.release();
                        black.release();
                        white.release();
                        int randomSigmaX = getRandom().nextInt(25);
                        Imgproc.GaussianBlur(mat, mat, new Size(11, 11), randomSigmaX);

                    }

//                        double forcedOutputWidth = mat.width() * (forcedOutputHeight / (double) mat.height());
//                        Imgproc.resize(mat, mat, new Size(forcedOutputWidth, forcedOutputHeight));

                    Imgcodecs.imwrite(fullPath, mat);
                    mat.release();
//                    merged.append(filename).append(" d d d d d d d ").append(text.trim().replace(" ", "|")).append("\n");
//                    String boxText = LayoutProc.convertToBoxFile(maxheight, maxWidth, text.trim());
//                    StringTools.writeFile(outputpath + "/" + filename + ".box", boxText);
                    out.print(merged);
                    merged = new StringBuilder();
                    if (counter == maxFiles) {
                        out.flush();
                        out.close();
                        return;
                    }
                }
            }
        }

        out.flush();
        out.close();
    }

    private static double getDoubleValue(CommandLine cmd, double defaultValue, String optionName) {
        if (cmd.hasOption(optionName)) {

            return Double.parseDouble(cmd.getOptionValue(optionName));

        }
        return defaultValue;
    }

    private static int getIntValue(CommandLine cmd, int defaultValue, String optionName) {
        if (cmd.hasOption(optionName)) {

            return Integer.parseInt(cmd.getOptionValue(optionName));

        }
        return defaultValue;
    }

    private static Font getRandomFont(List<String> fonts, int fontMinsize, int fontMaxSize, double chanceBold, double chanceItalic) {
        String fontName = fonts.get(getRandom().nextInt(fonts.size()));
        Font font;
        int fontSize = getRandom().nextInt(fontMaxSize - fontMinsize) + fontMinsize;
        if (getRandom().nextDouble() < chanceItalic) {
            font = new Font(fontName, Font.ITALIC, fontSize);
        } else if (getRandom().nextDouble() < chanceBold) {
            font = new Font(fontName, Font.BOLD, fontSize);

        } else {
            font = new Font(fontName, Font.PLAIN, fontSize);

        }
        return font;
    }

    private static BufferedImage generateTextLine(String text, Color foregroundColor, Color backgroundColor, int height, int width, Font font) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(foregroundColor);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        return img;
    }

    private static BufferedImage generatePageClean(List<String> lines, int totalHeight, int totalWidth, int height, int width, Font font, int spaceWidth, double spacing, int counter, String outputpath) throws IOException {
//        int pageHeight = 5000;
//        int pageWidth = 1000;
        PcGts page = new PcGts();
        TextRegion textRegion = new TextRegion();
        textRegion.setId(UUID.randomUUID().toString());
        page.getPage().getTextRegions().add(textRegion);
        System.out.println(totalWidth);
        BufferedImage img = new BufferedImage(totalWidth, (int) (height * lines.size() + lines.size() * spacing * 2), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2d = img.createGraphics();
        Color backGroundColor = new Color(getRandom().nextInt(60) + 180, getRandom().nextInt(60) + 180, getRandom().nextInt(30) + 180);
        g2d.setColor(backGroundColor);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        Color foreGroundColor = new Color(getRandom().nextInt(60), getRandom().nextInt(60), getRandom().nextInt(60));
        g2d.setColor(foreGroundColor);
//        FontMetrics fm = g2d.getFontMetrics();
//        width = fm.stringWidth( " " + text + " ");
//        spaceWidth = fm.stringWidth( " ");
//        height = fm.getHeight();
        FontMetrics fm = g2d.getFontMetrics();
        int linecounter = 0;
        double baselineY = 0;
        page.getPage().setImageWidth(img.getWidth());
        page.getPage().setImageHeight(img.getHeight());

        String filename = "synthetic" + String.format("%010d", counter);
        page.getPage().setImageFilename(filename + ".png");
        for (int i = 0; i < lines.size(); i++) {

            String text = lines.get(i).trim();
            if (chanceUpperCase > getRandom().nextDouble()) {
                text = text.toUpperCase();
                if (text.length() > maxTextLength) {
                    text = text.substring(0, (int) (maxTextLength / 1.5)).trim();
                }

            }

            if (text.length() > maxTextLength) {
                text = text.substring(0, maxTextLength).trim();
            }

            int stringHeight = fm.getHeight();
            baselineY += (stringHeight + spacing);
            if (Strings.isNullOrEmpty(text.trim())) {
                continue;
            }
            linecounter++;
            g2d.drawString(text, spaceWidth, (int) (baselineY));
            final int textWidth = Math.max(g2d.getFontMetrics().stringWidth(text), 1);
            final double charWidth = textWidth / (double) text.length();
            final double maxLength = Math.min(text.length(), spaceWidth * charWidth);
            System.out.println(maxLength);

            TextLine textLine = new TextLine();
            textLine.setId(UUID.randomUUID().toString());
            textLine.setCustom("readingOrder {index:" + linecounter + ";}");
            TextEquiv textEquiv = new TextEquiv();
            textLine.setTextEquiv(textEquiv);
            textEquiv.setPlainText(text.substring(0, (int) maxLength).trim());
            textEquiv.setUnicode(text.substring(0, (int) maxLength).trim());
            Baseline baseline = new Baseline();
            textLine.setBaseline(baseline);
            Coords coords = new Coords();
            textLine.setCoords(coords);
            List<Point> points = new ArrayList<>();
            points.add(new Point(spaceWidth, baselineY));
            int stringWidth = fm.stringWidth(text);
            final int baseLineWidth = Math.min(spaceWidth + stringWidth, totalWidth);
            points.add(new Point(baseLineWidth, baselineY));
            baseline.setPoints(StringConverter.pointToString(points));


            points.add(new Point(baseLineWidth + 1, baselineY + 1));
            points.add(new Point(baseLineWidth - 1, baselineY - 1));

            coords.setPoints(StringConverter.pointToString(points));

            textRegion.getTextLines().add(textLine);
            textRegion.setCoords(coords);
        }
        XmlMapper mapper = new XmlMapper();
        LayoutProc.reorderRegions(page);
        String pageXml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);

        final Path pageFolder = Paths.get(outputpath).resolve("page");
        pageFolder.toFile().mkdir();
        String fullPath = pageFolder.resolve(filename + ".xml").toFile().getAbsolutePath();

        StringTools.writeFile(fullPath, pageXml);
        g2d.dispose();

        return img;
    }

    private static String getRandomText() {
        String[] splitted = largeText.split("\n");
        int size = splitted.length;
        int candidate = getRandom().nextInt(size);
        String text = splitted[candidate].trim();
        if (text.length() > 100) {
            return text.substring(0, 100).trim();
        }
        return splitted[candidate].trim();
    }

    private static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }
}