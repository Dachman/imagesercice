package com.dachlab.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dachlab.exception.GoogleServicesException;
import com.dachlab.google.IGoogleDriveService;
import com.google.api.services.drive.model.File;

/**
 * Google Drive rest services implementation.
 * 
 * @author dcharles
 *
 */
@RestController
public class GoogleDriveRestService implements IGoogleDriveRestService {

	@Autowired
	IGoogleDriveService googleDriveService;

	@Override
	@RequestMapping(value = "/listStorageFolderElements", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<File>> listStorageFolderElements() throws GoogleServicesException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(googleDriveService.listStorageFolderElements());
		} catch (Exception e) {
			throw new GoogleServicesException("Failed to serve the listStorageFolderElements request.", e);
		}
	}

}
