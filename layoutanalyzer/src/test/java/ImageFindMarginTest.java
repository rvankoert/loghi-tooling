import nl.knaw.huc.di.images.imageanalysiscommon.imageConversion.ImageConversionHelper;
import nl.knaw.huc.di.images.layoutanalyzer.DocumentPage;
import nl.knaw.huc.di.images.layoutanalyzer.LayoutConfiguration;
import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.LayoutProc;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageFindMarginTest {

//    String testResourceUri = "testMarginsTwoTextLines.png";

//    @Test
//    public void marginsFoundCorrectlyForDummyImage() {
//        try {
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//            BufferedImage bufferedImage = ImageIO.read(getClass().getResource(testResourceUri));
//            Mat image = ImageConversionHelper.bufferImageToMat(bufferedImage);
//
//
//            LayoutConfiguration configuration = new LayoutConfiguration(image);
//            LayoutProc.setOutputDebug(false);
//            configuration.setOutputFile(false);
//            configuration.setOutputDebug(false);
//
//            LayoutConfiguration.setGlobal(configuration);
//
//            DocumentPage documentPage = new DocumentPage(image, testResourceUri);
//
//            Assert.assertEquals(String.format("topmargin incorrect: %s", documentPage.getTopMargin()), true, documentPage.getTopMargin() >= 136 && documentPage.getTopMargin() <= 234);
//            Assert.assertEquals(String.format("bottommargin incorrect: %s", documentPage.getBottomMargin()), true, documentPage.getBottomMargin() >= 1393 && documentPage.getBottomMargin() <= 1465);
//            Assert.assertEquals(String.format("leftmargin incorrect: %s", documentPage.getLeftMargin()), true, documentPage.getLeftMargin() >= 92 && documentPage.getLeftMargin() <= 181);
//            Assert.assertEquals(String.format("rightmargin incorrect: %s", documentPage.getRightMargin()), true, documentPage.getRightMargin() >= 1020 && documentPage.getRightMargin() <= 1127);
//
//
//        } catch (IOException iox) {
//
//        }
//    }

//    @Test
//    public void textLinesFoundCorrectlyForDummyImage() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        BufferedImage bufferedImage = ImageIO.read(getClass().getResource(testResourceUri));
//        Mat image = ImageConversionHelper.bufferImageToMat(bufferedImage);
//
//        LayoutConfiguration configuration = new LayoutConfiguration(image);
//        LayoutProc.setOutputDebug(false);
//        configuration.setOutputFile(true);
//        configuration.setOutputDebug(true);
//
//        LayoutConfiguration.setGlobal(configuration);
//        DocumentPage documentPage = new DocumentPage(image, testResourceUri);
//
//        System.out.println("number of text lines found: " + documentPage.getTextBlocks().get(0).getTextLines().size());
//        // Soft ok
//        boolean result = documentPage.getTextBlocks().get(0).getTextLines().size() == 2;
//
//        System.out.println("number of lines found: " + documentPage.getTextBlocks().get(0).getTextLines().size());
//
//        Assert.assertEquals("incorrect number of lines found", true, result);
//    }

//    @Test
//    public void textBlocksFoundCorrectlyForDummyImage() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        BufferedImage bufferedImage = ImageIO.read(getClass().getResource(testResourceUri));
//        Mat image = ImageConversionHelper.bufferImageToMat(bufferedImage);
//        LayoutConfiguration configuration = new LayoutConfiguration(image);
//        LayoutProc.setOutputDebug(false);
//        configuration.setOutputFile(false);
//        configuration.setOutputDebug(false);
//
//        LayoutConfiguration.setGlobal(configuration);
//        DocumentPage documentPage = new DocumentPage(image, testResourceUri);
//
//        // ok
//        boolean result = documentPage.getTextBlocks().size() == 1;
//        System.out.println("number of textBlocks found " + documentPage.getTextBlocks().size());
//
//        Assert.assertEquals("incorrect number of textBlocks found", true, result);
//
//    }


    @Test
    public void marginsFoundCorrectlyForActualImage() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        URL resource = getClass().getResource("0284_0011.tif");

        Mat image = Imgcodecs.imread(resource.getFile());

        LayoutConfiguration configuration = new LayoutConfiguration(image);
        LayoutProc.setOutputDebug(false);
        configuration.setOutputFile(false);
        configuration.setOutputDebug(false);

        LayoutConfiguration.setGlobal(configuration);

        DocumentPage documentPage = new DocumentPage(image, "0284_0011.tif");

        Assert.assertEquals(String.format("topmargin incorrect: %s", documentPage.getTopMargin()), true, documentPage.getTopMargin() >= 172 && documentPage.getTopMargin() <= 480);
        Assert.assertEquals(String.format("bottommargin incorrect: %s", documentPage.getBottomMargin()), true, documentPage.getBottomMargin() >= 5944 && documentPage.getBottomMargin() <= 6200);
        Assert.assertEquals(String.format("leftmargin incorrect: %s", documentPage.getLeftMargin()), true, documentPage.getLeftMargin() >= 428 && documentPage.getLeftMargin() <= 976);
        Assert.assertEquals(String.format("rightmargin incorrect: %s", documentPage.getRightMargin()), true, documentPage.getRightMargin() >= 4712 && documentPage.getRightMargin() <= 4816);
    }

//    @Test
//    public void textBlocksFoundCorrectlyForActualImage() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        URL resource = getClass().getResource("0284_0011.tif");
//
//        Mat image = Imgcodecs.imread(resource.getFile());
//
//        LayoutConfiguration configuration = new LayoutConfiguration(image);
//        LayoutProc.setOutputDebug(false);
//        configuration.setOutputFile(false);
//        configuration.setOutputDebug(false);
//
//        LayoutConfiguration.setGlobal(configuration);
//
//        DocumentPage documentPage = new DocumentPage(image, "0284_0011.tif");
//
//        // ok
//        boolean result = documentPage.getTextBlocks().size() == 2;
//        System.out.println("number of textBlocks found " + documentPage.getTextBlocks().size());
//
//        Assert.assertEquals("incorrect number of textBlocks found", true, result);
//    }


//    @Test
//    public void textLinesFoundCorrectlyForActualImage() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        URL resource = getClass().getResource("0284_0011.tif");
//
//        Mat image = Imgcodecs.imread(resource.getFile());
//
//        LayoutConfiguration configuration = new LayoutConfiguration(image);
//        LayoutProc.setOutputDebug(false);
//        configuration.setOutputFile(false);
//        configuration.setOutputDebug(false);
//
//        LayoutConfiguration.setGlobal(configuration);
//
//        DocumentPage documentPage = new DocumentPage(image, "0284_0011.tif");
//
//        boolean result = documentPage.getDocumentTextLines().size() >= 94; // 94 textlines and one header
//        result = result && documentPage.getDocumentTextLines().size() <= 96 * 1.1; // 1.1== error margin
//        System.out.println("number of DocumentTextLines found " + documentPage.getDocumentTextLines().size());
//
//        Assert.assertEquals("incorrect number of DocumentTextLines found", true, result);
//    }

}
