package at.fhooe.pro3.resistordetector.ui;

import android.support.v4.util.ArrayMap;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Implementation of the openCV cameraViewListener to draw the indicator on the preview image
 * and extract the image from within the indicator.
 * <p>
 * Created by stefan on 20.05.2017.
 */
public class CameraViewListener implements CameraBridgeViewBase.CvCameraViewListener2 {

    /**
     * Defines the possible sizes of the indicator.
     */
    public enum IndicatorSize {
        Small,
        Large,
    }

    /**
     * Defines the default value for (see constant name).
     */
    public static final float CONTRAST_MODIFIER_DEFAULT = 1;

    /**
     * Defines the minimum value for (see constant name).
     */
    public static final float CONTRAST_MODIFIER_MIN_VALUE = 0;

    /**
     * Defines the maximum value for (see constant name).
     */
    public static final float CONTRAST_MODIFIER_MAX_VALUE = 2;

    /**
     * Defines the default value for (see constant name).
     */
    public static final int BRIGHTNESS_MODIFIER_DEFAULT = 0;

    /**
     * Defines the minimum value for (see constant name).
     */
    public static final int BRIGHTNESS_MODIFIER_MIN_VALUE = -50;

    /**
     * Defines the maximum value for (see constant name).
     */
    public static final int BRIGHTNESS_MODIFIER_MAX_VALUE = 50;

    /**
     * Defines the default value for (see constant name).
     */
    public static final int COLOR_MODIFIER_DEFAULT = 100;

    /**
     * Defines the minimum value for (see constant name).
     */
    public static final int COLOR_MODIFIER_MIN_VALUE = 0;

    /**
     * Defines the maximum value for (see constant name).
     */
    public static final int COLOR_MODIFIER_MAX_VALUE = 200;

    /**
     * Defines the default value for (see constant name).
     */
    public static final IndicatorSize INDICATOR_SIZE_DEFAULT = IndicatorSize.Large;

    /**
     * The height of the indicator in pixels.
     */
    private static final int INDICATOR_HEIGHT = 80;

    /**
     * The width of the indicator in pixels.
     */
    private static final int INDICATOR_WIDTH = 170;

    /**
     * The thickness of the indicator line.
     */
    private static final int INDICATOR_THICKNESS = 2;

    /**
     * The distance of the indicator to the top of the preview image in percent.
     */
    private static final float INDICATOR_DISTANCE_TOP_PERCENT = 35;

    /**
     * Color of the indicatorSize indicatorSize in RGB.
     */
    private static final Scalar INDICATOR_COLOR = new Scalar(0, 0, 255, 255);

    /**
     * This matrix holds the last received full image frame.
     */
    private Mat fullImage;

    /**
     * This matrix is used for temporary processing.
     */
    private Mat fullImageTemp;

    /**
     * The current value used to modify the contrast of the preview image.
     */
    private float contrastModifier = CONTRAST_MODIFIER_DEFAULT;

    /**
     * The current value used to modify the brightness of the preview image.
     */
    private int brightnessModifier = BRIGHTNESS_MODIFIER_DEFAULT;

    /**
     * The current value used to modify the color red of the preview image.
     */
    private int colorModifierRed = COLOR_MODIFIER_DEFAULT;

    /**
     * The current value used to modify the color green of the preview image.
     */
    private int colorModifierGreen = COLOR_MODIFIER_DEFAULT;

    /**
     * The current value used to modify the color blue of the preview image.
     */
    private int colorModifierBlue = COLOR_MODIFIER_DEFAULT;

    /**
     * The current size of the indicator.
     */
    private IndicatorSize indicatorSize = INDICATOR_SIZE_DEFAULT;

    /**
     * A list of indicators, one for each possible indicator size.
     */
    private ArrayMap<String, Rect> indicatorRects = new ArrayMap<>(IndicatorSize.values().length);

    /**
     * Creates a new instance of this object with all modifiers set to default values.
     */
    public CameraViewListener() {
        resetBrightnessAndContrastModifiers();
    }

