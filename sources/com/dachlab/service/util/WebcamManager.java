package com.dachlab.service.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.face.FaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachlab.model.User;
import com.dachlab.properties.IWebcamProperties;
import com.dachlab.service.IUserSevice;

@Component("webcamManager")
public class WebcamManager {

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer(1, 8, 8, 8, 130);

	private boolean trained = false;

	private VideoCapture camera;

	private WebCamCapturer webcamCapturer;

	@Autowired
	private IUserSevice userService;

	private User authenticatedUSer;

	@Autowired
	private IWebcamProperties webcamProperties;

	/**
	 * Get an image from the webcam.
	 * 
	 * @return the image captured.
	 */
	public Mat getImagefromWebcam() {
		VideoCapture camera = null;
		try {
			camera = new VideoCapture(0);
			Mat frame = new Mat();
			camera.read(frame);
			return frame;
		} catch (Exception e) {
			return null;
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	/**
	 * Return a gray image from the webcam.
	 * 
	 * @return the image captured.
	 */
	public Mat getGrayImageFromWebcam() {
		return toGray(getImagefromWebcam());
	}

	/**
	 * Start capturing from the webcam using the default captured image handler.
	 * 
	 * @return true if succeeded.
	 */
	public boolean startCapture() {
		return startCapture(getDefaultCapturedImageHandler());
	}

	/**
	 * Start capturing from the webcam.
	 * 
	 * @return true if succeeded.
	 */
	public boolean startCapture(ICapturedImageHandler capturedImageHandler) {
		log.debug("Starting capture.");
		if (camera != null) {
			camera.release();
		}
		camera = new VideoCapture(0);
		if (!camera.isOpened()) {
			log.error("Unable to start the camera.");
			return false;
		}
		webcamCapturer = new WebCamCapturer(camera, capturedImageHandler);
		webcamCapturer.start();
		log.debug("Capture started.");
		return true;
	}

	/**
	 * Stop the image capture.
	 * 
	 * @return true if succeeded.
	 */
	public boolean stopCapture() {
		webcamCapturer.terminate();
		while (webcamCapturer.isAlive()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				log.error("Unable to monitor the termination of the webcam capturer thread. Unable to determine if it actually stopped.", e);
				break;
			}
		}
		log.debug("Releasing camera.");
		camera.release();
		log.debug("Released.");
		return true;
	}

	public ICapturedImageHandler getDefaultCapturedImageHandler() {
		return new FaceRecognitionCapturedImageHandler(this);
	}

	/**
	 * Save the image to disk.
	 * 
	 * @param image
	 *            the image to save.
	 * @return true if succeed.
	 */
	public boolean saveImage(final Mat image) {
		return saveImage(image, webcamProperties.getPath()) == null ? false : true;
	}

	/**
	 * Save the image to a specified folder.
	 * 
	 * @param image
	 *            the image to save.
	 * @param path
	 *            the path to save the image to.
	 * @return true if succeed.
	 */
	public String saveImage(final Mat image, final String path) {
		final String imageName = path + UUID.randomUUID() + ".png";
		try {
			if (Imgcodecs.imwrite(imageName, image)) {
				return imageName;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("Unable to save image " + image + ".", e);
			return null;
		}
	}

	/**
	 * Get the coordinates of the faces found in the image.
	 * 
	 * @param image
	 *            The image where to look for the faces.
	 * @return The coordinates of the faces found.
	 */
	private MatOfRect getFacesCoordinates(final Mat image) {
		final String classifierName = webcamProperties.getFaceDetectionClassifierName();
		try {

			MatOfRect faceDetections = new MatOfRect();
			CascadeClassifier faceDetector = new CascadeClassifier(classifierName);

			faceDetector.detectMultiScale(image, faceDetections);
			return faceDetections;
		} catch (Exception e) {
			log.error("Unabel to retrieve the face(s) from the image. Classifier file is " + classifierName + ".", e);
			return null;
		}
	}

	/**
	 * Get all the faces found in the image.
	 * 
	 * @param image
	 *            the image where to look for the faces.
	 * @return A list of images of the faces found.
	 */
	public List<Mat> getFaces(final Mat image) {
		final MatOfRect faces = getFacesCoordinates(image);
		final List<Mat> faceImages = new ArrayList<Mat>();
		if (faces != null) {
			for (Rect rect : faces.toArray()) {
				faceImages.add(new Mat(image, rect));
			}
		}
		return faceImages;
	}

	/**
	 * Get all the faces found in the image.
	 * 
	 * @param image
	 *            the image where to look for the faces.
	 * @param faces
	 *            coordinates of the faces on the image.
	 * @return A list of images of the faces found.
	 */
	public List<Mat> getFaces(final Mat image, final MatOfRect faces) {
		final List<Mat> faceImages = new ArrayList<Mat>();
		if (faces != null) {
			for (Rect rect : faces.toArray()) {
				faceImages.add(new Mat(image, rect));
			}
		}
		return faceImages;
	}

	/**
	 * Save all images to disk.
	 * 
	 * @param images
	 *            images to save.
	 */
	public boolean saveImagesToDisk(List<Mat> images) {

		try {

			for (Mat image : images) {
				saveImage(image);
			}
			return true;
		} catch (Exception e) {
			log.error("Failed to save all the images (" + images.size() + ") to disk.", e);
			return false;
		}
	}

	public boolean learnFaces() {
		// System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		log.debug("Starting to learn faces from" + webcamProperties.getFacesPath() + ".");
		File[] faces;
		File dir;
		int counter = 0;
		final List<Mat> images = new ArrayList<Mat>();
		List<java.lang.Integer> labels = new ArrayList<java.lang.Integer>();

		// Images filter.
		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".jpg");
			}
		};

