package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.location.LocationRepository;

@Service
public class DailyWeatherService {

	private DailyWeatherRepository dailyWeatherRepo;
	private LocationRepository locationRepo;
	
	public DailyWeatherService(DailyWeatherRepository dailyWeatherRepo, LocationRepository locationRepo) {
		super();
		this.dailyWeatherRepo = dailyWeatherRepo;
		this.locationRepo = locationRepo;
	}
	

	public List<DailyWeather> getByLocation(Location location) {
		//get country code and city name of location
		String countryCode = location.getCountryCode();
		String cityName = location.getCityName();
		
		//get location from the database base on country code and city name
		Location locationInDB = locationRepo.findByCountryCodeAndCityName(countryCode, cityName);
		
		//check if locationInDB is null
		if (locationInDB == null) {
			throw new LocationNotFoundException(countryCode, cityName);
		}
		
		return dailyWeatherRepo.findByLocationCode(locationInDB.getCode());
	}

	
	//service method that list daily weather forecast by location code
	public List<DailyWeather> getByLocationCode(String locationCode) {
		
		//get location from the db
		Location location = locationRepo.findByCode(locationCode);
		
		//check if location is  null or empty
		if (location == null) {
			throw new LocationNotFoundException(locationCode);
		}
		
		return dailyWeatherRepo.findByLocationCode(locationCode);
	}
	
	
	//service method that update daily weather forecast(list of daily weather forecast)
	public List<DailyWeather> updateByLocationCode(String code, List<DailyWeather> dailyWeatherInRequest) {
		//get the location code from the database
		Location location = locationRepo.findByCode(code);
		
		//check if location is null
		if (location == null) {
			throw new LocationNotFoundException(code);
		}
		
		//loop or iterate over the list of daily weather in request and set the location code for it.
		for (DailyWeather data : dailyWeatherInRequest) {
			//set id (location code) the for list of daily weather in request
			data.getId().setLocation(location);
		}
		
		//NB this part is for DELETING daily weather forecast which is in the db but not in the request body of the update list data of daily weather forecast.
		//Get list of daily weather forecast from the location object in the db.
		
		List<DailyWeather> dailyWeatherInDB = location.getListDailyWeather();
		
		//list daily weather forecast element to be delated
		List<DailyWeather> dailyWeatherToBeRemoved = new ArrayList<>();
		
		//iterate over each dailyWeatherForecast data in db
		for (DailyWeather forecast : dailyWeatherInDB) {
			//check if the data in dailyWeatherInRequest does not contain a data in dailyWeatherInDB
			if (!dailyWeatherInRequest.contains(forecast)) {
				//add the id of the data that is in dailyWeatherInDB but not in  dailyForecastInRequest to the list 
				//of daily weather forecast data to be deleted
				dailyWeatherToBeRemoved.add(forecast.getShallowCopy());
			}
		}
		
		//iterate over each data in dailyWeatherForecastToBeRemoved
		for (DailyWeather forecastToBeRemoved : dailyWeatherToBeRemoved) {
			//remove the item or data added to dailyWeatherForecastToBeRemoved and 
			//spring jpa will remove the item from the db
			dailyWeatherInDB.remove(forecastToBeRemoved);
		}
		
		return (List<DailyWeather>) dailyWeatherRepo.saveAll(dailyWeatherInRequest);
	}
	
	
	
	
	
	

}
