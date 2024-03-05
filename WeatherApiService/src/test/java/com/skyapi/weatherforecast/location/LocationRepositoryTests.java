package com.skyapi.weatherforecast.location;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class LocationRepositoryTests {
	
	@Autowired LocationRepository repository;
	
	//test method that add location
	@Test
	public void testAddSuccess() {
		Location location = new Location(); //create a location object
		location.setCode("MBMH_IN");
		location.setCityName("Mumbai");
		location.setRegionName("Mumbai");
		location.setCountryCode("IN");
		location.setCountryName("India");
		location.setEnabled(true);
		
		//save the location
		Location savedLocation = repository.save(location);
		
		assertThat(savedLocation).isNotNull();
		assertThat(savedLocation.getCode()).isEqualTo("MBMH_IN");
	}
	
	//NB this is another way of adding location details if commented code in Location.java is uncommented
//	@Test
//	public void testAddSuccess1() {
//		Location location = new Location()
//				.code("MBMH_IN")  //method chaining
//				.cityName("Mumbai") //method chaining
//				.regionName("Mumbai") //method chaining
//				.countryCode("IN") //method chaining
//				.countryName("India")
//				.enabled(true);
//		
//		//save the location
//		Location savedLocation = repository.save(location);
//		
//		assertThat(savedLocation).isNotNull();
//		assertThat(savedLocation.getCode()).isEqualTo("MBMH_IN");
//	}
	

	//test method that list location
	
	@Test
	public void testListSuccess() {
		List<Location> locations = repository.findUntrashed();
		
		assertThat(locations).isNotEmpty();
		
		locations.forEach(System.out :: println);
		
	}
	
	//test method that will return location not found with a given location code
	@Test
	public void testGetNotFound() {
		//get location by a given code
		String code = "ABCD";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull(); //we expect the return to be null
	}
	
	//test method that will return location found with a given location code
	@Test
	public void testGetFound() {
		//get location by a given code
		String code = "DELHI_IN";
		Location location = repository.findByCode(code);
		
		assertThat(location).isNotNull(); //we expect the return response not to be null
		
		//assert that the code of the location is equal to the given value
		assertThat(location.getCode()).isEqualTo(code);
		System.out.println(location);
	}
	
	//test method that trash location succesfully
	@Test
	public void testTrashSuccess() {
		//location code to be trashed
		String code = "LACA_USA";
		
		//trashed location by code
		repository.trashByCode(code);
		
		//find location after being trashed to ensure that it was successful
		//if data is return it means it wasn't successful
		Location location = repository.findByCode(code);
		
		assertThat(location).isNull();
	}
	
	
	//Repository test for realtime weather. this test add realtime weather data from Location side.
	//thus by using weather object from location class
	@Test
	public void testAddRealtimeWeatherData() {
		//declare location code for realtime weather
		String code = "NYC_USA"; 
		
		//get the location object from the database 
		Location location = repository.findByCode(code);
		
		//get realtime weather object from the location object
		RealtimeWeather realtimeWeather = location.getRealtimeWeather();
		
		//NB Because location may not have a realtime data we have to check if realtime weather is null first.
		if(realtimeWeather == null) {
			//create a new realtime weather object of realtime weather is null
			realtimeWeather = new RealtimeWeather();
			
			//set reference of location to this new realtime weather object
			realtimeWeather.setLocation(location);
			
			//set reference of realtime weather to location object back
			location.setRealtimeWeather(realtimeWeather);
		}
		
		//set values to the fields in realtime weather object
		realtimeWeather.setTemperature(-1);
		realtimeWeather.setHumidity(30);
		realtimeWeather.setPrecipitation(40);
		realtimeWeather.setStatus("Snowy");
		realtimeWeather.setWindSpeed(15);
		realtimeWeather.setLastUpdated(new Date());
		
		Location updatedLocation = repository.save(location);
		
		assertThat(updatedLocation.getRealtimeWeather().getLocationCode()).isEqualTo(code);
	}
	
	//repository test that adds an array of hourly weather forecast data 
	//based on location and hourOfDay
	@Test
	public void testAddHourlyWeatherData() {
		//get location from database
		Location location = repository.findById("DELHI_IN").get();
		
		//list hourly weather
		List<HourlyWeather> listHourlyWeather = location.getListHourlyWeather();
		
		//set the hourly weather data for the weather forecast using the builder methods
		HourlyWeather forecast1 = new HourlyWeather().id(location, 14) //id builder method in HourlyWeather class
										.temperature(18) //temperature builder method in HourlyWeather class
										.precipitarion(50) //precipitation builder method in HourlyWeather class
										.status("Sunny"); //status builder method in HourlyWeather class
		
		HourlyWeather forecast2 = new HourlyWeather()
									.location(location) //location builder method in HourlyWeather class
									.hourOfDay(15) //hourOfDay builder method in HourlyWeather class
									.temperature(20) 
									.precipitarion(55)
									.status("Cloudy");
		
		//add hourly weather data to the list of hourly weather forecase data
		listHourlyWeather.add(forecast1);
		listHourlyWeather.add(forecast2);
		
		//save the data
		Location updatedLocation = repository.save(location);

		assertThat(updatedLocation.getListHourlyWeather()).isNotEmpty();
	}
	
	
	//test method that return 404 not found response status code. That no location
	//found based on a given country code and city name
	@Test
	public void testFindByCountryCodeAndCityNameNotFound() {
		String countryCode = "BZ";
		String cityName = "City";
		
		Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
		
		assertThat(location).isNull(); //we expect location to be null
	}
	
	
	//test method that return 200 ok response status code. Location found based on a given country 
	//code and city name
	@Test
	public void testFindByCountryCodeAndCityNameFound() {
		String countryCode = "NYC_US";
		String cityName = "New York City";
		
		Location location = repository.findByCountryCodeAndCityName(countryCode, cityName);
		
		assertThat(location).isNotNull();
		assertThat(location.getCountryCode()).isEqualTo(countryCode);
		assertThat(location.getCityName()).isEqualTo(cityName);
	}
	
	
	
	//repository test that add daily weather
	@Test
	public void testAddDailyWeatherData() {
		//get location by code
		Location location = repository.findById("DELHI_IN").get();
		
		//Get list of daily weather from location
		List<DailyWeather> listDailyWeather = location.getListDailyWeather();
		
		//create daily weather forecast
		DailyWeather forecast1 = new DailyWeather()
								.location(location)
								.dayOfMonth(16)
								.month(7)
								.minTemp(25)
								.maxTemp(33)
								.precipitation(20)
								.status("Sunny");
		
		DailyWeather forecast2 = new DailyWeather()
				.location(location)
				.dayOfMonth(17)
				.month(7)
				.minTemp(26)
				.maxTemp(34)
				.precipitation(10)
				.status("Clear");
		
		//add daily weather forecast to the list of daily weather forecast
		listDailyWeather.add(forecast1);
		listDailyWeather.add(forecast2);
		
		Location updatedLocation = repository.save(location);
		
		assertThat(updatedLocation.getListDailyWeather()).isNotEmpty();
	}
	
	
	
	
	
	
	
	
	
}
