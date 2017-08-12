package at.fhooe.pro3.resistordetector;

import at.fhooe.pro3.resistordetector.detection.DetectionResult;


/**
 * This (static) class contains one static DetectionResult.
 * Used to easily transfare the detection result to the DetectionDetailsView
 * <p>
 * Created by stefan on 17.06.2017.
 */
public class DetectionResultHolder {
    /**
     * Saves a static DetectionResult.
     */
    private static DetectionResult detectionResult = null;

    /**
     * Returns true if the Holder contains a DetectionResult.
     *
     * @return true if the Holder contains a DetectionResult, false otherwise
     */
    public static Boolean detectionResultAvailable() {
        return detectionResult != null;
    }

    /**
     * Gets DetectionResult of this static DetectionResultHolder.
     *
     * @return the DetectionResult of this static DetectionResultHolder
     */
    public static DetectionResult getDetectionResult() {
        return detectionResult;
    }

    /**
     * Sets the DetectionResult of this static DetectionResultHolder.
     *
     * @param detectionResult the DetectionResult of this static DetectionResultHolder
     */
    public static void setDetectionResult(DetectionResult detectionResult) {
        DetectionResultHolder.detectionResult = detectionResult;
    }
}
