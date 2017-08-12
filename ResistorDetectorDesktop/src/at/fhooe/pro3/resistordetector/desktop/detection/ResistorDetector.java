package at.fhooe.pro3.resistordetector.desktop.detection;

import org.opencv.core.Mat;

/**
 * This abstract class defines the basic ResistorDetector.
 * All ResistorDetectors use this class as baseClass.
 * <p>
 * The ResistorDetector uses a ResultListener to return the result of the detection.
 * <p>
 * Created by stefan on 18.06.2017.
 */
public abstract class ResistorDetector {

    /**
     * This Interface defines the methods a ResultListener must implement.
     * The class implementing this interface is used to notify the caller
     * about a result of the detection process.
     */
    public interface ResultListener {
        /**
         * This method is called when the detection process finished
         * and a detection result is read.
         *
         * @param detectionResult The result of the resistor detection process
         */
        void resultReady(DetectionResult detectionResult);
    }

    /**
     * Defines the number of bands on the resistor.
     * This includes the tolerance ring.
     */
    public enum NumberOfBands {
        /**
         * Automatically detect the number of bands.
         */
        Auto,

        /**
         * Assume that the resistor has four bands.
         */
        Four,

        /**
         * Assume that the resistor has five bands.
         */
        Five,
    }

    /**
     * The number of bands the detection process should assume the resistor has.
     */
    protected NumberOfBands numberOfBands = NumberOfBands.Auto;

    /**
     * The result listener which gets notified about results.
     */
    private ResultListener resultListener = null;

    /**
     * Creates a new ResistorDetector with the given ResultListener.
     * The resultListener is notified when a result is ready.
     *
     * @param resultListener The resultListener that should be notified about detection results.
     */
    public ResistorDetector(ResultListener resultListener) {
        if (resultListener == null)
            throw new IllegalArgumentException("resultListener must not be null!");

        this.resultListener = resultListener;
    }

    /**
     * Sets the number of bands the detection process should assume the resistor has.
     *
     * @param numberOfBands number of bands the detection process should assume the resistor has.
     */
    public void setNumberOfBands(NumberOfBands numberOfBands) {
        this.numberOfBands = numberOfBands;
    }

    /**
     * Notifies the listener about a detectionResult.
     *
     * @param detectionResult the result of the detection process that is sent to the listener.
     */
    protected void notifyListenerAboutNewResult(DetectionResult detectionResult) {
        if (detectionResult == null)
            throw new IllegalArgumentException("detectionResult must not be null!");

        resultListener.resultReady(detectionResult);
    }

    /**
     * Abstract methods that performs the resistor detection.
     * Subclasses implement this method and perform the detection on the given resistorImage.
     * <p>
     * When the detection process finished, notifyListenerAboutNewResult must be called
     * to notify the listener about the result.
     *
     * @param resistorImage A OpenCV Matrix with the resistorImage, with BGR colors.
     */
    public abstract void detectResistorValue(Mat resistorImage);
}
