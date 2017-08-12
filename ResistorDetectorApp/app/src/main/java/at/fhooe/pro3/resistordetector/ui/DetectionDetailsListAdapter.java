package at.fhooe.pro3.resistordetector.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import at.fhooe.pro3.resistordetector.R;
import at.fhooe.pro3.resistordetector.detection.DetectionStepDetail;

/**
 * ListAdapter to display the list of DetectionStepDetails in an android listview
 * <p>
 * Created by stefan on 28.05.2017.
 */
public class DetectionDetailsListAdapter extends ArrayAdapter<DetectionStepDetail> {

    /**
     * The detection images are scaled with this factor.
     */
    private static final int IMAGE_SCALE_FACTOR = 4;

    /**
     * Creates a new instance of this class to display the given data.
     *
     * @param context android context
     * @param data    the data to display inside the list
     */
    public DetectionDetailsListAdapter(Context context, ArrayList<DetectionStepDetail> data) {
        super(context, R.layout.detection_details_list_row, data);
    }

    /**
     * Returns the number of elements in this adapter.
     *
     * @return
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Returns always false for all positions.
     * This disables all elements of the list.
     *
     * @param position position of the list element
     * @return false
     */
    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * Returns a view for one list element with data from the list provided in the constructor.
     * <p>
     * See android documentation.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.detection_details_list_row, null);
        }

        DetectionStepDetail rowData = getItem(position);

        if (rowData != null) {
            TextView textView = (TextView) view.findViewById(R.id.detection_deteils_activity_list_text);
            ImageView imageView = (ImageView) view.findViewById(R.id.detection_deteils_activity_list_image);

            if (textView != null) {
                textView.setText(rowData.getDescription());
            }

            if (imageView != null) {
                if (rowData.isImageAvailable()) {
                    Bitmap detectionStepImage = rowData.getImage();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(detectionStepImage, detectionStepImage.getWidth() * IMAGE_SCALE_FACTOR, detectionStepImage.getHeight() * IMAGE_SCALE_FACTOR, false);
                    imageView.setImageBitmap(scaledBitmap);
                } else {
                    imageView.setImageDrawable(null);
                }
            }
        }

        return view;
    }
}
