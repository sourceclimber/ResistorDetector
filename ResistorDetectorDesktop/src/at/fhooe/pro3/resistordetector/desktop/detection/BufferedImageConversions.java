package at.fhooe.pro3.resistordetector.desktop.detection;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * This class contains methods to convert from OpenCV Mat to BufferedImage,
 * as well as methods to convert from BufferedImage to Mat,
 * and methods to convert a BufferedImage to different types.
 * <p>
 * Created by stefan on 05.06.2017.
 */
public class BufferedImageConversions {

    /**
     * Converts a BufferedImage object to a new BufferedImage with the specified type.
     * The method returns a new object.
     * If the given BufferedImage has already the specified type, the same object is returned.
     * <p>
     * See https://stackoverflow.com/questions/21740729/converting-bufferedimage-to-mat-opencv-in-java/21795573#21795573
     *
     * @param original The BufferedImage to convert to a new BufferedImage with the given Type
     * @param type     The Type the image should be converted to.
     * @return A new BufferedImage object (a copy of the given image) with the given type.
     */
    public static BufferedImage toBufferedImageOfType(BufferedImage original, int type) {
        if (original == null) {
            throw new IllegalArgumentException("original == null");
        }

        // Don't convert if it already has correct type
        if (original.getType() == type) {
            return original;
        }

        // Create a buffered image
        BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), type);

        // Draw the image onto the new buffer
        Graphics2D g = image.createGraphics();
        try {
            g.setComposite(AlphaComposite.Src);
            g.drawImage(original, 0, 0, null);
        } finally {
            g.dispose();
        }

        return image;
    }

    /**
     * Creates a new OpenCv Matrix from an BufferImage with BGR color type.
     * <p>
     * Returns a new Matrix with height and width of the give image and type CvType.CV_8UC3.
     *
     * @param bufferedImageBgr The image to convert to a Matrix, must have BGR color type.
     * @return A new Matrix with type CV_8UC3, with BGR colors and with the data of the given image
     */
    public static Mat bufferedImageToMatBgr(BufferedImage bufferedImageBgr) {
        if (bufferedImageBgr == null)
            throw new IllegalArgumentException("bufferedImageBgr is null!");
        if (bufferedImageBgr.getType() != BufferedImage.TYPE_3BYTE_BGR)
            throw new IllegalArgumentException("bufferedImageBgr must have the type BufferedImage.TYPE_3BYTE_BGR");

        Mat mat = new Mat(bufferedImageBgr.getHeight(), bufferedImageBgr.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) bufferedImageBgr.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);

        return mat;
    }

    /**
     * Creates a new BufferedImage from an OpenCV Matrix object.
     * The given Matrix must use BGR or gray colors.
     * <p>
     * Returns a new BufferedImage with the width and height of the Matrix.
     * The type of the image is either TYPE_BYTE_GRAY if the matrix has one channel
     * or TYPE_3BYTE_BGR if the matrix has three channels.
     *
     * @param matBgrOrGray The Matrix to convert to a BufferedImage, with BGR or gray colors
     * @return A new BufferedImage with the data of the given Matrix, with type TYPE_BYTE_GRAY or TYPE_3BYTE_BGR.
     */
    public static BufferedImage matToBufferedImage(Mat matBgrOrGray) {
        if (matBgrOrGray == null)
            throw new IllegalArgumentException("matBgrOrGray is null!");

        int type = 0;

        if (matBgrOrGray.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (matBgrOrGray.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(matBgrOrGray.width(), matBgrOrGray.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        matBgrOrGray.get(0, 0, data);

        return image;
    }

    /**
     * Creates a new BufferedImage from an OpenCV Matrix object.
     * The given Matrix must use HSV colors.
     * <p>
     * Returns a new BufferedImage with the width and height of the Matrix.
     * The type of the image is TYPE_3BYTE_BGR.
     *
     * @param matHsv The Matrix to convert to a BufferedImage, with HSV colors
     * @return A new BufferedImage with the data of the given Matrix, with type TYPE_3BYTE_BGR.
     */
    public static BufferedImage matToBufferedImageHsv(Mat matHsv) {
        if (matHsv == null)
            throw new IllegalArgumentException("matHsv is null!");
        if (matHsv.channels() != 3)
            throw new IllegalArgumentException("matRgba must have 3 channels!");

        BufferedImage bitmap = null;

        Imgproc.cvtColor(matHsv, matHsv, Imgproc.COLOR_HSV2BGR);
        bitmap = matToBufferedImage(matHsv);
        Imgproc.cvtColor(matHsv, matHsv, Imgproc.COLOR_BGR2HSV);

        return bitmap;
    }

    /**
     * Creates a new BufferedImage from an OpenCV Matrix object.
     * The given Matrix must use RGBA colors.
     * <p>
     * Returns a new BufferedImage with the width and height of the Matrix.
     * The type of the image is TYPE_3BYTE_BGR.
     *
     * @param matRgba The Matrix to convert to a BufferedImage, with RGBA colors
     * @return A new BufferedImage with the data of the given Matrix, with type TYPE_3BYTE_BGR.
     */
    public static BufferedImage matToBufferedImageRgba(Mat matRgba) {
        if (matRgba == null)
            throw new IllegalArgumentException("matRgba is null!");
        if (matRgba.channels() != 4)
            throw new IllegalArgumentException("matRgba must have 4 channels!");

        BufferedImage bitmap = null;

        Imgproc.cvtColor(matRgba, matRgba, Imgproc.COLOR_RGBA2BGR);
        bitmap = matToBufferedImage(matRgba);
        Imgproc.cvtColor(matRgba, matRgba, Imgproc.COLOR_BGR2RGBA);

        return bitmap;
    }
}
