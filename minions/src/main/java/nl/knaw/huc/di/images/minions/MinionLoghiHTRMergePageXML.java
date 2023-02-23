package nl.knaw.huc.di.images.minions;

import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.layoutds.models.HTRConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.elasticsearch.common.Strings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MinionLoghiHTRMergePageXML extends BaseMinion implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MinionLoghiHTRMergePageXML.class);

    private final Map<String, String> fileTextLineMap;
    private final Consumer<PcGts> pageSaver;
    private final String pageFileName;
    private final Map<String, Double> confidenceMap;
    private final HTRConfig htrConfig;
    private final UnicodeToAsciiTranslitirator unicodeToAsciiTranslitirator;
    private final String identifier;
    private final Supplier<PcGts> pageSupplier;

    public MinionLoghiHTRMergePageXML(String identifier, Supplier<PcGts> pageSupplier, HTRConfig htrConfig, Map<String, String> fileTextLineMap, Map<String, Double> confidenceMap, Consumer<PcGts> pageSaver, String pageFileName) {
        this.identifier = identifier;
        this.pageSupplier = pageSupplier;
        this.htrConfig = htrConfig;
        this.confidenceMap = confidenceMap;
        this.fileTextLineMap = fileTextLineMap;
        this.pageSaver = pageSaver;
        this.pageFileName = pageFileName;
        unicodeToAsciiTranslitirator = new UnicodeToAsciiTranslitirator();
    }

    private void runFile(Supplier<PcGts> pageSupplier) throws IOException {
//        if (pageFilePath.toString().endsWith(".xml")) {
            LOG.info(identifier + " processing...");
//            String pageXml = StringTools.readFile(pageFilePath.toAbsolutePath().toString());
            PcGts page = pageSupplier.get();

            if (page == null) {
               LOG.error("Could not read page for {}.", identifier);
               return;
            }

            for (TextRegion textRegion : page.getPage().getTextRegions()) {
                for (TextLine textLine : textRegion.getTextLines()) {
                    String text = fileTextLineMap.get(pageFileName + "-" + textLine.getId());
                    if (text == null) {
                        continue;
                    }
                    Double confidence = confidenceMap.get(pageFileName + "-" + textLine.getId());
                    textLine.setTextEquiv(new TextEquiv(confidence, unicodeToAsciiTranslitirator.toAscii(text), text));
                    textLine.setWords(new ArrayList<>());
                }
            }
            page.getMetadata().setLastChange(new Date());
            page.getMetadata().setCreator("Loghi");
            page.getMetadata().setComments(htrConfig.toString());

            ArrayList<MetadataItem> metaDataItems = mapHTRConfigToMetaData(htrConfig);
            page.getMetadata().setMetadataItems(metaDataItems);

            pageSaver.accept(page);
//        }
    }

    private ArrayList<MetadataItem> mapHTRConfigToMetaData(HTRConfig htrConfig) {
        ArrayList<MetadataItem> metadataItems = new ArrayList<>();
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("htr");
        metadataItem.setValue("loghi-htr");
        Labels labels = new Labels();
        ArrayList<Label> labelsList = new ArrayList<>();
        for (String key : htrConfig.getValues().keySet()){
            Label label = new Label();
            label.setType(key);
            Object value = htrConfig.getValues().get(key);
            label.setValue(String.valueOf(value));
            labelsList.add(label);
        }
        labels.setLabel(labelsList);
        metadataItem.setLabels(labels);
        metadataItems.add(metadataItem);
        return metadataItems;
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

        return options;
    }

    public static void main(String[] args) throws Exception {
        int numthreads = Runtime.getRuntime().availableProcessors();
        numthreads = 4;
        Path inputPath = Paths.get("/media/rutger/DIFOR1/data/1.05.14/83/page");
        String resultsFile = "/tmp/output/results.txt";
        String configFile = null;

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

        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        HTRConfig htrConfig = readConfigFile(configFile);

        final HashMap<String, String> fileTextLineMap = new HashMap<>();
        final HashMap<String, Double> confidenceMap = new HashMap<>();

        fillDictionary(resultsFile, fileTextLineMap, confidenceMap);
        if (!Files.exists(inputPath)){
            LOG.error("input path does not exist: "+ inputPath.toAbsolutePath());
            System.exit(1);
        }
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            Consumer<PcGts> pageSaver = page -> {
                try {
                    String pageXmlString = PageUtils.convertPcGtsToString(page);
                    StringTools.writeFile(file.toAbsolutePath().toString(), pageXmlString);
                } catch (IOException e) {
                    LOG.error("Could not save page: {}", file.toAbsolutePath());
                }
            };

            final String pageFileName = file.toAbsolutePath().toString();
            Supplier<PcGts> pageSupplier = () -> {
                try {
                    return PageUtils.readPageFromFile(file);
                } catch (IOException e) {
                    LOG.error("Could not load page: {}", file.toAbsolutePath());
                    return null;
                }
            };

            Runnable worker = new MinionLoghiHTRMergePageXML(pageFileName, pageSupplier, htrConfig, fileTextLineMap, confidenceMap, pageSaver, pageFileName);
            executor.execute(worker);
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    public static HTRConfig readConfigFile(String configFile) throws IOException, org.json.simple.parser.ParseException {
        HTRConfig htrConfig = new HTRConfig();
        if (Strings.isNullOrEmpty(configFile) || !Files.exists(Paths.get(configFile))) {
            return htrConfig;
        }
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(configFile));

        String gitHash = jsonObject.get("git_hash").toString();
        String model = jsonObject.get("model").toString();
//
//        JSONArray arr = obj.getJSONArray("posts"); // notice that `"posts": [...]`
//        for (int i = 0; i < arr.length(); i++)
//        {
//            String post_id = arr.getJSONObject(i).getString("post_id");
//        }
        Map<String, Object> values = new HashMap<>();

        JSONObject args = (JSONObject) jsonObject.get("args");
        for (Object key: args.keySet()){
            LOG.debug(String.valueOf(key));
            LOG.debug(String.valueOf(args.get(key)));
            if (args.get(key)!=null) {
                values.put((String) key, String.valueOf(args.get(key)));
            }
        }
        htrConfig.setValues(values);

        return htrConfig;
    }

    private static void fillDictionary(String resultsFile, Map<String, String> fileTextLineMap, Map<String, Double> confidenceMap) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(resultsFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\t");
                String filename = splitted[0];
                double confidence = 0;

                try{
                    confidence = Double.parseDouble(splitted[1]);
                }catch (Exception ex){
                    LOG.error(filename + ex.getMessage());
                }
                StringBuilder text = new StringBuilder();
                for (int i = 2; i < splitted.length; i++) {
                    text.append(splitted[i]);//line.substring(filename.length() + 1);
                    text.append("\t");
                }
                text = new StringBuilder(text.toString().trim());
                splitted = filename.split("/");
                filename = splitted[splitted.length - 1].replace(".png", "").trim();
                fileTextLineMap.put(filename, text.toString().trim());
                confidenceMap.put(filename, confidence);
                LOG.debug(filename + " appended to dictionary");
            }
        }
    }

    @Override
    public void run() {
        try {
            this.runFile(this.pageSupplier);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
