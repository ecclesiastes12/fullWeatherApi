package com.skyapi.weatherforecast.hourly;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class HourlyWeatherRepositoryTests {

	@Autowired private HourlyWeatherRepository repo;
	
	//Test method that add a single hourly weather data forecast based on
	//location code and currentHour or hourOfDay
	@Test
	public void testAdd() {
		String locationCode = "DELHI_IN";
		int hourOfDay = 12;
		
		
		//Location location = new Location();
		//location.code(locationCode);
		
		//same as the commented code above
		Location location = new Location().code(locationCode);
		
		HourlyWeather forecast = new HourlyWeather()
						.location(location)
						.hourOfDay(hourOfDay)
						.temperature(13)
						.precipitarion(70)
						.status("Cloudy");
		
		HourlyWeather updatedForecast = repo.save(forecast);
		
		assertThat(updatedForecast.getId().getLocation()).isEqualTo(location);
		assertThat(updatedForecast.getId().getHourOfDay()).isEqualTo(hourOfDay);
				
	}
	
	
	//test method that delete hourly weather forecast data. 
	@Test
	public void testDelete() {
		Location location = new Location().code("DELHI_IN");
		
		HourlyWeatherId id = new HourlyWeatherId(10, location);
		repo.deleteById(id);
		
		//check if the deleted data is deleted
		Optional<HourlyWeather> result = repo.findById(id);
		assertThat(result).isNotPresent();
	}
	
	
	//test method that list all upcoming hourly weather forecast greater than the
	//current hour or after the current hour
	@Test
	public void testFindByLocationCodeFound() {
		String locationCode = "DELHI_IN";
		int currentHour = 15;
		
		//list all weather forecast with hours greater than the current hour which is 10
		List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);
		
		assertThat(hourlyForecast).isNotEmpty();//expects the result not to be empty
	}
	
	//test method that return No hourly weather forecast data given the current hour and the location code
	@Test
	public void testFindByLocationCodeNotFound() {
		String locationCode = "MBMH_IN";
		int currentHour = 6;
		
		//list all weather forecast with hours greater than the current hour which is 10
		List<HourlyWeather> hourlyForecast = repo.findByLocationCode(locationCode, currentHour);
		
		assertThat(hourlyForecast).isEmpty();//expects the result not to be empty
	}
	
}