		try {
			final File facesPath = new File(webcamProperties.getFacesPath());
			String[] facesDirectories = facesPath.list();

			// Iterate over the faces directories.
			for (String directory : facesDirectories) {
				directory = facesPath.getAbsolutePath().concat("/").concat(directory);
				log.debug("Learning faces from " + directory);
				dir = new File(directory);
				if (!dir.isDirectory()) {
					continue;
				}
				faces = dir.listFiles(fileFilter);
				// Store the corresponding name.
				faceRecognizer.setLabelInfo(counter, dir.getName());
				// Iterate over the face images in these directories.
				for (File faceImage : faces) {
					log.debug("Learning face " + faceImage.getAbsolutePath() + ".");

					// Get image and label:
					final Mat img = Imgcodecs.imread(faceImage.getAbsolutePath(), 0);
					images.add(img);
					labels.add(counter);

				}
				counter++;
			}
			faceRecognizer.train(images, Converters.vector_int_to_Mat(labels));
			trained = true;
			return true;
		} catch (Exception e) {
			log.error("Failed to learn the faces.", e);
			return false;
		}
	}

	/**
	 * Capture the first face found and save in the appropriate folder (name).
	 * 
	 * @param name
	 *            Name for the associated face.
	 * @return true if managed to do so.
	 */
	public boolean saveFaces(String name, List<Mat> faces) {

		if (faces.size() > 0) {
			return saveFace(name, faces.get(0));
		} else {
			log.info("No face found. Nothing to save to folder " + name + ".");
			return false;
		}
	}

	/**
	 * Capture a face and save in the appropriate folder (name).
	 * 
	 * @param name
	 *            Name for the associated face.
	 * @return true if managed to do so.
	 */
	public boolean saveFace(String name, Mat face) {
		// Images filter.
		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains(name);
			}
		};

		try {
			final File facesPath = new File(webcamProperties.getFacesPath());
			final File[] facesDirectories = facesPath.listFiles(fileFilter);

			Mat resizedFace = new Mat();
			Imgproc.resize(face, resizedFace, new Size(100, 100));
			String fileName = UUID.randomUUID() + ".png";
			if (facesDirectories.length > 0) {
				final String imageName = facesDirectories[0].getAbsolutePath() + "/" + fileName;
				Imgcodecs.imwrite(imageName, resizedFace);
			} else {
				final File newDirectory = new File(facesPath.getAbsolutePath() + "/" + name);
				final String imageName = newDirectory.getAbsolutePath() + "/" + fileName;
				newDirectory.mkdir();
				Imgcodecs.imwrite(imageName, resizedFace);
			}

			return true;
		} catch (Exception e) {
			log.error("Unable to save a face related to the name " + name + ".", e);
			return false;
		}
	}

	/**
	 * Predict the name related to the face captured.
	 * 
	 * @return the name found.
	 */
	public List<User> predictFace() {
		return predictFace(getImagefromWebcam());
	}

	/**
	 * Predict a name for each samples in the faces path.
	 * 
	 * @return the corresponding names.
	 */
	public List<User> predictFacesFromSample() {
		final File facesPath = new File(webcamProperties.getFacesPath());
		File imageFile;
		String[] facesDirectories = facesPath.list();
		List<User> users = null;

		// Iterate over the faces files.
		for (String file : facesDirectories) {
			file = facesPath.getAbsolutePath().concat("/").concat(file);
			imageFile = new File(file);
			if (imageFile.isDirectory()) {
				continue;
			}
			Mat img = Imgcodecs.imread(file, 0);

			users = predictFace(img);
			log.info(imageFile.getName() + " -> " + Arrays.toString(users.toArray(new User[users.size()])));
		}
		return users;
	}

	/**
	 * Predict the name related to the face given.
	 * 
	 * @param image
	 *            image to predict the face from.
	 * @return the name found.
	 */
	public List<User> predictFace(final Mat image) {
		ArrayList<User> users = new ArrayList<>();
		User userFound;
		double d;
		int[] id = { -1 };
		double[] dist = { -1 };
		int counter = 0;
		String nameFound = "";
		if (!trained) {
			learnFaces();
		}
		Mat grayImage = toGray(image);
		// Get the faces.
		final MatOfRect facesCoordinates = getFacesCoordinates(grayImage);
		Rect[] facesArray = facesCoordinates.toArray();
		final List<Mat> faces = getFaces(grayImage, facesCoordinates);
		for (Mat face : faces) {
			// Predict.
			faceRecognizer.predict(face, id, dist);
			if (id[0] == -1) {
				nameFound = "Unknown";
			} else {
				nameFound = faceRecognizer.getLabelInfo(id[0]);
				// Add the face to the collection related to the name.
				saveFace(nameFound, face);
			}
			d = ((int) (dist[0] * 100));
			userFound = userService.getByUserName(nameFound);
			if (userFound != null && userFound.getUserId() != 0) {
				userFound.setUserPassword("*****");
				users.add(userFound);
			} else {
				users.add(new User(0, nameFound, "", ""));
			}
			log.debug("Found face " + nameFound + " (" + d / 100 + ").");

			// Save the face and draw on source image.
			if (!webcamProperties.getPredictedFacesPath().equals("")) {
				Imgproc.resize(face, face, new Size(100, 100));
				saveImage(face, webcamProperties.getPredictedFacesPath());
			}
			Imgproc.rectangle(image, facesArray[counter].tl(), facesArray[counter].br(), new Scalar(255, 255, 255), 2);
			Imgproc.putText(image, nameFound, new Point(facesArray[counter].x, facesArray[counter].y + facesArray[counter].height + 20), Core.FONT_HERSHEY_PLAIN, 1.3, new Scalar(255, 255, 255), 2);
			counter++;
		}
		if (!webcamProperties.getPredictedImagesPath().equals("")) {
			saveImage(image, webcamProperties.getPredictedImagesPath());
		}
		return users;
	}

	/**
	 * Get the corresponding gray scaled image.
	 * 
	 * @param image
	 *            the original image.
	 * @return the grayed image.
	 */
	public Mat toGray(Mat image) {
		Mat grayImage = image;
		if (image.channels() > 1) {
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);
		}
		return grayImage;
	}

	/**
	 * Authenticate a user using face recognition.
	 * 
	 * @return the authenticated user, null otherwise .
	 */
	public User authenticate() {
		log.info("Starting authentication using face recognition.");
		if (!trained) {
			learnFaces();
		}
		startCapture();
		setAuthenticatedUSer(null);
		int loopCounter = webcamProperties.getLoopsForFaceRecognitionAuthentication();
		while (loopCounter-- > 0) {
			if (getAuthenticatedUSer() == null) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					log.error("Unable to sleep.", e);
				}
			} else {
				break;
			}
		}
		stopCapture();
		if (getAuthenticatedUSer() != null) {
			log.info("User " + getAuthenticatedUSer() + " authenticated.");
			return getAuthenticatedUSer();
		} else {
			log.info("Authenticated failed.");
			return new User();
		}
	}

	/**
	 * Get the properties accessor.
	 * 
	 * @return the webcam properties accessor.
	 */
	public IWebcamProperties getWebcamProperties() {
		return webcamProperties;
	}

	/**
	 * Get the authenticated user
	 * 
	 * @return the authenticated user.
	 */
	public User getAuthenticatedUSer() {
		return authenticatedUSer;
	}

	/**
	 * Set the authenticated user
	 * 
	 * @param authenticatedUSer
	 *            the authenticated user.
	 */
	public void setAuthenticatedUSer(User authenticatedUSer) {
		if (authenticatedUSer != null) {
			authenticatedUSer.setUserPassword("*****");
		}
		this.authenticatedUSer = authenticatedUSer;
	}

	/**
	 * Get the userService.
	 * 
	 * @return the userService.
	 */
	public IUserSevice getUserService() {
		return userService;
	}

}