package com.dachlab.service.util;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WatchImageHandler implements ICapturedImageHandler {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private WebcamManager webcamManager;

	/* Last time a body has been detected. */
	long lastBodyDetectedTime = 0;

	long lastBodyCheckTime = 0;

	/**
	 * Constructor using the WebcamManager.
	 * 
	 * @param webcamManager
	 *            WebcamManager.
	 */
	public WatchImageHandler(final WebcamManager webcamManager) {
		this.webcamManager = webcamManager;
	}

	@Override
	public void handle(final Mat image) {
		if (lastBodyDetectedTime != 0) {
			if (!webcamManager.isRecording() && System.currentTimeMillis() > DateUtils.addSeconds(new Date(lastBodyDetectedTime), webcamManager.webcamProperties.getVideoSequenceLength()).getTime()) {
				log.debug("Time comparison: " + DateFormatUtils.format(DateUtils.addSeconds(new Date(lastBodyDetectedTime), webcamManager.webcamProperties.getVideoSequenceLength()).getTime(),
						"HH:mm:ss") + ">" + DateFormatUtils.format(System.currentTimeMillis(), "HH:mm:ss") + " Result: " + (System.currentTimeMillis() > DateUtils
								.addSeconds(new Date(lastBodyDetectedTime), webcamManager.webcamProperties.getVideoSequenceLength()).getTime()));
				webcamManager.writeRecordedVideo();
				lastBodyDetectedTime = 0;
			} else {
				log.debug("Adding frame.");
				webcamManager.addCapturedFrame(image);
			}
		} else {
			getBodies(image);
		}
	}

	private void getBodies(final Mat image) {
		// Body check every 1 seconds.
		if (lastBodyCheckTime == 0 || System.currentTimeMillis() > DateUtils.addSeconds(new Date(lastBodyCheckTime), 1).getTime()) {
			final Rect[] bodyCoordinates = webcamManager.getBodyCoordinates(image).toArray();
			lastBodyCheckTime = System.currentTimeMillis();
			if (bodyCoordinates.length != 0) {
				log.debug(bodyCoordinates.length + " body found.");
				for (int i = 0; i < bodyCoordinates.length; i++) {
					webcamManager.drawRectangleOnImage(image, bodyCoordinates[i].tl(), bodyCoordinates[i].br(), null);
				}
				lastBodyDetectedTime = System.currentTimeMillis();
				webcamManager.record();
			} else {
				webcamManager.stopRecording();
			}
		}
	}

}
