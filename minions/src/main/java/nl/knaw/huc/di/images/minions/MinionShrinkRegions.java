package nl.knaw.huc.di.images.minions;

import com.google.common.base.Stopwatch;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.cli.*;
import org.opencv.core.Core;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinionShrinkRegions extends BaseMinion implements Runnable {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Path file;

    public MinionShrinkRegions(Path file) {
        this.file = file;
    }

    private static void shrinkRegions(Path path) throws IOException {
        int numthreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        try (DirectoryStream<Path> imagesFiles = Files.newDirectoryStream(path)) {
            for (Path imagesFile : imagesFiles) {
                if (Files.isDirectory(imagesFile) || !imagesFile.toString().endsWith(".jpg")) {
                    continue;
                }
                Runnable worker = new MinionShrinkRegions(imagesFile);
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
            System.out.println("Shrinking regions for: " + this.file.toAbsolutePath());
            Stopwatch stopwatch = Stopwatch.createStarted();
            PageUtils.shrinkRegions(this.file);
            System.out.println(this.file.toAbsolutePath() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
