package com.dachlab.service;

import java.util.List;

import com.dachlab.model.User;

/**
 * Interface for handling the webcam.
 * 
 * @author dcharles
 *
 */
public interface IWebcamService {

	/**
	 * Take a picture from the default webcam usign JavaCV.
	 * 
	 * @return true if managed to do so.
	 */
	boolean captureImage();

	/**
	 * Capture a face and save in the appropriate folder (name).
	 * 
	 * @param name
	 *            Name for the associated face.
	 * @return true if managed to do so.
	 */
	boolean captureFace(String name);

	/**
	 * Predict the name related to the face captured.
	 * 
	 * @return the names found.
	 */
	List<User> predictFace();

	/**
	 * Start learning stored faces.
	 * 
	 * @return true if succeeded.
	 */
	boolean learnFaces();

	/**
	 * Predict a name for each samples in the faces path.
	 * 
	 * @return the corresponding names.
	 */
	List<User> predictFacesFromSample();

	/**
	 * Start capturing from the webcam.
	 * 
	 * @return true if succeeded.
	 */
	boolean startCapture();

	/**
	 * Stop the image capture.
	 * 
	 * @return true if succeeded.
	 */
	boolean stopCapture();

	/**
	 * Authenticate a user using face recognition.
	 * 
	 * @return the name of the recognized user or null if not recognized.
	 */
	User authenticate();

	/**
	 * Predict faces based on an image.
	 * 
	 * @param image image to analyze.
	 * @return List of users found.
	 */
	List<User> predictFace(byte[] image);

	/**
	 * Start the watching process.
	 * @return true if succeeded.
	 */
	boolean startWatching();

	/**
	 * Stop the watching process
	 * @return true if succeeded.
	 */
	boolean stopWatching();

	/**
	 * Motion detection between 2 frames.
	 * @param frame1 Frame 1.
	 * @param frame2 Frame 2.
	 * @return True if motion detected.
	 */
	boolean detectMotion();

	/**
	 * Stop the motion detection process.
	 */
	void stopMotionDetection();

	/**
	 * Start the motion detection process.
	 */
	void startMotionDetection();

}
