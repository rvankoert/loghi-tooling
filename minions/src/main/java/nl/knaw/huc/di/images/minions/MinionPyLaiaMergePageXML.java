package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextRegion;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinionPyLaiaMergePageXML extends BaseMinion implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MinionPyLaiaMergePageXML.class);

    private final Path file;
    private static final Map<String, String> map = new HashMap<>();
    private final UnicodeToAsciiTranslitirator unicodeToAsciiTranslitirator;

    public MinionPyLaiaMergePageXML(Path file) {
        this.file = file;
        unicodeToAsciiTranslitirator = new UnicodeToAsciiTranslitirator();
    }

    private void runFile(Path file) throws IOException {
        if (file.toString().endsWith(".xml")) {
            LOG.info(file + " processing...");
            String pageXml = StringTools.readFile(file.toAbsolutePath().toString());
            PcGts page = PageUtils.readPageFromString(pageXml);

            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                for (TextLine textLine : textRegion.getTextLines()) {
                    String targetFileName = file.getFileName().toString();
                    String text = map.get(targetFileName + "-" + textLine.getId());
                    if (text == null) {
                        continue;
                    }
                    TextEquiv textEquiv = new TextEquiv(null, unicodeToAsciiTranslitirator.toAscii(text),text);
                    textLine.setTextEquiv(textEquiv);
//
//                    try (BufferedReader br = new BufferedReader(new FileReader("/home/rutger/src/PyLaia-examples/ijsberg/results.txt"))) {
//                        String line;
//                        while ((line = br.readLine()) != null) {
//                            String[] splitted = line.split(" ");
//                            String filename = splitted[0];
//                            splitted = filename.split(".xml-");
//                            if (!(splitted[0] + ".xml").equals(file.getFileName().toString())) {
//                                continue;
//                            }
//                            if (!splitted[1].equals(textLine.getId())) {
//                                continue;
//                            }
//                            String text = line.substring(filename.length() + 1);
//                            text = text.replace(" ", "").replace("<space>", " ");
//                            textLine.getTextEquiv().setUnicode(text);
//                            textLine.getTextEquiv().setPlainText(text);
//                            System.out.println(filename);
//                        }
//                    }

                }
            }
            String pageXmlString = PageUtils.convertPcGtsToString(page);
            StringTools.writeFile(file.toAbsolutePath().toString(), pageXmlString);
        }
    }

    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("input_path").hasArg(true).required(true)
                .desc("Page to be updated with the htr results").build()
        );

        options.addOption(Option.builder("results_file").hasArg(true).required(true)
                .desc("File with the htr results").build()
        );

        options.addOption("help", false, "prints this help dialog");

//        options.addOption("overwrite_existing_page", true, "true / false, default true");

        return options;
    }

    public static void main(String[] args) throws Exception {
        int numthreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        Path inputPath = Paths.get("/media/rutger/DIFOR1/data/1.05.14/83/page");
        String resultsFile = "/tmp/output/results.txt";
        boolean overwriteExistingPage = true;
        final Options options = getOptions();
        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java "+ MinionPyLaiaMergePageXML.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java "+ MinionPyLaiaMergePageXML.class.getName());
            return;
        }

        inputPath = Paths.get(commandLine.getOptionValue("input_path"));
        resultsFile = commandLine.getOptionValue("results_file");

        readDictionary(resultsFile);

        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            Runnable worker = new MinionPyLaiaMergePageXML(file);
            executor.execute(worker);
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private static void readDictionary(String resultsFile) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(resultsFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split(" ");
                String filename = splitted[0];
                String text = line.substring(filename.length() + 1);
                splitted = filename.split("/");
                filename = splitted[splitted.length - 1].replace(".jpg", "");
                text = text.replace(" ", "").replace("<space>", " ").trim();
                map.put(filename.trim(), text);
                LOG.debug(filename + " appended to dictionary");
            }
        }

    }

    @Override
    public void run() {
        try {
            this.runFile(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
