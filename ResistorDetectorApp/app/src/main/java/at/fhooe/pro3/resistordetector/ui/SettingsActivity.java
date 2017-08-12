package at.fhooe.pro3.resistordetector.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import at.fhooe.pro3.resistordetector.AppSettings;
import at.fhooe.pro3.resistordetector.R;

/**
 * This activity contains the different settings the user can adjust
 * to optimize the detection performance or the detection mode.
 * <p>
 * Created by stefan on 05.05.2017.
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * This instance of AppSettings is used to access and store the settings.
     */
    private AppSettings settings;

    /**
     * Sets up the view and initializes the view elements (controls).
     *
     * @param savedInstanceState see android documentation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = new AppSettings(getApplicationContext());

        setContentView(R.layout.activity_settings);

        setupBrightnessControl();
        setupContrastControl();
        setupColorAdjustmentControls();
        setupIndicatorSizeControl();

        setupModeSelectionControl();
    }

    /**
     * Sets up and initializes the brightness seek bar.
     * The initial value is set to the last saved value (if there is any)
     * or to the default value.
     */
    private void setupBrightnessControl() {
        SeekBar brightnessSeekBar = (SeekBar) findViewById(R.id.settings_activity_brightness);

        brightnessSeekBar.setMax(CameraViewListener.BRIGHTNESS_MODIFIER_MAX_VALUE - CameraViewListener.BRIGHTNESS_MODIFIER_MIN_VALUE);

        int brightnessModifier = settings.getBrightnessModifier();

        if (brightnessModifier < CameraViewListener.BRIGHTNESS_MODIFIER_MIN_VALUE || brightnessModifier > CameraViewListener.BRIGHTNESS_MODIFIER_MAX_VALUE) {
            brightnessModifier = CameraViewListener.BRIGHTNESS_MODIFIER_DEFAULT;
        }

        brightnessSeekBar.setProgress(brightnessModifier - CameraViewListener.BRIGHTNESS_MODIFIER_MIN_VALUE);

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSnapToCenter(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int newBrightnessModifier = seekBar.getProgress() + CameraViewListener.BRIGHTNESS_MODIFIER_MIN_VALUE;

                settings.saveBrightnessModifier(newBrightnessModifier);
            }
        });
    }

    /**
     * Sets up and initializes the contrast seek bar.
     * The initial value is set to the last saved value (if there is any)
     * or to the default value.
     */
    private void setupContrastControl() {
        SeekBar contrastSeekBar = (SeekBar) findViewById(R.id.settings_activity_contrast);

        final float stepSize = 0.01f;

        contrastSeekBar.setMax((int) ((CameraViewListener.CONTRAST_MODIFIER_MAX_VALUE - CameraViewListener.CONTRAST_MODIFIER_MIN_VALUE) / stepSize));

        float contrastModifier = settings.getContrastModifier();

        if (contrastModifier < CameraViewListener.CONTRAST_MODIFIER_MIN_VALUE || contrastModifier > CameraViewListener.CONTRAST_MODIFIER_MAX_VALUE) {
            contrastModifier = CameraViewListener.CONTRAST_MODIFIER_DEFAULT;
        }

        contrastSeekBar.setProgress((int) ((contrastModifier - CameraViewListener.CONTRAST_MODIFIER_MIN_VALUE) / stepSize));

        contrastSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSnapToCenter(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float newContrastModifier = seekBar.getProgress() * stepSize;
                newContrastModifier = newContrastModifier + CameraViewListener.CONTRAST_MODIFIER_MIN_VALUE;

                settings.saveContrastModifier(newContrastModifier);
            }
        });
    }

    /**
     * Sets up and initializes the three color adjustment seek bars.
     * The initial value is set to the last saved value (if there is any)
     * or to the default value.
     */
    private void setupColorAdjustmentControls() {
        final SeekBar colorRedSeekBar = (SeekBar) findViewById(R.id.settings_activity_color_red);
        final SeekBar colorGreenSeekBar = (SeekBar) findViewById(R.id.settings_activity_color_green);
        final SeekBar colorBlueSeekBar = (SeekBar) findViewById(R.id.settings_activity_color_blue);

        colorRedSeekBar.setMax(CameraViewListener.COLOR_MODIFIER_MAX_VALUE - CameraViewListener.COLOR_MODIFIER_MIN_VALUE);
        colorGreenSeekBar.setMax(CameraViewListener.COLOR_MODIFIER_MAX_VALUE - CameraViewListener.COLOR_MODIFIER_MIN_VALUE);
        colorBlueSeekBar.setMax(CameraViewListener.COLOR_MODIFIER_MAX_VALUE - CameraViewListener.COLOR_MODIFIER_MIN_VALUE);

        int[] colorModifiers = settings.getColorModifiers();

        for (int i = 0; i < colorModifiers.length; i++) {
            if (colorModifiers[i] < CameraViewListener.COLOR_MODIFIER_MIN_VALUE || colorModifiers[i] > CameraViewListener.COLOR_MODIFIER_MAX_VALUE) {
                colorModifiers[i] = CameraViewListener.COLOR_MODIFIER_DEFAULT;
            }
        }

        colorRedSeekBar.setProgress(colorModifiers[0]);
        colorGreenSeekBar.setProgress(colorModifiers[1]);
        colorBlueSeekBar.setProgress(colorModifiers[2]);

        SeekBar.OnSeekBarChangeListener changeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarSnapToCenter(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int colorRedModifier = colorRedSeekBar.getProgress();
                int colorGreenModifier = colorGreenSeekBar.getProgress();
                int colorBlueModifier = colorBlueSeekBar.getProgress();

                int[] colorModifiers = new int[]{
                        colorRedModifier,
                        colorGreenModifier,
                        colorBlueModifier
                };

                settings.saveColorModifiers(colorModifiers);
            }
        };

        colorRedSeekBar.setOnSeekBarChangeListener(changeListener);
        colorGreenSeekBar.setOnSeekBarChangeListener(changeListener);
        colorBlueSeekBar.setOnSeekBarChangeListener(changeListener);
    }

    /**
     * Sets up and initializes the indicator size spinner.
     * The initial value is set to the last saved value (if there is any)
     * or to the default value.
     */
    private void setupIndicatorSizeControl() {
        Spinner indicatorSizeSpinner = (Spinner) findViewById(R.id.settings_activity_indicator_size);

        final ArrayAdapter<String> indicatorSizeElements = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        for (CameraViewListener.IndicatorSize mode : CameraViewListener.IndicatorSize.values()) {
            indicatorSizeElements.add(mode.name());
        }

        CameraViewListener.IndicatorSize indicatorSize = settings.getIndicatoreSize();

        int selectionPosition = indicatorSizeElements.getPosition(indicatorSize.name());

        if (selectionPosition < 0 || selectionPosition > indicatorSizeElements.getCount()) {
            selectionPosition = indicatorSizeElements.getPosition(AppSettings.DEFAULT_INDICATOR_SIZE);
        }

        indicatorSizeElements.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        indicatorSizeSpinner.setAdapter(indicatorSizeElements);
        indicatorSizeSpinner.setSelection(selectionPosition);

        indicatorSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CameraViewListener.IndicatorSize selectedIndicatorSize = CameraViewListener.IndicatorSize.valueOf(indicatorSizeElements.getItem(position));

                settings.saveIndicatorSize(selectedIndicatorSize);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Sets up and initializes the detection mode selection spinner.
     * The initial value is set to the last saved value (if there is any)
     * or to the default value.
     */
    private void setupModeSelectionControl() {
        Spinner detectionModeSpinner = (Spinner) findViewById(R.id.settings_activity_mode_select);

        final ArrayAdapter<String> detectionModeElements = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        for (DetectionMode mode : DetectionMode.values()) {
            detectionModeElements.add(mode.name());
        }

        DetectionMode detectionMode = settings.getDetectionMode();

        int selectionPosition = detectionModeElements.getPosition(detectionMode.name());

        if (selectionPosition < 0 || selectionPosition > detectionModeElements.getCount()) {
            selectionPosition = detectionModeElements.getPosition(AppSettings.DEFAULT_DETECTION_MODE);
        }

        detectionModeElements.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        detectionModeSpinner.setAdapter(detectionModeElements);
        detectionModeSpinner.setSelection(selectionPosition);

        detectionModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DetectionMode selectedDetectionMode = DetectionMode.valueOf(detectionModeElements.getItem(position));

                settings.saveDetectionMode(selectedDetectionMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Method to snap the button of a seek bar to the center if the button is moved
     * within one percent of the seek bar length around the center.
     * <p>
     * This methods should be called in onProgressChanged() method
     * of the SeekBar.OnSeekBarChangeListener.
     *
     * @param seekBar  the seekbar on which the button should snap to the center
     * @param progress the new progress value of the given seekbar
     */
    private void seekBarSnapToCenter(SeekBar seekBar, int progress) {
        int seekBarCenter = seekBar.getMax() / 2;
        int centerMin = (int) (seekBarCenter - seekBarCenter * 0.1);
        int centerMax = (int) (seekBarCenter + seekBarCenter * 0.1);

        if (progress > centerMin && progress < centerMax) {
            seekBar.setProgress(seekBarCenter);
        }
    }
}
