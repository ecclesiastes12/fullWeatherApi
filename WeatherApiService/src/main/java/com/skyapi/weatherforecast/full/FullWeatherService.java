package com.skyapi.weatherforecast.full;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class FullWeatherService {

	private LocationRepository repo;
	
	public FullWeatherService(LocationRepository repo) {
		super();
		this.repo = repo;
	}
	
	//business method that return location object.
	//takes the client ip as a parameter
	public Location getByLocation(Location locationFromIP) {
		//get country code and city name from ip address
		String cityName = locationFromIP.getCityName();
		String countryCode = locationFromIP.getCountryCode();
		
		
		//invoke findByCountryCodeAndCityName of the location repository interface that returns the location object
		Location locationInDB = repo.findByCountryCodeAndCityName(countryCode, cityName);
		
		//check if the returned result is null or not
		if(locationInDB == null) {
			//throws LocationNotFoundExeception if the returned result is null
			throw new LocationNotFoundException(countryCode, cityName);
			
		}
		
		return locationInDB;
	}
	
	//business method that return location object by location code
	public Location get(String locationCode) {
		//get the location object from the db using the location code
		Location location = repo.findByCode(locationCode);
		
		if(location == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		return location;
	}
	
	//business method that update FullWeatherService. this method takes 2 parameters:
	//1 - String representing location code
	//2 - location object from the request which return the updated object
	public Location update(String locationCode, Location locationInRequest) {
		//get the location code from the db using findByCode method which return location object
		Location locationInDB = repo.findByCode(locationCode);
		  
		//throws exception if location is null
		if(locationInDB == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		/**
		 * update location if location is not null.Per the api documentation in the
		 * json request body there is no location information in realtime_weather,hourly_forecast 
		 * and daily_forecast. So we have to set location for realtime_weather,hourly_forecast
		 *  and daily_forecast when updating location.
		 */
		
		//get realtime-weather from locationInRequest object
		RealtimeWeather realtimeWeather = locationInRequest.getRealtimeWeather();
		
		//set location in realtime_weather
		realtimeWeather.setLocation(locationInDB);
		 
		//set last updated time
		realtimeWeather.setLastUpdated(new Date());
		
		//check if realtime weather for a location in the database is null
		if(locationInDB.getRealtimeWeather() == null) {
			//set realtime weather for the locationInDB
			locationInDB.setRealtimeWeather(realtimeWeather);
			//save locationInDB which is the location object
			repo.save(locationInDB);
		}
		
		//update location for each item in the listHourlyWeather and listDailyWeather in the location object
		List<DailyWeather> listDailyWeather = locationInRequest.getListDailyWeather();
		listDailyWeather.forEach(dw -> dw.getId().setLocation(locationInDB));
		
		
		List<HourlyWeather> listHourlyWeather = locationInRequest.getListHourlyWeather();
		listHourlyWeather.forEach(hw -> hw.getId().setLocation(locationInDB));
		
		/**
		 * NB since we will save the locationInRequest object but in the requestBody there
		 * there is no information on location such as countryName,cityName,countryCode etc.
		 * So we have to copy all the information in locationInDB object to locationInRequest object
		 * such as the code below. Always check the api documentation
		 */
		
		locationInRequest.setCode(locationInDB.getCode());
		locationInRequest.setCityName(locationInDB.getCityName());
		locationInRequest.setRegionName(locationInDB.getRegionName());
		locationInRequest.setCountryCode(locationInDB.getCountryCode());
		locationInRequest.setCountryName(locationInDB.getCountryName());
		locationInRequest.setEnabled(locationInDB.isEnabled());
		locationInRequest.setTrashed(locationInDB.isTrashed());
		
		
		return repo.save(locationInRequest);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
