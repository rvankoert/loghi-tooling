package nl.knaw.huc.di.images.minions;

import com.google.common.collect.Lists;
import nl.knaw.huc.di.images.imageanalysiscommon.UnicodeToAsciiTranslitirator;
import nl.knaw.huc.di.images.layoutds.models.HTRConfig;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.pagexmlutils.StyledString;
import nl.knaw.huc.di.images.pipelineutils.ErrorFileWriter;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.elasticsearch.common.Strings;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
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
    private final Map<String, String> batchMetadataMap;
    private final Consumer<PcGts> pageSaver;
    private final String pageFileName;
    private final Map<String, Double> confidenceMap;
    private final HTRConfig htrConfig;
    private final String gitHash;
    private final UnicodeToAsciiTranslitirator unicodeToAsciiTranslitirator;
    private final String identifier;
    private final Supplier<PcGts> pageSupplier;
    private final String comment;
    private final Optional<ErrorFileWriter> errorFileWriter;

    public MinionLoghiHTRMergePageXML(String identifier, Supplier<PcGts> pageSupplier, HTRConfig htrConfig,
                                      Map<String, String> fileTextLineMap, Map<String, String> batchMetadataMap, Map<String, Double> confidenceMap,
                                      Consumer<PcGts> pageSaver, String pageFileName, String comment, String gitHash,
                                      Optional<ErrorFileWriter> errorFileWriter) {

        this.identifier = identifier;
        this.pageSupplier = pageSupplier;
        this.htrConfig = htrConfig;
        this.gitHash = gitHash;
        this.confidenceMap = confidenceMap;
        this.fileTextLineMap = fileTextLineMap;
        this.batchMetadataMap = batchMetadataMap;
        this.pageSaver = pageSaver;
        this.pageFileName = pageFileName;
        this.comment = comment;
        this.errorFileWriter = errorFileWriter;
        unicodeToAsciiTranslitirator = new UnicodeToAsciiTranslitirator();
    }

    private void runFile(Supplier<PcGts> pageSupplier) throws IOException {
        LOG.info(identifier + " processing...");
        PcGts page = pageSupplier.get();

        if (page == null) {
            LOG.error("Could not read page for {}.", identifier);
            return;
        }

        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            for (TextLine textLine : textRegion.getTextLines()) {
                String text = fileTextLineMap.get(pageFileName + "-" + textLine.getId());
                // If text is empty just continue
                if (text == null) {
                    continue;
                }

                // Init TextLineCustom
                TextLineCustom textLineCustom = new TextLineCustom();
                final StyledString styledString = StyledString.fromStringWithStyleCharacters(text);
                styledString.getStyles().forEach(style -> textLineCustom.addCustomTextStyle(style.getStyles(), style.getOffset(), style.getLength()));
                final String cleanText = styledString.getCleanText();

                // Get confidence score for text line
                Double confidence = confidenceMap.get(pageFileName + "-" + textLine.getId());

                // Set TextEquiv elements and confidence score
                textLine.setTextEquiv(new TextEquiv(confidence, unicodeToAsciiTranslitirator.toAscii(cleanText), cleanText));
                textLine.setWords(new ArrayList<>());

                // Get batch_metadata for line ID
                String batchMetadata = batchMetadataMap.get(pageFileName + "-" + textLine.getId());

                // Set custom userAttribute
                // Either create simple UserAttribute(name, value) or detailed UserAttribute(name, description, type, value)
                textLine.addUserAttributeToUserDefined(new UserAttribute("batch_metadata", batchMetadata));

                // Set custom text attribute
                textLine.setCustom(textLineCustom.toString());

            }
        }
        page.getMetadata().setLastChange(new Date());
        if (this.comment != null) {
            String newComment = this.comment;
            if (!Strings.isNullOrEmpty(page.getMetadata().getComments())) {
                newComment = page.getMetadata().getComments() + "; " + this.comment;
            }
            page.getMetadata().setComments(newComment);
        }

        MetadataItem metadataItem = createProcessingStep(htrConfig, gitHash);
        if (page.getMetadata().getMetadataItems() == null) {
            page.getMetadata().setMetadataItems(new ArrayList<>());
        }
        page.getMetadata().getMetadataItems().add(metadataItem);

        pageSaver.accept(page);
    }


    private MetadataItem createProcessingStep(HTRConfig htrConfig, String gitHash) {
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setType("processingStep");
        metadataItem.setName("htr");
        metadataItem.setValue("loghi-htr");
        Labels labels = new Labels();
        ArrayList<Label> labelsList = new ArrayList<>();
        if (!Strings.isNullOrEmpty(gitHash)) {
            final Label githashLabel = new Label();
            githashLabel.setType("githash");
            githashLabel.setValue(gitHash.trim());
            labelsList.add(githashLabel);
        }
//        final Label modelLabel = new Label();
//        modelLabel.setType("model");
//        modelLabel.setValue(htrConfig.getModel());
//        labelsList.add(modelLabel);
        if (!Strings.isNullOrEmpty(htrConfig.getModelName())) {
            final Label modelNameLabel = new Label();
            modelNameLabel.setType("model_name");
            modelNameLabel.setValue(htrConfig.getModelName().trim());
            labelsList.add(modelNameLabel);
        }
        if (!Strings.isNullOrEmpty(htrConfig.getUrlCode())) {
            final Label urlCodeLabel = new Label();
            urlCodeLabel.setType("url-code");
            urlCodeLabel.setValue(htrConfig.getUrlCode().trim());
            labelsList.add(urlCodeLabel);
        }

        if (htrConfig.getUuid() != null) {
            final Label uuidLabel = new Label();
            uuidLabel.setType("uuid");
            uuidLabel.setValue("" + htrConfig.getUuid());
            labelsList.add(uuidLabel);
        }
        if (htrConfig.getValues()!=null) {
            for (String key : htrConfig.getValues().keySet()) {
                Label label = new Label();
                label.setType(key);
                Object value = htrConfig.getValues().get(key);
                label.setValue(String.valueOf(value));
                labelsList.add(label);
            }
        }
        labels.setLabel(labelsList);
        metadataItem.setLabels(labels);
        return metadataItem;
    }

    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("input_path").hasArg(true).required(true)
                .desc("Page to be updated with the htr results").build()
        );

        options.addOption(Option.builder("results_file").hasArg(true).required(true)
                .desc("File with the htr results").build()
        );

        options.addOption("config_file", true, "File with the htr model config.");
        options.addOption("htr_code_config_file", true, "File with the htr code config.");

        options.addOption("help", false, "prints this help dialog");

        options.addOption("threads", true, "number of threads to use, default 4");
        options.addOption("comment", true, "custom comments");
        options.addOption("use_2013_namespace", "set PageXML namespace to 2013, to avoid causing problems with Transkribus");
        final Option whiteListOption = Option.builder("config_white_list").hasArgs()
                .desc("a list with properties that should be added to the PageXML")
                .build();
        options.addOption(whiteListOption);

        return options;
    }


    public static HTRConfig readHTRConfigFile(String configFile, List<String> configWhiteList) throws IOException, org.json.simple.parser.ParseException {
        HTRConfig htrConfig = new HTRConfig();
        if (Strings.isNullOrEmpty(configFile) || !Files.exists(Paths.get(configFile))) {
            return htrConfig;
        }
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(configFile));

        String gitHash = jsonObject.get("git_hash").toString();
        String model = jsonObject.get("model").toString();

        htrConfig.setGithash(gitHash);
        htrConfig.setModel(model);
        if (jsonObject.containsKey("model_name")) {
            String modelName = null;
            if (jsonObject.get("model_name")!=null){
                modelName = jsonObject.get("model_name").toString();
            }
            htrConfig.setModelName(modelName);
        }
        if (jsonObject.containsKey("url-code")) {
            String urlCode = jsonObject.get("url-code").toString();
            htrConfig.setUrlCode(urlCode);
        }
        if (jsonObject.containsKey("uuid")) {
            htrConfig.setUuid(UUID.fromString(jsonObject.get("uuid").toString()));
        }

        Map<String, Object> values = new HashMap<>();

        JSONObject args = (JSONObject) jsonObject.get("args");
        for (Object key : args.keySet()) {
            LOG.debug(String.valueOf(key));
            LOG.debug(String.valueOf(args.get(key)));
            if (args.get(key) != null && configWhiteList.contains(key)) {
                values.put((String) key, String.valueOf(args.get(key)));
            }
        }
        htrConfig.setValues(values);

        return htrConfig;
    }

    private static void fillDictionary(String resultsFile,
                                       Map<String, String> fileTextLineMap,
                                       Map<String, String> batchMetadataMap,
                                       Map<String, Double> confidenceMap) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(resultsFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                ResultLine resultLine = getResultLine(line);
                fileTextLineMap.put(resultLine.filename, resultLine.text.toString().trim());
                confidenceMap.put(resultLine.filename, resultLine.confidence);
                batchMetadataMap.put(resultLine.filename, resultLine.metadata);
                LOG.debug(resultLine.filename + " appended to dictionary");
            }
        }
    }

    public static ResultLine getResultLine(String line) {
        int tabCount = countTabs(line);

        String[] splitted = line.split("\t");
        String filename = splitted[0].split("/")[splitted[0].split("/").length - 1].replace(".png", "").trim();
        double confidence;
        String metadata = "[]"; //set base value for metadata
        StringBuilder text = new StringBuilder();

        if (tabCount == 3) {
            // Format: filename\tmetadata\tconfidence\tpred_text (with pred_text potentially empty)
            metadata = splitted[1];
            confidence = Double.parseDouble(splitted[2]);
            if (splitted.length > 3) { // Check if pred_text is not empty
                text.append(splitted[3]);
            }
        } else if (tabCount == 2) {
            // Format: filename\tconfidence\tpred_text (with pred_text potentially empty)
            confidence = Double.parseDouble(splitted[1]);
            if (splitted.length > 2) { // Check if pred_text is not empty
                text.append(splitted[2]);
            }
        } else {
            throw new IllegalArgumentException("Input line does not match expected formats.");
        }

        ResultLine resultLine;
        if (!metadata.equals("[]")) {
            resultLine = new ResultLine(filename, confidence, metadata, text);
        } else {
            resultLine = new ResultLine(filename, confidence, text);
        }

        return resultLine;
    }

    private static int countTabs(String str) {
        int tabCount = 0;

        // Iterate over each character in the string
        for (int i = 0; i < str.length(); i++) {
            // Check if the current character is a tab
            if (str.charAt(i) == '\t') {
                tabCount++;
            }
        }

        return tabCount;
    }



    public static class ResultLine {
        private final String filename;
        private final double confidence;
        private final StringBuilder text;

        //Metadata init as null for default
        private String metadata = null;

        // Constructor without metadata
        public ResultLine(String filename, double confidence, StringBuilder text) {
            this.filename = filename;
            this.confidence = confidence;
            this.text = text;
        }

        // Constructor with metadata
        public ResultLine(String filename, double confidence, String metadata, StringBuilder text) {
            this(filename, confidence, text); // Calls the other constructor
            this.metadata = metadata; // Sets metadata
        }


        public String getFilename() {
            return filename;
        }

        public double getConfidence() {
            return confidence;
        }

        public StringBuilder getText() {
            return text;
        }

        public String getMetadata(){
            // Return "[]" if metadata is null, otherwise return metadata
            return metadata == null ? "[]" : metadata;
        }
    }

    public static void main(String[] args) throws Exception {
        int numthreads = 4;
        Path inputPath;
        String resultsFile;
        String htrModelConfigFile = null;
        String htrCodeConfigFile = null;
        String comment = null;
        final Options options = getOptions();
        final CommandLineParser parser = new DefaultParser();
        final CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException ex) {
            printHelp(options, "java " + MinionLoghiHTRMergePageXML.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java " + MinionLoghiHTRMergePageXML.class.getName());
            return;
        }
        String namespace = commandLine.hasOption("use_2013_namespace") ? PageUtils.NAMESPACE2013 : PageUtils.NAMESPACE2019;

        inputPath = Paths.get(commandLine.getOptionValue("input_path"));
        resultsFile = commandLine.getOptionValue("results_file");

        if (commandLine.hasOption("config_file")) {
            htrModelConfigFile = commandLine.getOptionValue("config_file");
        }

        if (commandLine.hasOption("htr_code_config_file")) {
            htrCodeConfigFile = commandLine.getOptionValue("htr_code_config_file");
        }

        if (commandLine.hasOption("threads")) {
            numthreads = Integer.parseInt(commandLine.getOptionValue("threads"));
        }

        if (commandLine.hasOption("comment")) {
            comment = commandLine.getOptionValue("comment");
        }

        final List<String> configWhiteList;
        if (commandLine.hasOption("config_white_list")) {
            configWhiteList = Arrays.asList(commandLine.getOptionValues("config_white_list"));
        } else {
            configWhiteList = Lists.newArrayList("batch_size");
        }


        ExecutorService executor = Executors.newFixedThreadPool(numthreads);

        HTRConfig htrModelConfig = readHTRConfigFile(htrModelConfigFile, configWhiteList);
        HTRConfig htrCodeConfig = readHTRConfigFile(htrCodeConfigFile, configWhiteList);

        final HashMap<String, String> fileTextLineMap = new HashMap<>();
        final HashMap<String, String> metadataMap = new HashMap<>();
        final HashMap<String, Double> confidenceMap = new HashMap<>();

        fillDictionary(resultsFile, fileTextLineMap, metadataMap, confidenceMap);
        if (!Files.exists(inputPath)) {
            LOG.error("input path does not exist: " + inputPath.toAbsolutePath());
            System.exit(1);
        }
        DirectoryStream<Path> fileStream = Files.newDirectoryStream(inputPath);
        List<Path> files = new ArrayList<>();
        fileStream.forEach(files::add);
        files.sort(Comparator.comparing(Path::toString));

        for (Path file : files) {
            if (file.toString().endsWith(".xml")) {
                Consumer<PcGts> pageSaver = page -> {
                    try {
                        String pageXmlString = PageUtils.convertAndValidate(page, namespace);
                        StringTools.writeFile(file.toAbsolutePath().toString(), pageXmlString);
                    } catch (IOException e) {
                        LOG.error("Could not save page: {}", file.toAbsolutePath());
                    } catch (TransformerException e) {
                        LOG.error("Could not transform page to 2013 version", e);
                    }
                };

                final String pageFileName = FilenameUtils.removeExtension(file.getFileName().toString());
                Supplier<PcGts> pageSupplier = () -> {
                    try {
                        return PageUtils.readPageFromFile(file);
                    } catch (IOException e) {
                        LOG.error("Could not load page: {}", file.toAbsolutePath());
                        return null;
                    }
                };

                Runnable worker = new MinionLoghiHTRMergePageXML(pageFileName, pageSupplier, htrModelConfig, fileTextLineMap, metadataMap,
                        confidenceMap, pageSaver, pageFileName, comment, htrCodeConfig.getGithash(), Optional.empty());
                executor.execute(worker);
            }
        }


        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }

    @Override
    public void run() {
        try {
            this.runFile(this.pageSupplier);
        } catch (IOException e) {
            errorFileWriter.ifPresent(errorFileWriter -> errorFileWriter.write(identifier, e, "Error while processing"));
            e.printStackTrace();
        }
    }
}