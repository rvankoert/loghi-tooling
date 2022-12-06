package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.layoutds.models.HTRConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextRegion;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.elasticsearch.common.Strings;

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

public class MinionLoghiHTRMergePageXML extends BaseMinion implements Runnable {

    private final Path file;
    private static Map<String, String> map = new HashMap<>();
    private static Map<String, Double> confidenceMap = new HashMap<>();
    private final HTRConfig htrConfig;

    public MinionLoghiHTRMergePageXML(Path file, HTRConfig htrConfig) {
        this.file = file;
        this.htrConfig = htrConfig;
    }

    private void runFile(Path file) throws IOException {
        if (file.toString().endsWith(".xml")) {
            System.out.println(file.toString());
            String pageXml = StringTools.readFile(file.toAbsolutePath().toString());
            PcGts page = PageUtils.readPageFromString(pageXml);

            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                for (TextLine textLine : textRegion.getTextLines()) {
                    String targetFileName = file.getFileName().toString();
                    String text = map.get(targetFileName + "-" + textLine.getId());
                    if (text == null) {
                        continue;
                    }
                    Double confidence = confidenceMap.get(targetFileName + "-" + textLine.getId());
                    textLine.setTextEquiv(new TextEquiv(confidence, text));
                    textLine.setWords(new ArrayList<>());
                }
            }
            page.getMetadata().setLastChange(new Date());
            page.getMetadata().setCreator("Loghi");
            page.getMetadata().setComments(htrConfig.toString());
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

        options.addOption("config_file", true, "File with the htr config.");

        options.addOption("help", false, "prints this help dialog");

        options.addOption("threads", true, "number of threads to use, default 4");

//        options.addOption("overwrite_existing_page", true, "true / false, default true");

        return options;
    }

    public static void main(String[] args) throws Exception {
        int numthreads = Runtime.getRuntime().availableProcessors();
        numthreads = 4;
        Path inputPath = Paths.get("/media/rutger/DIFOR1/data/1.05.14/83/page");
        String resultsFile = "/tmp/output/results.txt";
        String configFile = null;
        boolean overwriteExistingPage = true;

        final Options options = getOptions();
        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch(ParseException ex ){
            printHelp(options, "java " + MinionLoghiHTRMergePageXML.class.getName());
            return;
        }

        if (commandLine.hasOption("help")){
            printHelp(options, "java " + MinionLoghiHTRMergePageXML.class.getName());
            return;
        }

        inputPath = Paths.get(commandLine.getOptionValue("input_path"));
        resultsFile = commandLine.getOptionValue("results_file");
        
        if (commandLine.hasOption("config_file")) {
            configFile = commandLine.getOptionValue("config_file");
        }

        if (commandLine.hasOption("threads")) {
            numthreads = Integer.parseInt(commandLine.getOptionValue("threads"));
        }

//        if (commandLine.hasOption("overwrite_existing_page")) {
//            overwriteExistingPage = commandLine.getOptionValue("overwrite_existing_page").equals("true");
//        }

        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        HTRConfig htrConfig = readConfigFile(configFile);

        readDictionary(resultsFile);
        if (!Files.exists(inputPath)){
            System.err.println("input path does not exist: "+ inputPath.toAbsolutePath());
            System.exit(1);
        }
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            Runnable worker = new MinionLoghiHTRMergePageXML(file, htrConfig);
            executor.execute(worker);
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    private static HTRConfig readConfigFile(String configFile) throws IOException {
        HTRConfig htrConfig = new HTRConfig();
        if (Strings.isNullOrEmpty(configFile) || !Files.exists(Paths.get(configFile))) {
            return htrConfig;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("=");
                String varName = splitted[0];
                String varValue = splitted[1];
                if ("model".equals(varName)) {
                    htrConfig.setModel(varValue);
                }
                if ("batch_size".equals(varName)) {
                    htrConfig.setBatchSize(varValue);
                }
            }
        }
        return htrConfig;
    }

    private static void readDictionary(String resultsFile) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(resultsFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\t");
                String filename = splitted[0];
                double confidence = Double.parseDouble(splitted[1]);
                StringBuilder text = new StringBuilder();
                for (int i = 2; i < splitted.length; i++) {
                    text.append(splitted[i]);//line.substring(filename.length() + 1);
                    text.append("\t");
                }
                text = new StringBuilder(text.toString().trim());
                splitted = filename.split("/");
                filename = splitted[splitted.length - 1].replace(".png", "").trim();
                map.put(filename, text.toString().trim());
                confidenceMap.put(filename, confidence);
                System.out.println(filename);
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
