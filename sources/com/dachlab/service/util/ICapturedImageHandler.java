package com.dachlab.service.util;

import org.opencv.core.Mat;

public interface ICapturedImageHandler {

	/**
	 * Do something with the image.
	 * 
	 * @param image
	 *            Captured image.
	 */
	void handle(Mat image);

}
