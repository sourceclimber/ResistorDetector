package at.fhooe.pro3.resistordetector.detection;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class implements a ResistorDetector which analyzes the resistor image column wise.
 * <p>
 * 1. The resistorImage is filtered to remove the background and the reflections.
 * 2. The median color of each column (or some columns are combined) is calculated.
 * 3. The ColorName of each column is determined.
 * 4. The ColorNames of the columns are combined into bands.
 * <p>
 * Created by stefan on 21.05.2017.
 */
public class ColumnsResistorDetector extends ResistorDetector {

    /**
     * Defines the number of columns of the resistor image that will be combined
     * to perform the detection of the median color.
     */
    private static final int NR_OF_COLUMNS_TO_COMBINE = 5;

    /**
     * Defines the minimum width in pixel a band of the resistor must have.
     */
    private static final int MIN_BAND_WIDTH = NR_OF_COLUMNS_TO_COMBINE + 1;

    /**
     * Defines if all detection steps should be included in the list of DetectionStepDetails
     */
    private static final boolean VERBOSE_DETECTION_DETAILS = false;

    /**
     * The DetectionResult of the currently running detection process.
     */
    private DetectionResult detectionResult = null;

    /**
     * The height of the input matrix.
     */
    private int inputMatHeight = 0;

    /**
     * The width of the input matrix.
     */
    private int inputMatWidth = 0;

    /**
     * Creates a new ResistorDetector with the given ResultListener.
     * The resultListener is notified when a result is ready.
     *
     * @param resultListener The resultListener that should be notified about detection results.
     */
    public ColumnsResistorDetector(ResultListener resultListener) {
        super(resultListener);
    }

    /**
     * Performs the resistor detection with a column wise approach.
     *
     * @param resistorImage A OpenCV Matrix with the resistorImage, with BGR colors.
     */
    @Override
    public void detectResistorValue(Mat resistorImage) {
        inputMatHeight = resistorImage.height();
        inputMatWidth = resistorImage.width();

        detectionResult = new DetectionResult();
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("original Image", resistorImage));

        applyBilateralFilter(resistorImage);

        Imgproc.cvtColor(resistorImage, resistorImage, Imgproc.COLOR_BGR2HSV);

        Mat resistorMask = getResistorAsMask(resistorImage);

        Mat medianValues = getMedianColorsOfColumns(resistorImage, resistorMask);

        ColorName[] columnColorNames = getColumnColorNames(medianValues);

        List<BandInfo> bands = getBandInfo(columnColorNames);

        addBandInfoToDetectionDetails(bands);

        BandInfo[] bandsArray = new BandInfo[bands.size()];
        bandsArray = bands.toArray(bandsArray);
        detectionResult.setBandInfo(bandsArray);

        int resistance = calculateResistance(bands);

        if (resistance != -1)
            detectionResult.setResistorValue(resistance);

        resistorMask.release();
        medianValues.release();

