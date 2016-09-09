package com.dachlab.google;

import java.util.List;

import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

public interface IGoogleDriveService {

	String createFolder(String folderName);

	Permission createPermission(String folderId, String account);

	File addFileToFolder(String folderId, java.io.File file);

	List<File> listFolderElements(String folderId);

	List<File> listStorageFolderElements();

	File addFileToStorageFolder(java.io.File file);

	boolean startFileStorageProcess(String pathToFilesToStore);

	boolean stopFileStorageProcess();

}
