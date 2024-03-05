package com.skyapi.weatherforecast.hourly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class HourlyWeatherService {

	private HourlyWeatherRepository hourlyWeatherRepo;
	private LocationRepository locationRepo;
	
	public HourlyWeatherService(HourlyWeatherRepository hourlyWeatherRepo, LocationRepository locationRepo) {
		super();
		this.hourlyWeatherRepo = hourlyWeatherRepo;
		this.locationRepo = locationRepo;
	}
	
	//code modified after LocationNotFoundException is handled by GlobalExceptionHandler.java
	//and exception message moved to LocationNotFoundException.java. check HourlyWeatherService2.java for previous code
	
	//service method that list hourly weather forecast based on given location and current hour
	public List<HourlyWeather> getByLocation(Location location, int currentHour) {
		//get country code and city name from location object
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		
		//get location from the database base on country code and city name
		Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);
		
		//throws exception if result from locationInDB is null
		if(locationInDB == null) {
			throw new LocationNotFoundException( countryCode,cityName);
		}
		
		return hourlyWeatherRepo.findByLocationCode(locationInDB.getCode(), currentHour);
	}
	
	//code modified after LocationNotFoundException is handled by GlobalExceptionHandler.java
	//and exception message moved to LocationNotFoundException.java. check HourlyWeatherService2.java for previous code
	
	//service method that get hourly weather forecast by location code and current hour
	public List<HourlyWeather> getByLocationCode(String locationCode, int currentHour) {
		
		//Get location from the database
		Location locationInDB = locationRepo.findByCode(locationCode);
		
		//check if location is null
		if(locationInDB == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		return hourlyWeatherRepo.findByLocationCode(locationCode, currentHour);
		
	}
	
//	//business method that update list of hourly weather forecast information by location code
//	public List<HourlyWeather> updateByLocationCode(String locationCode, List<HourlyWeather> hourlyForecastInRequest) throws LocationNotFoundException{
//		//retrieve the location code
//		Location location = locationRepo.findByCode(locationCode);
//		
//		//check if location is null
//		if(location == null) {
//			//throws exception
//			throw new LocationNotFoundException("No location found with the given location code: " + locationCode);
//		}
//		
//		//return empty collection
//		return Collections.emptyList();
//	}
	
	//code modified after LocationNotFoundException is handled by GlobalExceptionHandler.java
	//and exception message moved to LocationNotFoundException.java. check HourlyWeatherService2.java for previous code
	
	//business method that update list of hourly weather forecast information by location code.
	//code modified to return list of hourly weather data not empty collection
	public List<HourlyWeather> updateByLocationCode(String locationCode, 
			List<HourlyWeather> hourlyWeatherInRequest) {
		//retrieve the location code
		Location location = locationRepo.findByCode(locationCode);
		
		//check if location is null
		if (location == null) {
			//throws exception
			throw new LocationNotFoundException(locationCode);
		}
		
		//loop through the list of Hourly weather in request and set the location code
		for (HourlyWeather item : hourlyWeatherInRequest) {
			item.getId().setLocation(location);
		}
		
		//NB this part is for DELETING hourly forecast which is in the db but not in the request body of update list of hourly weather forecast.
		//Get list of hourly weather forecast from the location object in the db.
		List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
		
		//list of hourly weather forecast elements to be deleted
		List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();
		
		//loops through each hourlyWeatherInDB 
		for (HourlyWeather item : hourlyWeatherInDB) {
			//check if hourlyWeatherInRequest does not contains hourly weather item in the db
			if (!hourlyWeatherInRequest.contains(item)) {
				//add the item to the list of hourly weather forecast to be deleted
				hourlyWeatherToBeRemoved.add(item.getShallowCopy());
			}
		}
		
		
		//iterate through each item of hourly weather to be removed
		for (HourlyWeather item : hourlyWeatherToBeRemoved) {
			//remove the item from the list hourlyweather in db and spring jpa will remove the item from the db
			hourlyWeatherInDB.remove(item);
		}
		
		
		
		//return 
		return (List<HourlyWeather>) hourlyWeatherRepo.saveAll(hourlyWeatherInRequest);
	}
	
	
	//updateByLocationCode the return empty collection
//	public List<HourlyWeather> updateByLocationCode(String locationCode, 
//			List<HourlyWeather> hourlyForecastInRequest){
//		
//		Location location = locationRepo.findByCode(locationCode);
//		
//		if(location == null) {
//			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
//		}
//		return Collections.emptyList();
//	}
	
	//updateByLocationCode that return list or collection of hourlyForecastInRequest
//	public List<HourlyWeather> updateByLocationCode(String locationCode, 
//			List<HourlyWeather> hourlyWeatherInRequest){
//		
//		Location location = locationRepo.findByCode(locationCode);
//		
//		if(location == null) {
//			throw new LocationNotFoundException("No location found with the given code: " + locationCode);
//		}
//		
//		
//		for(HourlyWeather item : hourlyWeatherInRequest) {
//			item.getId().setLocation(location);
//		}
//		
//		//for deleting data that is not in the request body when updating the hourly weather forecast
//		List<HourlyWeather> hourlyWeatherInDB = location.getListHourlyWeather();
//		List<HourlyWeather> hourlyWeatherToBeRemoved = new ArrayList<>();
//		
//		for(HourlyWeather item : hourlyWeatherInDB) {
//			if(!hourlyWeatherInRequest.contains(item)) {
//				hourlyWeatherToBeRemoved.add(item.getShallowCopy());
//			}
//		}
//		
//		//iterate through each item in the hourly weather to be removed
//		for(HourlyWeather item : hourlyWeatherToBeRemoved) {
//			hourlyWeatherInDB.remove(item);
//		}
//		
//		
//		//NB per the api specification updateByLocationCode should replace all existing weather forecast data.
//		//save list of hourly weather forecast
//		return (List<HourlyWeather>) hourlyWeatherRepo.saveAll(hourlyWeatherInRequest);
//	}
	
	
	
	
	
	
	
	
	
	
	
}
