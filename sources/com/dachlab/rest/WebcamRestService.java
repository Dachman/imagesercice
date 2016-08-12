package com.dachlab.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dachlab.exception.ImageServiceException;
import com.dachlab.model.User;
import com.dachlab.service.IWebcamService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

/**
 * Webcam rest services implementation.
 * 
 * @author dcharles
 *
 */
@RestController
public class WebcamRestService implements IWecamRestService {

	@Autowired
	IWebcamService webcamService;

	@Override
	@RequestMapping(value = "/shoot", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> takeAShot() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.captureImage());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the shoot request.", e);
		}
	}

	@Override
	@RequestMapping(value = "/predictFace", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody ResponseEntity<List<User>> predictFace() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.predictFace());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the predictFace request.", e);
		}
	}

	@Override
	@ApiImplicitParams({ @ApiImplicitParam(name = "name", value = "Names of the person being captured.", required = true, dataType = "string", paramType = "query", defaultValue = "") })
	@RequestMapping(value = "/getFace/{name}", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<Boolean> getFace(@PathVariable String name) throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.captureFace(name));
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the captureFace request.", e);
		}
	}

	@Override
	@RequestMapping(value = "/learnFaces", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> learnFaces() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.learnFaces());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the learnFace request.", e);
		}
	}

	/**
	 * Predict a name for each samples in the faces path.
	 * 
	 * @return the corresponding names.
	 * @throws ImageServiceException
	 */
	@Override
	@RequestMapping(value = "/predictFacesFromSample", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<List<User>> predictFacesFromSample() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.predictFacesFromSample());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the predictFace request.", e);
		}
	}

	@Override
	@RequestMapping(value = "/startCapture", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> startCapture() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.startCapture());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the startCapture request.", e);
		}
	}

	@Override
	@RequestMapping(value = "/stopCapture", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<Boolean> stopCapture() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.stopCapture());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the stopCapture request.", e);
		}
	}

	@Override
	@RequestMapping(value = "/authenticate", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<User>  authenticate() throws ImageServiceException {
		try {
			return ResponseEntity.status(HttpStatus.OK).body(webcamService.authenticate());
		} catch (Exception e) {
			throw new ImageServiceException("Failed to serve the authenticate request.", e);
		}
	}

}
