package com.skyapi.weatherforecast;

import java.util.Date;

/*
 * for error customization.
 * the fields provide will be used for error customization and it
 * represent the error details
 */

public class ErrorDTO1 {

	private Date timestamp; //the time the error occured
	private int status; // the error status code
	private String path; // the url for the error
	private String error; // the error message
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
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
