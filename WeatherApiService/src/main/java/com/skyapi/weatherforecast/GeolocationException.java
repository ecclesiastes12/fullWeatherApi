package com.skyapi.weatherforecast;

//public class GeolocationException extends Exception {
//
//	public GeolocationException(String message, Throwable cause) {
//		super(message, cause);
//		// TODO Auto-generated constructor stub
//	}
//
//	public GeolocationException(String message) {
//		super(message);
//		// TODO Auto-generated constructor stub
//	}
//
//}

//Geolocation exception is now handle by global exception handler
public class GeolocationException extends RuntimeException {

	public GeolocationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public GeolocationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

}