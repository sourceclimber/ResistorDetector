# Android App to Detect Resistor Values

The goal of this project was to determine the resistance of a resistor by taking a picture of the resistor with a mobile phone and analyze the image to extract the color information of the bands. The open source image processing library OpenCV is used to detect the color of the resistor bands and analyzes the image to calculate the resistance.

**Requirements:**
- Android Studio, version 2.3.2 
- Android SDK, platform and tool version 25 
- OpenCV, version 3.2.0

### Detection Steps

1. The taken image (only the content of the indicator rectangle) is passed as matrix to the resistor detector.
1. The bilateral filter is used to reduce the noise in the image. This filter keeps the edges sharp, compared to other algorithms.
1. The brightest spots in the images are masked. The brightest spots are most likely the reflections. The mask marks all bright spots with white pixels.
1. The background of the image is masked. This is done in two steps (top and bottom background) by masking all pixels with approximately the same color as the very first and very last pixel row in the image. The mask marks the background with white pixels.
1. The resistor is masked by combining the background and reflection mask and negating it. The mask marks the resistor with white pixels. Only the pixels of the original image where the corresponding pixels in this mask are white are used for all further calculations.
1. The median HSV color of each column (one column can be defined with e.g. 5px with) is calculated. The masked-out areas are not used for the calculation. The resulting image is only one pixel high, but stretched for demonstration purposes.
1. Every column is associated with a specific name of the color. This is done by comparing the median color of each column with a table of minimum and maximum values for every defined color.
1. The column color names are converted to BandInfo objects. Two small columns are ignored. The resulting BandInfo for each column contains the color and the width of the band.
1. Each color has an associated value. The first two (or three) represent the numbers 0 to 9, while the last one (the fourth or fifth) represents the multiplier. 


## Android App
The android app contains the following packages:
- ***.resistordetector.ui**:
This package contains the classes for the Android UI. This includes Activities (the MainActivity, the SettingsActivity and the DetectionDetailsActivity), Adapters and CameraViews (subclasses from OpenCV classes). The package also includes the classes that provide the camera image for OpenCV.
- ***.resistordetector.detection:**:
This package includes the classes for the Detection Algorithms. This includes one class for each of the four detection methods and some smaller helper classes for image, matrices, and color processing.

### Screenshots Android App
<img src="/doc/Android_App.jpg" width="60%">

## Desktop Application
The desktop application is used to develop, test, and experiment different methods and algorithms to detect the resistance value with a picture of a resistor.
The application loads test images from the file system and executes the image processing and resistor detection method on those images. The individual steps of the detection process as well as the final result (the resistance value) is shown for each loaded image.
The resistor images are loaded from the directory ‘resistorImages’ (in the same directory as the application itself) when the program is started.

Each row shows the details of the detection process for one resistor image. The first row shows the name of the detection step for each column.Each column shows one detection step for all loaded images. The last column shows the result of the detection process.

The Desktop app has a similar structure to the Android App:
- ***.resistordetector.desktop.ui:**
This package contains the classes for the Desktop UI. The package on the desktop only includes one class, a subclass displaying an Java AWT Frame. The package also contains methods to import resistor images from the file system and to save detection step details as images.
- ***.resistordetector.desktop.detection:**
This package includes almost the same classes for the Detection Algorithms like the android app. This includes one class for each of the four detection methods and some smaller helper classes for image, matrices, and color processing.

Both *.detection* packages, the one for the Android App and the one for the desktop, contain almost the same classes and can be interchanged. A small difference is in the handling of images, where Java on the desktop uses the BufferedImage class and Android uses the Bitmap class. When copying the detection code from the desktop to the android project, this classes must be replaced.

### Screenshot Test Application (Desktop)
![Desktop Application](/doc/Desktop_Application.jpg)
