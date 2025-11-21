package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nl.knaw.huc.di.images.altoxmlutils.AltoUtils;
import nl.knaw.huc.di.images.imageanalysiscommon.DocumentTypeConverter;
import nl.knaw.huc.di.images.layoutds.models.Alto.AltoDocument;
import nl.knaw.huc.di.images.layoutds.models.DocumentPage;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.stringtools.StringTools;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Locale;



public class MinionConvertOCRResult extends BaseMinion {

    public static String detectFileFormatContents(String content) {
        if (content == null || content.isEmpty()) {
            System.err.println("Content is empty or null");
            return null;
        }
        if (content.startsWith("<?xml")) {
//            remove first line if it is an XML declaration
            content = content.replaceFirst("(?i)^<\\?xml.*?\\?>", "").trim();
        } else if (content.startsWith("<!DOCTYPE")) {
            // remove DOCTYPE declaration
            content = content.replaceFirst("(?i)^<!DOCTYPE.*?>", "").trim();
        } else if (content.startsWith("<")) {
            // If it starts with a tag, we assume it's XML
            content = content.trim();
        } else {
            System.err.println("Content does not start with XML declaration or tag");
            return null;
        }

        if (content.contains("<PcGts")) {
            if (content.contains("http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15")) {
                return "pagexml2013";
            } else if (content.contains("http://schema.primaresearch.org/PAGE/gts/pagecontent/2019-07-15")) {
                return "pagexml2019";
            } else {
                System.err.println("Unknown PageXML version in content");
                return null;
            }
        } else if (content.startsWith("<alto")) {
            return "alto";
        } else if (content.contains("<html") && content.contains("class=\"ocr\"")) {
            return "hocr";
        }
        return null;
    }

    public static String detectFileFormat(String filename) throws IOException {
        if (filename.toLowerCase().endsWith(".xml")) {
            String content = StringTools.readFile(filename);
            return detectFileFormatContents(content);
        } else if (filename.toLowerCase().endsWith("hocr")) {
            return "hocr";
        } else {
            System.err.println("Unknown file format for: " + filename);
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        CommandLineParser parser = new DefaultParser();

        Options options = new Options();
        options.addOption("i", "inputpath", true, "Input directory");
        options.addOption("o", "outputpath", true, "Output directory");
        options.addOption("t", "targetformat", true, "Target format (PageXML2013, PageXML2019, hOCR, ALTO)");
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Error parsing arguments: " + e.getMessage());
            new HelpFormatter().printHelp("MinionConvertOCRResult", options);
            return;
        }

        String inputPath = cmd.getOptionValue("inputpath", "/scratch/altotest/");
        String outputPath = cmd.getOptionValue("outputpath", "/tmp/limited/");
        String targetFormat = cmd.getOptionValue("targetformat", "PageXML2013").toLowerCase(Locale.ROOT);


//        For each file in the input directory detect if it is a PageXML 2013, PageXML 2019, hOCR, or ALTO file

        switch (targetFormat) {
            case "pagexml2013":
                // Generate PageXML 2013 output
                // String pageXml2013 = ...;
                // StringTools.writeFile(outputFile, pageXml2013);

                break;
            case "pagexml2019":
                // Generate PageXML 2019 output
                File inputDir = new File(inputPath);
                File[] files = inputDir.listFiles();
                if (files == null) {
                    System.err.println("Input path does not exist or is not a directory: " + inputPath);
                    return;
                }
                for (File file : files) {
                    if (file.isFile()) {
                        String sourceFormat = detectFileFormat(file.getAbsolutePath());
                        if (sourceFormat == null) {
                            System.err.println("Skipping file with unknown format: " + file.getAbsolutePath());
                        }else if (sourceFormat.equals("pagexml2013")) {
//                            String pageXml = StringTools.readFile(file.getAbsolutePath());
//                            PcGts page = Generator.convertPageXml2013ToPageXml2019(pageXml);
//                            XmlMapper mapper = new XmlMapper();
//                            String pageXml2019 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
//                            String outputFile = outputPath + file.getName();
//                            if (!Files.exists(Paths.get(outputPath))) {
//                                Files.createDirectories(Paths.get(outputPath));
//                            }
//                            StringTools.writeFile(outputFile, pageXml2019);
                        } else if (sourceFormat.equals("alto")) {
                            // Convert ALTO to PageXML 2019
                            System.out.println("Converting ALTO to PageXML 2019: " + file.getAbsolutePath());
                            String altoXml = StringTools.readFile(file.getAbsolutePath());
                            AltoDocument altoDocument = AltoUtils.readAltoDocumentFromString(altoXml);
                            if (altoDocument == null) {
                                System.err.println("Failed to parse ALTO document: " + file.getAbsolutePath());
                                continue;
                            }
                            DocumentPage documentPage = DocumentTypeConverter.altoDocumentToDocumentPage(altoDocument);
                            PcGts page = DocumentTypeConverter.documentPageToPage(documentPage);
                            XmlMapper mapper = new XmlMapper();
                            String pageXml2019 = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
                            String outputFile = outputPath +"/"+ file.getName();
                            Path path = Paths.get(outputPath);
                            if (!Files.exists(path)) {
                                Files.createDirectories(path);
                            }
                            StringTools.writeFile(outputFile, pageXml2019);
                        } else {
                            System.err.println("Unsupported source format: " + sourceFormat);
                        }
                    }
                }
                break;
            case "hocr":
                // Generate hOCR output
                // String hocr = ...;
                // StringTools.writeFile(outputFile, hocr);
                break;
            case "alto":
                // Generate ALTO output (already implemented)


//                StringTools.writeFile(outputFileAlto, altoXmlB);
                break;
            default:
                System.err.println("Unknown format: " + targetFormat);
                break;
        }
    }
}