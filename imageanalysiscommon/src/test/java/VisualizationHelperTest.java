import nl.knaw.huc.di.images.imageanalysiscommon.visualization.VisualizationHelper;
import nl.knaw.huc.di.images.layoutds.models.connectedComponent.ConnectedComponent;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.opencv.core.CvType.CV_32FC3;

public class VisualizationHelperTest {

    @Test
    public void countLines()  {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat colorized = Mat.zeros(500,500, CV_32FC3);
        BufferedImage bufferedImage = new BufferedImage(200,100, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(new Color (123,123,123));
        graphics.fillRect(0,0,100,100);
        ConnectedComponent coco = new ConnectedComponent(100,100, bufferedImage, VisualizationHelper.getRandomColor());
        VisualizationHelper.colorize(colorized,coco);

//        Imgcodecs.imwrite("/tmp/coco.png", colorized);
    }


}