    /**
     * Called from openCV when the camera view is started.
     * Creates the indicators for different indicator sizes
     * according to the size of the frames that will be deliverd.
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered
     */
    @Override
    public void onCameraViewStarted(int width, int height) {
        fullImage = new Mat(height, width, CvType.CV_8UC4);
        fullImageTemp = new Mat(height, width, CvType.CV_8UC4);

        int xPos = width / 2;
        int yPos = (int) (height * INDICATOR_DISTANCE_TOP_PERCENT / 100);

        Point smallRectPoint1 = new Point(xPos - INDICATOR_WIDTH / 2, yPos - INDICATOR_HEIGHT / 2);
        Point smallRectPoint2 = new Point(xPos + INDICATOR_WIDTH / 2, yPos + INDICATOR_HEIGHT / 2);

        Rect smallRect = new Rect(smallRectPoint1, smallRectPoint2);

        Point largeRectPoint1 = new Point(xPos - INDICATOR_WIDTH, yPos - INDICATOR_HEIGHT);
        Point largeRectPoint2 = new Point(xPos + INDICATOR_WIDTH, yPos + INDICATOR_HEIGHT);

        Rect largRect = new Rect(largeRectPoint1, largeRectPoint2);

        indicatorRects.put(IndicatorSize.Small.name(), smallRect);
        indicatorRects.put(IndicatorSize.Large.name(), largRect);
    }

    /**
     * Called when the openCv camera view will stop.
     * Frees used resources.
     */
    @Override
    public void onCameraViewStopped() {
        if (fullImage != null)
            fullImage.release();

        if (fullImageTemp != null)
            fullImageTemp.release();
    }

    /**
     * Called from OpenCV when a new camera preview frame is available.
     * <p>
     * This method performs the image modifications/adjustments (eg. brightness, color, ...)
     * if they are not set to the default values
     * and draws the indicator into the image.
     * <p>
     * synchronized with getResistorImage() to ensure the image in fullImage
     * is always one complete frame when getResistorImage is called.
     *
     * @param inputFrame the new camera preview image
     * @return the new camera preview image with the indicator drawn into it.
     */
    @Override
    public synchronized Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        /*
         * To prevent frame drops to 0 and app/device freeze:
         * Do not create new Mat objects here!
         * Do not ignore the return matrix of functions (always assign them to a member variable) here!
         * Do not use the clone() method here!
         */

        /*
         * All matrices are contain RGBA colors.
         */

        fullImage = inputFrame.rgba();

        //change brightnes and contrast
        //g(x) = contrastModifier * f(x) + brightnessModifier
        //see http://docs.opencv.org/2.4/doc/tutorials/core/basic_linear_transform/basic_linear_transform.html
        if (contrastModifier != CONTRAST_MODIFIER_DEFAULT || brightnessModifier != BRIGHTNESS_MODIFIER_DEFAULT) {
            fullImage.convertTo(fullImage, -1, contrastModifier, brightnessModifier);
        }


        //change color amount in image of red, green and blue
        if (colorModifierRed != COLOR_MODIFIER_DEFAULT || colorModifierGreen != COLOR_MODIFIER_DEFAULT || colorModifierBlue != COLOR_MODIFIER_DEFAULT) {
            float multiplierRed = (float) (colorModifierRed) / COLOR_MODIFIER_DEFAULT;
            float multiplierGreen = (float) (colorModifierGreen) / COLOR_MODIFIER_DEFAULT;
            float multiplierBlue = (float) (colorModifierBlue) / COLOR_MODIFIER_DEFAULT;
            float multiplierAlpha = 1;

            Scalar multiplier = new Scalar(multiplierRed, multiplierGreen, multiplierBlue, multiplierAlpha);

            Core.multiply(fullImage, multiplier, fullImage);
        }

        //draw the indicatorSize only to a temporary image
        //display the temporary image with the indicatorSize
        fullImage.copyTo(fullImageTemp);

        Rect indicator = getIndicator();
        Imgproc.rectangle(fullImageTemp, indicator.tl(), indicator.br(), INDICATOR_COLOR, INDICATOR_THICKNESS);

