package com.dachlab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachlab.model.User;
import com.dachlab.properties.IWebcamProperties;
import com.dachlab.service.util.WebcamManager;

/**
 * Webcam services.
 * 
 * @author dcharles
 *
 */
@Service("webcamSercice")
public class WebcamService implements IWebcamService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IWebcamProperties webcamProperties;

	@Autowired
	WebcamManager webcamManager;

	@Override
	public boolean captureImage() {
		return webcamManager.saveImagesToDisk(webcamManager.getFaces(webcamManager.getImagefromWebcam()));
	}

	@Override
	public boolean captureFace(String name) {
		return webcamManager.saveFaces(name, webcamManager.getFaces(webcamManager.getImagefromWebcam()));
	}

	@Override
	public String[] predictFace() {
		return webcamManager.predictFace();
	}

	@Override
	public boolean learnFaces() {
		return webcamManager.learnFaces();
	}

	@Override
	public String predictFacesFromSample() {
		return webcamManager.predictFacesFromSample();
	}

	@Override
	public boolean startCapture() {
		return webcamManager.startCapture();
	}

	@Override
	public boolean stopCapture() {
		return webcamManager.stopCapture();
	}

	@Override
	public User authenticate() {
		return webcamManager.authenticate();
	}

}
