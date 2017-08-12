package at.fhooe.pro3.resistordetector.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class contains some experimental code and different techniques
 * that can be used to optimize the detection of the resistor.
 * <p>
 * Contains mainly experimental and untested code.
 * <p>
 * Created by stefan on 21.05.2017.
 */
public class ExperimentsResistorDetector extends ResistorDetector {

    private static final int NR_OF_COLUMNS_TO_COMBINE = 5;
    private static final int MIN_BAND_WIDTH = NR_OF_COLUMNS_TO_COMBINE + 1;

    private boolean verboseDetectionDetails = false;
    private DetectionResult detectionResult = null;

    public ExperimentsResistorDetector(ResultListener resultListener) {
        super(resultListener);
    }

    @Override
    public void detectResistorValue(Mat resistorImage) {

        detectionResult = new DetectionResult();
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("original Image", resistorImage));

        applyFilters(resistorImage);

        Imgproc.cvtColor(resistorImage, resistorImage, Imgproc.COLOR_BGR2HSV);

        Mat resistorMask = getResistorAsMask(resistorImage);

        Mat medianValues = getMedianColorsOfColumns(resistorImage, resistorMask);

        //        ////detect edges
        //        Mat detectedEdges = new Mat(medianValues.rows(), medianValues.cols(), CvType.CV_8UC1);
        //
        //        Imgproc.cvtColor(medianValues, detectedEdges, Imgproc.COLOR_HSV2BGR);
        //        Imgproc.cvtColor(detectedEdges, detectedEdges, Imgproc.COLOR_BGR2GRAY);
        //        Imgproc.blur(detectedEdges, detectedEdges, new Size(3, 3));
        //        Imgproc.Canny(detectedEdges, detectedEdges, 25, 25 * 3, 3, false);
        //
        //        Imgproc.cvtColor(detectedEdges, tmpMat, Imgproc.COLOR_GRAY2BGR);
        //        Imgproc.resize(tmpMat, tmpMat, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        //        detectionResult.addDetectionStepDetail(new DetectionStepDetail("detected edges", tmpMat));
        //
        //        if (detectedEdges.rows() != 1)
        //            throw new AssertionError();
        //
        //        ArrayList<Integer> boundaries = new ArrayList<>();
        //
        //        boundaries.add(0);  //first column is a boundary
        //
        //        for(int i = 0; i < detectedEdges.cols(); i++){
        //            double[] a = detectedEdges.get(0, i);
        //
        //            if(a[0] == 255)
        //                boundaries.add(i);
        //        }
        //
        //        boundaries.add(detectedEdges.cols());   //last column is a boundary
        //
        //        //// histogram
        //
        //        int lowerBound2 = boundaries.get(0);
        //        for(int i = 1; i<boundaries.size();i++){
        //
        //            Mat col = resistorImage.colRange(lowerBound2, boundaries.get(i));
        //            Mat mask = resistorMask.colRange(lowerBound2, boundaries.get(i));
        //
        //            Scalar colColor = getColorUsingHsvHistogram(col, mask);
        //
        //            col.setTo(colColor);
        //
        //                        ///use s and v from mean
        //                        //double[] hsvMean = meanBands.get(0,i);
        //                        //cols.setTo(new Scalar(hMax, hsvMean[1], hsvMean[2]));
        //
        //            bandNames.add(ColorDefinitionsHsv.getColorName(colColor));
        //
        //            lowerBound2 = boundaries.get(i);
        //        }
        //
        //        Imgproc.resize(resistorImage, tmpMat, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        //        Imgproc.cvtColor(tmpMat, tmpMat, Imgproc.COLOR_HSV2BGR);
        //        detectionResult.addDetectionStepDetail(new DetectionStepDetail("mean of bands (using histogram)", tmpMat));
        //
        //
        //        StringBuilder sb2 = new StringBuilder();
        //        for(int i = 0; i<boundaries.size()-1;i++){
        //
        //            String name = bandNames.get(i).toString();
        //            sb2.append(name).append("|");
        //        }
        //
        //        detectionResult.addDetectionStepDetail(new DetectionStepDetail(sb2.toString()));
        //
        //        Imgproc.cvtColor(resistorImage, resistorImage, Imgproc.COLOR_HSV2BGR);

        //detectedEdges.release();


