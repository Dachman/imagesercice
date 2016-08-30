package com.dachlab.service;

import java.util.List;

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
	public List<User> predictFace() {
		return webcamManager.predictFace();
	}

	@Override
	public boolean learnFaces() {
		return webcamManager.learnFaces();
	}

	@Override
	public List<User> predictFacesFromSample() {
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

	@Override
	public List<User> predictFace(byte[] image) {
		return webcamManager.predictFace(image);
	}

	@Override
	public boolean startWatching() {
		return webcamManager.startWatching();
	}

	@Override
	public boolean stopWatching() {
		return webcamManager.stopWatching();
	}
}
