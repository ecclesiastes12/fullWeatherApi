package com.skyapi.weatherforecast;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import com.skyapi.weatherforecast.common.Location;

@Service
public class GeolocationService {
	//logger for logging the error
	private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);

	//declare path for IP2location database
	private String DBPath = "/ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	
	//create field of type IP2Location and instantiate it
	private IP2Location ipLocator = new IP2Location();

//	//open the ip2location db in the constructor
//	public GeolocationService() {
//		try {
//			//opens the ip2location database
//			ipLocator.Open(DBPath);
//			
//		} catch (IOException ex) {
//			// log the error in case there's exception
//			//parameter 1 is the error message
//			//parameter 2 is the exception object
//			LOGGER.error(ex.getMessage(), ex);
//		}
//	}
	
	//code modified to read ip2location db file from the resources directory.This is because 
	//initially ip2location db file was outside resources directory 
	//open the ip2location db in the constructor
	public GeolocationService() {
		try {
			//read the files inside the resources folder in the java file as an input stream
			InputStream inputStream = getClass().getResourceAsStream(DBPath);
			
			//reads the data in bytes
			byte[] data = inputStream.readAllBytes();
			
			//opens the ip2location database (data)
			ipLocator.Open(data);
			
			//close the inputstream
			inputStream.close();
			
			
		} catch (IOException ex) {
			// log the error in case there's exception
			//parameter 1 is the error message
			//parameter 2 is the exception object
			LOGGER.error(ex.getMessage(), ex);
		}
	}
		
	
	//Get location method that returns a location object from an ip address
	public Location getLocation(String ipAddress) throws GeolocationException {
		try {
			//get location data by using ipLocation object which returns IPResult
			//IPResult is used to store geolocation data returned by IP2Location class or database.
			IPResult result = ipLocator.IPQuery(ipAddress);
			
			//check status of ipresult object
			if(!"OK".equals(result.getStatus())) { //if status is not ok
				//throws new exception
				throw new GeolocationException("Geolocation failed with status: " + result.getStatus());
			}
			
			//returns new location object and populate with values from IPResult object
			//this values are stated in the location constructor
			return new Location(result.getCity(), result.getRegion(), result.getCountryLong(), result.getCountryShort());
			
		} catch (IOException ex) {
			throw new GeolocationException("Error quering IP database", ex);
		}
	}
	
	
	
	
	
	
	
}
