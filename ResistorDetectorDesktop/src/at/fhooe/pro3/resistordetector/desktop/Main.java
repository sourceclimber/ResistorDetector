package at.fhooe.pro3.resistordetector.desktop;

import at.fhooe.pro3.resistordetector.desktop.ui.RDTestWindow;
import org.opencv.core.Core;

/**
 * Test Application to test different resistor detection methods and techniques.
 * <p>
 * Created by stefan on 05.06.2017.
 */
public class Main {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    /**
     * Creates a new ResistorDetectionTestWindow
     * and starts the detection process.
     *
     * @param args
     */
    public static void main(String[] args) {
        RDTestWindow window = new RDTestWindow();

        window.setSize(1800, 600);
        window.setVisible(true);
    }
}
