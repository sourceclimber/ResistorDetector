package at.fhooe.pro3.resistordetector.detection;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * This class contains methods to convert OpenCV matrices to Bitmaps.
 * <p>
 * Created by stefan on 17.06.2017.
 */
class MatToBitmap {

    /**
     * Creates a new Bitmap from an OpenCV Matrix object.
     * The given Matrix must use HSV colors.
     * <p>
     * Returns a new Bitmap with the width and height of the Matrix.
     *
     * @param image The Matrix to convert to a Bitmap, with HSV colors
     * @return A new BufferedImage with the data of the given Matrix.
     */
    public static Bitmap matToBitmapHsv(Mat image) {

        Bitmap bitmap = null;

        Imgproc.cvtColor(image, image, Imgproc.COLOR_HSV2BGR);

        bitmap = matToBitmapBgr(image);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);

        return bitmap;
    }

    /**
     * Creates a new Bitmap from an OpenCV Matrix object.
     * The given Matrix must use BGR colors.
     * <p>
     * Returns a new Bitmap with the width and height of the Matrix.
     *
     * @param image The Matrix to convert to a Bitmap, with BGR colors
     * @return A new BufferedImage with the data of the given Matrix.
     */
    public static Bitmap matToBitmapBgr(Mat image) {

        Bitmap bitmap = null;

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGBA);

        bitmap = matToBitmapRgba(image);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGBA2BGR);

        return bitmap;
    }

    /**
     * Creates a new Bitmap from an OpenCV Matrix object.
     * The given Matrix must use RGBA colors.
     * <p>
     * Returns a new Bitmap with the width and height of the Matrix.
     *
     * @param image The Matrix to convert to a Bitmap, with RGBA colors
     * @return A new BufferedImage with the data of the given Matrix.
     */
    public static Bitmap matToBitmapRgba(Mat image) {

        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(image, bitmap);
        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }

        return bitmap;
    }
}
