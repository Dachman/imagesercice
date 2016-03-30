package com.dachlab.rest;

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
	 */
	boolean takeAShot();

	/**
	 * Get a face.
	 * 
	 * @param name
	 *            Name associated to the face
	 * @return true if succeed.
	 */
	boolean getFace(String name);

	/**
	 * Predict the name related to the face captured.
	 * 
	 * @return the name found.
	 */
	String[] predictFace();

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
	String predictFacesFromSample();

	/**
	 * Read a text from the captured image.
	 * 
	 * @return the text read.
	 */
	String readText();

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
	 * @return the authenticated user.
	 */
	String authenticate();

}
