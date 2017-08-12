package at.fhooe.pro3.resistordetector.detection;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * This Class contains method to convert Matrices between different Color Types.
 * Each methods returns a copy of the original matrix.
 * <p>
 * Created by stefan on 24.06.2017.
 */
public class MatColorConversions {

    /**
     * Returns a new matrix with the same size as the given matrix and with color type BGR.
     * The given matrix must contain HSV colors.
     *
     * @param hsvMat the matrix that should be converted, contains HSV colors.
     * @return a new matrix with the content of the given matrix but with BGR colors.
     */
    public static Mat newBgrMatFromHsv(Mat hsvMat) {
        if (hsvMat == null)
            throw new IllegalArgumentException("hsvMat must not be null!");

        Mat bgrMat = new Mat(hsvMat.rows(), hsvMat.cols(), hsvMat.type());

        Imgproc.cvtColor(hsvMat, bgrMat, Imgproc.COLOR_HSV2BGR);

        return bgrMat;
    }

    /**
     * Returns a new matrix with the same size as the given matrix and with color type BGR.
     * The given matrix must contain Gray colors.
     *
     * @param grayMat the matrix that should be converted, contains Gray colors.
     * @return a new matrix with the content of the given matrix but with BGR colors.
     */
    public static Mat newBgrMatFromGray(Mat grayMat) {
        if (grayMat == null)
            throw new IllegalArgumentException("grayMat must not be null!");

        Mat bgrMat = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());

        Imgproc.cvtColor(grayMat, bgrMat, Imgproc.COLOR_GRAY2BGR);

        return bgrMat;
    }
}
