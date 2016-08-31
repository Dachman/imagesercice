package com.dachlab.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Webcam properties.
 * 
 * @author dcharles
 *
 */
@Component("googleServicesProperties")
@ConfigurationProperties(prefix = "google")
public class GoogleServicesProperties implements IGoogleServicesProperties {

	String pathToGoogleCredentials;
	String storageFolderID;

	@Override
	public String getPathToGoogleCredentials() {
		return pathToGoogleCredentials;
	}

	public void setPathToGoogleCredentials(String pathToGoogleCredentials) {
		this.pathToGoogleCredentials = pathToGoogleCredentials;
	}

	@Override
	public String getStorageFolderID() {
		return storageFolderID;
	}

	public void setStorageFolderID(String storageFolderID) {
		this.storageFolderID = storageFolderID;
	}

}
