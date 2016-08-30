package com.dachlab.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Webcam properties.
 * 
 * @author dcharles
 *
 */
@Component("webcamProperties")
@ConfigurationProperties(prefix = "webcam")
public class WebcamProperties implements IWebcamProperties {
	private Map<String, Integer> dimension;
	private int width;
	private int height;
	private String path;
	private String facesPath;
	private String predictedFacesPath;
	private String predictedImagesPath;
	private String predictedTextImagesPath;
	private String textReaderTrainingDataPath;
	private String faceDetectionClassifierName;
	private String bodyDetectionClassifierName;
	private Integer authenticationConfidenceFactor;
	private Integer loopsForFaceRecognitionAuthentication;
	private Integer maximumFramesInVideoFiles;
	private String videoFilesPath;
	private int videoSequenceLength;
	private int videoFPS;

	@Override
	public int getWidth() {
		width = dimension.get("width");
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		height = dimension.get("height");
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public Map<String, Integer> getDimension() {
		return dimension;
	}

	@Override
	public Integer getLoopsForFaceRecognitionAuthentication() {
		return loopsForFaceRecognitionAuthentication;
	}

	public void setDimension(Map<String, Integer> dimension) {
		this.dimension = dimension;
	}

	@Override
	public String getFacesPath() {
		return facesPath;
	}

	public void setPredictedImagesPath(String predictedImagesPath) {
		this.predictedImagesPath = predictedImagesPath;
	}

	@Override
	public String getFaceDetectionClassifierName() {
		return faceDetectionClassifierName;
	}

	public void setFaceDetectionClassifierName(String faceDetectionClassifierName) {
		this.faceDetectionClassifierName = faceDetectionClassifierName;
	}

	@Override
	public String getPredictedFacesPath() {
		return predictedFacesPath;
	}

	@Override
	public String getPredictedImagesPath() {
		return predictedImagesPath;
	}

	public void setFacesPath(String facesPath) {
		this.facesPath = facesPath;
	}

	public void setPredictedFacesPath(String predictedFacesPath) {
		this.predictedFacesPath = predictedFacesPath;
	}

	@Override
	public String getPredictedTextImagesPath() {
		return predictedTextImagesPath;
	}

	public void setPredictedTextImagesPath(String predictedTextImagesPath) {
		this.predictedTextImagesPath = predictedTextImagesPath;
	}

	@Override
	public String getTextReaderTrainingDataPath() {
		return textReaderTrainingDataPath;
	}

	@Override
	public Integer getAuthenticationConfidenceFactor() {
		return authenticationConfidenceFactor;
	}

	public void setAuthenticationConfidenceFactor(Integer authenticationConfidenceFactor) {
		this.authenticationConfidenceFactor = authenticationConfidenceFactor;
	}

	public void setLoopsForFaceRecognitionAuthentication(Integer loopsForFaceRecognitionAuthentication) {
		this.loopsForFaceRecognitionAuthentication = loopsForFaceRecognitionAuthentication;
	}

	@Override
	public Integer getMaximumFramesInVideoFiles() {
		return maximumFramesInVideoFiles;
	}

	public void setMaximumFramesInVideoFiles(Integer maximumFramesInVideoFiles) {
		this.maximumFramesInVideoFiles = maximumFramesInVideoFiles;
	}

	@Override
	public String getVideoFilesPath() {
		return videoFilesPath;
	}

	public void setVideoFilesPath(String videoFilesPath) {
		this.videoFilesPath = videoFilesPath;
	}

	@Override
	public String getBodyDetectionClassifierName() {
		return bodyDetectionClassifierName;
	}

	public void setBodyDetectionClassifierName(String bodyDetectionClassifierName) {
		this.bodyDetectionClassifierName = bodyDetectionClassifierName;
	}

	@Override
	public int getVideoSequenceLength() {
		return videoSequenceLength;
	}

	public void setVideoSequenceLength(int videoSequenceLength) {
		this.videoSequenceLength = videoSequenceLength;
	}

	@Override
	public int getVideoFPS() {
		return videoFPS;
	}

	public void setVideoFPS(int videoFPS) {
		this.videoFPS = videoFPS;
	}

}
