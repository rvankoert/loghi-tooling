//import nl.knaw.huc.di.images.layoutanalyzer.layoutlib.ARUProcessor;
//import nl.knaw.huc.di.images.layoutds.models.*;
//import org.junit.Assert;
//import org.junit.Test;
//import org.opencv.core.Core;
//import org.opencv.core.Mat;
//import org.opencv.core.Point;
//import org.opencv.core.Size;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.ximgproc.SuperpixelSLIC;
//import org.opencv.ximgproc.Ximgproc;
//
//import javax.swing.text.Document;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import static org.opencv.core.CvType.CV_32FC3;
//
//public class ARUProcessorTest {
//
//
//    private String originalInputStatenGeneraal = "NL-HaNA_1.01.02_3780_0067-in.jpg";
//    private String aruProcessedStatenGeneraal = "NL-HaNA_1.01.02_3780_0067-out.jpg";
//
//    private String originalInputStatenGeneraalSkewed = "NL-HaNA_1.01.02_3855_0059-in.jpg";
//    private String aruProcessedStatenGeneraalSkewed = "NL-HaNA_1.01.02_3855_0059-out.jpg";
//
//    private String originalInputMedieval = "0284_0011.tif";
//    private String aruProcessedMedieval = "0284_0011-out.tif";
//
//
//    @Test
//    public void countLines() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//
//        List<LineDescriptor> lines = ARUProcessor.extractTextLines(aruProcessedImage, originalInputImage, 0, "none", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertTrue(lines.size() > 240 && lines.size() < 280);
//    }
//
//    @Test
//    public void checkSingleLine() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//
//        List<LineDescriptor> lines = ARUProcessor.extractTextLines(aruProcessedImage, originalInputImage, 0, "none", "none", "none", 10, 10, false, false, colorized);
//
//        int matches = 0;
//        for (LineDescriptor lineDescriptor : lines) {
//            if (lineDescriptor.getBaseline().get(0).y > 378 && lineDescriptor.getBaseline().get(0).x < 1374 &&
//                    lineDescriptor.getTopLine().get(lineDescriptor.getTopLine().size() - 1).y < 410 && lineDescriptor.getBaseline().get(lineDescriptor.getBaseline().size() - 1).x > 1374
//            ) {
//                // this should be the page number on the left page
//                matches++;
//            }
//        }
//
//        Assert.assertEquals(1, matches);
//    }
//
//    @Test
//    public void findTextBlocks() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        ArrayList<LineDescriptor> lines = ARUProcessor.extractTextLines(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, null);
//        List<DocumentTextBlock> textBlocks = ARUProcessor.getColumns(aruProcessedImage, originalInputImage, lines,null, false);
//        System.out.println(textBlocks.size());
//
//
//        Assert.assertEquals(5, textBlocks.size());
//    }
//
//    @Test
//    public void findTextBlocksSkewed() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraalSkewed).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraalSkewed).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        ArrayList<LineDescriptor> lines = ARUProcessor.extractTextLines(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, null);
//        List<DocumentTextBlock> textBlocks = ARUProcessor.getColumns(aruProcessedImage, originalInputImage, lines,null, false);
//        System.out.println(textBlocks.size());
//
//
//        Assert.assertEquals(5, textBlocks.size());
//    }
//
////    @Test
////    public void findTextBlocksMedieval() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputMedieval).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedMedieval).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "", "", "null", 0, 0, false, false, colorized);
////        System.out.println(documentPage.getDocumentTextBlocks().size());
////
////
////        Assert.assertEquals(2, documentPage.getDocumentTextBlocks().size());
////    }
//
//    @Test
//    public void findXHeightTest() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        int xheight = ARUProcessor.getMedianXHeight(aruProcessedImage, originalInputImage, 0, "none", "none", "none", 10, 10, false);
//        Assert.assertTrue(xheight >= 23 && xheight <= 27);
//    }
//
//
//    @Test
//    public void smoothLinesTest() {
//        ArrayList<Point> points = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            Point point = new Point(i, 123);
//            points.add(point);
//        }
//        ArrayList<Point> smoothPoints = ARUProcessor.smoothLine(points);
//
//        Assert.assertEquals(points.size(), smoothPoints.size());
//    }
//
//    @Test
//    public void smoothLinesSmallTest() {
//        ArrayList<Point> points = new ArrayList<>();
//        for (int i = 0; i < 4; i++) {
//            Point point = new Point(i, 123);
//            points.add(point);
//        }
//        ArrayList<Point> smoothPoints = ARUProcessor.smoothLine(points);
//
//        Assert.assertEquals(points.size(), smoothPoints.size());
//    }
//
//    @Test
//    public void smoothLinesEdgeCaseTest() {
//        ArrayList<Point> points = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Point point = new Point(i, 123);
//            points.add(point);
//        }
//        List<Point> smoothPoints = ARUProcessor.smoothLine(points);
//
//        Assert.assertEquals(points.size(), smoothPoints.size());
//    }
//
//    @Test
//    public void getDocumentPage() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource(originalInputStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource(aruProcessedStatenGeneraal).getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_63358505() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("63358505-in.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("63358505-out.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_63454939() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(3, documentPage.getDocumentTextBlocks().size());
//    }
//
//    // 31 textlines left
//    // 34 textlines right
//    // one header right
////    @Test
////    public void getDocumentPageLines_63454939() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63454939.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(3, documentPage.getDocumentTextBlocks().get(1).getDocumentParagraphs().size());
////        Assert.assertEquals(1, documentPage.getDocumentTextBlocks().get(2).getDocumentParagraphs().size());
////    }
//
//
//
////    @Test
////    //contains marginalia
////    public void getDocumentPage_63454848() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63454848.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63454848.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(2, documentPage.getDocumentTextBlocks().size());
////    }
//
//    @Test
//    //contains marginalia
//    public void getDocumentPage_63454645() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63454645.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63454645.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Imgproc.GaussianBlur(originalInputImage, originalInputImage, new Size(3, 3), 101);
//
////        SuperpixelSLIC superpixelSLIC = Ximgproc.createSuperpixelSLIC(originalInputImage, Ximgproc.SLIC);
////        superpixelSLIC.iterate(20);
//
////        Random random = new Random();
//
////        Mat labelsOut = new Mat();
////        superpixelSLIC.getLabels(labelsOut);
////        Mat labels = new Mat();
////        Mat stats = new Mat();
////        Mat centroids = new Mat();
////        Imgproc.connectedComponentsWithStats(labelsOut, labels, stats, centroids);
//
//
////        ArrayList<byte[]> colors = new ArrayList<>();
////        for (int i =0; i< superpixelSLIC.getNumberOfSuperpixels();i++) {
////            byte[] color = new byte[1];
////            color[0] = (byte)random.nextInt(255);
////            colors.add(color);
////        }
////        for (int i =0; i< labelsOut.height();i++){
////            for (int j =0; j< labelsOut.width();j++) {
////                int[] index =new int[1];
////                labelsOut.get(i,j,index);
////                originalInputImage.put(i,j,colors.get(index[0]));
////            }
////        }
////
////        Imgcodecs.imwrite("/tmp/contours.png", originalInputImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//        List<LineDescriptor> lines = ARUProcessor.extractTextLines(aruProcessedImage, originalInputImage, 0, "none", "none", "none", 10, 10, false, false, colorized);
//        Imgcodecs.imwrite("/tmp/testcutting.png", originalInputImage);
//
////        Assert.assertEquals(2, documentPage.getDocumentTextBlocks().size());
//    }
//
////    @Test
////    //contains marginalia
////    public void getDocumentPage_63396325() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63396325.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63396325.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(2, documentPage.getDocumentTextBlocks().size());
////    }
//
//    @Test
//    public void getDocumentPage_63373173() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63373173.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63373173.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63373173.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    // almost blank frontpage
//    public void getDocumentPage_63364352() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63364352.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63364352.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63364352.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(1, documentPage.getDocumentTextBlocks().size());
//    }
//
//    //63363319 is too complex for now
////63363318 too complex
//
//
//    @Test
//    // blank pages
//    public void getDocumentPage_63363233() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63363233.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63363233.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63363233.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(0, documentPage.getDocumentTextBlocks().size());
//    }
//
//    //63363172 too complex
//    //63363158 too complex
//
//
////    @Test
////    // tafel der respecten, 6 columns
////    public void getDocumentPage_63363133() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63363133.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63363133.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63363133.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(6, documentPage.getDocumentTextBlocks().size());
////    }
//
////63362976 4 columns
//
//    @Test
//    //63362881 2 two final page
//    public void getDocumentPage_63362881() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63362881.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63362881.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63362881.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(3, documentPage.getDocumentTextBlocks().size());
//    }
//
//    //63360071 index, too complex
//
//    @Test
//    //63359620 4 columns
//    public void getDocumentPage_63359620() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63359620.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63359620.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63359620.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, true, colorized);
//
//        for (DocumentTextBlock documentTextBlock : documentPage.getDocumentTextBlocks()) {
//            for (DocumentParagraph documentParagraph : documentTextBlock.getDocumentParagraphs()) {
//                for (DocumentTextLine documentTextLine : documentParagraph.getDocumentTextLines()) {
//                    System.out.println(documentTextLine.getText());
//                }
//            }
//            for (DocumentTextBlock subDocumentTextBlock : documentTextBlock.getDocumentTextBlocks()) {
//                for (DocumentParagraph documentParagraph : subDocumentTextBlock.getDocumentParagraphs()) {
//                    for (DocumentTextLine documentTextLine : documentParagraph.getDocumentTextLines()) {
//                        System.out.println(documentTextLine.getText());
//                    }
//                }
//            }
//        }
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    //63359298 index too complex
//    // 63359140 index too complex
//    //63359109 index too complex
//    //63359100 index too complex
//
////    @Test
////    //63359094 15? columns, index page
////    public void getDocumentPage_63359094() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63359094.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63359094.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(15, documentPage.getDocumentTextBlocks().size());
////    }
//
//    //63359089 index too complex
//
//
//    @Test
//    //NL-HaNA_1.10.94_447_00064 columns
//    public void getDocumentPage_NLHaNA_11094_447_0006() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.10.94_447_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.10.94_447_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.10.94_447_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    //NL-HaNA_1.10.94_445_0006.jpg columns
//    public void getDocumentPage_NLHaNA_11094_445_0006() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.10.94_445_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.10.94_445_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.10.94_445_0006.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
////    @Test // Index page
////    public void getDocumentPage_NLHaNA_10102_3805_0009() throws Exception {
////        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
////
////        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3805_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3805_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3805_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
////
////        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
////
////        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
////        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
////
////        Assert.assertEquals(4, documentPage.getDocumentTextBlocks().size());
////    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3799_0009() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3799_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3799_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3799_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3790_0008() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3790_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3790_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3790_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3772_0009() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3772_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3772_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3772_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3771_0009() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3771_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3771_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3771_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3766_0009() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3766_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3766_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3766_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3765_0008() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3765_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3765_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3765_0008.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//    @Test
//    public void getDocumentPage_NLHaNA_10102_3764_0009() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/NL-HaNA_1.01.02_3764_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/NL-HaNA_1.01.02_3764_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/NL-HaNA_1.01.02_3764_0009.jpg").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, false, colorized);
//
//        Assert.assertEquals(5, documentPage.getDocumentTextBlocks().size());
//    }
//
//
//    @Test
//    public void getDocumentPage_63469349() throws Exception {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//        Mat originalInputImage = Imgcodecs.imread(getClass().getResource("in/63469349.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedImage = Imgcodecs.imread(getClass().getResource("out/63469349.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//        Mat aruProcessedSepImage = Imgcodecs.imread(getClass().getResource("out-sep/63469349.png").getPath(), Imgcodecs.IMREAD_GRAYSCALE);
//
//        Core.subtract(aruProcessedImage, aruProcessedSepImage, aruProcessedImage);
//
//        Mat colorized = Mat.zeros(originalInputImage.size(), CV_32FC3);
//        DocumentPage documentPage = ARUProcessor.getDocumentPage(aruProcessedImage, originalInputImage, 0, "/tmp/testtextlines/", "none", "none", 10, 10, false, true, colorized);
//    }
//
//}