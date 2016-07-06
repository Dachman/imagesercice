package com.dachlab.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dachlab.service.IWebcamService;

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
	@RequestMapping("/shoot")
	public @ResponseBody boolean takeAShot() {
		return webcamService.captureImage();
	}

	@Override
	@RequestMapping("/predictFace")
	public @ResponseBody String[] predictFace() {
		return webcamService.predictFace();
	}

	@Override
	@RequestMapping("/getFace/{name}")
	public @ResponseBody boolean getFace(@PathVariable String name) {
		return webcamService.captureFace(name);
	}

	@Override
	@RequestMapping("/learnFaces")
	public @ResponseBody boolean learnFaces() {
		return webcamService.learnFaces();
	}

	/**
	 * Predict a name for each samples in the faces path.
	 * 
	 * @return the corresponding names.
	 */
	@Override
	@RequestMapping("/predictFacesFromSample")
	public @ResponseBody String predictFacesFromSample() {
		return webcamService.predictFacesFromSample();
	}

	@Override
	@RequestMapping("/startCapture")
	public @ResponseBody boolean startCapture() {
		return webcamService.startCapture();
	}

	@Override
	@RequestMapping("/stopCapture")
	public @ResponseBody boolean stopCapture() {
		return webcamService.stopCapture();
	}

	@Override
	@RequestMapping("/authenticate")
	public @ResponseBody String authenticate() {
		return webcamService.authenticate().toString();
	}
	
}
