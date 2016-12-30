package com.dachlab.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.opencv.core.Mat;
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

	/** Background image used to detect motion. */
	private Mat motionDetectionBackgroundFrame;

	/** Last update date of the background image for motion detection. */
	private Date motionDetectionBackgroundFrameLastUpdate = null;

	/** Indicate whether a motion detection is currently running. */
	private boolean detectingMotion = false;

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

	/**
	 * Get the background image for motion detection. Get a new background image
	 * every 60 seconds if requested.
	 * 
	 * @return The background image for motion detection.
	 */
	private Mat getMotionDetectionBackgroundFrame() {
		if (motionDetectionBackgroundFrameLastUpdate == null || System.currentTimeMillis() > DateUtils.addSeconds(motionDetectionBackgroundFrameLastUpdate, 60).getTime() && !webcamManager
				.detectMotion(motionDetectionBackgroundFrame, webcamManager.getImagefromWebcam())) {
			motionDetectionBackgroundFrame = webcamManager.getImagefromWebcam();
			motionDetectionBackgroundFrameLastUpdate = new Date();
		}
		return motionDetectionBackgroundFrame;
	}

	@Override
	public boolean detectMotion() {
		Mat background = getMotionDetectionBackgroundFrame();
		Mat frame = webcamManager.getImagefromWebcam();
		return webcamManager.detectMotion(background, frame);
	}

	@Override
	public void startMotionDetection() {
//		detectingMotion = true;
//		int captureDuration = 5; // TODO To be configurable
//		while (detectingMotion) {
//			log.debug("1");
//			if (!webcamManager.isCapturing() && detectMotion()) {
//				log.debug("2: startCaptureMotion");
//				webcamManager.startCaptureMotion();
//			} else {
//				if (webcamManager.isCapturing()) {
//					log.debug("3: stopCaptureMotion because isCapturing");
//					webcamManager.stopCaptureMotion();
//				}
//				try {
//					log.debug("4: Sleeping 2 sec.");
//					Thread.sleep(2 * 1000);
//				} catch (InterruptedException e) {
//					log.error("Unable to sleep.", e);
//				}
//			}
//			log.debug("5: End of loop.");
//		}
//		if (webcamManager.isCapturing()) {
//			log.debug("6: stopCaptureMotion because isCapturing");
//			webcamManager.stopCaptureMotion();
//		}
//		log.debug("7: Exiting startMotionDetection");
		webcamManager.startMotionDetection();
	}

	@Override
	public synchronized void stopMotionDetection() {
//		detectingMotion = false;
//		log.debug("Setting detectingMotion to  " + detectingMotion);
		webcamManager.stopMotionDetection();
	}

}
