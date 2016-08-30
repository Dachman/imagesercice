package com.dachlab.service.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
		if (webcamManager.getAuthenticatedUSer() == null) {
			List<User> users = webcamManager.predictFaces(image);
			if (users.size() != 0) {
				log.info(users.size() + " faces detected. " + Arrays.toString(users.toArray(new User[users.size()])));
				for (User user : users) {
					facesFound.put(user.getUserName(), facesFound.getOrDefault(user.getUserName(), 0) + 1);
					log.info("Face found : " + user.getUserName() + "(" + facesFound
							.get(user.getUserName()) + ")      " + (int) facesFound.get(user.getUserName()) + "?==" + (int) webcamManager.getWebcamProperties().getAuthenticationConfidenceFactor());
					if ((int) facesFound.get(user.getUserName()) == (int) webcamManager.getWebcamProperties().getAuthenticationConfidenceFactor()) {
						User userFound = webcamManager.getUserService().getByUserName(user.getUserName());
						if (userFound != null) {
							webcamManager.setAuthenticatedUSer(userFound);
							log.info("User " + user.getUserName() + " found in the DB.");
						} else {
							webcamManager.setAuthenticatedUSer(null);
							log.debug("User not found in the DB, failed to authenticate.");
						}
					}
				}
			}
		}
	}
}
