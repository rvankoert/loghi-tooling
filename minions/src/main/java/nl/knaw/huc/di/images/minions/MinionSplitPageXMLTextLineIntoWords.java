package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MinionSplitPageXMLTextLineIntoWords {
    private static final Logger LOG = LoggerFactory.getLogger(MinionSplitPageXMLTextLineIntoWords.class);

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
                String pageXml = StringTools.readFile(file);
                PcGts page = PageUtils.readPageFromString(pageXml);
                LayoutProc.splitLinesIntoWords(page);
                String newPageXml = PageUtils.convertPcGtsToString(page);
                StringTools.writeFile(file.toAbsolutePath().toString(), newPageXml);
            }

        }
    }
}
