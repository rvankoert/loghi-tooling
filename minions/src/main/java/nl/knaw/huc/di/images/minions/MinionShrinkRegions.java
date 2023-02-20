package nl.knaw.huc.di.images.minions;

import com.google.common.base.Stopwatch;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinionShrinkRegions extends BaseMinion implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MinionShrinkRegions.class);

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Path imageFile;
    private final Path pageFile;

    public MinionShrinkRegions(Path imageFile, Path pageFile) {
        this.imageFile = imageFile;
        this.pageFile = pageFile;
    }

    private static void shrinkRegions(Path imagePath) throws IOException {
        int numthreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        try (DirectoryStream<Path> imageFiles = Files.newDirectoryStream(imagePath)) {
            for (Path imageFile : imageFiles) {
                if (Files.isDirectory(imageFile) || !imageFile.toString().endsWith(".jpg")) {
                    continue;
                }
                Path pagePath = imageFile.toAbsolutePath().getParent().resolve("page").resolve(FilenameUtils.removeExtension(imageFile.getFileName().toString()) + ".xml");
                Runnable worker = new MinionShrinkRegions(imageFile, pagePath);
                executor.execute(worker);//calling execute method of ExecutorService

            }
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("input").hasArg(true).required(true)
                .desc("input folder that contains the images and page-folder that has to be updated").build());
        options.addOption("help", false, "prints this help dialog");

        return options;
    }

    public static void main(String[] args) throws Exception {
        String path = "/data/statengeneraalhandwritten/";

        CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            printHelp(getOptions(), "java " + MinionShrinkRegions.class.getName());
            return;
        }

        if (commandLine.hasOption("help")){
            printHelp(getOptions(), "java " + MinionShrinkRegions.class.getName());
            return;
        }

        path = commandLine.getOptionValue("input");

        shrinkRegions(Paths.get(path));
    }

    @Override
    public void run() {
        try {
            LOG.info("Shrinking regions for: " + this.imageFile.toAbsolutePath());
            Stopwatch stopwatch = Stopwatch.createStarted();
            PageUtils.shrinkRegions(this.imageFile, this.pageFile);
            LOG.debug(this.imageFile.toAbsolutePath() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
