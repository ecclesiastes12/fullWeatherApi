package com.skyapi.weatherforecast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * for error customization.
 * the fields provide will be used for error customization and it
 * represent the error details
 */

public class ErrorDTO {

	private Date timestamp; // the time the error occured
	private int status; // the error status code
	private String path; // the url for the error

	// modified from String error to List<String> errors. check ErrorDTO1
	// NB this is take array of error message for each element or invalid field of
	// the request body
	private List<String> errors = new ArrayList<>(); // the error messages

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	//method that add the error message
	public void addError(String message) {
		this.errors.add(message);
	}
}