        notifyListenerAboutNewResult(detectionResult);
    }

    /**
     * Applies a bilateral filter on the image.
     * This reduces the noise in the image but keeps the edges fairly sharp.
     * <p>
     * The given image is modified!
     * <p>
     * See also:
     * - Imgproc.bilateralFilter: http://docs.opencv.org/2.4/modules/imgproc/doc/filtering.html#bilateralfilter
     * - Imgproc.bilateralFilter: http://docs.opencv.org/2.4/doc/tutorials/imgproc/gausian_median_blur_bilateral_filter/gausian_median_blur_bilateral_filter.html
     *
     * @param resistorImage The image the filter should be applied to. This image will be modified.
     */
    private void applyBilateralFilter(Mat resistorImage) {
        Mat filteredResistorImage = new Mat();
        Imgproc.bilateralFilter(resistorImage, filteredResistorImage, 5, 80, 80);

        if (VERBOSE_DETECTION_DETAILS)
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("filtered Image", filteredResistorImage));

        //copy result back to original image
        filteredResistorImage.copyTo(resistorImage);

        filteredResistorImage.release();
    }

    /**
     * Returns a mask of the resistor where the background and reflections are masked out.
     * <p>
     * See also:
     * - Core.bitwise_or: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#bitwise-or
     * - Core.bitwise_not: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#bitwise-not
     *
     * @param resistorImage The image of the resistor
     * @return a mask of the resistor where the background and reflections are masked out.
     */
    private Mat getResistorAsMask(Mat resistorImage) {
        Mat reflectionMask = getReflectionsAsMask(resistorImage);

        Mat backgroundMask = getBackgroundAsMask(resistorImage);

        Mat resistorMask = new Mat();

        Core.bitwise_or(reflectionMask, backgroundMask, resistorMask);
        Core.bitwise_not(resistorMask, resistorMask);

        Mat tmpMat = MatColorConversions.newBgrMatFromGray(resistorMask);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("resistor mask", tmpMat));

        backgroundMask.release();
        reflectionMask.release();

        return resistorMask;
    }

    /**
     * Returns a mask of the reflections in the image.
     * This returns a mask where everything but the reflections is masked out.
     * <p>
     * See also:
     * - Core.inRange: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#inrange
     * - Core.inRange: http://docs.opencv.org/trunk/da/d97/tutorial_threshold_inRange.html
     * - Imgproc.erode: http://docs.opencv.org/2.4/modules/imgproc/doc/filtering.html?highlight=erode#erode
     * - Imgproc.erode: http://docs.opencv.org/2.4/doc/tutorials/imgproc/erosion_dilatation/erosion_dilatation.html#erosion
     *
     * @param resistorImage The image for which the mask should be created
     * @return A mask of the reflections (everything but the reflections is masked out)
     */
    private Mat getReflectionsAsMask(Mat resistorImage) {
        Mat mask = new Mat();

        Core.inRange(resistorImage, new Scalar(0, 0, 200), new Scalar(180, 256, 256), mask);

        //erode to smooth the edges of the mask and increase the size of the masked areas
        //invert mask to increase the reflections
        Core.bitwise_not(mask, mask);
        Imgproc.erode(mask, mask, new Mat(), new Point(-1, -1), 2);
        Core.bitwise_not(mask, mask);

        if (VERBOSE_DETECTION_DETAILS) {
            Mat tmpMat = MatColorConversions.newBgrMatFromGray(mask);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("reflections", tmpMat));
        }

        return mask;
    }

    /**
     * Returns a mask of the background of the image.
     * This returns a mask where everything but the background is masked out.
     * <p>
     * See also:
     * - Core.mean: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#mean
     * - Core.inRange: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#inrange
     * - Core.bitwise_or: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#bitwise-or
     *
     * @param resistorImage The image for which the mask should be created
     * @return A mask of the background (everything but the background is masked out)
     */
    private Mat getBackgroundAsMask(Mat resistorImage) {
        Mat backgroundMaskTop = new Mat();
        Mat backgroundMaskBottom = new Mat();
        Mat backgroundMask = new Mat();

        Scalar backgroundColorTop = Core.mean(resistorImage.rowRange(0, 1), new Mat());
        Scalar backgroundColorBottom = Core.mean(resistorImage.rowRange(resistorImage.rows() - 2, resistorImage.rows() - 1), new Mat());

        Core.inRange(resistorImage, backgroundColorTop.mul(new Scalar(0.6, 0.6, 0.6)), backgroundColorTop.mul(new Scalar(1.4, 1.4, 1.4)), backgroundMaskTop);
        Core.inRange(resistorImage, backgroundColorBottom.mul(new Scalar(0.6, 0.6, 0.6)), backgroundColorBottom.mul(new Scalar(1.4, 1.4, 1.4)), backgroundMaskBottom);

        Core.bitwise_or(backgroundMaskTop, backgroundMaskBottom, backgroundMask);

        if (VERBOSE_DETECTION_DETAILS) {
            Mat tmpMat = MatColorConversions.newBgrMatFromGray(backgroundMask);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("background", tmpMat));
        }

        backgroundMaskTop.release();
        backgroundMaskBottom.release();

        return backgroundMask;
    }

    /**
     * Returns a new Matrix with the median color for each column of the given resistor image.
     * If NR_OF_COLUMNS_TO_COMBINE is greater than 1, NR_OF_COLUMNS_TO_COMBINE defines the number
     * of columns that get grouped together. The median color is than calculated over more than
     * one column.
     * The mask defines the regions of the resistor image that should be used for the calculation.
     * All masked out areas are not used for the median calculation.
     * <p>
     * The returned Matrix has only one row and the same number of columns as the input image.
     * <p>
     * See also:
     * - Mat.submat: http://docs.opencv.org/java/2.4.2/org/opencv/core/Mat.html#submat(int, int, int, int)
     * - Mat.put: http://docs.opencv.org/java/2.4.2/org/opencv/core/Mat.html#put(int, int, double...)
     *
     * @param resistorImage The image for which the median column color should be calculated.
     * @param resistorMask  The mask defining the areas to consider in the calculation.
     * @return A new Matrix with one row and as many columns as the input image with the median color for each column.
     */
    private Mat getMedianColorsOfColumns(Mat resistorImage, Mat resistorMask) {
        Mat medianValues = new Mat(1, resistorImage.cols(), resistorImage.type());

        int n = NR_OF_COLUMNS_TO_COMBINE;

        for (int i = 0; i < resistorImage.cols() - n; i += n) {
            Mat col = resistorImage.submat(new Rect(i, 0, n, resistorImage.rows()));
            Mat mask = resistorMask.submat(new Rect(i, 0, n, resistorImage.rows()));

            Scalar median = getColorUsingHsvMedian(col, mask);

            for (int j = 0; j < n; j++) {
                medianValues.put(0, i + j, median.val[0], median.val[1], median.val[2]);

            }
        }

        Mat tmpMat = MatColorConversions.newBgrMatFromHsv(medianValues);
        Imgproc.resize(tmpMat, tmpMat, new Size(resistorImage.cols(), resistorImage.rows()), 0, 0, Imgproc.INTER_NEAREST);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("median value of colums", tmpMat));
        tmpMat.release();

        return medianValues;
    }

    /**
     * Returns the median color of the given matrix.
     * The mask defines which pixel should be used for the calculation.
     * <p>
     * See also:
     * - Core.split: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#split
     * - Mat.get: http://docs.opencv.org/java/2.4.2/org/opencv/core/Mat.html#get(int, int)
     *
     * @param image The matrix for which the media color should be calculated.
     * @param mask  The mask defining which pixel should be used for the calculation.
     * @return The median color of the matrix, a Scalar (HSV color)
     */
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

        hMat.release();
        sMat.release();
        vMat.release();

        return new Scalar(hMedian, sMedian, vMedian);
    }

    /**
     * Returns the median value of a matrix.
     * The mask defines which pixel should be used for the calculation.
     * <p>
     * See also:
     * - Mat.reshape: http://docs.opencv.org/java/2.4.2/org/opencv/core/Mat.html#reshape(int, int)
     * - Mat.get: http://docs.opencv.org/java/2.4.2/org/opencv/core/Mat.html#get(int, int)
     *
     * @param image The matrix for which the media value should be calculated.
     * @param mask  The mask defining which pixel should be used for the calculation.
     * @return The median value of the matrix
     */
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

        mask2.release();

        return valueList.get(median);
    }

    /**
     * Returns an array with the ColorName for each column of the medianColors.
     * The given matrix with the median colors must have one row.
     * <p>
     * The resulting array has as many elements as the matrix has columns.
     *
     * @param medianColors The matrix for which the color names per column should be returned.
     * @return An array with the ColorName for each column.
     */
    private ColorName[] getColumnColorNames(Mat medianColors) {
        ColorName[] columnColors = new ColorName[medianColors.cols()];

        Mat tmpMat = new Mat(medianColors.rows(), medianColors.cols(), medianColors.type());

        for (int i = 0; i < medianColors.cols(); i++) {

            double[] colValue = medianColors.get(0, i);
            ColorName colColor = ColorDefinitionsHsv.getColorName(new Scalar(colValue));

            columnColors[i] = colColor;

            Scalar detectedColor = ColorDefinitionsHsv.getColorFromName(colColor);
            tmpMat.put(0, i, detectedColor.val[0], detectedColor.val[1], detectedColor.val[2]);
        }

        Mat tmpMat2 = MatColorConversions.newBgrMatFromHsv(tmpMat);
        Imgproc.resize(tmpMat2, tmpMat2, new Size(inputMatWidth, inputMatHeight), 0, 0, Imgproc.INTER_NEAREST);
        detectionResult.addDetectionStepDetail(new DetectionStepDetail("Detected color per column", tmpMat2));
        tmpMat.release();
        tmpMat2.release();

        return columnColors;
    }

    /**
     * Returns a list with Resistor Band Infos calculated from the columnColorNames.
     * The same color names in one row are combined into one entry (the width is also saved).
     * Only columns wider than MIN_BAND_WIDTH are added to the resulting list.
     *
     * @param columnColorNames An array with the column name for each column
     * @return A list with BandInfo element for each band of the resistor.
     */
    private List<BandInfo> getBandInfo(ColorName[] columnColorNames) {
        List<BandInfo> bands = new ArrayList<>();

        ColorName tmpName;
        int tmpWidth;

        for (int i = 0; i < columnColorNames.length; i++) {
            tmpName = columnColorNames[i];
            tmpWidth = 0;

            i++;

            while (i < columnColorNames.length && columnColorNames[i] == tmpName) {
                i++;
                tmpWidth++;
            }

            if (tmpWidth >= MIN_BAND_WIDTH) {
                if (tmpName != ColorName.Unknown) {
                    bands.add(new BandInfo(tmpName, tmpWidth));
                }
            }
        }

        return bands;
    }

    /**
     * Draws the given list of BandInfo elements and adds the image to the DetectionStepDetails.
     *
     * @param bands A list of resistor band info elements.
     */
    private void addBandInfoToDetectionDetails(List<BandInfo> bands) {
        if (bands.size() != 0) {
            int width = 0;
            for (BandInfo band : bands) {
                width += band.getWidth();
            }

            Mat tmpMat = new Mat(1, width, CvType.CV_8UC3);

            int count = 0;

            for (BandInfo band : bands) {
                for (int j = 0; j < band.getWidth(); j++) {

                    Scalar detectedColor = ColorDefinitionsHsv.getColorFromName(band.getColor());

                    tmpMat.put(0, count, detectedColor.val[0], detectedColor.val[1], detectedColor.val[2]);
                    count++;
                }
            }

            Mat tmpMat2 = MatColorConversions.newBgrMatFromHsv(tmpMat);
            Imgproc.resize(tmpMat2, tmpMat2, new Size(width, inputMatHeight), 0, 0, Imgproc.INTER_NEAREST);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("Detected color per band", tmpMat2));
            tmpMat.release();
            tmpMat2.release();
        } else {
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("No bands found"));
        }
    }

    /**
     * Calculates the resistance of the resistor based on the colors of the resistor bands.
     *
     * @param bands A list of resistor band info elements.
     * @return the calculated resistance value, or -1 if the calculation is not possible.
     */
    private int calculateResistance(List<BandInfo> bands) {
        if (numberOfBands == NumberOfBands.Four && bands.size() == 4) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0).getColor());
            int secondDigit = ColorValues.getValueForColor(bands.get(1).getColor());
            int multiplier = ColorValues.getValueForColor(bands.get(2).getColor());
            //int tolerance = ColorValues.getValueForColor(bands.get(3));

            int resistance = (int) ((firstDigit * 10 + secondDigit) * Math.pow(10, multiplier));

            return resistance;
        } else if (numberOfBands == NumberOfBands.Five && bands.size() == 5) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0).getColor());
            int secondDigit = ColorValues.getValueForColor(bands.get(1).getColor());
            int thirdDigit = ColorValues.getValueForColor(bands.get(2).getColor());
            int multiplier = ColorValues.getValueForColor(bands.get(3).getColor());
            //int tolerance = ColorValues.getValueForColor(bands.get(4));

            int resistance = (int) ((firstDigit * 100 + secondDigit * 10 + thirdDigit) * Math.pow(10, multiplier));

            return resistance;
        } else if (bands.size() >= 3) {
            int firstDigit = ColorValues.getValueForColor(bands.get(0).getColor());
            int secondDigit = ColorValues.getValueForColor(bands.get(1).getColor());
            int multiplier = ColorValues.getValueForColor(bands.get(2).getColor());
            //int tolerance = ColorValues.getValueForColor(bands.get(3));

            int resistance = (int) ((firstDigit * 10 + secondDigit) * Math.pow(10, multiplier));

            return resistance;
        } else {
            return -1;
        }
    }
}
