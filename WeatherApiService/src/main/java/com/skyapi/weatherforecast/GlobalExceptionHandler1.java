package com.skyapi.weatherforecast;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;

/*
 * for global error 
 */

//Before ResponseEntityExceptionHandler and the error field in ErroDTO was changed from String error to List<String> errors = new ArrayList<>().
//this correspond with ErroDTO1.java

@ControllerAdvice
public class GlobalExceptionHandler1 {
	
	//for loggin the exception
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler1.class); //GlobalExceptionHandler is the log class

	//method that returns object of type ErrorDTO that represent the error details
	
	//NB since we want to handle all generic exception will use the Exception class type which is the super class of all exceptions
	@ExceptionHandler(Exception.class) //specify the exact type of exception or error that this error will catch.
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //specify the error response status to of the API client. In this case is internal server error
	@ResponseBody //indicate the return value of this method will be directly bound to the web request
	public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex) {
		
		//create new ErrorDTO object
		ErrorDTO error = new ErrorDTO();
		
		//set values for the fields of ErrorDTO
		error.setTimestamp(new Date()); //set date the error occured
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); // return an integer value that represent the error status code
		//error.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()); //get the error message
		error.setPath(request.getServletPath()); //return the end point url or request path 
	
		//log the exception object
		LOGGER.error(ex.getMessage(), ex);
		
		return error;
	}
	
	
	
}
