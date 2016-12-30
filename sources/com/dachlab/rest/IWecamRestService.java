package com.dachlab.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.dachlab.exception.ImageServiceException;
import com.dachlab.model.User;

/**
 * Webcam rest services interface.
 * 
 * @author dcharles
 *
 */
public interface IWecamRestService {

	/**
	 * Take a shot.
	 * 
	 * @return true if succeed.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<Boolean> takeAShot() throws ImageServiceException;

	/**
	 * Get a face.
	 * 
	 * @param name
	 *            Name associated to the face
	 * @return true if succeed.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<Boolean> getFace(String name) throws ImageServiceException;

	/**
	 * Predict the name related to the face captured.
	 * 
	 * @return the name found.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<List<User>> predictFace() throws ImageServiceException;

	/**
	 * Start learning stored faces.
	 * 
	 * @return true if succeeded.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<Boolean> learnFaces() throws ImageServiceException;

	/**
	 * Predict a name for each samples in the faces path.
	 * 
	 * @return the corresponding names.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<List<User>> predictFacesFromSample() throws ImageServiceException;

	/**
	 * Start capturing from the webcam.
	 * 
	 * @return true if succeeded.
	 * @throws ImageServiceException
	 *             Exception if any issue.
	 */
	ResponseEntity<Boolean> startCapture() throws ImageServiceException;

	/**
	 * Stop the image capture.
	 * 
	 * @return true if succeeded.
	 * @throws ImageServiceException
	 *             Exception if any issue
	 */
	ResponseEntity<Boolean> stopCapture() throws ImageServiceException;

	/**
	 * Authenticate a user using face recognition.
	 * 
	 * @return the authenticated user.
	 * @throws ImageServiceException
	 *             Exception if any issue
	 */
	ResponseEntity<User> authenticate() throws ImageServiceException;

	/**
	 * Start the watching process.
	 * 
	 * @return true if succeeded.
	 * @throws ImageServiceException
	 *             Exception if any issue
	 */
	ResponseEntity<Boolean> startWatching() throws ImageServiceException;

	/**
	 * Stop the watching process
	 * 
	 * @return true if succeeded.
	 * @throws ImageServiceException
	 *             Exception if any issue
	 */
	ResponseEntity<Boolean> stopWatching() throws ImageServiceException;

	/**
	 * Motion detection between 2 frames.
	 * @return True if motion detected.
	 */
	ResponseEntity<Boolean> detectMotion() throws ImageServiceException;

	/**
	 * Start the motion detection.
	 * @throws ImageServiceException Exception if any issue
	 */
	void startMotionDetection() throws ImageServiceException;

	/**
	 * Stop the motion detection.
	 * @throws ImageServiceException Exception if any issue
	 */
	void stopMotionDetection() throws ImageServiceException;
	
}
