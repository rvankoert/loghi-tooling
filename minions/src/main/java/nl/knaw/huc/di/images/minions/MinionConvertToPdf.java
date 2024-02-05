package nl.knaw.huc.di.images.minions;

import com.google.common.base.Strings;
import nl.knaw.huc.di.images.imageanalysiscommon.StringConverter;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
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
    private static final int XHEIGHT_DEFAULT = 12;

    public static void getPdfnew(String pdfFileName, String directory) throws IOException {
        getPdfnew(pdfFileName, Paths.get(directory));
    }

    public static void getPdfnew(String pdfFileName, Path directoryPath) throws IOException {
        try (PDDocument pdDocument = new PDDocument(MemoryUsageSetting.setupMixed(1024 * 1024 * 8))) {
            DirectoryStream<Path> fileStream = Files.newDirectoryStream(directoryPath);
            List<Path> files = new ArrayList<>();
            fileStream.forEach(files::add);
            files.sort(Comparator.comparing(Path::toString));

            for (Path file : files) {
                System.out.println(file.getFileName());
                if (!file.getFileName().toString().endsWith(".jpg")) {
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
//                    pdPageContentStream.drawImage(pdImage, 0, 0, (float) scale * bufferedImage.getWidth(), (float) scale * bufferedImage.getHeight());

                    PcGts page = null;
                    String pagePath = file.getParent().toAbsolutePath().toString() + "/page/" + FilenameUtils.removeExtension(file.getFileName().toString()) + ".xml";

                    page = PageUtils.readPageFromFile(pagePath);
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

//                                calculate font size
                                List<Point> baselinePoints = StringConverter.stringToPoint(textLine.getBaseline().getPoints());
                                double distance = LayoutProc.getDistance(baselinePoints.get(0), baselinePoints.get(baselinePoints.size() - 1));

                                TextEquiv textEquiv = textLine.getTextEquiv();
                                pdPageContentStream.setFont(PDType1Font.TIMES_ROMAN, xHeight != null ? xHeight : XHEIGHT_DEFAULT);
                                if (textEquiv != null) {
                                    String text = textEquiv.getPlainText();
                                    if (!Strings.isNullOrEmpty(text)) {
                                        text = textEquiv.getPlainText();
                                    }
                                    if (!Strings.isNullOrEmpty(text)) {
                                        if (xHeight == null) {
                                            float textWidth = (PDType1Font.TIMES_ROMAN.getStringWidth(text) * XHEIGHT_DEFAULT) / 1000;
                                            double multiplier = distance / textWidth;

                                            pdPageContentStream.setFont(PDType1Font.TIMES_ROMAN, (float) (multiplier * XHEIGHT_DEFAULT));
                                        }
                                        pdPageContentStream.showText(text);
                                    }

                                }
                                pdPageContentStream.endText();
                            }
                        }
                    }
                    pdPageContentStream.drawImage(pdImage, 0, 0, (float) scale * bufferedImage.getWidth(), (float) scale * bufferedImage.getHeight());

                    pdPageContentStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            pdDocument.save(pdfFileName);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: MinionConvertToPdf <output.pdf> <image-directory>");
            System.exit(1);
        }
        getPdfnew(args[0], args[1]);
    }

}
