package nl.knaw.huc.di.images.minions;

import com.google.common.base.Strings;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutds.HibernateHelper;
import nl.knaw.huc.di.images.layoutds.models.Page.PcGts;
import nl.knaw.huc.di.images.layoutds.models.Page.TextEquiv;
import nl.knaw.huc.di.images.layoutds.models.Page.TextLine;
import nl.knaw.huc.di.images.layoutds.models.Page.TextRegion;
import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MinionConvertToPdf {
    public static void getPdfnew(Path directoryPath) throws IOException {
        try (PDDocument pdDocument = new PDDocument(MemoryUsageSetting.setupMixed(1024 * 1024 * 8))) {
//            for file in directory
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(directoryPath);
            List<Path> files = new ArrayList<>();
            fileStream.forEach(files::add);
            files.sort(Comparator.comparing(Path::toString));

            for (Path file : files) {
                if (!file.getFileName().endsWith(".jpg")){
                    continue;
                }
                System.out.println(file.getFileName());
                BufferedImage bufferedImage;
                try {
                    bufferedImage = ImageIO.read(file.toFile());
                    PDImageXObject pdImage = JPEGFactory.createFromImage(pdDocument, bufferedImage);
                    PDPage pdPage = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    pdDocument.addPage(pdPage);
                    PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, pdPage, PDPageContentStream.AppendMode.OVERWRITE, true);
                    double heightRatio = pdPage.getMediaBox().getHeight() / bufferedImage.getHeight();
                    double widthRatio = pdPage.getMediaBox().getWidth() / bufferedImage.getWidth();

                    double scale = heightRatio;

                    if (heightRatio > widthRatio) {
                        scale = widthRatio;
                    }
                    pdPageContentStream.drawImage(pdImage, 0, 0, (float) scale * bufferedImage.getWidth(), (float) scale * bufferedImage.getHeight());

                    PcGts page = null;
                    String pagePath = file.getParent().toAbsolutePath().toString() + "/page/" + FilenameUtils.removeExtension(file.getFileName().toString()) + ".xml";

                    page = PageUtils.readPageFromString(pagePath);
                    if (page != null) {
                        for (TextRegion textRegion : page.getPage().getTextRegions()) {
                            for (TextLine textLine : textRegion.getTextLines()) {
                                ArrayList<Point> points = StringConverter.stringToPoint(textLine.getBaseline().getPoints());

                                Point start = points.get(0);
                                pdPageContentStream.beginText();
                                pdPageContentStream.newLineAtOffset((int) start.x, (bufferedImage.getHeight() - (int) start.y));
                                Integer xHeight = null;
                                if (textLine.getTextStyle() != null) {
                                    xHeight = textLine.getTextStyle().getxHeight();
                                }
                                pdPageContentStream.setFont(PDType1Font.TIMES_ROMAN, xHeight != null ? xHeight : 12);
                                String text = null;
                                TextEquiv textEquiv = textLine.getTextEquiv();
                                text = textEquiv.getUnicode();
                                if (Strings.isNullOrEmpty(text)) {
                                    text = textEquiv.getPlainText();
                                }
                                pdPageContentStream.showText(text);
                                pdPageContentStream.endText();
                            }
                        }
                    }

                    pdPageContentStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pdDocument.save("/tmp/testpdf.pdf");
        }
    }
    public static void main(String[] args) throws IOException{
        getPdfnew(Paths.get("/scratch/2.01.01.01/110/"));
    }

}
