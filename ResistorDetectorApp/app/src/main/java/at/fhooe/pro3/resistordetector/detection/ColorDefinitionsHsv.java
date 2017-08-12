package at.fhooe.pro3.resistordetector.detection;

import org.opencv.core.Scalar;

/**
 * This class contains definitions for the color used for resistor bands
 * in the form of a minimum and maximum HSV value.
 * <p>
 * Colors can be associated with their names by checking if the color
 * is between the lower and upper bounds of a color.
 * <p>
 * HSV:
 * Scalar(H, S, V);
 * Scalar(0-180, 0-255, 0-255);
 * <p>
 * Created by stefan on 25.05.2017.
 */
public class ColorDefinitionsHsv {

    /**
     * Red wraps around and is therefore defined twice
     */
    public static final Scalar RED1_MIN = new Scalar(0, 65, 100);
    public static final Scalar RED1_MAX = new Scalar(6, 250, 150);

    public static final Scalar ORANGE_MIN = new Scalar(7, 150, 150);
    public static final Scalar ORANGE_MAX = new Scalar(18, 250, 250);

    public static final Scalar YELLOW_MIN = new Scalar(25, 130, 100);
    public static final Scalar YELLOW_MAX = new Scalar(34, 250, 160);

    public static final Scalar GREEN_MIN = new Scalar(35, 60, 60);
    public static final Scalar GREEN_MAX = new Scalar(75, 250, 150);

    public static final Scalar BLUE_MIN = new Scalar(82, 60, 49);
    public static final Scalar BLUE_MAX = new Scalar(128, 255, 255);

    public static final Scalar VIOLET_MIN = new Scalar(129, 60, 50);
    public static final Scalar VIOLET_MAX = new Scalar(165, 250, 150);

    /**
     * Red wraps around and is therefore defined twice
     */
    public static final Scalar RED2_MIN = new Scalar(166, 65, 50);
    public static final Scalar RED2_MAX = new Scalar(180, 250, 150);

    public static final Scalar BLACK_MIN = new Scalar(0, 0, 0);
    public static final Scalar BLACK_MAX = new Scalar(180, 250, 40);

    public static final Scalar BROWN_MIN = new Scalar(0, 31, 41);
    public static final Scalar BROWN_MAX = new Scalar(25, 250, 99);

    public static final Scalar GREY_MIN = new Scalar(0, 0, 41);
    public static final Scalar GREY_MAX = new Scalar(180, 30, 130);

    public static final Scalar WHITE_MIN = new Scalar(0, 0, 150);
    public static final Scalar WHITE_MAX = new Scalar(180, 30, 255);

    public static final Scalar GOLD_MIN = new Scalar(0, 0, 0);     //// TODO:
    public static final Scalar GOLD_MAX = new Scalar(0, 0, 0);

    public static final Scalar SILVER_MIN = new Scalar(0, 0, 0);   //// TODO:
    public static final Scalar SILVER_MAX = new Scalar(0, 0, 0);

    /**
     * This color (black) is used for unknown color values
     * when converting from color names to colors
     */
    public static final Scalar UNKNOWN = new Scalar(0, 0, 0);

    /**
     * Calculate the mean value for H, S and V of two Scalars.
     *
     * @param scalar1 The first scalar
     * @param scalar2 The second scalar
     * @return The mean value of scalar1 and scalar2
     */
    private static Scalar meanOfScalars(Scalar scalar1, Scalar scalar2) {
        if (scalar1.val.length != scalar2.val.length)
            System.err.println("scalar1 and scalar2 size mismatch");

        Scalar result = new Scalar(0, 0, 0, 0);

        for (int i = 0; i < scalar1.val.length; i++) {
            result.val[i] = (scalar1.val[i] + scalar2.val[i]) / 2;
        }

        return result;
    }

