package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nl.knaw.huc.di.images.altoxmlutils.AltoUtils;
import nl.knaw.huc.di.images.imageanalysiscommon.DocumentTypeConverter;
import nl.knaw.huc.di.images.layoutds.models.Alto.AltoDocument;
import nl.knaw.huc.di.images.layoutds.models.DocumentPage;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.stringtools.StringTools;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;


public class MinionConvertOCRResult extends BaseMinion {



    public static void main(String[] args) throws IOException {

        String input = "/scratch/altotest/";
        String outputDir = "/tmp/limited/";
        String outputDirAlto = "/tmp/alto/";

        XmlMapper mapper = new XmlMapper();

        Path path = Paths.get(input);
        try (DirectoryStream<Path> files = Files.newDirectoryStream(path)) {
            for (Path file : files) {

                String filename = file.toAbsolutePath().toString();

                if (filename.endsWith("MMGARO01_000180302_001_alto.xml")) {
                    String altoXml = StringTools.readFile(filename);
//                    System.out.println(filename);
                    AltoDocument altoDocument = AltoUtils.readAltoDocumentFromString(altoXml);

//                    AltoDocument altoDocument = mapper.readValue(altoXml, AltoDocument.class);

                    DocumentPage documentPage = DocumentTypeConverter.altoDocumentToDocumentPage(altoDocument);

                    AltoDocument newAlto = DocumentTypeConverter.documentPageToAlto(documentPage);
//                    mapper.setDefaultUseWrapper(false);

                    String altoXmlA = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(altoDocument);
                    String altoXmlB = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newAlto);
                    if (!altoXmlA.equals(altoXmlB)) {
                        System.err.println("not equal");
                    }
                    if (Objects.equals(altoXmlA, altoXmlB)) {
                        System.out.println("equal");
                    }
                    PcGts page = DocumentTypeConverter.documentPageToPage(documentPage);
                    String pageXml = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(page);
                    String outputFile = outputDir + file.getFileName().toString();
                    String outputFileAlto = outputDirAlto + file.getFileName().toString();

                    if (!Files.exists(Paths.get(outputDir))) {
                        Files.createDirectories(Paths.get(outputDir));
                    }
                    if (!Files.exists(Paths.get(outputDirAlto))) {
                        Files.createDirectories(Paths.get(outputDirAlto));
                    }
                    StringTools.writeFile(outputFile, pageXml);
                    StringTools.writeFile(outputFileAlto, altoXmlB);

                }
            }
        } catch (AccessDeniedException adx) {
            System.err.println("access denied: " + path.toAbsolutePath().toString());
        }


    }
}