package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MinionConvertPageToTxt {


    private static Options getOptions() {
        Options options = new Options();
        options.addOption("pagexmldir", true, "pagexmldir.");
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
            System.out.println("No pagexmldir specified, exiting.");
            System.exit(1);
        }

        Path pagePath = Paths.get(pagePathTxt);

        try (DirectoryStream<Path> files = Files.newDirectoryStream(pagePath)) {
            for (Path file : files) {
                if (!file.toAbsolutePath().toString().endsWith(".xml")) {
                    continue;
                }
                PcGts page = PageUtils.readPageFromFile(file, true);
                String text = PageUtils.convertToTxt(page, true);
                String outputFile = FilenameUtils.removeExtension(file.toAbsolutePath().toString()) + ".txt";
                System.out.println("writing: " + outputFile);
                StringTools.writeFile(outputFile, text);
            }
        }
    }
}