    /**
     * Checks if a scalar value is between an lower and and upper limit.
     * A scalar is between two other scalars if ALL values of the scalar are between the bounds.
     *
     * @param scalar     The scalar to check if it is between the lower and upper bound
     * @param lowerBound The lower bound. Every value of scalar has to be above these values.
     * @param upperBound The upper bound. Every value of scalar has to be below these values.
     * @return true if the scalar is between the bounds, false otherwise
     */
    private static boolean isScalarBetweenBounds(Scalar scalar, Scalar lowerBound, Scalar upperBound) {
        if (scalar == null || lowerBound == null || upperBound == null)
            throw new IllegalArgumentException("scalars must not be null!");

        if (lowerBound.val.length != upperBound.val.length)
            System.err.println("upper and lower bound size mismatch");

        if (scalar.val.length != lowerBound.val.length)
            System.err.println("scalar and bounds size mismatch");

        for (int i = 0; i < scalar.val.length; i++) {
            if (scalar.val[i] < lowerBound.val[i] || scalar.val[i] > upperBound.val[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Converts a HSV color to a color name.
     * The name of a color is determined by checking if the provided color
     * is between the bounds of the color name.
     * <p>
     * ColorName.Unknown is returned if no matching color name can be found.
     * The method prints error messages if color definitions overlap.
     *
     * @param colorHsv The color in HSV space to convert to a name.
     * @return The name associated with the given color, or ColorName.Unknown.
     */
    public static ColorName getColorName(Scalar colorHsv) {
        if (colorHsv == null)
            throw new IllegalArgumentException("colorHsv must not be null!");

        ColorName name = ColorName.Unknown;

        if (isScalarBetweenBounds(colorHsv, RED1_MIN, RED1_MAX) ||
                isScalarBetweenBounds(colorHsv, RED2_MIN, RED2_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Red + ")!");

            name = ColorName.Red;
        }

        if (isScalarBetweenBounds(colorHsv, ORANGE_MIN, ORANGE_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Orange + ")!");

            name = ColorName.Orange;
        }

        if (isScalarBetweenBounds(colorHsv, YELLOW_MIN, YELLOW_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Yellow + ")!");

            name = ColorName.Yellow;
        }

        if (isScalarBetweenBounds(colorHsv, GREEN_MIN, GREEN_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Green + ")!");

            name = ColorName.Green;
        }

        if (isScalarBetweenBounds(colorHsv, BLUE_MIN, BLUE_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Blue + ")!");

            name = ColorName.Blue;
        }

        if (isScalarBetweenBounds(colorHsv, VIOLET_MIN, VIOLET_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Violet + ")!");

            name = ColorName.Violet;
        }

        if (isScalarBetweenBounds(colorHsv, BROWN_MIN, BROWN_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Brown + ")!");

            name = ColorName.Brown;
        }

        if (isScalarBetweenBounds(colorHsv, BLACK_MIN, BLACK_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Black + ")!");

            name = ColorName.Black;
        }

        if (isScalarBetweenBounds(colorHsv, GREY_MIN, GREY_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Grey + ")!");

            name = ColorName.Grey;
        }

        if (isScalarBetweenBounds(colorHsv, WHITE_MIN, WHITE_MAX)) {
            if (name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.White + ")!");

            name = ColorName.White;
        }

        /*
        if(isScalarBetweenBounds(colorHsv, GOLD_MIN, GOLD_MAX)){
            if(name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Gold + ")!");

            name = ColorName.Gold;
        }

        if(isScalarBetweenBounds(colorHsv, SILVER_MIN, SILVER_MAX)){
            if(name != ColorName.Unknown)
                System.err.println("overlapping colorHsv name definitions (" + name + " and " + ColorName.Silver + ")!");

            name = ColorName.Silver;
        }
        */


        return name;
    }

    /**
     * Returns the HSV color representing a color name.
     * The color representing a name is calculated with the mean value
     * of the lower and upper bound of the color definition.
     * <p>
     * Returns the color UNKNOWN (black) for unknown colors.
     *
     * @param name The color name to convert to a HSV color
     * @return The HSV color representing the given name, or the color UNKNOWN.
     */
    public static Scalar getColorFromName(ColorName name) {
        switch (name) {
            case Black:
                return meanOfScalars(BLACK_MAX, BLACK_MIN);
            case Brown:
                return meanOfScalars(BROWN_MAX, BROWN_MIN);
            case Red:
                return meanOfScalars(RED1_MAX, RED1_MIN);
            case Orange:
                return meanOfScalars(ORANGE_MAX, ORANGE_MIN);
            case Yellow:
                return meanOfScalars(YELLOW_MAX, YELLOW_MIN);
            case Green:
                return meanOfScalars(GREEN_MAX, GREEN_MIN);
            case Blue:
                return meanOfScalars(BLUE_MAX, BLUE_MIN);
            case Violet:
                return meanOfScalars(VIOLET_MAX, VIOLET_MIN);
            case Grey:
                return meanOfScalars(GREY_MAX, GREY_MIN);
            case White:
                return meanOfScalars(WHITE_MAX, BLACK_MIN);

            /*
            case Gold:
                return meanOfScalars(GOLD_MAX, GOLD_MIN);
            case Silver:
                return meanOfScalars(SILVER_MAX, SILVER_MIN);
            */

            case Unknown:
            default:
                return UNKNOWN;
        }
    }
}
