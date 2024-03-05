package com.skyapi.weatherforecast.daily;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.DailyWeatherId;
import com.skyapi.weatherforecast.common.Location;

@DataJpaTest
@AutoConfigureTestDatabase(replace =  Replace.NONE)
@Rollback(false)
public class DailyWeatherRepositoryTest {

	@Autowired private DailyWeatherRepository repo;
	
	@Test
	public void testAdd() {
		
		//location code
		String locationCode = "DANA_VN";
		
		//create location object from the given location code
		Location location = new Location().code(locationCode);
		
		//create daily weather forecast
		DailyWeather forecast = new DailyWeather()
								.location(location)
								.dayOfMonth(16)
								.month(7)
								.minTemp(23)
								.maxTemp(32)
								.precipitation(40)
								.status("Cloudy");
		
		DailyWeather addedForecast = repo.save(forecast);
		
		assertThat(addedForecast.getId().getLocation().getCode()).isEqualTo(locationCode);
		
	}
	
	//repository test method that delete daily weather forecast
	@Test
	public void testDelete() {
		//location
		String locationCode = "DELHI_IN";
		
		//get the location object
		Location location = new Location().code(locationCode);
		
		//delete daily weather by dailyweatherid object which is a composite key
		DailyWeatherId id = new DailyWeatherId(16, 7, location);
		
		repo.deleteById(id);
		
		 Optional<DailyWeather> result = repo.findById(id);
		
		 assertThat(result).isNotPresent();
	}
	
	
	//test method that find daily weather by ip address
	@Test
	public void testFindByLocationCodeFound() {
		String locationCode = "DELHI_IN";
		
		List<DailyWeather> dailyWeather = repo.findByLocationCode(locationCode);
		
		assertThat(dailyWeather).isNotEmpty();
		dailyWeather.forEach(System.out :: println);
	}
	
	//test that method that return location not found
	@Test
	public void testFindByLocationCodeNotFound() {
		String locationCode = "ABC_XYZ";
		
		List<DailyWeather> dailyWeather = repo.findByLocationCode(locationCode);
		
		assertThat(dailyWeather).isEmpty();
		dailyWeather.forEach(System.out :: println);
	}
}