        //find background color of resistor
        //the most common color *should* be the background
//        Scalar mainColor = getColorUsingHsvHistogram(resistorImage, resistorMask);
//        Mat tmpMat2 = new Mat(50,50,resistorImage.type());
//        tmpMat2.setTo(mainColor);
//        Imgproc.cvtColor(tmpMat2, tmpMat2, Imgproc.COLOR_HSV2BGR);
//        detectionResult.addDetectionStepDetail(new DetectionStepDetail("main resistor color", tmpMat2));
//
//        Mat m = new Mat();
//        Core.inRange(resistorImage, mainColor.mul(new Scalar(0.6, 0.6, 0.6)), mainColor.mul(new Scalar(1.4, 1.4, 1.4)), m);
//
//        Mat tmpMat3 = new Mat();
//        Imgproc.cvtColor(m, tmpMat3, Imgproc.COLOR_GRAY2BGR);
//        detectionResult.addDetectionStepDetail(new DetectionStepDetail("main resistor color mask", tmpMat3));
//


        ColorName[] columnColors = new ColorName[medianValues.cols()];

        for (int i = 0; i < medianValues.cols(); i++) {

            double[] colValue = medianValues.get(0, i);
            ColorName colColor = ColorDefinitionsHsv.getColorName(new Scalar(colValue));

            columnColors[i] = colColor;
        }

        for (int i = 0; i < columnColors.length; i++) {
            Scalar detectedColor = ColorDefinitionsHsv.getColorFromName(columnColors[i]);

            medianValues.put(0, i, detectedColor.val[0], detectedColor.val[1], detectedColor.val[2]);
        }

        Mat tmpMat = MatColorConversions.newBgrMatFromHsv(medianValues);
        Imgproc.resize(tmpMat, tmpMat, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("Detected color per column", tmpMat));

        List<ColorName> bands = new ArrayList<>();
        List<Integer> bandsWidth = new ArrayList<>();

        ColorName tmpName;
        int tmpWidth;

        for (int i = 0; i < columnColors.length; i++) {
            tmpName = columnColors[i];
            tmpWidth = 0;

            i++;

            while (i < columnColors.length && columnColors[i] == tmpName) {
                i++;
                tmpWidth++;
            }

            if (tmpWidth >= MIN_BAND_WIDTH) {
                if (tmpName != ColorName.Unknown) {
                    bands.add(tmpName);
                    bandsWidth.add(tmpWidth);
                }
            }
        }

        medianValues.setTo(new Scalar(0, 0, 0));
        int count = 0;
        for (int i = 0; i < bands.size(); i++) {

            for (int j = 0; j < bandsWidth.get(i); j++) {

                Scalar detectedColor = ColorDefinitionsHsv.getColorFromName(bands.get(i));

                medianValues.put(0, count, detectedColor.val[0], detectedColor.val[1], detectedColor.val[2]);
                count++;
            }
        }

        Mat tmpMat2 = MatColorConversions.newBgrMatFromHsv(medianValues);
        Imgproc.resize(tmpMat2, tmpMat2, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("Detected color per band", tmpMat2));

