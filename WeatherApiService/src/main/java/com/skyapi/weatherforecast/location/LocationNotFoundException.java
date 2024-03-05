package com.skyapi.weatherforecast.location;

//public class LocationNotFoundException extends Exception {
//
////	public LocationNotFoundException(String message) {
////		super(message);
////	}
//	
//	//modified with exeption message
//	public LocationNotFoundException(String locationCode) {
//		super("No location found with the given code: " + locationCode);
//	}
//
//}


//modified to extend RuntimeException because this exception is now handled by GlobalExceptionHandler.java
public class LocationNotFoundException extends RuntimeException {

	//modified with exeption message
	public LocationNotFoundException(String locationCode) {
		super("No location found with the given code: " + locationCode);
	}

	
	//exeption that take two parameters country code and city name. this exception will be used by realtime weather service class 
	public LocationNotFoundException(String countryCode, String cityName) {
		super("No location found with the given country code: " + countryCode + " and city name: " + cityName);
	}
}
