package com.dachlab.google;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Process in charge of getting the files from a folder and adding them to a
 * Drive directory.
 * 
 * @author dcharles
 *
 */
public class FileStorageProcessThread extends Thread {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private IGoogleDriveService googleDriveService;
	private String pathToFilesToStore;

	public FileStorageProcessThread(final IGoogleDriveService googleDriveService, final String pathToFilesToStore) {
		this.googleDriveService = googleDriveService;
		this.pathToFilesToStore = pathToFilesToStore;
	}

	@Override
	public void run() {
		File folder = new File(pathToFilesToStore);
		while (true) {
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles.length > 0) {
				log.debug("Uploading file " + listOfFiles[0].getName() + " to the Drive directory.");
				googleDriveService.addFileToStorageFolder(listOfFiles[0]);
				listOfFiles[0].delete();
				log.debug("File updloaded and deleted locally");
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				log.error("Unable to put the thread to bed...", e);
			}
		}
	}
}
