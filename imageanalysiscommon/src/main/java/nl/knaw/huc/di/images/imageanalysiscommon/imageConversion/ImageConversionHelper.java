package nl.knaw.huc.di.images.imageanalysiscommon.imageConversion;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageConversionHelper {

    public ImageConversionHelper() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


//    // taken from https://stackoverflow.com/questions/7051025/how-do-i-scale-a-streaming-bitmap-in-place-without-reading-the-whole-image-first
//    /**
//     * Read the image from the stream and create a bitmap scaled to the desired
//     * size.  Resulting bitmap will be at least as large as the
//     * desired minimum specified dimensions and will keep the image proportions
//     * correct during scaling.
//     */
//    protected Bitmap createScaledBitmapFromStream( InputStream s, int minimumDesiredBitmapWidth, int minimumDesiredBitmapHeight ) {
//
//        final BufferedInputStream is = new BufferedInputStream(s, 32*1024);
//        try {
//            final Options decodeBitmapOptions = new Options();
//            // For further memory savings, you may want to consider using this option
//            // decodeBitmapOptions.inPreferredConfig = Config.RGB_565; // Uses 2-bytes instead of default 4 per pixel
//
//            if( minimumDesiredBitmapWidth >0 && minimumDesiredBitmapHeight >0 ) {
//                final Options decodeBoundsOptions = new Options();
//                decodeBoundsOptions.inJustDecodeBounds = true;
//                is.mark(32*1024); // 32k is probably overkill, but 8k is insufficient for some jpgs
//                BitmapFactory.decodeStream(is,null,decodeBoundsOptions);
//                is.reset();
//
//                final int originalWidth = decodeBoundsOptions.outWidth;
//                final int originalHeight = decodeBoundsOptions.outHeight;
//
//                // inSampleSize prefers multiples of 2, but we prefer to prioritize memory savings
//                decodeBitmapOptions.inSampleSize= Math.max(1,Math.min(originalWidth / minimumDesiredBitmapWidth, originalHeight / minimumDesiredBitmapHeight));
//
//            }
//
//            return BitmapFactory.decodeStream(is,null,decodeBitmapOptions);
//
//        } catch( IOException e ) {
//            throw new RuntimeException(e); // this shouldn't happen
//        } finally {
//            try {
//                is.close();
//            } catch( IOException ignored ) {}
//        }
//
//    }


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
        if (matrix != null) {
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
        } else { // mat was null
            throw new Exception("Mat is null");
        }
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

    public static byte[] readStream(InputStream stream) throws IOException {
        // Copy content of the image to byte-array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] temporaryImageInMemory = buffer.toByteArray();
        buffer.close();
        stream.close();
        return temporaryImageInMemory;
    }


}
