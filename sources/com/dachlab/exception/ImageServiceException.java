package com.dachlab.exception;

public class ImageServiceException extends Throwable {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for new related webcam and image services.
	 * 
	 * @param message
	 *            messages of the exception.
	 * @param cause
	 *            cause of the exception
	 */
	public ImageServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor for new related webcam and image services.
	 * 
	 * @param message
	 *            messages of the exception.
	 */
	public ImageServiceException(String message) {
		super(message);
	}
}
