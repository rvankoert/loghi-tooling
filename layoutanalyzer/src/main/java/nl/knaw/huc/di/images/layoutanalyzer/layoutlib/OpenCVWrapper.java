package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static org.opencv.core.CvType.*;

public class OpenCVWrapper {


    public static Mat newMat() {
        Mat newMat = new Mat();
        return newMat;
    }
    public static Mat newMat(Size size, int cvType) {
        Mat newMat = new Mat(size, cvType);
        return newMat;
    }

    public static Mat zeros(Size size, int cvType) {
        Mat newMat = Mat.zeros(size, cvType);
        return newMat;
    }


    public static Mat release(Mat mat) {
        if (mat != null) {
            mat.release();
        }else{
            System.out.println("Mat is already null. Calling release on null mat.");
        }
        return null;
    }

    public static MatOfInt release(MatOfInt mat) {
        if (mat != null) {
            mat.release();
        }else{
            System.out.println("Mat is already null. Calling release on null mat.");
        }
        return null;
    }

    public static MatOfPoint release(MatOfPoint mat) {
        if (mat != null) {
            mat.release();
        }else{
            System.out.println("Mat is already null. Calling release on null mat.");
        }
        return null;
    }

    public static MatOfPoint2f release(MatOfPoint2f mat) {
        if (mat != null) {
            mat.release();
        }else{
            System.out.println("Mat is already null. Calling release on null mat.");
        }
        return null;
    }

    public static Mat bitwise_not(Mat input) {
        Mat output = newMat();
        Core.bitwise_not(input, output);
        return output;
    }

    public static Mat warpAffine(Mat input, Mat rotationMat, Size newSize) {
        Mat correctImg = newMat();
        Imgproc.warpAffine(input, correctImg, rotationMat, newSize, Imgproc.INTER_LINEAR);
        return correctImg;
    }

    public static Mat adaptiveThreshold(Mat input, int size) {
        Mat binary = newMat();
        Imgproc.adaptiveThreshold(input, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, size, 15);
        return binary;
    }

    public static Mat addWeighted(Mat input1, Mat input2) {
        Mat result = newMat();
        Core.addWeighted(input1, 0.5, input2, 0.5, 0.0, result);
        return result;
    }

    public static Mat GaussianBlur(Mat input1) {
        Mat result = newMat(input1.size(), input1.type());
        Imgproc.GaussianBlur(input1, result, new Size(5, 5), 0);
        return result;
    }

    public static Mat cvtColor(Mat input) {
        Mat grayImage = newMat(input.size(), CV_8UC1);
        Imgproc.cvtColor(input, grayImage, Imgproc.COLOR_BGR2GRAY);
        return grayImage;
    }


    public static Mat merge(List<Mat> toMerge) {
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
        Mat finalOutput = newMat(toMerge.get(0).size(), toMerge.get(0).type());
        Core.merge(toMerge, finalOutput);
        return finalOutput;
    }

    public static void line(Mat image, Point point1, Point point2, Scalar scalar, int thickness) {
        Imgproc.line(image, point1, point2, scalar, thickness);
    }

    public static void line(Mat image, Point point1, Point point2) {
        Imgproc.line(image, point1, point2, new Scalar(255), 5);
    }

    public static Mat Sobel(Mat input, int ddepth, int dx, int dy, int ksize, int scale, int delta) {
        Mat sobel = newMat();
        Imgproc.Sobel(input, sobel, ddepth, dx, dy, ksize, scale, delta);
        return sobel;
    }

    public static Mat imread(String inputFile) {
        Mat image = Imgcodecs.imread(inputFile);
        return image;
    }


}
