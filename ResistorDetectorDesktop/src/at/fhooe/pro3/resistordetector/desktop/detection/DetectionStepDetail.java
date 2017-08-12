package at.fhooe.pro3.resistordetector.desktop.detection;

import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

/**
 * This class represents one step of the detection process
 * and contains information (descriptions and image) about the detection step.
 * <p>
 * Created by stefan on 28.05.2017.
 */
public class DetectionStepDetail {
    /**
     * Description about this DetectionStep.
     */
    private String description = null;

    /**
     * Image representing the DetectionStep or the result of the DetectionStep.
     */
    private BufferedImage image = null;

    /**
     * Creates a new DetectionStepDetail object with a description and an image.
     * The image is creates from the given Matrix.
     * The Matrix must have BGR colors.
     *
     * @param description the string describing the detection step
     * @param imageMatBgr a matrix representing the DetectionStep or the result of the DetectionStep, in BGR colors
     */
    public DetectionStepDetail(String description, Mat imageMatBgr) {
        if (description == null)
            throw new IllegalArgumentException("description must not be null!");
        if (imageMatBgr == null)
            throw new IllegalArgumentException("imageMatBgr must not be null!");

        this.description = description;
        this.image = BufferedImageConversions.matToBufferedImage(imageMatBgr);
    }

    /**
     * Creates a new DetectionStepDetail object with a description
     * but without an image.
     *
     * @param description the string describing the detection step
     */
    public DetectionStepDetail(String description) {
        if (description == null)
            throw new IllegalArgumentException("description must not be null!");

        this.description = description;
    }

    /**
     * Returns the description of this DetectionStep.
     *
     * @return the description of this DetectionStep
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the image representing the DetectionStep or the result of the DetectionStep.
     *
     * @return the image representing the DetectionStep
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns if this DetectionStepDetail contains an image.
     *
     * @return true if this DetectionStepDetail contains an image
     */
    public boolean isImageAvailable() {
        return image != null;
    }

    /**
     * Returns if this DetectionStepDetail contains a description.
     *
     * @return true if this DetectionStepDetail contains a description
     */
    public boolean isDescriptionAvailable() {
        return description != null && !description.equals("");
    }
}
