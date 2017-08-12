package at.fhooe.pro3.resistordetector.detection;

import android.util.SparseIntArray;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified version of the resistor detection code from GitHub: https://github.com/thegouger/ResistorScanner.
 * Link to original code: https://github.com/thegouger/ResistorScanner/blob/master/app/src/main/java/ca/parth/resistordecoder/ResistorImageProcessor.java
 * <p>
 * Created by stefan on 18.06.2017.
 */
public class ContoursModResistorDetector extends ResistorDetector {

    /**
     * Defines if all detection steps should be included in the list of DetectionStepDetails
     */
    private static final boolean VERBOSE_DETECTION_DETAILS = false;

    private static final int NUM_CODES = 10;

    // HSV colour bounds
    private static final Scalar COLOR_BOUNDS[][] = {
            {ColorDefinitionsHsv.BLACK_MIN, ColorDefinitionsHsv.BLACK_MAX},    // black
            {ColorDefinitionsHsv.BROWN_MIN, ColorDefinitionsHsv.BROWN_MAX},    // brown
            {ColorDefinitionsHsv.RED1_MIN, ColorDefinitionsHsv.RED1_MAX},         // red (defined by two bounds)
            {ColorDefinitionsHsv.ORANGE_MIN, ColorDefinitionsHsv.ORANGE_MAX},   // orange
            {ColorDefinitionsHsv.YELLOW_MIN, ColorDefinitionsHsv.YELLOW_MAX}, // yellow
            {ColorDefinitionsHsv.GREEN_MIN, ColorDefinitionsHsv.GREEN_MAX},   // green
            {ColorDefinitionsHsv.BLUE_MIN, ColorDefinitionsHsv.BLUE_MAX},  // blue
            {ColorDefinitionsHsv.VIOLET_MIN, ColorDefinitionsHsv.VIOLET_MAX}, // purple
            {ColorDefinitionsHsv.GREY_MIN, ColorDefinitionsHsv.GREY_MAX},       // gray
            {ColorDefinitionsHsv.WHITE_MIN, ColorDefinitionsHsv.WHITE_MAX}      // white
    };

    // red wraps around in HSV, so we need two ranges
    private static Scalar LOWER_RED1 = ColorDefinitionsHsv.RED1_MIN;
    private static Scalar UPPER_RED1 = ColorDefinitionsHsv.RED1_MAX;
    private static Scalar LOWER_RED2 = ColorDefinitionsHsv.RED2_MIN;
    private static Scalar UPPER_RED2 = ColorDefinitionsHsv.RED2_MAX;

    private SparseIntArray locationValues = new SparseIntArray(4);

    public ContoursModResistorDetector(ResultListener resultListener) {
        super(resultListener);
    }

    private DetectionResult detectionResult;

    /**
     * Performs the resistor detection by trying to find the locations
     * of each possible color.
     *
     * @param resistorImage A OpenCV Matrix with the resistorImage, with BGR colors.
     */
    @Override
    public void detectResistorValue(Mat resistorImage) {

        detectionResult = new DetectionResult();

        detectionResult.addDetectionStepDetail(new DetectionStepDetail("original Image", resistorImage));

        Mat filteredMat = new Mat();
        Imgproc.bilateralFilter(resistorImage, filteredMat, 5, 80, 80);

        if (VERBOSE_DETECTION_DETAILS)
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("filtered Image", filteredMat));

        Imgproc.cvtColor(filteredMat, filteredMat, Imgproc.COLOR_BGR2HSV);

        findLocations(filteredMat);

        if (locationValues.size() >= 3) {
            // recover the resistor value by iterating through the centroid locations
            // in an ascending manner and using their associated colour values
            int kTens = locationValues.keyAt(0);
            int kUnits = locationValues.keyAt(1);
            int kPower = locationValues.keyAt(2);

            int value = 10 * locationValues.get(kTens) + locationValues.get(kUnits);
            value *= Math.pow(10, locationValues.get(kPower));

            detectionResult.setResistorValue(value);
        }

        notifyListenerAboutNewResult(detectionResult);
    }

    /**
     * See also:
     * - Core.inRange: http://docs.opencv.org/2.4/modules/core/doc/operations_on_arrays.html#inrange
     * - Imgproc.findContours: http://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html?#findcontours
     * - Imgproc.findContours: http://docs.opencv.org/2.4/doc/tutorials/imgproc/shapedescriptors/find_contours/find_contours.html
     * - Imgproc.contourArea: http://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html#contourarea
     * - Imgproc.moments: http://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html#moments
     *
     * @param searchMat
     */
    private void findLocations(Mat searchMat) {
        locationValues.clear();
        SparseIntArray areas = new SparseIntArray(4);

        for (int i = 0; i < NUM_CODES; i++) {
            Mat mask = new Mat();
            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy = new Mat();

            if (i == 2) {
                // combine the two ranges for red
                Core.inRange(searchMat, LOWER_RED1, UPPER_RED1, mask);
                Mat rmask2 = new Mat();
                Core.inRange(searchMat, LOWER_RED2, UPPER_RED2, rmask2);
                Core.bitwise_or(mask, rmask2, mask);
            } else
                Core.inRange(searchMat, COLOR_BOUNDS[i][0], COLOR_BOUNDS[i][1], mask);

            Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

            Mat tmpMat = MatColorConversions.newBgrMatFromHsv(searchMat);
            Imgproc.drawContours(tmpMat, contours, -1, new Scalar(255, 255, 255), 1);
            detectionResult.addDetectionStepDetail(new DetectionStepDetail("area of color " + ColorDefinitionsHsv.getColorName(COLOR_BOUNDS[i][0]), tmpMat));

            for (int contIdx = 0; contIdx < contours.size(); contIdx++) {
                int area;
                if ((area = (int) Imgproc.contourArea(contours.get(contIdx))) > 20) {
                    Moments M = Imgproc.moments(contours.get(contIdx));
                    int cx = (int) (M.get_m10() / M.get_m00());

                    // if a colour band is split into multiple contours
                    // we take the largest and consider only its centroid
                    boolean shouldStoreLocation = true;
                    for (int locIdx = 0; locIdx < locationValues.size(); locIdx++) {
                        if (Math.abs(locationValues.keyAt(locIdx) - cx) < 10) {
                            if (areas.get(locationValues.keyAt(locIdx)) > area) {
                                shouldStoreLocation = false;
                                break;
                            } else {
                                locationValues.delete(locationValues.keyAt(locIdx));
                                areas.delete(locationValues.keyAt(locIdx));
                            }
                        }
                    }

                    if (shouldStoreLocation) {
                        areas.put(cx, area);
                        locationValues.put(cx, i);
                    }
                }
            }
        }
    }
}


