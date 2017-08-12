package at.fhooe.pro3.resistordetector.desktop.ui;

import at.fhooe.pro3.resistordetector.desktop.detection.*;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * This class displays a JFrame, loads resistor images, performs the detection on these images
 * and displays the detection steps and the result in the frame.
 * <p>
 * Resistor images are loaded from the 'resistorImages' directory.
 * <p>
 * Created by stefan on 05.06.2017.
 */
public class RDTestWindow extends JFrame {

    /**
     * This queue contains the unprocessed images of resistors.
     */
    private Queue<BufferedImage> imageQueue = new ArrayDeque<BufferedImage>();

    /**
     * This list contains the results of the already performed detection processes.
     */
    private ArrayList<DetectionResult> detectionResults = new ArrayList<DetectionResult>();

    /**
     * The ResistorDetector to use for the processing.
     */
    private ResistorDetector resistorDetector = new ColumnsResistorDetector(new ResultListener());

    /**
     * Creates a new JFrame, loads the resistor images and starts the detection process.
     */
    public RDTestWindow() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        resistorDetector.setNumberOfBands(ResistorDetector.NumberOfBands.Four);

        loadImages();

        processNextImageInQueue();
    }

    /**
     * Result listener for the ResistorDetector.
     * <p>
     * Adds the DetectionResult to a list and starts a new detection process if more
     * resistor images are available.
     * Displays the Results of all detection processes if no more images are available.
     */
    private class ResultListener implements ResistorDetector.ResultListener {

        @Override
        public void resultReady(DetectionResult detectionResult) {

            detectionResults.add(detectionResult);

            if (imageQueue.size() > 0) {
                processNextImageInQueue();
            } else {
                displayResults();
                //saveImages();
            }

        }
    }

    /**
     * Starts the detection on one image of the queue.
     */
    private void processNextImageInQueue() {
        if (imageQueue.size() > 0) {
            BufferedImage image = imageQueue.poll();
            Mat imageMat = BufferedImageConversions.bufferedImageToMatBgr(image);

            resistorDetector.detectResistorValue(imageMat);
        }
    }

    /**
     * Displays the result of all detection processes.
     */
    private void displayResults() {
        if (detectionResults.size() > 0) {
            ScrollPane scrollPane = new ScrollPane();


            Panel scrollContent = new Panel();

            GridLayout layout = new GridLayout(detectionResults.size() + 1, detectionResults.get(0).getDetectionStepDetails().size());
            layout.setHgap(5);
            layout.setVgap(10);
            scrollContent.setLayout(layout);

            for (DetectionStepDetail detail : detectionResults.get(0).getDetectionStepDetails()) {
                if (detail.isDescriptionAvailable())
                    scrollContent.add(new JLabel(detail.getDescription()));
                else
                    scrollContent.add(new JLabel());
            }

            scrollContent.add(new JLabel("resistance value"));

            for (DetectionResult result : detectionResults) {

                if (result.detectionStepDetailsAvailable()) {
                    for (DetectionStepDetail detail : result.getDetectionStepDetails()) {
                        if (detail.isImageAvailable())
                            scrollContent.add(new JLabel(new ImageIcon(detail.getImage())));
                        else {
                            JLabel a = new JLabel(detail.getDescription());
                            scrollContent.add(a);
                        }
                    }
                }

                scrollContent.add(new Label(result.getResistorValue() + ""));
            }

            scrollPane.add(scrollContent);
            add(scrollPane);
        }
    }

    /**
     * Saves the images of the detectionStepDetails of every DetectionResult to a file.
     * The images are saved as *.png files into the 'detectionStepImages' folder.
     */
    private void saveImages(){
        File folder = new File("detectionStepImages");

        if(folder.isDirectory() && folder.exists())
            folder.delete();

        folder.mkdirs();

        int i = 0;
        for (DetectionResult result : detectionResults) {

            String fileName = "detectionResult_" + i++;

            if (result.detectionStepDetailsAvailable()) {

                int j = 0;
                for (DetectionStepDetail detail : result.getDetectionStepDetails()) {
                    if (detail.isImageAvailable()){
                        String tFilename = fileName + "_" + j++ + "_" + detail.getDescription().replace(' ', '_') + ".png";

                        try {
                            ImageIO.write(detail.getImage(), "png", new File(folder, tFilename));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Loads resistor images from the 'resistorImages' directory and adds them to a queue.
     */
    private void loadImages() {
        File folder = new File("resistorImages");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".png")) {

                try {
                    BufferedImage image = ImageIO.read(new File(listOfFiles[i].getAbsolutePath()));

                    BufferedImage imageBgr = BufferedImageConversions.toBufferedImageOfType(image, BufferedImage.TYPE_3BYTE_BGR);

                    imageQueue.add(imageBgr);

                    System.out.println("Loaded image " + listOfFiles[i].getName());

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
