package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MinionSplitPageXMLTextLineIntoWords implements Runnable, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(MinionSplitPageXMLTextLineIntoWords.class);

    private final String identifier;
    private final Supplier<PcGts> pageSupplier;
    private final String outputFile;
    private final Consumer<String> errorLog;

    public MinionSplitPageXMLTextLineIntoWords(String identifier, Supplier<PcGts> pageSupplier, String outputFile) {
        this(identifier, pageSupplier, outputFile, error -> {});
    }

    public MinionSplitPageXMLTextLineIntoWords(String identifier, Supplier<PcGts> pageSupplier, String outputFile, Consumer<String> errorLog) {

        this.identifier = identifier;
        this.pageSupplier = pageSupplier;
        this.outputFile = outputFile;
        this.errorLog = errorLog;
    }

    public static Options getOptions() {
        final Options options = new Options();
        options.addOption(
                Option.builder("input_path").required(true).hasArg(true).desc("The folder that contains the PAGE files that should be updated").build()
        );
        options.addOption("help", false, "prints this help dialog");
        return options;
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }

    public static void main(String[] args) throws Exception {
        int numthreads = 4;

        ExecutorService executor = Executors.newFixedThreadPool(numthreads);
        String input = "/scratch/limited/page";

        final Options options = getOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java " + MinionSplitPageXMLTextLineIntoWords.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java " + MinionSplitPageXMLTextLineIntoWords.class.getName());
            return;
        }
        input = commandLine.getOptionValue("input_path");

//        if (args.length > 0) {
//            input = args[0];
//        }
        Path inputPath = Paths.get(input);
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            if (file.getFileName().toString().endsWith(".xml")) {
                LOG.info(file + ": processing");

                if (Files.exists(file)) {
                    final Supplier<PcGts> pageSupplier = () -> {
                        try {
                            return PageUtils.readPageFromFile(file);
                        } catch (IOException e) {
                            LOG.error("Cannot read page: " + e);
                            return null;
                        }
                    };
                    String outputFile = file.toString();

                    Runnable worker = new MinionSplitPageXMLTextLineIntoWords(outputFile, pageSupplier, outputFile);
                    executor.execute(worker);
                }
            }
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    public void splitIntoWords(Supplier<PcGts> pageSupplier, String outputFile) throws IOException {
        PcGts page = pageSupplier.get();
        if (page == null) {
            throw new IOException("Could not load page.");
        }
        LayoutProc.splitLinesIntoWords(page);

        try {
            final Path outputFilePath = Paths.get(outputFile);
            final Path parent = outputFilePath.getParent();
            if (!Files.exists(parent)) {
                Files.createDirectory(parent);
            }
            PageUtils.writePageToFile(page, outputFilePath);
        } catch (IOException ex) {
            errorLog.accept("Could not write '" + outputFile+"'");
            throw ex;
        }
    }

    @Override
    public void run() {
        try {
            LOG.info(this.identifier);
            splitIntoWords(this.pageSupplier, outputFile);
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
