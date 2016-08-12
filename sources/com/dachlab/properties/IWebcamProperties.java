package com.dachlab.properties;

import java.util.Map;

public interface IWebcamProperties {

	Map<String, Integer> getDimension();

	int getWidth();

	int getHeight();

	String getPath();

	String getFacesPath();

	String getPredictedFacesPath();

	String getPredictedImagesPath();

	String getFaceDetectionClassifierName();

	String getPredictedTextImagesPath();
	
	String getTextReaderTrainingDataPath();

	Integer getAuthenticationConfidenceFactor();
	
	Integer getLoopsForFaceRecognitionAuthentication();

}
