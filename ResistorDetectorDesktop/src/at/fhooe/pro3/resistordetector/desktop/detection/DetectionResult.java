package at.fhooe.pro3.resistordetector.desktop.detection;

import java.util.ArrayList;

/**
 * This class represents the Result of a Resistor Detection process.
 * <p>
 * A result contains:
 * the resistance value of the resistor
 * the colors of the bands of the resistor
 * a list of DetectionStrepDetails (details about the detection process)
 * <p>
 * Created by stefan on 17.06.2017.
 */
public class DetectionResult {

    /**
     * Value for unknown resistance.
     */
    public static final Integer UNKNOWN_RESISTANCE_VALUE = -1;

    /**
     * A list of DetectionStepDetails describing the individual steps of detection process.
     */
    private ArrayList<DetectionStepDetail> detectionStepDetails = null;

    /**
     * The detected resistance value of the resistor.
     * IS UNKNOWN_RESISTANCE_VALUE if the detection was not successfull.
     */
    private int resistorValue = UNKNOWN_RESISTANCE_VALUE;

    /**
     * The information about the detected resistor bands. May be null.
     */
    private BandInfo bandInfo[] = null;

    /**
     * Creates a new DetectionResult with default values.
     * resistorValue = UNKNOWN_RESISTANCE_VALUE
     * bandColors = null;
     * detectionStepDetails = empty List
     */
    public DetectionResult() {
        detectionStepDetails = new ArrayList<DetectionStepDetail>();
    }

    /**
     * Returns if this DetectionResult contains DetectionStepDetails.
     *
     * @return true if DetectionStepDetails are available, false otherwise
     */
    public Boolean detectionStepDetailsAvailable() {
        return detectionStepDetails != null && detectionStepDetails.size() > 0;
    }

    /**
     * Returns the DetectionStepDetails of this DetectionResult.
     *
     * @return the DetectionStepDetails of this DetectionResult
     */
    public ArrayList<DetectionStepDetail> getDetectionStepDetails() {
        return detectionStepDetails;
    }

    /**
     * Adds a new DetectionStep to the list of DetectionStepDetails.
     *
     * @param detectionStepDetail the DetectionStep to add to the list of DetectionStepDetails
     */
    public void addDetectionStepDetail(DetectionStepDetail detectionStepDetail) {
        if (detectionStepDetail == null)
            throw new IllegalArgumentException("detectionStepDetail must not be null!");

        detectionStepDetails.add(detectionStepDetail);
    }

    /**
     * Returns the resistance value (the resul of the detection process).
     *
     * @return the resistance value of the resistor.
     */
    public int getResistorValue() {
        return resistorValue;
    }

    /**
     * Sets the detected resistance value of the resistor.
     *
     * @param resistorValue the detected resistance value of the resistor
     */
    public void setResistorValue(int resistorValue) {
        this.resistorValue = resistorValue;
    }

    /**
     * Returns an array with the detected information of the resistor bands.
     *
     * @return an array with the detected information of the resistor bands
     */
    public BandInfo[] getBandInfo() {
        return bandInfo;
    }

    /**
     * Sets the detected information of the resistor bands.
     *
     * @param bandInfo the detected information of the resistor bands.
     */
    public void setBandInfo(BandInfo[] bandInfo) {
        this.bandInfo = bandInfo;
    }
}
