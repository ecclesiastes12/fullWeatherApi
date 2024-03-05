package com.skyapi.weatherforecast.hourly;

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
import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

@WebMvcTest(HourlyWeatherApiController.class)
public class HourlyWeatherApiControllerTests {

	private static final String X_CURRENT_HOUR = "X-Current-Hour";

	//static api end point
	private static final String END_POINT_PATH = "/v1/hourly";
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@MockBean private HourlyWeatherService hourlyWeatherService;
	@MockBean private GeolocationService locationService;
	
	//handler test method that return 400 Bad Request response status code, because
	//there is no X-Current-Hour in the request header. X-Current-Hour is used to determine
	//the current hour when an http request is made
	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseNoHeaderXCurrentHour() throws Exception {
		//makes http request to the server
		mockMvc.perform(get(END_POINT_PATH))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}
	
	//handler test method that returns 400 Bad Request response status code, because of
	//geolocation. That is for a given ip address and current hour in the request header no geolocation is found in the IP2Location database
	@Test
	public void testGetByIPShouldReturn400BadRequestBecauseGelocationException() throws Exception {
		
		//fakes the geolocation service and invoke the getlocation method
		Mockito.when(locationService.getLocation(Mockito.anyString())) //Mockito.anyString() is for any ip address used
				.thenThrow(GeolocationException.class);
		
		//makes http request to the server
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, "9"))
				.andExpect(status().isBadRequest())
				.andDo(print());
	}
	
	//handler test method that return 204 No Content response status code, for hourly weather forecast
	//data with a given ip address and request header which contains X-Current-Hour
	@Test
	public void testGetByIPShouldReturn204NoContent() throws Exception {
		int currentHour = 9;
		Location location = new Location().code("DELHI_IN");
		
		//fakes the geolocation service call to invoke the get location method
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);//first method in listHourlyForecastByIPAddress 
		//of the HourlyWeatherApiController
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(new ArrayList<>());//second method in listHourlyForecastByIPAddress
		//of the HourlyWeatherApiController
		
		
		//makes http request to the server
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isNoContent())
				.andDo(print());
	}
	
	//handler test method that return 200 OK response status code, for hourly weather forecast
	//data with a given ip address, hourly weather objects and request header which contains X-Current-Hour
	@Test
	public void testGetByIPShouldReturn200OK() throws Exception {
		int currentHour = 9;
		
		//create a location object
		Location location = new Location();
		//use regular setters to set field values
		location.setCode("NYC_USA");   
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		//create hourly weather forecast object with method chaining instead of regular setters
		HourlyWeather forecast1 = new HourlyWeather()
									.location(location)
									.hourOfDay(10)
									.temperature(13)
									.precipitarion(70)
									.status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitarion(60)
				.status("Sunny");
		
		//fakes the geolocation service call to invoke the get location method
		Mockito.when(locationService.getLocation(Mockito.anyString())).thenReturn(location);//first method in listHourlyForecastByIPAddress 
		//of the HourlyWeatherApiController
		when(hourlyWeatherService.getByLocation(location, currentHour)).thenReturn(List.of(forecast1, forecast2));//second method in listHourlyForecastByIPAddress
		//of the HourlyWeatherApiController
		
		String expectedLocation = location.toString();
		
		//makes http request to the server
		mockMvc.perform(get(END_POINT_PATH).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.location", is(expectedLocation)))
				.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
				.andDo(print());
	}
	
	
	//handler test method that return 400 Bad Request response status code, when hourly forecast is retrieved by location code
	@Test
	public void getByCodeShouldReturn400BadRequest() throws Exception {
		//location code
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		//makes a request
		mockMvc.perform(get(requestURI))
			.andExpect(status().isBadRequest())
			.andDo(print());
	}
	
	
	//Test method that return 404 Not Found response status code when a given location code is does not exist in the database
	@Test
	public void testGetByCodeShouldReturn404NotFound() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURL = END_POINT_PATH + "/" + locationCode;
		int currentHour = 9;
	
		Mockito.when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(requestURL).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	//test method that return 204 No Content response status code for a given location code and current hour
	@Test
	public void testGetByCodeShouldReturn204NoContent() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURL = END_POINT_PATH + "/" + locationCode;
		int currentHour = 15;
		
		
		Mockito.when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(Collections.emptyList());
		
		mockMvc.perform(get(requestURL).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
			.andExpect(status().isNoContent())
			.andDo(print());
	}
	
	//test method that return 200 OK response status code for a given location code and current hour
	@Test
	public void testGetByCodeShouldReturn200OK() throws Exception {
		String locationCode = "DELHI_IN";
		String requestURI = END_POINT_PATH + "/" + locationCode;
		int currentHour = 9;
		
		Location location = new Location();
		location.setCode(locationCode);
		location.setCityName("New York City");
		location.setCityName("United States of America");
		location.setRegionName("New York");
		location.setCountryCode("US");
		
		HourlyWeather forecast1 = new HourlyWeather()
									  .location(location)
									  .hourOfDay(10)
									  .temperature(13)
									  .precipitarion(70)
									  .status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
									  .location(location)
									  .hourOfDay(11)
									  .temperature(15)
									  .precipitarion(60)
									  .status("Sunny");
		
		var hourlyForecast = List.of(forecast1, forecast2);
		
when(hourlyWeatherService.getByLocationCode(locationCode, currentHour)).thenReturn(hourlyForecast);
		
		mockMvc.perform(get(requestURI).header(X_CURRENT_HOUR, String.valueOf(currentHour)))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$.location", is(location.toString())))
				.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))				
				.andDo(print());
	}
	
	
