package com.skyapi.weatherforecast.realtime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.RealtimeWeather;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RealtimeWeatherRepositoryTests {
	
	@Autowired
	private RealtimeWeatherRepository repo;
	
	//repository test that update realtime weather 
	@Test
	public void testUpdate() {
		//declare location code to update the realtime weather
		String locationCode = "NYC_USA";
		
		//get the realtime object from the database
		RealtimeWeather realtimeWeather =repo.findById(locationCode).get();
		
		//set value for the realtime weather fields
		realtimeWeather.setTemperature(-2);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setPrecipitation(42);
		realtimeWeather.setStatus("Snowy");
		realtimeWeather.setWindSpeed(12);
		realtimeWeather.setLastUpdated(new Date());
		
		//save realtime weather object
		RealtimeWeather updatedRealtimeWeather = repo.save(realtimeWeather);
		
		assertThat(updatedRealtimeWeather.getHumidity()).isEqualTo(32);
	}
	
	
	//test method for negative case where country code and city name does not exist in the location table
	@Test
	public void testFindByCountryCodeAndCityNotFound() {
		//declare country code and city name
		String countryCode = "JP";
		String cityName = "Tokyo";
		
		RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);
		
		assertThat(realtimeWeather).isNull();
	}
	
	//test method for positive case where country code and city name exist in the database
	@Test
	public void testFindByCountryCodeAndCityFound() {
		//declare country code and city name
		String countryCode = "US";
		String cityName = "New York City";
		
		RealtimeWeather realtimeWeather = repo.findByCountryCodeAndCity(countryCode, cityName);
		
		assertThat(realtimeWeather).isNotNull();
		assertThat(realtimeWeather.getLocation().getCityName()).isEqualTo(cityName);
		assertThat(realtimeWeather.getLocation().getCountryCode()).isEqualTo(countryCode);
		
	}
	
	
	//test method that return location not found response by a given location code
	@Test
	public void testFindByLocationNotFound() {
		//declare location variable
		String code = "ABCD";
		
		RealtimeWeather realtimeWeather = repo.findByLocationCode(code);
		
		assertThat(realtimeWeather).isNull();
	}
	
	//test method that return location not found response by a given location code but the location code is trashed
	@Test
	public void testFindByTrashedLocationNotFound() {
		//declare location variable
		String code = "LACA_USA";
		
		RealtimeWeather realtimeWeather = repo.findByLocationCode(code);
		
		assertThat(realtimeWeather).isNull();
	}
	
	//test method that return location found response by a given location code
		@Test
		public void testFindByLocationFound() {
			//declare location variable
			String code = "NYC_USA";
			
			RealtimeWeather realtimeWeather = repo.findByLocationCode(code);
			
			assertThat(realtimeWeather).isNotNull();
			assertThat(realtimeWeather.getLocation().getCode()).isEqualTo(code);
		}
	
}
