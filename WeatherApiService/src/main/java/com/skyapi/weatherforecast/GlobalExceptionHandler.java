package com.skyapi.weatherforecast;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.skyapi.weatherforecast.location.LocationNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/*
 * for global error 
 */

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
	//for loggin the exception
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class); //GlobalExceptionHandler is the log class

	//method that returns object of type ErrorDTO that represent the error details
	
	//NB since we want to handle all generic exception will use the Exception class type which is the super class of all exceptions
	@ExceptionHandler(Exception.class) //specify the exact type of exception or error that this error will catch.
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //specify the error response status to of the API client.In this case is internal server error
	@ResponseBody //indicate the return value of this method will be directly bound to the web request
	public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex) {
		
		//create new ErrorDTO object
		ErrorDTO error = new ErrorDTO();
		
		//set values for the fields of ErrorDTO
		error.setTimestamp(new Date()); //set date the error occured
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()); // return an integer value that represent the error status code
		//error.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()); //get the error message
		
		//error.setError... change to error.addError because in ErrorDTO String error is changed to List<String> errors
		//and therefore cannot set error but rather add error from the addError method
		error.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		error.setPath(request.getServletPath()); //return the end point url or request path 
	
		//log the exception object
		LOGGER.error(ex.getMessage(), ex);
		
		return error;
	}

//	//handle bad Request exception
//	@ExceptionHandler(BadRequestException.class) //specify the exact type of exception or error that this error will catch.
//	@ResponseStatus(HttpStatus.BAD_REQUEST) //specify the error response status to of the API client.In this case is bad request error
//	@ResponseBody //indicate the return value of this method will be directly bound to the web request
//	public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex) {
//		
//		//create new ErrorDTO object
//		ErrorDTO error = new ErrorDTO();
//		
//		//set values for the fields of ErrorDTO
//		error.setTimestamp(new Date()); //set date the error occured
//		error.setStatus(HttpStatus.BAD_REQUEST.value()); // return an integer value that represent the error status code
//		
//		error.addError(ex.getMessage()); //error message from the exception object
//		error.setPath(request.getServletPath()); //return the end point url or request path 
//	
//		//log the exception object
//		LOGGER.error(ex.getMessage(), ex);
//		
//		return error;
//	}
	
	//NB Geolocation exception has been added to BadRequest exception
	//handle bad Request exception
		@ExceptionHandler({BadRequestException.class, GeolocationException.class}) //specify the exact type of exception or error that this error will catch.
		@ResponseStatus(HttpStatus.BAD_REQUEST) //specify the error response status to of the API client.In this case is bad request error
		@ResponseBody //indicate the return value of this method will be directly bound to the web request
		public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex) {
			
			//create new ErrorDTO object
			ErrorDTO error = new ErrorDTO();
			
			//set values for the fields of ErrorDTO
			error.setTimestamp(new Date()); //set date the error occured
			error.setStatus(HttpStatus.BAD_REQUEST.value()); // return an integer value that represent the error status code
			
			error.addError(ex.getMessage()); //error message from the exception object
			error.setPath(request.getServletPath()); //return the end point url or request path 
		
			//log the exception object
			LOGGER.error(ex.getMessage(), ex);
			
			return error;
		}
	
//	//handle constraint violation exception
//	@ExceptionHandler(ConstraintViolationException.class) //specify the exact type of exception or error that this error will catch.
//	@ResponseStatus(HttpStatus.BAD_REQUEST) //specify the error response status to of the API client.In this case is bad request error
//	@ResponseBody //indicate the return value of this method will be directly bound to the web request
//	public ErrorDTO handleConstraintViolationtException(HttpServletRequest request, Exception ex) {
//		
//		//create new ErrorDTO object
//		ErrorDTO error = new ErrorDTO();
//		
//		//set values for the fields of ErrorDTO
//		error.setTimestamp(new Date()); //set date the error occured
//		error.setStatus(HttpStatus.BAD_REQUEST.value()); // return an integer value that represent the error status code
//		
//		error.addError(ex.getMessage()); //error message from the exception object
//		error.setPath(request.getServletPath()); //return the end point url or request path 
//	
//		//log the exception object
//		LOGGER.error(ex.getMessage(), ex);
//		
//		return error;
//	}
	
	
	//handle constraint violation exception modified to separate the exceptions in an array
	@ExceptionHandler(ConstraintViolationException.class) //specify the exact type of exception or error that this error will catch.
	@ResponseStatus(HttpStatus.BAD_REQUEST) //specify the error response status to of the API client.In this case is bad request error
	@ResponseBody //indicate the return value of this method will be directly bound to the web request
	public ErrorDTO handleConstraintViolationtException(HttpServletRequest request, Exception ex) {
		
		//create new ErrorDTO object
		ErrorDTO error = new ErrorDTO();
		
		ConstraintViolationException violationException =  (ConstraintViolationException) ex;
		
		//set values for the fields of ErrorDTO
		error.setTimestamp(new Date()); //set date the error occured
		error.setStatus(HttpStatus.BAD_REQUEST.value()); // return an integer value that represent the error status code
		error.setPath(request.getServletPath()); //return the end point url or request path 
		
		//get the exceptions in the violationException object
		var constraintViolation = violationException.getConstraintViolations();
		
		//iterate through each ConstraintViolationException in the violationException object
		constraintViolation.forEach(constrain -> {
			//add errors in the field path. thus the field that has the error using the getPropertyPath() method
			// and the error message for the affected field
			error.addError(constrain.getPropertyPath() + ": " + constrain.getMessage());
		});
	
		//log the exception object
		LOGGER.error(ex.getMessage(), ex);
		
		return error;
	}
	
	//handle LOCATION NOT FOUND exception
	@ExceptionHandler(LocationNotFoundException.class) //specify the exact type of exception or error that this error will catch.
	@ResponseStatus(HttpStatus.NOT_FOUND) //specify the error response status to of the API client.In this case is bad request error
	@ResponseBody //indicate the return value of this method will be directly bound to the web request
	public ErrorDTO handleLocationNotFoundException(HttpServletRequest request, Exception ex) {
		
		//create new ErrorDTO object
		ErrorDTO error = new ErrorDTO();
		
		//set values for the fields of ErrorDTO
		error.setTimestamp(new Date()); //set date the error occured
		error.setStatus(HttpStatus.NOT_FOUND.value()); // return an integer value that represent the error status code
		
		error.addError(ex.getMessage()); //error message from the exception object
		error.setPath(request.getServletPath()); //return the end point url or request path 
	
		//log the exception object
		LOGGER.error(ex.getMessage(), ex);
		
		return error;
	}
	
	
	//customize the error response
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		//log the exception object
		LOGGER.error(ex.getMessage(), ex);
		
		//create new ErrorDTO object
		ErrorDTO error = new ErrorDTO();
		
		//set values for the fields of ErrorDTO. 
		error.setTimestamp(new Date());
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setPath(((ServletWebRequest) request).getRequest().getServletPath());
		
		//error message for each field in the request body
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		
		fieldErrors.forEach(fieldError -> {
			//add message to the error object declared above
			error.addError(fieldError.getDefaultMessage()); //get default error message for the fields
		});
		
		//return new response entity by passing in the error, headers and status
		return new ResponseEntity<>(error, headers, status);
	}
	
	
	
	
}
