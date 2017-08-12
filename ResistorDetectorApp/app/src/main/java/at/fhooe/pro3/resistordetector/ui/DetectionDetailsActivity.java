package at.fhooe.pro3.resistordetector.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import at.fhooe.pro3.resistordetector.DetectionResultHolder;
import at.fhooe.pro3.resistordetector.R;

/**
 * This activity displays the Detection Step Details of the last detection process.
 * The data to display is from the DetectionResultHolder static class.
 * <p>
 * Created by stefan on 28.05.2017.
 */
public class DetectionDetailsActivity extends AppCompatActivity {

    /**
     * Sets up the view and initializes the view elements (controls).
     *
     * @param savedInstanceState see android documentation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_details);

        if (DetectionResultHolder.detectionResultAvailable()) {
            ListView detectionDetailsList = (ListView) findViewById(R.id.detection_deteils_activity_list);

            DetectionDetailsListAdapter adapter = new DetectionDetailsListAdapter(this, DetectionResultHolder.getDetectionResult().getDetectionStepDetails());

            detectionDetailsList.setAdapter(adapter);
        }
    }
}
