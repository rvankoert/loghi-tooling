package nl.knaw.huc.di.images.layoutanalyzer.layoutlib;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;

public class OpenCVWrapper {


    public static Mat newMat() {
        Mat newMat = new Mat();
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
        return mat;
    }

    public static MatOfPoint release(MatOfPoint mat) {
        if (mat != null) {
            mat.release();
        }
        return mat;
    }

    public static MatOfPoint2f release(MatOfPoint2f mat) {
        if (mat != null) {
            mat.release();
        }
        return mat;
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
        Mat result = newMat();
        Imgproc.GaussianBlur(input1, result, new Size(5, 5), 0);
        return result;
    }

    public static Mat cvtColor(Mat input) {
        Mat grayImage = newMat();
        Imgproc.cvtColor(input, grayImage, Imgproc.COLOR_BGR2GRAY);
        return grayImage;
    }


    public static Mat merge(List<Mat> toMerge) {
        Mat finalOutput = newMat();
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
