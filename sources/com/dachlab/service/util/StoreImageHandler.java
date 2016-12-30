package com.dachlab.service.util;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.opencv.core.Mat;

public class StoreImageHandler implements ICapturedImageHandler {

	private WebcamManager webcamManager;

	/** Background image used to detect motion. */
	private Mat motionDetectionBackgroundFrame;

	/** Last update date of the background image for motion detection. */
	private Date motionDetectionBackgroundFrameLastUpdate = null;

	/** Last update date of the background image for motion detection. */
	private Date lastMmotionDetectionDate = null;

	/**
	 * Constructor using the WebcamManager.
	 * 
	 * @param webcamManager
	 *            WebcamManager.
	 */
	public StoreImageHandler(final WebcamManager webcamManager) {
		this.webcamManager = webcamManager;
	}

	@Override
	public void handle(final Mat image) {
		Mat background = getMotionDetectionBackgroundFrame(image);
		if (lastMmotionDetectionDate != null && System.currentTimeMillis() < DateUtils.addSeconds(lastMmotionDetectionDate, 2).getTime()) {
			webcamManager.addCapturedFrame(image);
		} else {
			lastMmotionDetectionDate = null;
			if (webcamManager.detectMotion(background, image)) {
				webcamManager.addCapturedFrame(image);
				lastMmotionDetectionDate = new Date();
			}
		}
	}

	/**
	 * Get the background image for motion detection. Get a new background image
	 * every 60 seconds if requested.
	 * 
	 * @return The background image for motion detection.
	 */
	private Mat getMotionDetectionBackgroundFrame(final Mat image) {
		if (motionDetectionBackgroundFrameLastUpdate == null || System.currentTimeMillis() > DateUtils.addSeconds(motionDetectionBackgroundFrameLastUpdate, 60).getTime() && !webcamManager
				.detectMotion(motionDetectionBackgroundFrame, image)) {
			motionDetectionBackgroundFrame = image.clone();
			motionDetectionBackgroundFrameLastUpdate = new Date();
		}
		return motionDetectionBackgroundFrame;
	}
}
