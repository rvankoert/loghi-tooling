package nl.knaw.huc.di.images.minions;

import com.google.common.base.Strings;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huygens.pergamon.nlp.langident.Model;
import nl.knaw.huygens.pergamon.nlp.langident.NaiveBayes;
import nl.knaw.huygens.pergamon.nlp.langident.TrainingSet;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * pathOfTrainingSet expects a folder with text files.
 * The name of the text files will be used as the name of the language.
 * It will be the choice of the user to use the PageXML standard.
 * For more information see the LanguageSimpleType definition of https://github.com/PRImA-Research-Lab/PAGE-XML/blob/master/pagecontent/schema/pagecontent.xsd
 */
public class MinionDetectLanguageOfPageXml implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(MinionDetectLanguageOfPageXml.class);

    private static String pathOfTrainingSet;
    private final String identifier;
    private final Supplier<PcGts> pageLoader;
    private final Consumer<PcGts> pageSaver;
    private final Model model;

    /**
     *
     * @param pageLoader
     * @param pageSaver
     * @param model the model to guess the language
     */
    public MinionDetectLanguageOfPageXml(String identifier, Supplier<PcGts> pageLoader, Consumer<PcGts> pageSaver, Model model) {
        this.identifier = identifier;
        this.pageLoader = pageLoader;
        this.pageSaver = pageSaver;
        this.model = model;
    }




    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("page").hasArg(true).desc("Path to the page file or directory with page files")
                .required().build()
        );
        options.addOption(Option.builder("lang_train_data").hasArg(true)
                .desc("Folder that contains training data for language detector (optional)").required(false).build()
        );
        options.addOption("help", false, "prints this help dialog");


        return options;
    }

    public static void main(String[] args) throws Exception {
        final Options options = getOptions();
        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine;

        try {
            commandLine = commandLineParser.parse(options, args);
        }
        catch (ParseException e) {
            printHelp(options, "java " + MinionDetectLanguageOfPageXml.class.getName());
            return;
        }

        if (commandLine.hasOption("help")) {
            printHelp(options, "java " + MinionDetectLanguageOfPageXml.class.getName());
            return;
        }

        String pathToPage = commandLine.getOptionValue("page");

        String pathOfTrainingSet = null;
        if (commandLine.hasOption("lang_train_data")) {
            pathOfTrainingSet = commandLine.getOptionValue("lang_train_data");
        }

        runStatic(pathOfTrainingSet, pathToPage);
    }

    public static void printHelp(Options options, String callName) {
        final HelpFormatter helpFormatter = new HelpFormatter();

        helpFormatter.printHelp(callName, options, true);
    }

    private static void runOnFile(File pageFile, Model guesser) throws IOException {
        if (pageFile.isDirectory()) {
            LOG.info(pageFile + ": processing directory...");
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(pageFile.toPath());

            List<Path> files = new ArrayList<>();
            fileStream.forEach(files::add);
            files.sort(Comparator.comparing(Path::toString));

            for (Path file : files) {
                if (!file.toFile().isFile()) {
                    continue;
                }
                if (file.getFileName().toString().endsWith(".xml")) {
                    runOnFile(file.toFile(), guesser);
                }
            }
        } else {
            LOG.info(pageFile + ": processing file...");
            PcGts pcGts = PageUtils.readPageFromFile(pageFile.toPath());

            Page page = pcGts.getPage();
            String pageText = "";

            for (TextRegion textRegion : page.getTextRegions()) {
                String textOfRegion = "";
                for (TextLine textLine : textRegion.getTextLines()) {
                    TextEquiv textEquiv = textLine.getTextEquiv();
                    if (textEquiv != null && !Strings.isNullOrEmpty(textEquiv.getUnicode())) {
                        textOfRegion += textEquiv.getUnicode() + "\n";
                    }
                }
                final String languageOfRegion = guesser.predictBest(textOfRegion);
                textRegion.setPrimaryLanguage(languageOfRegion);

                for (TextLine textLine : textRegion.getTextLines()) {
                    TextEquiv textEquiv = textLine.getTextEquiv();
                    if (textEquiv != null && textEquiv.getPlainText() != null) {
                        final String languageOfLine = guesser.predictBest(textEquiv.getPlainText());
                        textLine.setPrimaryLanguage(languageOfLine);
                    }
                }
                pageText += textOfRegion;
            }
            String language = guesser.predictBest(pageText);
            page.setPrimaryLanguage(language);

            PageUtils.writePageToFile(pcGts, pageFile.toPath());
        }
    }



    public void run() {
        LOG.info(identifier + ": processing file...");
        PcGts pcGts = this.pageLoader.get();

        Page page = pcGts.getPage();
        String pageText = "";

        for (TextRegion textRegion : page.getTextRegions()) {
            String textOfRegion = "";
            for (TextLine textLine : textRegion.getTextLines()) {
                TextEquiv textEquiv = textLine.getTextEquiv();
                if (textEquiv != null && !Strings.isNullOrEmpty(textEquiv.getUnicode())) {
                    textOfRegion += textEquiv.getUnicode() + "\n";
                }
            }
            final String languageOfRegion = model.predictBest(textOfRegion);
            textRegion.setPrimaryLanguage(languageOfRegion);

            for (TextLine textLine : textRegion.getTextLines()) {
                TextEquiv textEquiv = textLine.getTextEquiv();
                if (textEquiv != null && textEquiv.getPlainText() != null) {
                    final String languageOfLine = model.predictBest(textEquiv.getPlainText());
                    textLine.setPrimaryLanguage(languageOfLine);
                }
            }
            pageText += textOfRegion;
        }
        String language = model.predictBest(pageText);
        page.setPrimaryLanguage(language);

        pageSaver.accept(pcGts);
    }

    public static void runStatic(String pathOfTrainingSet, String pathToPage) throws IOException {
        TrainingSet trainingSet;
        if (pathOfTrainingSet == null) {
            trainingSet = TrainingSet.getBuiltin();
        } else {
            LOG.info("training data: " + pathOfTrainingSet);
            List<CharSequence> docs = new ArrayList<>();
            final ArrayList<String> labels = new ArrayList<>();
            final File file = new File(pathOfTrainingSet);
            LOG.info("file exists: " + file.exists());
            if (file.isDirectory()) {
                for (File doc : file.listFiles()) {
                    processTrainingFile(docs, labels, doc);
                }
            } else {
                processTrainingFile(docs, labels, file);
            }

            trainingSet = new TrainingSet(docs, labels);
        }
        Model guesser = new NaiveBayes().train(trainingSet);

        File pageFile = new File(pathToPage);
        runOnFile(pageFile, guesser);

    }

    /**
     *
     * @param trainingData map of language name, language example data
     * @return
     */
    public static Model trainModel(Map<String, String> trainingData) {
        final List<CharSequence> docs = new ArrayList<>();
        final List<String> labels = new ArrayList<>();

        for (Map.Entry<String, String> languageExamplePair : trainingData.entrySet()) {
            String language = languageExamplePair.getKey();

             languageExamplePair.getValue().lines().forEach(line -> {
                 docs.add(line);
                 labels.add(language);
             });
        }

        final TrainingSet trainingSet = new TrainingSet(docs, labels);

        return new NaiveBayes().train(trainingSet);
    }



    private static void processTrainingFile(List<CharSequence> docs, ArrayList<String> labels, File doc) throws IOException {
        final List<String> lines = Files.readAllLines(doc.toPath());
        final String language = FilenameUtils.getBaseName(doc.getName());

        for (String line : lines) {
            if (!StringUtils.isBlank(line)) {
                docs.add(line);
                labels.add(language);
            }
        }
    }
}
