package com.skyapi.weatherforecast.realtime;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class RealtimeWeatherService {

	//reference of realtime weather repository
	private RealtimeWeatherRepository realtimeWeatherRepo;
	private LocationRepository locationRepo;

	//constructor base injection
	public RealtimeWeatherService(RealtimeWeatherRepository realtimeWeatherRepo,
			LocationRepository locationRepo) {
		super();
		this.realtimeWeatherRepo = realtimeWeatherRepo;
		this.locationRepo = locationRepo;
	}
	
//	//service method that return realtime weather object based on location object
//	public RealtimeWeather getByLocation(Location location) throws LocationNotFoundException {
//		//get the country code and city name from the location object
//		String countryCode = location.getCountryCode();
//		String cityName = location.getCityName();
//		
//		//find realtime weather based on country code and city name
//		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);
//		
//		//check if the returned results is null
//		if(realtimeWeather == null) {
//			//throws exception if the above condition is true
//			throw new LocationNotFoundException("No location found with the given country code and city name");
//		}
//		
//		return realtimeWeather;
//		
//	}
	
	//code modified after LocationNotFoundException handled by GlobalExceptionHandler.java 
	//service method that return realtime weather object based on location object
	public RealtimeWeather getByLocation(Location location) {
		//get the country code and city name from the location object
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		
		//find realtime weather based on country code and city name
		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByCountryCodeAndCity(countryCode, cityName);
		
		//check if the returned results is null
		if(realtimeWeather == null) {
			//throws exception if the above condition is true
			throw new LocationNotFoundException(countryCode,cityName);
		}
		
		return realtimeWeather;
		
	}
	
	//code modified after LocationNotFoundException handled by GlobalExceptionHandler.java 
	//service method that return realtime weather object based on a given location code
	public RealtimeWeather getByLocationCode(String locationCode) {
		//find realtime weather based on location code
		RealtimeWeather realtimeWeather = realtimeWeatherRepo.findByLocationCode(locationCode);
		
		//check if returne results is null
		if(realtimeWeather == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		return realtimeWeather;
	}
	
	//code modified after LocationNotFoundException handled by GlobalExceptionHandler.java 
	
	//method that update realtime weather api. this update is used by the api client installed at the weather station.
	//this method takes two parameters
	//parameter 1 the location code for weather location to be updated
	//parameter 2 the realtimeWeather object that represent realtime weather data
	public RealtimeWeather update(String locationCode, RealtimeWeather realtimeWeather)  {
		//get location from the database
		Location location = locationRepo.findByCode(locationCode);
		
		//check if location is null
		if(location == null) {
			//throws location not found exception
			throw new LocationNotFoundException(locationCode);
		}
		  
		//set values for realtime weather object if the above condition is false
		//by setting a reference to location
		realtimeWeather.setLocation(location);
		realtimeWeather.setLastUpdated(new Date());//set last updated time
		
		//check if a realtime weather data for a location is null or location has no associated realtime weather data
		if(location.getRealtimeWeather() == null) {
			//set realtime weather data for location if location has no realtime weather data
			location.setRealtimeWeather(realtimeWeather);
			
			Location updatedLocation = locationRepo.save(location); //update location
			
			return updatedLocation.getRealtimeWeather();// return updated location weather data
		}
		
		return realtimeWeatherRepo.save(realtimeWeather);
	
	}
	
	
}