        //fullImage contains the last captured image frame
        //fullImageTemp contains the last captured image frame AND the search indicatorSize
        return fullImageTemp;
    }

    /**
     * Returns a copy of the last camera image frame inside the indicatorSize rectangle.
     * Release the returned matrix if not needed anymore.
     * The returned matrix is in RGBA color.
     * <p>
     * synchronized with onCameraFrame() to ensure the image in fullImage
     * is always one complete frame when getResistorImage() is called.
     *
     * @return A new matrix object containing the image inside the search indicatorSize (of the last frame).
     */
    public synchronized Mat getResistorImage() {
        Rect indicator = getIndicator();

        Mat resistorImageRgba = new Mat(indicator.height, indicator.width, fullImage.type());

        fullImage.submat(indicator).copyTo(resistorImageRgba);

        return resistorImageRgba;
    }

    /**
     * (Re)Sets all camera image modifications/adjustments to their default values.
     */
    public void resetBrightnessAndContrastModifiers() {
        contrastModifier = CONTRAST_MODIFIER_DEFAULT;
        brightnessModifier = BRIGHTNESS_MODIFIER_DEFAULT;

        colorModifierRed = COLOR_MODIFIER_DEFAULT;
        colorModifierGreen = COLOR_MODIFIER_DEFAULT;
        colorModifierBlue = COLOR_MODIFIER_DEFAULT;

        indicatorSize = INDICATOR_SIZE_DEFAULT;
    }

    /**
     * Sets the new brightness modifier.
     *
     * @param brightnessModifier the new brightness modifier.
     */
    public void setBrightnessModifier(int brightnessModifier) {
        if (brightnessModifier < BRIGHTNESS_MODIFIER_MIN_VALUE || contrastModifier > BRIGHTNESS_MODIFIER_MAX_VALUE) {
            throw new IllegalArgumentException("brightnessModifier must be between "
                    + BRIGHTNESS_MODIFIER_MIN_VALUE + " and " + BRIGHTNESS_MODIFIER_MAX_VALUE);
        }

        this.brightnessModifier = brightnessModifier;
    }

    /**
     * Sets the new contrast modifier.
     *
     * @param contrastModifier the new contrast modifier.
     */
    public void setContrastModifier(float contrastModifier) {
        if (contrastModifier < CONTRAST_MODIFIER_MIN_VALUE || contrastModifier > CONTRAST_MODIFIER_MAX_VALUE) {
            throw new IllegalArgumentException("contrastModifier must be between "
                    + CONTRAST_MODIFIER_MIN_VALUE + " and " + CONTRAST_MODIFIER_MAX_VALUE);
        }

        this.contrastModifier = contrastModifier;
    }

    /**
     * Sets the new color correction modifiers.
     *
     * @param red   the new correction value for the color red.
     * @param green the new correction value for the color green.
     * @param blue  the new correction value for the blue .
     */
    public void setColorModifiers(int red, int green, int blue) {
        if (red < COLOR_MODIFIER_MIN_VALUE || red > COLOR_MODIFIER_MAX_VALUE) {
            throw new IllegalArgumentException("red must be between "
                    + COLOR_MODIFIER_MIN_VALUE + " and " + COLOR_MODIFIER_MAX_VALUE);
        }

        if (green < COLOR_MODIFIER_MIN_VALUE || green > COLOR_MODIFIER_MAX_VALUE) {
            throw new IllegalArgumentException("gree must be between "
                    + COLOR_MODIFIER_MIN_VALUE + " and " + COLOR_MODIFIER_MAX_VALUE);
        }

        if (blue < COLOR_MODIFIER_MIN_VALUE || blue > COLOR_MODIFIER_MAX_VALUE) {
            throw new IllegalArgumentException("blue must be between "
                    + COLOR_MODIFIER_MIN_VALUE + " and " + COLOR_MODIFIER_MAX_VALUE);
        }

        colorModifierRed = red;
        colorModifierGreen = green;
        colorModifierBlue = blue;
    }

    /**
     * Sets the size of the indicator.
     *
     * @param size the new size of the indicator
     */
    public void setIndicatorSize(IndicatorSize size) {
        indicatorSize = size;
    }

    /**
     * Returns the current size of the indicator.
     *
     * @return the current size of the indicator.
     */
    private Rect getIndicator() {
        return indicatorRects.get(indicatorSize.name());
    }
}
