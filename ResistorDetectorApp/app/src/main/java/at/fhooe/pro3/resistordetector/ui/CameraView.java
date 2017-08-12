package at.fhooe.pro3.resistordetector.ui;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import java.util.List;

/**
 * Subclass implementation of PortraitCameraView with some optional settings for the camera.
 * This class allows to set the zoom factor of the camera
 * and to enable/disable the flash in torch mode.
 * <p>
 * Created by stefan on 20.05.2017.
 */
@SuppressWarnings("deprecation")
public class CameraView extends PortraitCameraView /* implements Camera.PictureCallback */ {

    /**
     * Interface defining a method that is called when the initialization of the camera is finished.
     */
    public interface OnCameraInitializedCallback {
        void cameraViewInitialized();
    }

    /**
     * The callback to call when the initialization of the camera has finished.
     */
    private OnCameraInitializedCallback onCameraInitializedCallback;

    /**
     * Creates a new camera view object.
     * See OpenCV documentation.
     */
    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initializes the camera preview.
     * Calls the initializeCamera() method of the base class (see OpenCV documentation)
     * Calls the OnCameraInitializedCallback after the initialization has finished.
     */
    @Override
    protected boolean initializeCamera(int width, int height) {

        boolean initializeCameraResult = super.initializeCamera(width, height);

        Camera.Parameters parameters = getCameraParameters();

//        //Needed for takePicture()
//        Camera.Size pictureSize = getCameraParameters().getSupportedPictureSizes().get(0);
//
//        parameters.setPictureSize(pictureSize.width, pictureSize.height);
//        parameters.setPreviewSize(width, height);

        setCameraParameters(parameters);

        if (onCameraInitializedCallback != null)
            onCameraInitializedCallback.cameraViewInitialized();

        return initializeCameraResult;


    }

//    public void takePicture() {
//        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
//        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
//        mCamera.setPreviewCallback(null);
//
//        // PictureCallback is implemented by the current class
//        mCamera.takePicture(null, null, this);
//    }


    /**
     * This method sets the OnCameraInitializedCallback to call
     * when the initialization of the camera has finished.
     * <p>
     * If the camera is initialized already, the callback is called instantly!
     *
     * @param callback the callback to call when the initialization of the camera has finished.
     */
    public void setOnCameraInitializedCallback(OnCameraInitializedCallback callback) {
        if (mCamera != null)
            callback.cameraViewInitialized();
        else
            onCameraInitializedCallback = callback;
    }

    /**
     * Sets the zoom level of the camera preview.
     *
     * @param zoomLevel the new zoom level
     */
    public void setZoomLevel(int zoomLevel) {
        Camera.Parameters parameters = getCameraParameters();

        parameters.setZoom(zoomLevel);

        setCameraParameters(parameters);
    }

    /**
     * Returns whether the camera supports zooming.
     *
     * @return true if the camera supports zooming, false otherwise.
     */
    public boolean isZoomSupported() {
        return getCameraParameters().isZoomSupported();
    }

    /**
     * Returns whether the camera supports to enable the flash in torch mode.
     *
     * @return true if the camera supports to enable the flash in torch mode, false otherwise.
     */
    public boolean isFlashSupported() {
        List<String> flashModes = getCameraParameters().getSupportedFlashModes();

        return flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH);
    }

    /**
     * Returns the maximum zoom level of the camera.
     *
     * @return the maximum zoom level of the camera.
     */
    public int getMaxZoom() {
        return getCameraParameters().getMaxZoom();
    }

    /**
     * Sets the state of the flash (enables/disables the flash)
     *
     * @param flashEnabled the new state of the flashlight.
     */
    public void setFlashState(boolean flashEnabled) {
        if (flashEnabled) {
            enableFlash();
        } else {
            disableFlash();
        }
    }

    /**
     * Enables the torch flash mode of the camera.
     */
    public void enableFlash() {
        Camera.Parameters parameters = getCameraParameters();

        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

        setCameraParameters(parameters);
    }

    /**
     * Enables the flash of the camera.
     */
    public void disableFlash() {
        Camera.Parameters parameters = getCameraParameters();

        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        setCameraParameters(parameters);
    }

    /**
     * Returns the parameters of the camera.
     *
     * @return the parameters of the camera
     */
    private Camera.Parameters getCameraParameters() {
        return mCamera.getParameters();
    }

    /**
     * Sets the parameters of the camera.
     *
     * @param parameters The new camera parameters to set.
     */
    private void setCameraParameters(Camera.Parameters parameters) {
        mCamera.setParameters(parameters);
    }

//    /**
//     * Called after a picture is taken.
//     * See Android documentation.
//     *
//     * Currently not used.
//     */
//    @Override
//    public void onPictureTaken(byte[] data, Camera camera) {
//        // The camera preview was automatically stopped. Start it again.
//        mCamera.startPreview();
//        mCamera.setPreviewCallback(this);
//
//        // Write the image in a file (in jpeg format)
//        try {
//            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "resistorDetector");
//            boolean isPresent = true;
//            if (!folder.exists()) {
//                isPresent = folder.mkdir();
//            }
//
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//
//            int bitmapWidth = bitmap.getWidth();
//            int bitmapHeight = bitmap.getHeight();
//
//            int width = (int) (bitmapWidth * 0.1);
//            int height = (int) (bitmapHeight * 0.1);
//
//            int x = bitmapWidth / 2 - width;
//            int y = bitmapHeight / 2 - height;
//
//            if (isPresent) {
//                File file = new File(folder, UUID.randomUUID().toString() + ".png");
//                FileOutputStream out = new FileOutputStream(file);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//                out.close();
//
//                Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (java.io.IOException e) {
//            Log.e("PictureDemo", "Exception in photoCallback", e);
//        }
//    }
}
