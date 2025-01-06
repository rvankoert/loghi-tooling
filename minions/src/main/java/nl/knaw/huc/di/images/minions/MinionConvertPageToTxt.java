package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MinionConvertPageToTxt {
    private static final Logger LOG = LoggerFactory.getLogger(MinionConvertPageToTxt.class);


    private static Options getOptions() {
        Options options = new Options();
        options.addOption("pagexmldir", true, "pagexmldir.");
        options.addOption("no_overwrite", false, "do not overwrite txt files.");
        return options;
    }

    public static void main(String[] args) throws Exception {
        String pagePathTxt = "/media/rutger/HDI0002/ijsberg/1.05.21/page";

        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption("pagexmldir")) {
            pagePathTxt = cmd.getOptionValue("pagexmldir");
        } else {
            LOG.error("No pagexmldir specified, exiting.");
            System.exit(1);
        }
        boolean overwriteTxtFiles = !cmd.hasOption("no_overwrite");
        Path pagePath = Paths.get(pagePathTxt);

        try (DirectoryStream<Path> files = Files.newDirectoryStream(pagePath)) {
            for (Path file : files) {
                if (!file.toAbsolutePath().toString().endsWith(".xml")) {
                    continue;
                }
                String outputFile = FilenameUtils.removeExtension(file.toAbsolutePath().toString()) + ".txt";

                if (!overwriteTxtFiles && Files.exists(Paths.get(outputFile))){
                    LOG.info("skipping: " + outputFile);
                    continue;
                }
                PcGts page = PageUtils.readPageFromFile(file, true);
                String text = PageUtils.convertToTxt(page, true);
                LOG.info("writing: " + outputFile);
                StringTools.writeFile(outputFile, text);
            }
        }
    }
}