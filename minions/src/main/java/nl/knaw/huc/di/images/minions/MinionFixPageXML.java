package nl.knaw.huc.di.images.minions;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextRegion;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import nl.knaw.huc.di.images.stringtools.StringTools;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.nio.file.*;

/* this just reads and then writes the pagexml again. Fixing some small problems that exist in existing pagexml */
public class MinionFixPageXML {

    public static String fixPageXml(String pageXmlString, String targetNameSpace, boolean removeText, boolean removeWords) throws JsonProcessingException, TransformerException {
        PcGts page = PageUtils.readPageFromString(pageXmlString);
        for (TextRegion textRegion : page.getPage().getTextRegions()) {
            if (removeText) {
                textRegion.setTextEquiv(null);
            }
            for (TextLine textLine : textRegion.getTextLines()) {
                if (removeText) {
                    textLine.setTextEquiv(null);
                }
                if (removeWords) {
                    textLine.setWords(null);
                }

            }
        }
        String newPageXml = PageUtils.convertPcGtsToString(page, targetNameSpace);
        return newPageXml;
    }

    private static void readPages(Path directory, boolean removeWords, boolean removeText, String targetNamespace) throws Exception {
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory)) {
            for (Path path : files) {
                File file = new File(path.toAbsolutePath().toString());
                if (file.isDirectory()) {
                    readPages(path, removeWords, removeText, targetNamespace);
                } else {

                    String filename = path.toAbsolutePath().toString();
                    System.out.println(filename);
                    if (filename.endsWith(".xml")) {
                        String pageXmlString = StringTools.readFile(filename);
                        String newPageXml = fixPageXml(pageXmlString, targetNamespace, removeText, removeWords);
                        StringTools.writeFile(filename, newPageXml);
                    }
                }
            }
        } catch (AccessDeniedException adx) {
            System.err.println("access denied: " + directory.toAbsolutePath().toString());
        }
    }

    public static void main(String[] args) throws Exception {
        String inputXmls = "/home/rutger/republic/datasets/randomprint2/page/";
        if (args.length > 0) {
            inputXmls = args[0];
        }
        String namespace = PageUtils.NAMESPACE2019;
        if (args.length > 1) {
            namespace = args[1];
        }

        Path path = Paths.get(inputXmls);
        readPages(path, false, false, namespace);
    }
}