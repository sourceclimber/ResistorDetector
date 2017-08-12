package at.fhooe.pro3.resistordetector.desktop.detection;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the values a specific color of a resistor band stands for.
 * <p>
 * This are the values associated with resistor band colors:
 * Black, 0
 * Brown, 1
 * Red, 2
 * Orange, 3
 * Yellow, 4
 * Green, 5
 * Blue, 6
 * Violet, 7
 * Grey, 8
 * White, 9
 * <p>
 * For unknown colors the value UNKNOWN_COLOR_VALUE (-1) is returned.
 * <p>
 * Created by stefan on 24.06.2017.
 */
public class ColorValues {

    /**
     * Value for unknown colors.
     */
    public static final Integer UNKNOWN_COLOR_VALUE = -1;

    /**
     * This map contains the mapping of ColorName to value.
     */
    private static Map<ColorName, Integer> colorValues = new HashMap<>();

    /**
     * static constructor to initialize the mapping between ColorName and value
     */
    static {
        colorValues.put(ColorName.Black, 0);
        colorValues.put(ColorName.Brown, 1);
        colorValues.put(ColorName.Red, 2);
        colorValues.put(ColorName.Orange, 3);
        colorValues.put(ColorName.Yellow, 4);
        colorValues.put(ColorName.Green, 5);
        colorValues.put(ColorName.Blue, 6);
        colorValues.put(ColorName.Violet, 7);
        colorValues.put(ColorName.Grey, 8);
        colorValues.put(ColorName.White, 9);
    }

    /**
     * Returns the value a resistor band color is representing.
     * <p>
     * If the colorName is Unknown or has ho color associated,
     * the value UNKNOWN_COLOR_VALUE is returned.
     *
     * @param colorName The color name of a resistor band
     * @return The value the given color is representing, or UNKNOWN_COLOR_VALUE
     */
    public static int getValueForColor(ColorName colorName) {
        if (colorValues.containsKey(colorName)) {
            return colorValues.get(colorName);
        }

        return UNKNOWN_COLOR_VALUE;
    }
}
