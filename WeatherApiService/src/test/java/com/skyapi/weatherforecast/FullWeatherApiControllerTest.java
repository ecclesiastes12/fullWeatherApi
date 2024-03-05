package com.skyapi.weatherforecast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.daily.DailyWeatherDTO;
import com.skyapi.weatherforecast.full.FullWeatherApiController;
import com.skyapi.weatherforecast.full.FullWeatherDTO;
import com.skyapi.weatherforecast.full.FullWeatherService;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.location.LocationNotFoundException;
import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;

@WebMvcTest(FullWeatherApiController.class)
public class FullWeatherApiControllerTest {

	//request uri 
	private static final String END_POINT_PATH = "/v1/full";
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@MockBean private FullWeatherService weatherService;
	@MockBean private GeolocationService locationService;
	
	
	//test method that return 400 Bad Request response status code
	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
		//geolocation exception object
		GeolocationException ex = new GeolocationException("Geolocation error");
		
		//mocks getLocation method geolocation service and throws exception if IPAddress of location is not found
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
				.andDo(print());
	}
	
	//test method that return 404 Not found response status code
	@Test
	public void testGetByIPShouldReturn404NotFound() throws Exception {
		Location location = new Location().code("DELHI_IN");
		
		//mocks getLocation method geolocation service and throws exception if IPAddress of location is not found
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);
		
		LocationNotFoundException ex = new LocationNotFoundException(location.getCode());
		when(weatherService.getByLocation(location)).thenThrow(ex);
		
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
				.andDo(print());
	}
	
	//test method that return 200 ok entity response status code
	@Test
	public void testGetByIPShouldReturn200OK() throws Exception {
		// create a location object
		Location location = new Location();
		// use regular setters to set field values
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		//creates Realtime Weather object
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		
		//set values for realtime weather fields
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		//set reference of realtime weather for location object
		location.setRealtimeWeather(realtimeWeather);

		DailyWeather dailyForecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32)
				.status("Clear").precipitation(40);

		DailyWeather dailyForecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34)
				.status("Sunny").precipitation(30);

		//set information for list daily weather forecast
		location.setListDailyWeather(List.of(dailyForecast1,dailyForecast2));
		
		
		//create hourly weather forecast object with method chaining instead of regular setters
		HourlyWeather hourlyForecast1 = new HourlyWeather()
									.location(location)
									.hourOfDay(10)
									.temperature(13)
									.precipitarion(70)
									.status("Cloudy");
		
		HourlyWeather hourlyForecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitarion(60)
				.status("Sunny");
		
		//set list hourly weather forecast for the location object
		location.setListHourlyWeather(List.of(hourlyForecast1,hourlyForecast2));
		
		// fakes the geolocation service call to invoke the get location method
		when(locationService.getLocation(Mockito.anyString())).thenReturn(location);

		when(weatherService.getByLocation(location)).thenReturn(location);

		String expectedLocation = location.toString();

		// makes http request
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation)))
				.andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
				 .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
				 .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
				.andDo(print());

	}
	
	//test method that return 404 Not found status code with a given location code
	@Test
	public void testGetByCodeShouldReturn404NotFound() throws Exception {
		String locationCode = "ABC123";
		String requeURI = END_POINT_PATH + "/" + locationCode;
		
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		//mocks get method in fullweather service and throws location is not found if the given location doesn't exist in the database
		when(weatherService.get(locationCode)).thenThrow(ex);
		
		//perform http request
		mockMvc.perform(get(requeURI))
					.andExpect(status().isNotFound())
					.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
					.andDo(print());
		
	}
	
	
	//Test method that return 200 OK response status code given the location code
	@Test
	public void testGetByCodeShouldReturn200OK() throws Exception {
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		// create a location object
		Location location = new Location();
		// use regular setters to set field values
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		//creates Realtime Weather object
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		
		//set values for realtime weather fields
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		//set reference of realtime weather for location object
		location.setRealtimeWeather(realtimeWeather);

		DailyWeather dailyForecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32)
				.status("Clear").precipitation(40);

		DailyWeather dailyForecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34)
				.status("Sunny").precipitation(30);

		//set information for list daily weather forecast
		location.setListDailyWeather(List.of(dailyForecast1,dailyForecast2));
		
		
		//create hourly weather forecast object with method chaining instead of regular setters
		HourlyWeather hourlyForecast1 = new HourlyWeather()
									.location(location)
									.hourOfDay(10)
									.temperature(13)
									.precipitarion(70)
									.status("Cloudy");
		
		HourlyWeather hourlyForecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitarion(60)
				.status("Sunny");
		
		//set list hourly weather forecast for the location object
		location.setListHourlyWeather(List.of(hourlyForecast1,hourlyForecast2));
		
		// fakes the geolocation service call to invoke the get location method
		when(locationService.getLocation(Mockito.anyString())).thenReturn(location);

		when(weatherService.getByLocation(location)).thenReturn(location);

		String expectedLocation = location.toString();

		// makes http request
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation)))
				.andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
				 .andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
				 .andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
				.andDo(print());
		
	}
	
	
	//test method that return 400 Bad Request response status code for update full weather service
	//because listHourlyWeather data for given location is empty
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoHourlyWeather() throws Exception {
		
		//location code
		String locationCode = "NYC_USA";	
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		//create a fullweather dto object
		FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
		
		//use object objectMapper to serialize or convert the dto object to json string
		String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
		
		DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO().dayOfMonth(16).month(7).minTemp(23).maxTemp(32)
				.status("Clear").precipitation(40);
		
		fullWeatherDTO.getListDailyWeather().add(dailyForecast1);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is("Hourly Weather data cannot be empty")))
				.andDo(print());
	}
	
	//test method that return 400 Bad Request response status code for update full weather service
	//because listDailyWeather data for given location is empty
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoDailyWeather() throws Exception {
		
		//location code
		String locationCode = "NYC_USA";	
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		//create a fullweather dto object
		FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
		
		HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
				.hourOfDay(10)
				.temperature(13)
				.precipitarion(70)
				.status("Cloudy");
		
		fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);
		
		
		//use object objectMapper to serialize or convert the dto object to json string
		String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is("Daily Weather data cannot be empty")))
				.andDo(print());
	}
	
	
	//test method that return 400 Bad Request response status code for update full weather service
	//because invalid realtime weather data for given location is empty
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseInvalidRealtimeWeather() throws Exception {
		
		//location code
		String locationCode = "NYC_USA";	
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		//create a fullweather dto object
		FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
		
		HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
				.hourOfDay(10)
				.temperature(13)
				.precipitarion(70)
				.status("Cloudy");
		
		fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);
		
		DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
				.dayOfMonth(16)
				.month(7)
				.minTemp(23)
				.maxTemp(32)
				.status("Clear")
				.precipitation(40);
		
		fullWeatherDTO.getListDailyWeather().add(dailyForecast1);
		
		
		//creates Realtime Weather object
		RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
		
		//set values for realtime weather fields
		realtimeDTO.setTemperature(122); //invalid temperature value
		realtimeDTO.setHumidity(32);
		realtimeDTO.setLastUpdated(new Date());
		realtimeDTO.setPrecipitation(88);
		realtimeDTO.setStatus("Cloudy");
		realtimeDTO.setWindSpeed(5);
		
		fullWeatherDTO.setRealtimeWeather(realtimeDTO);
		
		//use object objectMapper to serialize or convert the dto object to json string
		String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in the range")))
				.andDo(print());
	}
	
	//test method that return 400 Bad Request response status code for update full weather service
		//because invalid hourly weather data for given location is empty
		@Test
		public void testUpdateShouldReturn400BadRequestBecauseInvalidHourlyWeatherData() throws Exception {
			
			//location code
			String locationCode = "NYC_USA";	
			String requestURI = END_POINT_PATH + "/" + locationCode;
			
			//create a fullweather dto object
			FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
			
			HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
					.hourOfDay(10)
					.temperature(133)//invalid temperature value
					.precipitarion(70)
					.status("Cloudy");
			
			fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);
			
			DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
					.dayOfMonth(16)
					.month(7)
					.minTemp(23)
					.maxTemp(32)
					.status("Clear")
					.precipitation(40);
			
			fullWeatherDTO.getListDailyWeather().add(dailyForecast1);
			
			
			//creates Realtime Weather object
			RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
			
			//set values for realtime weather fields
			realtimeDTO.setTemperature(12); 
			realtimeDTO.setHumidity(32);
			realtimeDTO.setLastUpdated(new Date());
			realtimeDTO.setPrecipitation(88);
			realtimeDTO.setStatus("Cloudy");
			realtimeDTO.setWindSpeed(5);
			
			fullWeatherDTO.setRealtimeWeather(realtimeDTO);
			
			//use object objectMapper to serialize or convert the dto object to json string
			String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
			
			mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in the range")))
					.andDo(print());
		}
		
		//test method that return 400 Bad Request response status code for update full weather service
		//because invalid daily weather data for given location is empty
		@Test
		public void testUpdateShouldReturn400BadRequestBecauseInvalidDailyWeatherData() throws Exception {
			
			//location code
			String locationCode = "NYC_USA";	
			String requestURI = END_POINT_PATH + "/" + locationCode;
			
			//create a fullweather dto object
			FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
			
			HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
					.hourOfDay(10)
					.temperature(33)
					.precipitarion(70)
					.status("Cloudy");
			
			fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);
			
			DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
					.dayOfMonth(16)
					.month(7)
					.minTemp(23)
					.maxTemp(32)
					.status("")//invalid status value
					.precipitation(40);
			
			fullWeatherDTO.getListDailyWeather().add(dailyForecast1);
			
			
			//creates Realtime Weather object
			RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
			
			//set values for realtime weather fields
			realtimeDTO.setTemperature(12); 
			realtimeDTO.setHumidity(32);
			realtimeDTO.setLastUpdated(new Date());
			realtimeDTO.setPrecipitation(88);
			realtimeDTO.setStatus("Cloudy");
			realtimeDTO.setWindSpeed(5);
			
			fullWeatherDTO.setRealtimeWeather(realtimeDTO);
			
			//use object objectMapper to serialize or convert the dto object to json string
			String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
			
			mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
					.andExpect(status().isBadRequest())
					.andExpect(jsonPath("$.errors[0]", containsString("Status must be in between")))
					.andDo(print());
		}
		
		
		//test method that return 404 Not found status code with a given location code for updating fullweather 
		@Test
		public void testUpdateShouldReturn404NotFound() throws Exception {
			String locationCode = "NYC_USA";
			String requestURI = END_POINT_PATH + "/" + locationCode;
			
			Location location = new Location();
			// use regular setters to set field values
			location.setCode(locationCode);
			
			//create a fullweather dto object
			FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
			
			HourlyWeatherDTO hourlyForecast1 = new HourlyWeatherDTO()
					.hourOfDay(10)
					.temperature(33)
					.precipitarion(70)
					.status("Cloudy");
			
			fullWeatherDTO.getListHourlyWeather().add(hourlyForecast1);
			
			DailyWeatherDTO dailyForecast1 = new DailyWeatherDTO()
					.dayOfMonth(16)
					.month(7)
					.minTemp(23)
					.maxTemp(32)
					.status("Sunny")
					.precipitation(40);
			
			fullWeatherDTO.getListDailyWeather().add(dailyForecast1);
			
			
			//creates Realtime Weather object
			RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
			
			//set values for realtime weather fields
			realtimeDTO.setTemperature(12); 
			realtimeDTO.setHumidity(32);
			realtimeDTO.setLastUpdated(new Date());
			realtimeDTO.setPrecipitation(88);
			realtimeDTO.setStatus("Cloudy");
			realtimeDTO.setWindSpeed(5);
			
			fullWeatherDTO.setRealtimeWeather(realtimeDTO);
			
			String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
			
			LocationNotFoundException ex = new LocationNotFoundException(locationCode);
			//mocks get method in fullweather service and throws location is not found if the given location doesn't exist in the database
			when(weatherService.update(Mockito.eq(locationCode),Mockito.any())).thenThrow(ex);
				
			//perform http request
			mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
						.andDo(print());
			
		}
		
	
		//test method that return 200 OK status code with a given location code for updating fullweather 
		@Test
		public void testUpdateShouldReturn200OK() throws Exception {
			String locationCode = "NYC_USA";
			String requestURI = END_POINT_PATH + "/" + locationCode;
			
			// create a location object
			Location location = new Location();
			// use regular setters to set field values
			location.setCode(locationCode);
			location.setCityName("New York City");
			location.setRegionName("New York");
			location.setCountryCode("US");
			location.setCountryName("United States of America");
			
			//creates Realtime Weather object
			RealtimeWeather realtimeWeather = new RealtimeWeather();
			
			//set values for realtime weather fields
			realtimeWeather.setTemperature(12);
			realtimeWeather.setHumidity(32);
			realtimeWeather.setLastUpdated(new Date());
			realtimeWeather.setPrecipitation(88);
			realtimeWeather.setStatus("Cloudy");
			realtimeWeather.setWindSpeed(5);
			
			//set reference of realtime weather for location object
			location.setRealtimeWeather(realtimeWeather);

			DailyWeather dailyForecast1 = new DailyWeather()
					.location(location)
					.dayOfMonth(16)
					.month(7)
					.minTemp(23)
					.maxTemp(32)
					.status("Clear")
					.precipitation(40);

			
			//set information for list daily weather forecast
			location.setListDailyWeather(List.of(dailyForecast1));
			
			
			//create hourly weather forecast object with method chaining instead of regular setters
			HourlyWeather hourlyForecast1 = new HourlyWeather()
										.location(location)
										.hourOfDay(10)
										.temperature(13)
										.precipitarion(70)
										.status("Cloudy");
			
			//set list hourly weather forecast for the location object
			location.setListHourlyWeather(List.of(hourlyForecast1));
			
			
			//create a fullweather dto object
			FullWeatherDTO fullWeatherDTO = new FullWeatherDTO();
			
			HourlyWeatherDTO hourlyForecastDTO1 = new HourlyWeatherDTO()
					.hourOfDay(10)
					.temperature(33)
					.precipitarion(70)
					.status("Cloudy");
			
			fullWeatherDTO.getListHourlyWeather().add(hourlyForecastDTO1);
			
			DailyWeatherDTO dailyForecastDTO1 = new DailyWeatherDTO()
					.dayOfMonth(16)
					.month(7)
					.minTemp(23)
					.maxTemp(32)
					.status("Clear")
					.precipitation(40);
			
			fullWeatherDTO.getListDailyWeather().add(dailyForecastDTO1);
			
			
			//creates Realtime Weather object
			RealtimeWeatherDTO realtimeDTO = new RealtimeWeatherDTO();
			
			//set values for realtime weather fields
			realtimeDTO.setTemperature(12); 
			realtimeDTO.setHumidity(32);
			realtimeDTO.setLastUpdated(new Date());
			realtimeDTO.setPrecipitation(88);
			realtimeDTO.setStatus("Cloudy");
			realtimeDTO.setWindSpeed(5);
			
			fullWeatherDTO.setRealtimeWeather(realtimeDTO);
			
			String requestBody = objectMapper.writeValueAsString(fullWeatherDTO);
			
			//mocks get method in fullweather service and throws location is not found if the given location doesn't exist in the database
			when(weatherService.update(Mockito.eq(locationCode),Mockito.any())).thenReturn(location);
				
			//perform http request
			mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
						.andExpect(status().isOk())
						//for a more strict assertion you can use json path
						.andExpect(jsonPath("$.realtime_weather.temperature", is(12)))
						.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
						.andExpect(jsonPath("$.daily_forecast[0].precipitation", is(40)))
						.andDo(print());
			
		}
		
		
		
		
}
