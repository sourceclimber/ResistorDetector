package at.fhooe.pro3.resistordetector.detection;

/**
 * This class contains information about one band (color ring) of a resistor.
 * The band colorName and the with of the band are stored.
 * <p>
 * Width is the width of the band in pixels.
 * <p>
 * Created by stefan on 24.06.2017.
 */
public class BandInfo {
    /**
     * The ColorName of this resistor band.
     */
    private ColorName color;

    /**
     * The width of this resistor band in pixels.
     */
    private int width;

    /**
     * Creates a new resistor band info object with the given colorName and width.
     *
     * @param color The colorName of the resistor band
     * @param width The width of the resistor band in pixels
     */
    public BandInfo(ColorName color, int width) {
        this.color = color;
        this.width = width;
    }

    /**
     * Returns the ColorName of the resistor band.
     *
     * @return the ColorName of the resistor band
     */
    public ColorName getColor() {
        return color;
    }

    /**
     * Returns the width of the resistor band in pixels.
     *
     * @return the width of the resistor band in pixels
     */
    public int getWidth() {
        return width;
    }
}
