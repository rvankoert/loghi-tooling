package nl.knaw.huc.di.images.imageanalysiscommon.imageConversion;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageConversionHelper {

    public ImageConversionHelper() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    // method taken from https://stackoverflow.com/questions/9417356/bufferedimage-resize
    public static BufferedImage resize(BufferedImage bufferedImage, int newWidth, int newHeight) {
        Image tmp = bufferedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage destinationImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = destinationImage.createGraphics();
        graphics2D.drawImage(tmp, 0, 0, null);
        graphics2D.dispose();

        return destinationImage;
    }


    public static BufferedImage matToBufferedImage(Mat matrix) throws Exception {
        if (matrix == null) {
            throw new Exception("Matrix is null");
        }
        int columns = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[columns * rows * elemSize];
        int type;
        matrix.get(0, 0, data);
        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;
            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;
                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;
            case 4:
                type = BufferedImage.TYPE_4BYTE_ABGR;
                // bgr to rgb
                byte databyte;
                for (int i = 0; i < data.length; i = i + 4) {
                    databyte = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = databyte;
                }
                break;
            default:
                throw new Exception("conversion not supported");
        }

        BufferedImage bufferedImage = new BufferedImage(columns, rows, type);
        bufferedImage.getRaster().setDataElements(0, 0, columns, rows, data);
        return bufferedImage;
    }

    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);
        return mat;
    }

    public static Mat bufferedImageToBinaryMat(BufferedImage bufferedImage) {
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC(1));
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, pixels);

        return mat;
    }
}
