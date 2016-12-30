package com.dachlab.service.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
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
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dachlab.google.IGoogleDriveService;
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

	// OpenCV needs to be trained before being able to recognize faces.
	private boolean trained = false;

	private VideoCapture camera;

	private WebCamCapturer webcamCapturer;

	@Autowired
	private IUserSevice userService;

	private User authenticatedUSer;

	private List<Mat> capturedFramesQueue = new ArrayList<Mat>();

	private boolean recording = false;
	private boolean capturing = false;

	@Autowired
	protected IWebcamProperties webcamProperties;

	@Autowired
	private IGoogleDriveService googleDriveServices;

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
	 * Capture the video for the defined duration and save it into a file.
	 * 
	 * @param duration
	 *            duration of the video in seconds.
	 * @return true if succeeded.
	 */
	public boolean captureMotion(final int duration) {
		startCapture(new StoreImageHandler(this));
		// TODO FIXME Video files produced are empty. File seems to not be empty
		// if no sleep...
		try {
			Thread.sleep(duration * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopCaptureMotion();

		return true;
	}

	/**
	 * Start capturing video frames.
	 * 
	 * @return true if succeeded.
	 */
	public boolean startCaptureMotion() {
		return startCapture(new StoreImageHandler(this));
	}

	/**
	 * Stop capturing video frames.
	 * 
	 * @return true if succeeded.
	 */
	public boolean stopCaptureMotion() {
		try {
			stopCapture();
			writeRecordedVideo();
			return true;
		} catch (Exception e) {
			log.error("Unable to complete the stop capture operation.", e);
			return false;
		}
	}

	/**
	 * Start capturing from the webcam.
	 * 
	 * @return true if succeeded.
	 */
	public boolean startCapture(final ICapturedImageHandler capturedImageHandler) {
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
		capturing = true;
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
		try {
			webcamCapturer.join(1000);
		} catch (InterruptedException e) {
			log.error("Error while attempting to terminate the Webcapturer", e);
		}
		log.debug("Capture stopped. Releasing camera.");
		camera.release();
		capturing = false;
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
			return getObjectCoordinates(image, classifierName);
		} catch (Exception e) {
			log.error("Unabel to retrieve the face(s) from the image. Classifier file is " + classifierName + ".", e);
			return null;
		}
	}

	/**
	 * Get the coordinates of the bodies found in the image.
	 * 
	 * @param image
	 *            The image where to look for the bodies.
	 * @return The coordinates of the bodies found.
	 */
	protected MatOfRect getBodyCoordinates(final Mat image) {
		final String classifierName = webcamProperties.getBodyDetectionClassifierName();
		try {
			return getObjectCoordinates(image, classifierName);
		} catch (Exception e) {
			log.error("Unabel to retrieve the body(ies) from the image. Classifier file is " + classifierName + ".", e);
			return null;
		}
	}

	/**
	 * Get the coordinates of the objects found in the image.
	 * 
	 * @param image
	 *            the image to analyze.
	 * @param classifierName
	 *            the classifier finle path that will represent the object to
	 *            find in the image.
	 * @return The coordinates of all the objects found in the image.
	 * @throws Exception
	 */
	private MatOfRect getObjectCoordinates(final Mat image, String classifierName) throws Exception {
		MatOfRect objectCoordinates = new MatOfRect();
		CascadeClassifier objectDetector = new CascadeClassifier(classifierName);
		objectDetector.detectMultiScale(image, objectCoordinates);
		return objectCoordinates;
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
		return predictFaces(getImagefromWebcam());
	}

	/**
	 * Predict the name related to the face captured.
	 * 
	 * @return the name found.
	 */
	public List<User> predictFace(byte[] image) {
		// TODO - FIX - Transform the byte[] into a Mat.
		return predictFaces(Imgcodecs.imdecode(new MatOfByte(image), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED));
	}

	/**
	 * Write in a file the video composed by the list of images provided.
	 * 
	 * @param images
	 *            images that will compose the video;
	 */
	public synchronized void writeRecordedVideo(List<Mat> images) {
		final String videoFileName = webcamProperties.getVideoFilesPath() + DateFormatUtils.format(new Date(), "yyyyMMdd HH-mm-ss") + "-" + UUID.randomUUID() + ".avi";
		log.debug("Writing video to disk. File name : " + videoFileName + " (" + images.size() + " frames).");
		final Size frameSize = new Size((int) camera.get(Videoio.CAP_PROP_FRAME_WIDTH), (int) camera.get(Videoio.CAP_PROP_FRAME_HEIGHT));
		final VideoWriter videoWriter = new VideoWriter();
		videoWriter.open(videoFileName, VideoWriter.fourcc('D', 'I', 'V', 'X'), webcamProperties.getVideoFPS(), frameSize, true);
		// log.info("videoWriter open ? " + videoWriter.isOpened());
		final long startTime = System.currentTimeMillis();
		for (Mat image : images) {
			videoWriter.write(image);
		}
		videoWriter.release();

		// Free memory.
		for (Mat image : images) {
			image.release();
		}
		images.clear();
		final long estimatedTime = System.currentTimeMillis() - startTime;
		log.info("Video file " + videoFileName + " written to disk in " + (double) (estimatedTime / 1000) + " seconds.");

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

			users = predictFaces(img);
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
	public List<User> predictFaces(final Mat image) {
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
			drawRectangleOnImage(image, facesArray[counter].tl(), facesArray[counter].br(), nameFound);
			counter++;
		}
		if (!webcamProperties.getPredictedImagesPath().equals("")) {
			saveImage(image, webcamProperties.getPredictedImagesPath());
		}
		return users;
	}

	/**
	 * Draw a rectangle and add a label if any on the image.
	 * 
	 * @param Image
	 *            image where to add the rectangle and the label
	 * @param topLeft
	 *            Top left coordinate of the rectangle
	 * @param bottomRight
	 *            Bottom right coordinate of the rectable.
	 * @param label
	 *            Label to add.
	 */
	protected void drawRectangleOnImage(Mat image, Point topLeft, Point bottomRight, String label) {
		Imgproc.rectangle(image, topLeft, bottomRight, new Scalar(255, 255, 255), 2);
		if (label != null && !label.equals("")) {
			Imgproc.putText(image, label, new Point(topLeft.x, topLeft.y - 20), Core.FONT_HERSHEY_PLAIN, 1.3, new Scalar(255, 255, 255), 2);
		}
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

		setAuthenticatedUSer(null);
		final List<User> users = predictFace();
		for (User user : users) {
			if (user.getUserId() != null && user.getUserId() != 0) {
				setAuthenticatedUSer(user);
				log.info("User " + getAuthenticatedUSer() + " authenticated.");
				break;
			}
		}

		if (getAuthenticatedUSer() == null) {
			log.info("Authenticated failed.");
		}

		return getAuthenticatedUSer();
	}

	/**
	 * Authenticate a user using face recognition.
	 * 
	 * @return the authenticated user, null otherwise .
	 */
	public User authenticateWithAuthenticationFactor() {
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

	public boolean startWatching() {
		try {
			startCapture(new WatchImageHandler(this));
			return googleDriveServices.startFileStorageProcess(webcamProperties.getVideoFilesPath());
		} catch (Exception e) {
			log.error("Error while starting to Watch.", e);
			return false;
		}
	}

	public boolean stopWatching() {
		this.recording = false;
		stopCapture();
		return googleDriveServices.stopFileStorageProcess();
	}

	public boolean startMotionDetection() {
		try {
			startCapture(new StoreImageHandler(this));
			return googleDriveServices.startFileStorageProcess(webcamProperties.getVideoFilesPath());
		} catch (Exception e) {
			log.error("Error while starting Motion Detection.", e);
			return false;
		}
	}

	public boolean stopMotionDetection() {
		this.recording = false;
		stopCapture();
		return googleDriveServices.stopFileStorageProcess();
	}
	
	
	/**
	 * Motion detection between 2 frames.
	 * 
	 * @param frame1
	 *            Frame 1. Must be the background. TODO Create a setBackground
	 *            somewhere...
	 * @param frame2
	 *            Frame 2.
	 * @return True if motion detected.
	 */
	public boolean detectMotion(Mat frame1, Mat frame2) {

		boolean targetDetected = false;
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		// Move these frames to gray scales.
		frame1 = toGray(frame1);
		Imgproc.GaussianBlur(frame1, frame1, new Size(21, 21), 0);
		frame2 = toGray(frame2);
		Imgproc.GaussianBlur(frame2, frame2, new Size(21, 21), 0);
		Mat result = frame1.clone();

		// saveImage(frame1, "C:/tmp/shots/motionDetection/");
		// saveImage(frame2, "C:/tmp/shots/motionDetection/");

		Core.absdiff(frame1, frame2, result);
		// Core.subtract(frame2, frame1, result);
		// saveImage(result, "C:/tmp/shots/motionDetection/");

		// Imgproc.adaptiveThreshold(result, result, 255,
		// Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 2);
		Imgproc.threshold(result, result, 25, 255, Imgproc.THRESH_BINARY);
		// saveImage(result, "C:/tmp/shots/motionDetection/");
		contours.clear();
		Imgproc.findContours(result, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		// Draw the contours on the frame 2.
		for (int i = 0; i < contours.size(); i++) {
			Mat contour = contours.get(i);
			double contourarea = Imgproc.contourArea(contour);
			if (contourarea > 700) {
				targetDetected = true;
				drawRectangleOnImage(frame2, Imgproc.boundingRect(contours.get(i)).br(), Imgproc.boundingRect(contours.get(i)).tl(), "");
				saveImage(frame2, "C:/tmp/shots/motionDetection/");
				// Imgproc.drawContours(frame1, contours, i, new Scalar(255,
				// 255, 255), 2);
			}

		}

		if (contours.size() > 0) {
			targetDetected = true;
		} else {
			targetDetected = false;
		}
		return targetDetected;

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

	/**
	 * Add a frame at the beginning of the queue. Remove the last one if the
	 * maximum limit has been reached.
	 * 
	 * @param image
	 *            image to add to the queue.
	 */
	protected void addCapturedFrame(final Mat image) {
		if (capturedFramesQueue.size() >= webcamProperties.getMaximumFramesInVideoFiles()) {
			writeRecordedVideo(capturedFramesQueue);
		}
		final Mat newImage = image.clone(); // Had to do that, otherwise always
											// the same frame stored...
		capturedFramesQueue.add(newImage);
	}

	/**
	 * Return the captured frames queue.
	 * 
	 * @return
	 */
	protected List<Mat> getCapturedFrames() {
		return capturedFramesQueue;
	}

	/**
	 * Flag to know if frames are being captured.
	 * 
	 * @return true if yes, or false.
	 */
	public boolean isRecording() {
		return recording;
	}

	/**
	 * Set the recording flag to true.
	 */
	public void record() {
		recording = true;
	}

	/**
	 * Set the recording flag to false.
	 */
	public void stopRecording() {
		recording = false;
	}

	/**
	 * Flush the captured frames into a video file.
	 */
	public void writeRecordedVideo() {
		writeRecordedVideo(capturedFramesQueue);
	}

	/**
	 * @return True if a capture is underway.
	 */
	public boolean isCapturing() {
		return capturing;
	}

}