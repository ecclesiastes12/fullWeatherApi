package com.skyapi.weatherforecast.daily;

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
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(DailyWeatherApiController.class)
public class DailyWeatherApiControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private GeolocationService locationService;
	@MockBean
	private DailyWeatherService dailyWeatherService;
	@Autowired
	private ObjectMapper objectMapper;

	private static final String END_POINT_PATH = "/v1/daily";

	// handler test method that returns 400 Bad Request response status code,
	// because of
	// geolocation. That is for a given ip address no geolocation is found in the
	// IP2Location database

	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGeolocationException() throws Exception {
		GeolocationException ex = new GeolocationException("Geolocation error");
		// fakes DailyWeatherSerivce class
		Mockito.when(locationService.getLocation(Mockito.anyString())) // Mockito.anyString() is for any ip address used
				.thenThrow(ex);

		// perferm http request
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage()))).andDo(print());
	}

	@Test
	public void testGetByIPShouldReturn404NotFound() throws Exception {

		String locationCode = "DELHI_IN";
		Location location = new Location().code(locationCode);

		// fakes DailyWeatherSerivce class
//		Mockito.when(geolocationService.getLocation(Mockito.anyString())) //Mockito.anyString() is for any ip address used
//		.thenThrow(GeolocationException.class);

		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);

		// location not found exception
		LocationNotFoundException ex = new LocationNotFoundException(location.getCode());

		when(dailyWeatherService.getByLocation(location)).thenThrow(ex);

		// make http request
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage()))).andDo(print());

	}

	@Test
	public void testGetByIPShouldReturn204NoContent() throws Exception {

		String locationCode = "DELHI_IN";
		Location location = new Location().code(locationCode);

		when(locationService.getLocation(Mockito.anyString())).thenReturn(location);

		when(dailyWeatherService.getByLocation(location)).thenReturn(new ArrayList<>());

		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isNoContent()).andDo(print());

	}

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

		DailyWeather forecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32)
				.status("Clear").precipitation(40);

		DailyWeather forecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34)
				.status("Sunny").precipitation(30);

		// fakes the geolocation service call to invoke the get location method
		when(locationService.getLocation(Mockito.anyString())).thenReturn(location);

		when(dailyWeatherService.getByLocation(location)).thenReturn(List.of(forecast1, forecast2));

		String expectedLocation = location.toString();

		// makes http request
		mockMvc.perform(get(END_POINT_PATH)).andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation)))
				// "$.daily_forecast[0].day_of_month" --> day_of_month in the first element of
				// the
				// array daily_forecast
				.andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
				// .andExpect(jsonPath("$.daily_forecast[0].month", is(7)))
				.andDo(print());

	}

	// test method that return 404 not found for getByLocationCode
	@Test
	public void testGetByCodeShouldReturn404NotFound() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURL = END_POINT_PATH + "/" + locationCode;

		// fakes DailyWeatherSerivce class
		// Mockito.when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(LocationNotFoundException.class);

		// location not found exception
		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
		// fakes DailyWeatherSerivce class
		when(dailyWeatherService.getByLocationCode(locationCode)).thenThrow(ex);

		// make http request
		mockMvc.perform(get(requestURL)).andExpect(status().isNotFound())
				// .andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
				.andDo(print());
	}

	// test method that return 204 content not found for getByLocationCode
	@Test
	public void testGetByCodeShouldReturn204NoContent() throws Exception {
		String locationCode = "MADRID_ES";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		// fake the daily weather service class
		when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(new ArrayList<>());

		mockMvc.perform(get(requestURI)).andExpect(status().isNoContent()).andDo(print());
	}

//test method that return 200 OK for getByLocationCode
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

		DailyWeather forecast1 = new DailyWeather().location(location).dayOfMonth(16).month(7).minTemp(23).maxTemp(32)
				.status("Clear").precipitation(40);

		DailyWeather forecast2 = new DailyWeather().location(location).dayOfMonth(17).month(7).minTemp(25).maxTemp(34)
				.status("Sunny").precipitation(30);

		// fake the daily weather service class
		Mockito.when(dailyWeatherService.getByLocationCode(locationCode)).thenReturn(List.of(forecast1, forecast2));

		String expectedLocation = location.toString();

		// makes http request
		mockMvc.perform(get(requestURI)).andExpect(status().isOk()).andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(expectedLocation)))
				// "$.daily_forecast[0].day_of_month" --> day_of_month in the first element of
				// the
				// array daily_forecast
				.andExpect(jsonPath("$.daily_forecast[0].day_of_month", is(16)))
				// .andExpect(jsonPath("$.daily_forecast[0].month", is(7)))
				.andDo(print());

	}

	// dailyweather test update method that return 400 bad request status because
	// there is no data
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
		// location code
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		// list daily weather dto objects
		List<DailyWeatherDTO> listDto = Collections.emptyList();

		// converts listDto to json string
		String requestBody = objectMapper.writeValueAsString(listDto);

		// make http request
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0]", is("Daily weather forecast cannot be empty"))).andDo(print());

	}

	@Test
	public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
		// uri and location code
		String locationCode = "NYC_USA";
		String requestURI = END_POINT_PATH + "/" + locationCode;

		DailyWeatherDTO dailyWeather1 = new DailyWeatherDTO();
		dailyWeather1.dayOfMonth(40);
		dailyWeather1.month(7);
		dailyWeather1.minTemp(23);
		dailyWeather1.maxTemp(30);
		dailyWeather1.precipitation(20);
		dailyWeather1.status("Sunny");

		DailyWeatherDTO dailyWeather2 = new DailyWeatherDTO();
		dailyWeather2.dayOfMonth(20);
		dailyWeather2.month(7);
		dailyWeather2.minTemp(23);
		dailyWeather2.maxTemp(30);
		dailyWeather2.precipitation(20);
		dailyWeather2.status("Sunny");
//
//		DailyWeatherDTO dailyWeather3 = new DailyWeatherDTO().dayOfMonth(18).month(7).minTemp(12).maxTemp(100)
//				.precipitarion(40).status("Sunny");

		// list daily weather objects as dto
		List<DailyWeatherDTO> listDTO = List.of(dailyWeather1, dailyWeather2);

		// convert daily weather object to json string
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", containsString("Day of month must be between 1-31 ")));

	}

	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception{
		//location code
		String locationCode = "NYC_USA";
		//url
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		DailyWeatherDTO dto1 = new DailyWeatherDTO()
				  .dayOfMonth(17)
				  .month(7)
				  .minTemp(52)
				  .maxTemp(78)
				  .precipitation(40)
				  .status("Cloudy");
		
		List<DailyWeatherDTO> listDtos = List.of(dto1);
		
		//convert from daily weather object to json string
		String requestBody = objectMapper.writeValueAsString(listDtos);
		
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isNotFound())
				.andDo(print());
	}
	
	
	@Test
	public void testUpdateShouldReturn200OK() {
		
	}
	
}
