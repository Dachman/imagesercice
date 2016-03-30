package com.dachlab.service.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dachlab.model.User;

public class FaceRecognitionCapturedImageHandler implements ICapturedImageHandler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private WebcamManager webcamManager;

	private Map<String, Integer> facesFound = new HashMap<String, Integer>();

	/**
	 * Constructor using the WebcamManager to predict faces.
	 * 
	 * @param webcamManager
	 *            WebcamManager.
	 */
	public FaceRecognitionCapturedImageHandler(final WebcamManager webcamManager) {
		this.webcamManager = webcamManager;
	}

	@Override
	public void handle(Mat image) {
		String[] namesFound = webcamManager.predictFace(image);
		if (namesFound.length != 0) {
			log.info(namesFound.length + " faces detected. " + Arrays.toString(namesFound));
			for (String name : namesFound) {
				facesFound.put(name, facesFound.getOrDefault(name, 0) + 1);
				if (facesFound.get(name).compareTo(webcamManager.getWebcamProperties().getAuthenticationConfidenceFactor()) > 0) {
					User userFound = webcamManager.getUserService().getByUserName(name);
					if (userFound != null) {
						webcamManager.setAuthenticatedUSer(userFound);
						log.debug("User " + name + " found in the DB.");
					} else {
						webcamManager.setAuthenticatedUSer(null);
						log.debug("User not found in the DB, failed to authenticate.");
					}
				}
			}
		}
	}
}
