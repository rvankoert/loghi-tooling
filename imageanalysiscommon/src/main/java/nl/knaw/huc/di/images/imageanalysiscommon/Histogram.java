package nl.knaw.huc.di.images.imageanalysiscommon;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;

public class Histogram {
    public Mat createCombinedRGBHistogram(Mat first, Mat second) {
        Mat firstHisto = createRGBHistogram(first);
        Mat secondHisto = createRGBHistogram(second);

        Mat combined = new Mat();
        List<Mat> imagesToConcat = Arrays.asList(firstHisto, secondHisto);
        Core.vconcat(imagesToConcat, combined);
        return combined;

    }

    public Mat createRGBHistogram(Mat src) {
        Mat srcRefGray = new Mat();
//        src.convertTo(srcRefGray, CV_8UC1);
        Imgproc.cvtColor(src, srcRefGray, Imgproc.COLOR_RGB2GRAY);

        Mat srcRefMask = new Mat();

        Imgproc.threshold(srcRefGray, srcRefMask, 127, 255, Imgproc.THRESH_OTSU);
        Mat invertcolormatrix = new Mat(srcRefMask.rows(), srcRefMask.cols(), srcRefMask.type(), new Scalar(255, 255, 255));
        Core.subtract(invertcolormatrix, srcRefMask, srcRefMask);

        /// Using 256 bins rgb values
        int bins = 256;
        MatOfInt histSize = new MatOfInt(bins, bins, bins);

        // hue varies from 0 to 179, saturation from 0 to 255
        MatOfFloat ranges = new MatOfFloat(0f, 255f, 0f, 255f, 0f, 255f);

        // we compute the histogram from the 0-th and 1-st channels
        MatOfInt channels = new MatOfInt(0, 1, 2);


        Mat histRef = new Mat();

        ArrayList<Mat> histImages = new ArrayList<>();
        histImages.add(src);
        Imgproc.calcHist(histImages,
                channels,
                srcRefMask,
                histRef,
                histSize,
                ranges,
                false);

        List<Mat> bgr_planes = new ArrayList<>();
        Core.split(src, bgr_planes);

        List<Mat> bPlane = new ArrayList<>();
        bPlane.add(bgr_planes.get(0));
        List<Mat> gPlane = new ArrayList<>();
        gPlane.add(bgr_planes.get(1));
        List<Mat> rPlane = new ArrayList<>();
        rPlane.add(bgr_planes.get(2));

        Mat histImageb = getHistImage(srcRefMask, bins, bPlane);
        Mat histImageg = getHistImage(srcRefMask, bins, gPlane);
        Mat histImager = getHistImage(srcRefMask, bins, rPlane);

        Mat concatted = new Mat();
        List<Mat> imagesToConcat = Arrays.asList(histImager, histImageg, histImageb);
        Core.hconcat(imagesToConcat, concatted);

        return concatted;
    }

    public Mat getHistImage(Mat srcRefMask, int bins, List<Mat> bPlane) {
        MatOfInt histSize;
        MatOfInt channels;
        Mat bHist = new Mat();

        histSize = new MatOfInt(bins);
        MatOfFloat histRange = new MatOfFloat(0, 255);

        channels = new MatOfInt(0);

        /// Compute the histograms:
        Imgproc.calcHist(bPlane, channels, srcRefMask, bHist, histSize, histRange, true);

        int hist_w = 512;
        int hist_h = 400;
        int bin_w = (hist_w / bins);

        Mat histImageb = Mat.zeros(hist_h, hist_w, CV_8UC3);

        /// Normalize the result to [ 0, histImage.rows ]
        Core.normalize(bHist, bHist, 0, histImageb.rows(), Core.NORM_MINMAX, -1, new Mat());

        /// Draw for each channel
        for (int i = 0; i < bins; i++) {
            if (bHist.get(i, 0) != null) {
                Imgproc.line(histImageb,
                        new Point(bin_w * (i), histImageb.height() - 1),
                        new Point(bin_w * (i), hist_h - bHist.get(i, 0)[0]),
                        new Scalar(255, 0, 0), 2, 8, 0);
            }
        }
        return histImageb;
    }
}
