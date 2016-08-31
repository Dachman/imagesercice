package com.dachlab.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.dachlab.exception.GoogleServicesException;
import com.google.api.services.drive.model.File;

public interface IGoogleDriveRestService {

	ResponseEntity<List<File>> listStorageFolderElements() throws GoogleServicesException;

}
