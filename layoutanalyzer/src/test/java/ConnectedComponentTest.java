import nl.knaw.huc.di.images.imageanalysiscommon.connectedComponent.ConnectedComponentProc;
import nl.knaw.huc.di.images.imageanalysiscommon.imageConversion.ImageConversionHelper;
import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.net.URL;

import static org.opencv.core.CvType.CV_8UC1;

public class ConnectedComponentTest {

    @Test
    public void singleLineFoundAsOneTest() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat oneCoCo = Mat.zeros(100, 100, CvType.CV_8U);
        Imgproc.line(oneCoCo, new Point(2, 2), new Point(2, 2), new Scalar(255),2);

        ConnectedComponentProc detector = new ConnectedComponentProc();
        java.util.List<ConnectedComponent> cocos = detector.process(ImageConversionHelper.matToBufferedImage(oneCoCo), false);
        Assert.assertEquals("Wrong number of coco's found", 1, cocos.size());

    }

    @Test
    public void twoLinesTouchingLineFoundAsOne() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat oneCoCo = Mat.zeros(100, 100, CvType.CV_8U);
        oneCoCo.setTo(new Scalar(0));
        Imgproc.line(oneCoCo, new Point(2, 2), new Point(2, 4), new Scalar(255),2);
        Imgproc.line(oneCoCo, new Point(2, 4), new Point(10, 10), new Scalar(255),2);

        ConnectedComponentProc detector = new ConnectedComponentProc();
        java.util.List<ConnectedComponent> cocos = detector.process(ImageConversionHelper.matToBufferedImage(oneCoCo), false);
        Assert.assertEquals("Wrong number of coco's found", 1, cocos.size());

    }

    @Test
    public void twoLinesNotTouchingLineFoundAsTwo() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat oneCoCo = Mat.zeros(100, 100, CvType.CV_8U);
//        Imgcodecs.imwrite("/tmp/coco.png", oneCoCo);
        Imgproc.line(oneCoCo, new Point(2, 2), new Point(2, 4), new Scalar(255),2);
        Imgproc.line(oneCoCo, new Point(15, 15), new Point(10, 10), new Scalar(255),2);
//        Imgcodecs.imwrite("/tmp/coco.png", oneCoCo);

        ConnectedComponentProc detector = new ConnectedComponentProc();
        java.util.List<ConnectedComponent> cocos = detector.process(ImageConversionHelper.matToBufferedImage(oneCoCo), false);
        Assert.assertEquals("Wrong number of coco's found", 2, cocos.size());

    }

    @Test
    public void eightCocosDetected() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String testResourceUri8Cocos = "8cocos.bmp";
        URL url = getClass().getResource(testResourceUri8Cocos);
        Mat grayImage = new Mat();
        Mat image = Imgcodecs.imread(url.getPath());

        image.convertTo(grayImage, CV_8UC1);
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
        BufferedImage bufferedImage = ImageConversionHelper.matToBufferedImage(grayImage);

        ConnectedComponentProc detector = new ConnectedComponentProc();
        java.util.List<ConnectedComponent> cocos = detector.process(bufferedImage, false);
        Assert.assertEquals("Wrong number of coco's found", 8, cocos.size());
    }

    @Test
    public void oneCocoDetected() throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        String testResourceUri1Cocos = "1coco.png";
        URL url = getClass().getResource(testResourceUri1Cocos);
        Mat grayImage = new Mat();
        Mat image = Imgcodecs.imread(url.getPath());

        image.convertTo(grayImage, CV_8UC1);
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
        BufferedImage bufferedImage = ImageConversionHelper.matToBufferedImage(grayImage);


        ConnectedComponentProc detector = new ConnectedComponentProc();
        java.util.List<ConnectedComponent> cocos = detector.process(bufferedImage, false);
        Assert.assertEquals("Wrong number of coco's found", 1, cocos.size());

    }

}