# Android App to Detect Resistor Values

The goal of this project was to determine the resistance of a resistor by taking a picture of the resistor with a mobile phone and analyze the image to extract the color information of the bands. The open source image processing library OpenCV is used to detect the color of the resistor bands and analyzes the image to calculate the resistance.

**Requirements:**
- Android Studio, version 2.3.2 
- Android SDK, platform and tool version 25 
- OpenCV, version 3.2.0


## Android App
The android app contains the following packages:
- ***.resistordetector.ui**:
This package contains the classes for the Android UI. This includes Activities (the MainActivity, the SettingsActivity and the DetectionDetailsActivity), Adapters and CameraViews (subclasses from OpenCV classes). The package also includes the classes that provide the camera image for OpenCV.
- ***.resistordetector.detection:**:
This package includes the classes for the Detection Algorithms. This includes one class for each of the four detection methods and some smaller helper classes for image, matrices, and color processing.

![Android App](/doc/Android_App_2_Detected_framed.png)


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

![Desktop Application](/doc/DesktopApp_small.PNG)
