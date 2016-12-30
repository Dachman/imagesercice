package com.dachlab.service.util;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * Capture images from the webcam.
 * 
 * @author dcharles
 */
class WebCamCapturer extends Thread {

	private volatile boolean stopRequested = false;

	private volatile VideoCapture camera;

	private ICapturedImageHandler capturedImageHandler;

	private int duration;

	/**
	 * Constructor using a camera.
	 * 
	 * @param camera
	 *            camera to capture from.
	 * @param capturedImageHandler
	 *            Image handled.
	 * @param duration
	 *            capture duration in seconds. -1 if unlimited.
	 */
	public WebCamCapturer(final VideoCapture camera, final ICapturedImageHandler capturedImageHandler) {
		this.camera = camera;
		this.capturedImageHandler = capturedImageHandler;
	}

	@Override
	public void run() {
		stopRequested = false;
		if (camera != null && camera.isOpened()) {
			Mat image = new Mat();
			while (camera.read(image)) {
				if (stopRequested) {
					break;
				}
				if (capturedImageHandler != null) {
					capturedImageHandler.handle(image);
				}
			}
		}
	}

	/**
	 * Terminate the thread.
	 */
	public void terminate() {
		stopRequested = true;
	}

}