//		//controller test method that update hourly weather forecast that return 400 Bad Request response status code for no data
//		@Test
//		public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
//			//url
//			String requestURI = END_POINT_PATH + "/NYC_USA";
//			
//			//list hourly weather dto object
//			List<HourlyWeatherDTO> listDTO = Collections.emptyList();
//			
//			//convert listDTO to Json String using object mapper
//			String requestBody = objectMapper.writeValueAsString(listDTO);
//			
//			//perform http request
//			mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
//				.andExpect(status().isBadRequest())
//				//.andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
//				.andDo(print());
//			
//			//NB the response status code for this test is 202
//		}

	
	//controller test method that update hourly weather forecast that return 400 Bad Request response status code for no data
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseNoData() throws Exception {
		//url
		String requestURI = END_POINT_PATH + "/NYC_USA";
		
		//list hourly weather dto object
		List<HourlyWeatherDTO> listDTO = Collections.emptyList();
		
		//convert listDTO to Json String using object mapper
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		//perform http request
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", is("Hourly forecast data cannot be empty")))
			.andDo(print());
	}
	
	
	//controller test method that update hourly weather forecast and return 400 Bad Request response status code for invalid data 
	//or values for some fields
	@Test
	public void testUpdateShouldReturn400BadRequestBecauseInvalidData() throws Exception {
		//url
		String requestURI = END_POINT_PATH + "/NYC_USA";
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				 
				  .hourOfDay(10)
				  .temperature(133)
				  .precipitarion(70)
				  .status("Cloudy");

		HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
				 
				  .hourOfDay(11)
				  .temperature(15)
				  .precipitarion(60)
				  .status("Sunny");
		
		//list hourly weather dto object
		List<HourlyWeatherDTO> listDTO = List.of(dto1, dto2);
		
		//convert listDTO to Json String using object mapper
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		//perform http request
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors[0]", containsString("Temperature must be in the range")))
			.andDo(print());
	}
	
	//controller test method that update hourly weather forecast and return 404 not found response status code
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		//location code
		String locationCode = "NYC_USA";
		//url
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				  .hourOfDay(10)
				  .temperature(13)
				  .precipitarion(70)
				  .status("Cloudy");

		
		
		//list hourly weather dto object
		List<HourlyWeatherDTO> listDTO = List.of(dto1);
		
		//convert listDTO to Json String using object mapper
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		//fakes updateByLocationCode using Mockito. 
		//NB you can use either Mockito.when(...) or when(...)
		when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
											.thenThrow(LocationNotFoundException.class);//throws exception if not content is found
		
		//perform http request
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	
	//controller test method that update hourly weather forecast and return 200 OK response status code for successful operation
	@Test
	public void testUpdateShouldReturn200OK() throws Exception {
		//location code
		String locationCode = "NYC_USA";
		//url
		String requestURI = END_POINT_PATH + "/" + locationCode;
		
		HourlyWeatherDTO dto1 = new HourlyWeatherDTO()
				  .hourOfDay(10)
				  .temperature(13)
				  .precipitarion(70)
				  .status("Cloudy");

		HourlyWeatherDTO dto2 = new HourlyWeatherDTO()
				  .hourOfDay(11)
				  .temperature(15)
				  .precipitarion(60)
				  .status("Sunny");
		
		//create a location object
		Location location = new Location();
		location.setCode("NYC_USA");
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryCode("US");
		location.setCountryName("United States of America");
		
		//create hourl weather forecast object
		HourlyWeather forecast1 = new HourlyWeather()
									.location(location)
									.hourOfDay(10)
									.temperature(13)
									.precipitarion(70)
									.status("Cloudy");
		
		HourlyWeather forecast2 = new HourlyWeather()
				.location(location)
				.hourOfDay(11)
				.temperature(15)
				.precipitarion(60)
				.status("Sunny");
		
		//list hourly weather dto object
		List<HourlyWeatherDTO> listDTO = List.of(dto1,dto2);
		
		var hourlyForecast = List.of(forecast1, forecast2);
		
		//convert listDTO to Json String using object mapper
		String requestBody = objectMapper.writeValueAsString(listDTO);
		
		//fakes updateByLocationCode using Mockito. 
		//NB you can use either Mockito.when(...) or when(...)
		when(hourlyWeatherService.updateByLocationCode(Mockito.eq(locationCode), Mockito.anyList()))
											.thenReturn(hourlyForecast);
		
		//perform http request
		mockMvc.perform(put(requestURI).contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isOk())
			//for location object
			.andExpect(jsonPath("$.location", is(location.toString())))//NB $.location is from HourlyWeatherDTO
			//for hourly weather forecast object
			.andExpect(jsonPath("$.hourly_forecast[0].hour_of_day", is(10)))
			.andDo(print());
	}
	
}
