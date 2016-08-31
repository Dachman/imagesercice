package com.dachlab.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachlab.properties.IGoogleServicesProperties;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

@Service("googleDriveService")
public class GoogleDriveService implements IGoogleDriveService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	protected IGoogleServicesProperties googleServicesProperties;

	/** Application name. */
	private static final String APPLICATION_NAME = "CharlesVideoStore";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Google Drive scopes. */
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

	private Drive driveService;

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	private Drive getDriveService() throws IOException {
		if (driveService == null) {
			// Load client secrets.
			InputStream in = new FileInputStream(new java.io.File("C:/DEV/JAVA/workspace/common/sources/GoogleDriveCredentials.json"));
			Credential credential = GoogleCredential.fromStream(in).createScoped(SCOPES);
			driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
		}
		return driveService;
	}

	/**
	 * Create a folder in the application Drive
	 * 
	 * @param folderName
	 *            The folder name.
	 * @return The folder ID or null.
	 */
	@Override
	public String createFolder(String folderName) {
		File fileMetadata = new File();
		fileMetadata.setName(folderName);
		fileMetadata.setMimeType("application/vnd.google-apps.folder");

		File file;
		try {
			file = getDriveService().files().create(fileMetadata).setFields("id").execute();
		} catch (IOException e) {
			log.error("An error occured while creating folder " + folderName + ".", e);
			return null;
		}

		return file.getId();
	}

	/**
	 * Create a new write permission for a folder.
	 * 
	 * @param folderID
	 *            ID of the folder.
	 * @param account
	 *            Account name (usually, the email).
	 * @return the permission or null.
	 */
	@Override
	public Permission createPermission(String folderId, String account) {
		Permission permission = new Permission();

		permission.setEmailAddress(account);
		permission.setType("user");
		permission.setRole("writer");

		try {
			return getDriveService().permissions().create(folderId, permission).execute();
		} catch (IOException e) {
			log.error("An error occured while adding permission to folder " + folderId + " to user " + account + ".", e);
			return null;
		}
	}

	/**
	 * Add a file to a folder.
	 * 
	 * @param folderId
	 *            ID of the folder.
	 * @param file
	 *            The file to add to this folder.
	 * @return the {@link}File added.
	 */
	@Override
	public File addFileToFolder(String folderId, java.io.File file) {
		final File driveFile = new File();
		driveFile.setName(file.getName());
		driveFile.setParents(Collections.singletonList(folderId));
		final FileContent mediaContent = new FileContent(null, file);
		try {
			return getDriveService().files().create(driveFile, mediaContent).setFields("id, parents").execute();
		} catch (IOException e) {
			log.error("An error occured while adding file " + driveFile.getName() + " to folder " + folderId + ".", e);
			return null;
		}
	}

	/**
	 * List the elements in a folder.
	 * 
	 * @param folderId
	 *            ID of the folder.
	 * @return The list of the elements or null.
	 */
	@Override
	public List<File> listFolderElements(String folderId) {
		FileList result;
		try {
			result = getDriveService().files().list().setQ("'" + folderId + "' in parents").setPageSize(50).setFields("nextPageToken, files(id, name)").execute();
			return result.getFiles();
		} catch (IOException e) {
			log.error("An error occured while listing the elements in the folder " + folderId + ".", e);
			return null;
		}

	}

	/**
	 * List the elements of the storage folder.
	 * 
	 * @return The list of the elements or null.
	 */
	@Override
	public List<File> listStorageFolderElements() {
		return listFolderElements(googleServicesProperties.getStorageFolderID());

	}
}