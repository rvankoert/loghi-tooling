package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import nl.knaw.huc.di.images.pagexmlutils.PageUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.opencv.core.CvType.*;

public class OpenCVWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(OpenCVWrapper.class);


    public static Mat newMat() {
        Mat newMat = new Mat();
        return newMat;
    }
//    public static Mat newMat(Size size, int cvType) {
//        Mat newMat = new Mat(size, cvType);
//        return newMat;
//    }

    public static Mat zeros(Size size, int cvType) {
        Mat newMat = Mat.zeros(size, cvType);
        return newMat;
    }


    public static Mat release(Mat mat) {
        if (mat != null) {
//            Imgproc.resize(mat,mat, new Size(0,0));
//            mat.reshape(0,0);
//            mat.release();
        }else{
            LOG.error("Mat is already null. Calling release on null Mat.");
            throw new RuntimeException("Mat is already null. Calling release on null Mat.");
        }
        return null;
    }

    public static MatOfInt release(MatOfInt mat) {
        if (mat != null) {
//            mat.release();
        }else{
            LOG.error("Mat is already null. Calling release on null MatOfInt.");
        }
        return null;
    }

    public static MatOfPoint release(MatOfPoint mat) {
        if (mat != null) {
//            mat.release();
        }else{
            LOG.error("Mat is already null. Calling release on null MatOfPoint.");
        }
        return null;
    }

    public static MatOfPoint2f release(MatOfPoint2f mat) {
        if (mat != null) {
//            mat.release();
        }else{
            LOG.error("Mat is already null. Calling release on null MatOfPoint2f.");
        }
        return null;
    }

    public static void bitwise_not(Mat input, Mat destination) {
        if (input == null) {
            LOG.error("Input is null. ");
            throw new RuntimeException("Input is null.");
        }
        if (destination == null) {
            LOG.error("Destination is null.");
            throw new RuntimeException("Destination is null.");
        }
        if (input.type() != CV_8UC1){
            LOG.error("Input is not a valid type.");
            throw new RuntimeException("Input is not a valid type.");
        }
        Core.bitwise_not(input, destination);
    }

    public static void warpAffine(Mat input, Mat rotationMat, Size newSize, Mat destination) {
        if (input == null) {
            LOG.error("Input is null.");
            throw new IllegalArgumentException("Input is null.");
        }
        if (rotationMat == null) {
            LOG.error("Rotation matrix is null.");
            throw new IllegalArgumentException("Rotation matrix is null.");
        }
        if (newSize == null) {
            LOG.error("New size is null.");
            throw new IllegalArgumentException("New size is null.");
        }

        Imgproc.warpAffine(input, destination, rotationMat, newSize, Imgproc.INTER_LINEAR);
    }

    public static void adaptiveThreshold(Mat input, Mat destination, int blocksize) {
        if (input == null) {
            LOG.error("Input is null.");
            throw new IllegalArgumentException("Input is null.");
        }

        Imgproc.adaptiveThreshold(input, destination, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, blocksize, 15);
    }

    public static void addWeighted(Mat input1, Mat input2, Mat destination) {
        if (input1 == null) {
            LOG.error("Input1 is null.");
            throw new IllegalArgumentException("Input1 is null.");
        }
        if (input2 == null) {
            LOG.error("Input2 is null.");
            throw new IllegalArgumentException("Input2 is null.");
        }
        if (input1.size().equals(input2.size())) {
            Core.addWeighted(input1, 0.5, input2, 0.5, 0.0, destination);
        } else {
            LOG.error("Matrices are not the same size.");
            throw new IllegalArgumentException("Matrices are not the same size.");
        }
    }

    public static void GaussianBlur(Mat input1, Mat destination) {
        Imgproc.GaussianBlur(input1, destination, new Size(5, 5), 0);
    }

    public static void cvtColor(Mat input, Mat grayImage) {
        Imgproc.cvtColor(input, grayImage, Imgproc.COLOR_BGR2GRAY);
    }


    public static void merge(List<Mat> toMerge, Mat destination) {
        int cvtype;
        if (toMerge.size() == 3) {
            cvtype = CV_8UC3;
        } else if (toMerge.size()==4) {
            cvtype = CV_8UC4;
        } else {
            throw new IllegalArgumentException("Can only merge 3 or 4 Mats");
        }
        int initialType = toMerge.get(0).type();
        for (Mat mat : toMerge) {
            if (mat.type() != CV_8UC1) {
                throw new IllegalArgumentException("All Mats must be CV_8UC1");
            }
            if (mat.type() != initialType) {
                throw new IllegalArgumentException("All Mats must have the same type");
            }
        }
        Core.merge(toMerge, destination);
    }

    public static void line(Mat image, Point point1, Point point2, Scalar scalar, int thickness) {
        if (image == null) {
            LOG.error("Image is null. Not drawing line.");
            return;
        }
        if (point1 == null) {
            LOG.error("Point1 is null. Not drawing line.");
            return;
        }
        if (point2 == null) {
            LOG.error("Point2 is null. Not drawing line.");
            return;
        }
        if (scalar == null) {
            LOG.error("Scalar is null. Not drawing line.");
            return;
        }
        if (point1.x < 0 || point1.y < 0 || point2.x < 0 || point2.y < 0) {
            LOG.error("Point is negative. Not drawing line.");
            return;
        }
        if (point1.x >= image.width() || point1.y >= image.height() || point2.x >= image.width() || point2.y >= image.height()) {
            LOG.error("Point is outside image. Not drawing line.");
            return;
        }
        Imgproc.line(image, point1, point2, scalar, thickness);
    }

    public static void line(Mat image, Point point1, Point point2) {
        Imgproc.line(image, point1, point2, new Scalar(255), 5);
    }

    public static void Sobel(Mat input, int ddepth, int dx, int dy, int ksize, int scale, int delta, Mat destination) {
                Imgproc.Sobel(input, destination, ddepth, dx, dy, ksize, scale, delta);
    }

    public static void threshold(Mat input, Mat output, int threshold, boolean invert) {
        if (invert) {
            Imgproc.threshold(input, output, threshold, 255, Imgproc.THRESH_BINARY_INV);
        } else {
            Imgproc.threshold(input, output, threshold, 255, Imgproc.THRESH_BINARY);
        }
    }
}