        if (numberOfBands == NumberOfBands.Four && bands.size() == 4) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0));
            int secondDigit = ColorValues.getValueForColor(bands.get(1));
            int multiplier = ColorValues.getValueForColor(bands.get(2));
            //int tolerance = ColorValues.getValueForColor(bands.get(3));

            int resistance = (int) ((firstDigit * 10 + secondDigit) * Math.pow(10, multiplier));

            detectionResult.setResistorValue(resistance);
        } else if (numberOfBands == NumberOfBands.Five && bands.size() == 5) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0));
            int secondDigit = ColorValues.getValueForColor(bands.get(1));
            int thirdDigit = ColorValues.getValueForColor(bands.get(2));
            int multiplier = ColorValues.getValueForColor(bands.get(3));
            //int tolerance = ColorValues.getValueForColor(bands.get(4));

            int resistance = (int) ((firstDigit * 100 + secondDigit * 10 + thirdDigit) * Math.pow(10, multiplier));

            detectionResult.setResistorValue(resistance);
        } else if (bands.size() >= 3) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0));
            int secondDigit = ColorValues.getValueForColor(bands.get(1));
            int multiplier = ColorValues.getValueForColor(bands.get(2));
            //int tolerance = ColorValues.getValueForColor(bands.get(3));

            int resistance = (int) ((firstDigit * 10 + secondDigit) * Math.pow(10, multiplier));

            detectionResult.setResistorValue(resistance);
        } else {
            detectionResult.setResistorValue(-1);
        }

        medianValues.release();
        tmpMat.release();

        notifyListenerAboutNewResult(detectionResult);
    }

    private Mat getMedianColorsOfColumns(Mat resistorImage, Mat resistorMask) {


        Mat medianValues = new Mat(1, resistorImage.cols(), resistorImage.type());

//        for(int i = 0; i< resistorImage.cols(); i++){
//            Scalar m = Core.mean(resistorImage.submat(new Rect(i,0,1, resistorImage.rows())), resistorMask.submat(new Rect(i,0,1, resistorImage.rows())));
//            meanValues.put(0, i, m.val[0], m.val[1], m.val[2]);
//        }

        int n = NR_OF_COLUMNS_TO_COMBINE;

        for (int i = 0; i < resistorImage.cols() - n; i += n) {
            Mat col = resistorImage.submat(new Rect(i, 0, n, resistorImage.rows()));
            Mat mask = resistorMask.submat(new Rect(i, 0, n, resistorImage.rows()));

            //result with hist and medianOfMat is almost the same
            Scalar median = getColorUsingHsvHistogram(col, mask);
            //Scalar medianOfMat = getColorUsingHsvMedian(col, mask);

            for (int j = 0; j < n; j++) {
                medianValues.put(0, i + j, median.val[0], median.val[1], median.val[2]);

            }
        }

        //one row below the middle instead of mean
        //        for(int i = 0; i< resistorImage.cols(); i++){
        //            int x = resistorImage.rows()/2+resistorImage.rows()/10;
        //            double[] m = resistorImage.get(x, i);
        //            meanValues.put(0, i, m[0], m[1], m[2]);
        //        }

        Mat tmpMat = MatColorConversions.newBgrMatFromHsv(medianValues);
        Imgproc.resize(tmpMat, tmpMat, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("hist max value of colums", tmpMat));

        return medianValues;
    }

    private void applyFilters(Mat resistorImage) {
        Mat filteredResistorImage = new Mat();
        Imgproc.bilateralFilter(resistorImage, filteredResistorImage, 5, 80, 80);

        if (verboseDetectionDetails)
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("filtered Image", filteredResistorImage));

        //copy result back to original image
        filteredResistorImage.copyTo(resistorImage);
    }

    private void test(Mat resistorImage) {

//        Mat img = new Mat();
//        resistorImage.copyTo(img);
//
//        Mat mask = Mat.zeros(img.size(), CvType.CV_8U);
//        Mat bgModel = Mat.ones(1,65, CvType.CV_64F);
//        Mat fgModel = Mat.ones(1,65, CvType.CV_64F);
//
//        Rect rect = new Rect(10,10, img.width()-10, img.height()-10);
//
//        Imgproc.grabCut(img, mask, rect, bgModel, fgModel,1, Imgproc.GC_INIT_WITH_RECT);
//
//        detectionResult.addDetectionStepDetail(new DetectionStepDetail("test", img));
    }

    private Scalar getColorUsingHsvMedian(Mat image, Mat mask) {
        List<Mat> hsvPlanes = new ArrayList<Mat>();
        Core.split(image, hsvPlanes);

        Mat hMat = new Mat();
        hsvPlanes.get(0).copyTo(hMat, mask);
        double hMedian = medianOfMat(hMat, mask);

        Mat sMat = new Mat();
        hsvPlanes.get(1).copyTo(sMat, mask);
        double sMedian = medianOfMat(sMat, mask);

        Mat vMat = new Mat();
        hsvPlanes.get(2).copyTo(vMat, mask);
        double vMedian = medianOfMat(vMat, mask);

        return new Scalar(hMedian, sMedian, vMedian);
    }

    private double medianOfMat(Mat image, Mat mask) {
        image.reshape(image.channels(), 1);
        Mat mask2 = new Mat();
        mask.copyTo(mask2);
        mask2.reshape(mask2.channels(), 1);

        ArrayList<Double> valueList = new ArrayList<>();

        for (int i = 0; i < image.rows(); i++) {
            if (mask2.get(i, 0)[0] != 0)
                valueList.add(image.get(i, 0)[0]);
        }

        Collections.sort(valueList);

        int median = valueList.size() / 2;

        if (median == 0)
            return 0;

        return valueList.get(median);
    }

    private Scalar getColorUsingHsvHistogram(Mat image, Mat mask) {
        boolean accumulate = false;

        if (image.width() == 0 || image.height() == 0)
            return new Scalar(0);

        List<Mat> hsvPlanes = new ArrayList<Mat>();
        Core.split(image, hsvPlanes);

        Mat hHist = new Mat();
        Mat sHist = new Mat();
        Mat vHist = new Mat();

        Imgproc.calcHist(hsvPlanes, new MatOfInt(0), mask, hHist, new MatOfInt(180), new MatOfFloat(0f, 180f), accumulate);
        Imgproc.calcHist(hsvPlanes, new MatOfInt(1), mask, sHist, new MatOfInt(256), new MatOfFloat(0f, 265f), accumulate);
        Imgproc.calcHist(hsvPlanes, new MatOfInt(2), mask, vHist, new MatOfInt(256), new MatOfFloat(0f, 265f), accumulate);

        Core.normalize(hHist, hHist, 1, hHist.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(sHist, sHist, 1, sHist.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(vHist, vHist, 1, vHist.rows(), Core.NORM_MINMAX, -1, new Mat());

        //draw histogram
//        Mat histImage = Mat.zeros( 100, (int)histSize.get(0, 0)[0], CvType.CV_8UC1);
//
//        for( int j = 0; j < (int)histSize.get(0, 0)[0]; j++ )
//        {
//            Imgproc.line(
//                    histImage,
//                    new org.opencv.core.Point( j, histImage.rows() ),
//                    new org.opencv.core.Point( j, histImage.rows()-Math.round( hHist.get(j,0)[0] )) ,
//                    new Scalar( 255, 255, 255),
//                    1, 8, 0 );
//        }
//
//        Imgproc.cvtColor(histImage, histImage, Imgproc.COLOR_GRAY2BGR);
//
//        detectionResult.addDetectionStepDetail(new DetectionStepDetail("mean of columns", histImage));

        double hMax = peakValueOfHist2(hHist);
        double sMax = peakValueOfHist2(sHist);
        double vMax = peakValueOfHist2(vHist);

        return new Scalar(hMax, sMax, vMax);
    }

    private Mat getResistorAsMask(Mat resistorImage) {
        Mat reflectionMask = getReflectionsAsMask(resistorImage);

        Mat backgroundMask = getBackgroundAsMask(resistorImage);

        Mat resistorMask = new Mat();

        Core.bitwise_or(reflectionMask, backgroundMask, resistorMask);
        Core.bitwise_not(resistorMask, resistorMask);

        Mat tmpMat = MatColorConversions.newBgrMatFromGray(resistorMask);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("resistor mask", tmpMat));

        return resistorMask;
    }

    private Mat getReflectionsAsMask(Mat resistorImage) {
        Mat mask = new Mat();

        //TODO: use mean of value to calculate the brightes spots
        //Scalar meanColor = Core.mean(resistorImage);
        //double meanValue = meanColor.val[2];

        Core.inRange(resistorImage, new Scalar(0, 0, 200), new Scalar(180, 256, 256), mask);

        //erode to smooth the edges of the mask and increase the size of the masked areas
        //invert mask to increase the reflections
        Core.bitwise_not(mask, mask);
        Imgproc.erode(mask, mask, new Mat(), new Point(-1, -1), 2);
        Core.bitwise_not(mask, mask);

        if (verboseDetectionDetails) {
            Mat tmpMat = MatColorConversions.newBgrMatFromGray(mask);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("reflections", tmpMat));
        }

        return mask;
    }

    private Mat getBackgroundAsMask(Mat resistorImage) {
        Mat backgroundMaskTop = new Mat();
        Mat backgroundMaskBottom = new Mat();

        Scalar backgroundColorTop = Core.mean(resistorImage.rowRange(0, 1), new Mat());
        Scalar backgroundColorBottom = Core.mean(resistorImage.rowRange(resistorImage.rows() - 2, resistorImage.rows() - 1), new Mat());

        Core.inRange(resistorImage, backgroundColorTop.mul(new Scalar(0.6, 0.6, 0.6)), backgroundColorTop.mul(new Scalar(1.4, 1.4, 1.4)), backgroundMaskTop);
        Core.inRange(resistorImage, backgroundColorBottom.mul(new Scalar(0.6, 0.6, 0.6)), backgroundColorBottom.mul(new Scalar(1.4, 1.4, 1.4)), backgroundMaskBottom);

        Core.bitwise_or(backgroundMaskTop, backgroundMaskBottom, backgroundMaskTop);

        if (verboseDetectionDetails) {
            Mat tmpMat = MatColorConversions.newBgrMatFromGray(backgroundMaskTop);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("background", tmpMat));
        }

        return backgroundMaskTop;
    }

    private Scalar getHistAverageHSV(Mat hsvImg, Mat mask) {
        List<Mat> hsvPlanes = new ArrayList<Mat>();
        Core.split(hsvImg, hsvPlanes);

        // init
        double averageH = 0.0;
        double averageS = 0.0;
        double averageV = 0.0;
        Mat histHue = new Mat();
        Mat histSat = new Mat();
        Mat histVal = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        List<Mat> s = new ArrayList<>();
        List<Mat> v = new ArrayList<>();
        hue.add(hsvPlanes.get(0));
        s.add(hsvPlanes.get(1));
        v.add(hsvPlanes.get(2));

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), mask, histHue, histSize, new MatOfFloat(0, 179));
        Imgproc.calcHist(s, new MatOfInt(0), mask, histSat, histSize, new MatOfFloat(0, 179));
        Imgproc.calcHist(v, new MatOfInt(0), mask, histVal, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            averageH += (histHue.get(h, 0)[0] * h);
        }

        for (int i = 0; i < histSat.rows(); i++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            averageS += (histSat.get(i, 0)[0] * i);
        }

        for (int i = 0; i < histVal.rows(); i++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            averageV += (histVal.get(i, 0)[0] * i);
        }

        // return the average hue of the image
        averageH = averageH / hsvImg.size().height / hsvImg.size().width;
        averageS = averageS / hsvImg.size().height / hsvImg.size().width;
        averageV = averageV / hsvImg.size().height / hsvImg.size().width;

        return new Scalar(averageH, averageS, averageV);
    }

    private double getHistAverageH(Mat hsvImg, Mat hueValues) {
        // init
        double average = 0.0;
        Mat histHue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), histHue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++) {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (histHue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        return average = average / hsvImg.size().height / hsvImg.size().width;
    }

    private double peakValueOfHist1(Mat hist) {

        int nrOfColors = 10;

        double maxSum = 0;

        for (int i = 0; i < nrOfColors; i++) {
            double maxLoc = Core.minMaxLoc(hist).maxLoc.y;
            double maxVal = Core.minMaxLoc(hist).maxVal;

            switch (i) {
                case 0:
                    maxSum += maxLoc * 0.7;
                    break;
                case 1:
                    maxSum += maxLoc * 0.1;
                    break;
                case 2:
                    maxSum += maxLoc * 0.05;
                    break;
                case 3:
                    maxSum += maxLoc * 0.05;
                    break;
                case 4:
                    maxSum += maxLoc * 0.025;
                    break;
                case 5:
                    maxSum += maxLoc * 0.025;
                    break;
                case 6:
                    maxSum += maxLoc * 0.025;
                    break;
                case 7:
                    maxSum += maxLoc * 0.025;
                    break;
                default:
                    maxSum += maxLoc * 0;
                    break;

            }

            hist.row((int) maxLoc).setTo(new Scalar(0));
        }

        return maxSum;
    }

    private double peakValueOfHist2(Mat hist) {

        int nrOfColors = 100;

        double percentage = 0.7;
        double usedPercentage = 0;
        double maxSum = 0;

        for (int i = 0; i < nrOfColors; i++) {
            double maxLoc = Core.minMaxLoc(hist).maxLoc.y;

            maxSum += maxLoc * percentage;

            usedPercentage += percentage;

            percentage = (1 - usedPercentage) / 2;

            hist.row((int) maxLoc).setTo(new Scalar(0));
        }

        return maxSum;
    }
}
