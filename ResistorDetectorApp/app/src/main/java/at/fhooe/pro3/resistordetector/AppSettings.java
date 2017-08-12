package at.fhooe.pro3.resistordetector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import at.fhooe.pro3.resistordetector.ui.CameraViewListener;
import at.fhooe.pro3.resistordetector.ui.DetectionMode;

/**
 * This class manages the persistence of app settings.
 * <p>
 * The settings are saved using SharedPreferences.
 * <p>
 * Created by stefan on 28.05.2017.
 */
public class AppSettings {

    private static final String FLASH_ENABLED = "flash_enabled";
    private static final String ZOOM_LEVEL = "zoom_level";

    private static final String BRIGHTNESS_MODIFIER = "brightness_modifier";
    private static final String CONTRAST_MODIFIER = "contrast_modifier";

    private static final String COLOR_MODIFIER_1 = "color_modifier_1";
    private static final String COLOR_MODIFIER_2 = "color_modifier_2";
    private static final String COLOR_MODIFIER_3 = "color_modifier_3";

    private static final String INDICATOR_SIZE = "indicator_size";

    private static final String DETECTION_MODE = "detection_mode";

    public static final boolean DEFAULT_FLASH_ENABLED = false;
    public static final int DEFAULT_ZOOM_LEVEL = -1;
    public static final int DEFAULT_BRIGHTNESS_MODIFIER = CameraViewListener.BRIGHTNESS_MODIFIER_DEFAULT;
    public static final float DEFAULT_CONTRAST_MODIFIER = CameraViewListener.CONTRAST_MODIFIER_DEFAULT;

    public static final int DEFAULT_COLOR_MODIFIER = CameraViewListener.COLOR_MODIFIER_DEFAULT;

    public static final String DEFAULT_INDICATOR_SIZE = CameraViewListener.INDICATOR_SIZE_DEFAULT.name();

    public static final String DEFAULT_DETECTION_MODE = DetectionMode.ColumnResistorDetection.name();

    /**
     * The shared preferences object used by this AppSettings object.
     */
    private SharedPreferences myPreferences;

    /**
     * Creates a new AppSettings object using the given context to get the
     * default shared preferences object.
     *
     * @param context this context is used to get the default shared preferences.
     */
    public AppSettings(Context context) {
        myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param flashEnabled the new value of the setting (see method name)
     */
    public void saveFlashEnabled(boolean flashEnabled) {
        setPreferencesBool(FLASH_ENABLED, flashEnabled);
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public boolean getFlashEnabled() {
        return myPreferences.getBoolean(FLASH_ENABLED, DEFAULT_FLASH_ENABLED);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param zoomLevel the new value of the setting (see method name)
     */
    public void saveZoomLevel(int zoomLevel) {
        setPreferencesInt(ZOOM_LEVEL, zoomLevel);
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public int getZoomLevel() {
        return myPreferences.getInt(ZOOM_LEVEL, DEFAULT_ZOOM_LEVEL);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param brightnessModifier the new value of the setting (see method name)
     */
    public void saveBrightnessModifier(int brightnessModifier) {
        setPreferencesInt(BRIGHTNESS_MODIFIER, brightnessModifier);
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public int getBrightnessModifier() {
        return myPreferences.getInt(BRIGHTNESS_MODIFIER, DEFAULT_BRIGHTNESS_MODIFIER);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param contrastModifier the new value of the setting (see method name)
     */
    public void saveContrastModifier(float contrastModifier) {
        setPreferencesFloat(CONTRAST_MODIFIER, contrastModifier);
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public float getContrastModifier() {
        return myPreferences.getFloat(CONTRAST_MODIFIER, DEFAULT_CONTRAST_MODIFIER);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param colorModifiers the new value of the setting (see method name)
     */
    public void saveColorModifiers(int[] colorModifiers) {
        if (colorModifiers.length != 3)
            throw new IllegalArgumentException("colorModifiers must be an array with 3 elements (red, gree, blue)!");

        setPreferencesInt(COLOR_MODIFIER_1, colorModifiers[0]);
        setPreferencesInt(COLOR_MODIFIER_2, colorModifiers[1]);
        setPreferencesInt(COLOR_MODIFIER_3, colorModifiers[2]);
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public int[] getColorModifiers() {
        int[] colorModifiers = new int[3];

        colorModifiers[0] = myPreferences.getInt(COLOR_MODIFIER_1, DEFAULT_COLOR_MODIFIER);
        colorModifiers[1] = myPreferences.getInt(COLOR_MODIFIER_2, DEFAULT_COLOR_MODIFIER);
        colorModifiers[2] = myPreferences.getInt(COLOR_MODIFIER_3, DEFAULT_COLOR_MODIFIER);

        return colorModifiers;
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param indicatorSize the new value of the setting (see method name)
     */
    public void saveIndicatorSize(CameraViewListener.IndicatorSize indicatorSize) {
        setPreferencesString(INDICATOR_SIZE, indicatorSize.name());
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public CameraViewListener.IndicatorSize getIndicatoreSize() {
        String indicatorSizeString = myPreferences.getString(INDICATOR_SIZE, DEFAULT_INDICATOR_SIZE);

        return CameraViewListener.IndicatorSize.valueOf(indicatorSizeString);
    }

    /**
     * Sets the value of the setting (see method name).
     *
     * @param detectionMode the new value of the setting (see method name)
     */
    public void saveDetectionMode(DetectionMode detectionMode) {
        setPreferencesString(DETECTION_MODE, detectionMode.name());
    }

    /**
     * Gets the value of the setting (see method name).
     *
     * @return the value of the setting (see method name), or the default value
     */
    public DetectionMode getDetectionMode() {
        String indicatorSizeString = myPreferences.getString(DETECTION_MODE, DEFAULT_DETECTION_MODE);

        return DetectionMode.valueOf(indicatorSizeString);
    }

    /**
     * Removes all saved preferences and settings.
     */
    public void removeAll() {
        SharedPreferences.Editor editor = myPreferences.edit();

        editor.remove(FLASH_ENABLED);
        editor.remove(ZOOM_LEVEL);
        editor.remove(BRIGHTNESS_MODIFIER);
        editor.remove(CONTRAST_MODIFIER);
        editor.remove(COLOR_MODIFIER_1);
        editor.remove(COLOR_MODIFIER_2);
        editor.remove(COLOR_MODIFIER_3);
        editor.remove(INDICATOR_SIZE);
        editor.remove(DETECTION_MODE);

        editor.apply();
    }

    /**
     * Sets the given value with the given key in the currently open shared preferences.
     *
     * @param key   the key of the value to save.
     * @param value the value to save
     */
    private void setPreferencesInt(String key, Integer value) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Sets the given value with the given key in the currently open shared preferences.
     *
     * @param key   the key of the value to save.
     * @param value the value to save
     */
    private void setPreferencesFloat(String key, float value) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    /**
     * Sets the given value with the given key in the currently open shared preferences.
     *
     * @param key   the key of the value to save.
     * @param value the value to save
     */
    private void setPreferencesString(String key, String value) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Sets the given value with the given key in the currently open shared preferences.
     *
     * @param key   the key of the value to save.
     * @param value the value to save
     */
    private void setPreferencesBool(String key, boolean value) {
        SharedPreferences.Editor editor = myPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
