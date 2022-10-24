package nl.knaw.huc.di.images.minions;

import com.google.common.base.Stopwatch;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.cli.*;
import org.opencv.core.Core;

import javax.activation.MimetypesFileTypeMap;
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

public class MinionShrinkTextLines extends BaseMinion implements Runnable {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private final Path file;

    public MinionShrinkTextLines(Path file) {
        this.file = file;
    }

    private static void shrinkTextLines(Path path) throws IOException {
        int numthreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        DirectoryStream<Path> fileStream = Files.newDirectoryStream(path);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

        for (Path imagesFile : files) {
            if (!mimetypesFileTypeMap.getContentType(imagesFile.toFile()).split("/")[0].equals("image")) {
                continue;
            }
            Runnable worker = new MinionShrinkTextLines(imagesFile);
            executor.execute(worker);//calling execute method of ExecutorService

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

    public static void main(String[] args) throws Exception{
//        shrinkTextLines(Paths.get("/home/rutger/republic/randomprint2/"));
        String path = "/media/rutger/HDI0002/difor-data-hannah-divide6/";
//        String path = "/home/rutger/data/antal/baselines/";

        CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = parser.parse(getOptions(), args);
        } catch (ParseException e) {
            printHelp(getOptions(), "java " + MinionShrinkTextLines.class.getName());
            return;
        }

        if (commandLine.hasOption("help")){
            printHelp(getOptions(), "java " + MinionShrinkTextLines.class.getName());
            return;
        }

        path = commandLine.getOptionValue("input");

        shrinkTextLines(Paths.get(path));
    }

    @Override
    public void run() {
        try {
            System.out.println("Shrinking lines for: " + this.file.toAbsolutePath().toString());
            Stopwatch stopwatch = Stopwatch.createStarted();
            PageUtils.shrinkTextLines(this.file);
            System.out.println(this.file.toAbsolutePath().toString() + " took " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " milliseconds");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
