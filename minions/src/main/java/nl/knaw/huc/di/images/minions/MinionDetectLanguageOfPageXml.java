package nl.knaw.huc.di.images.minions;

import com.google.common.base.Strings;
import nl.knaw.huc.di.images.layoutds.models.Page.*;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huygens.pergamon.nlp.langident.Model;
import nl.knaw.huygens.pergamon.nlp.langident.NaiveBayes;
import nl.knaw.huygens.pergamon.nlp.langident.TrainingSet;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * pathOfTrainingSet expects a folder with text files.
 * The name of the text files will be used as the name of the language.
 * It will be the choice of the user to use the PageXML standard.
 * For more information see the LanguageSimpleType definition of https://github.com/PRImA-Research-Lab/PAGE-XML/blob/master/pagecontent/schema/pagecontent.xsd
 */
public class MinionDetectLanguageOfPageXml {

    private static String pathToPage;
    private static String pathOfTrainingSet;
    private static Model guesser = null;


    public static Options getOptions() {
        final Options options = new Options();

        options.addOption(Option.builder("page").hasArg(true).desc("Path to the page file or directory with page files")
                .required().build()
        );
        options.addOption(Option.builder("lang_train_data").hasArg(true)
                .desc("Folder that contains training data for language detector (optional)").required(false).build()
        );

        return options;
    }

    public static void main(String[] args) throws Exception {
        final Options options = getOptions();
        final CommandLineParser commandLineParser = new DefaultParser();
        final CommandLine commandLine = commandLineParser.parse(options, args);


        pathToPage = commandLine.getOptionValue("page");

        if(commandLine.hasOption("lang_train_data")) {
            pathOfTrainingSet = commandLine.getOptionValue("lang_train_data");
        }

        run();
    }

    private static void runOnFile(File pageFile) throws IOException {
        if (pageFile.isDirectory()) {
            System.out.println(pageFile.toString());
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(pageFile.toPath());

            List<Path> files = new ArrayList<>();
            fileStream.forEach(files::add);
            files.sort(Comparator.comparing(Path::toString));

            for (Path file : files) {
                if (!file.toFile().isFile()) {
                    continue;
                }
                if (file.getFileName().toString().endsWith(".xml")) {
                    runOnFile(file.toFile());
                }
            }
        } else {
            System.out.println(pageFile.toString());
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

    public static void run() throws IOException {
        TrainingSet trainingSet;
        if (pathOfTrainingSet == null) {
            trainingSet = TrainingSet.getBuiltin();
        } else {
            System.out.println("training data: " + pathOfTrainingSet);
            List<CharSequence> docs = new ArrayList<>();
            final ArrayList<String> labels = new ArrayList<>();
            final File file = new File(pathOfTrainingSet);
            System.out.println(file.exists());
            if (file.isDirectory()) {
                for (File doc : file.listFiles()) {
                    processTrainingFile(docs, labels, doc);
                }
            } else {
                processTrainingFile(docs, labels, file);
            }

            trainingSet = new TrainingSet(docs, labels);
        }
        guesser = new NaiveBayes().train(trainingSet);

        File pageFile = new File(pathToPage);
        runOnFile(pageFile);

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
