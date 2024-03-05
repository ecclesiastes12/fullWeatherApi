package com.skyapi.weatherforecast.realtime;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyapi.weatherforecast.GeolocationException;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;
import com.skyapi.weatherforecast.common.RealtimeWeather;
import com.skyapi.weatherforecast.location.LocationNotFoundException;

//NB @WebMvcTest loads only spring mvc components eg REST controllers
@WebMvcTest(RealtimeWeatherApiController.class) //LocationApiController is the controller class we want to load for the test
public class RealtimeWeatherApiControllerTests {

	//static end point path
	private static final String END_POINT_PATH = "/v1/realtime";
	
	//reference of the MockMvc
	@Autowired MockMvc mockMvc; //MockMVc performs api calls (http request) and assertion on the responses

	//ObjectMapper serializes or converts java objects to json and vice versa
	@Autowired ObjectMapper mapper; //for faster xml jackson data bind

	//A reference of RealtimeWeatherService class and GeolocationService class. Note how this is done,
	//because if you don't reference any class, you don't have to mock any class
	//NB since @WebMvcTest does not load service components we will use MockBean
	//to create a fake RealtimeWeatherService class and GeolocationService class
	@MockBean RealtimeWeatherService realtimeWeatherService;
	@MockBean GeolocationService locationService;
	
	
	//Rest controller test method that return 400 bad request response status
	//when the ip address does not exist in the IP2Location database
	@Test
	public void testGetShouldReturnStatus400BadRequest() throws Exception {
		//for any ip address we use Mockito.anyString()
		//mock the getLocation method in GeolocationService
		Mockito.when(locationService.getLocation(Mockito.anyString()))
			.thenThrow(GeolocationException.class); //exception class to be thrown
		
		//perform api call or http request to the server
		mockMvc.perform(get(END_POINT_PATH))//end point path
			.andExpect(status().isBadRequest()) //expected result
			.andDo(print()); //print details of request and response
		
		
	}
	
	//code modified after LocationNotFoundException is handled by GlobalExceptionHandler.java
	//Rest controller test method that return 404 NotFound response status.
	//when the given location does not exist in the location table.
	//NB for this one, that is when ip address is correct but location(country code and city name)
	//does not exist in the location table
	@Test
	public void testGetShouldReturnStatus404NotFound() throws Exception {
		
		//create location object
		Location location = new Location();
		location.setCountryCode("US");
		location.setCityName("Tampa");
		
		//create a new LocationNotFoundException object
		LocationNotFoundException ex = new LocationNotFoundException(location.getCountryCode(), location.getCityName());
		
		//when ip address from the client is correct.
		//for any ip address we use Mockito.anyString()
		//mock the getLocation method in GeolocationService
		Mockito.when(locationService.getLocation(Mockito.anyString()))
			.thenReturn(location); //return location object
		
		//when country code and city name does not exist
		Mockito.when(realtimeWeatherService.getByLocation(location))
			.thenThrow(ex); //exception class thrown
		
		//perform api call or http request to the server
		mockMvc.perform(get(END_POINT_PATH))//end point path
			.andExpect(status().isNotFound()) //expected result
			.andDo(print()); //print details of request and response
		
		
	}
	
	
	//Rest controller test method that return 200 OK response status.
	//when ip address is correct and location data exist in location table
	@Test
	public void testGetShouldReturnStatus200OK() throws Exception {
		//create location object
		Location location = new Location();
		
		//set values for location fields
		location.setCode("SFCA_USA");
		location.setCityName("San Francisco");
		location.setRegionName("California");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		
		//creates Realtime Weather object
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		
		//set values for realtime weather fields
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		//set reference of location for realtime weather object
		realtimeWeather.setLocation(location);
		
		//set reference of realtime weather for location object
		location.setRealtimeWeather(realtimeWeather);
		
		
		
		//for any ip address we use Mockito.anyString()
		//mock the getLocation method in GeolocationService
		Mockito.when(locationService.getLocation(Mockito.anyString()))
			.thenReturn(location); //return location object
		
		//fake the getByLocation method in RealtimeWeatherService class
		Mockito.when(realtimeWeatherService.getByLocation(location))
			.thenReturn(realtimeWeather); //return realtime weather object
		
		
		//expected location is for formating the return body response for location 
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		
		//perform api call or http request to the server
		mockMvc.perform(get(END_POINT_PATH))//end point path
			.andExpect(status().isOk()) //expected status result
			.andExpect(content().contentType("application/json")) //expected response body type
			.andExpect(jsonPath("$.location", is(expectedLocation)))// verify json fields for location in the response body
			.andDo(print()); //print details of request and response
	}
	
	
	
	//RestController test method that return 404 Not Found response status code
	//for getByLocationCode
	
	@Test
	public void testGetByLocationCodeShouldReturn404NotFound() throws Exception {
		//location code
		String locationCode = "ABCD";
		String locationUrl = END_POINT_PATH + "/" + locationCode;
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode))
			.thenThrow(LocationNotFoundException.class);
		
		mockMvc.perform(get(locationUrl))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	
	@Test
	public void testGetByLocationCodeShouldReturn200OK() throws Exception {
		
		//location code
		String locationCode = "NYC_USA";
		
		//location object
		Location location = new Location();
		
		String locationUrl = END_POINT_PATH + "/" + locationCode;
		
		
		//set values for location fields
		//location.setCode(locationCode);
		location.setCityName("New York City");
		location.setRegionName("New York");
		location.setCountryName("United States of America");
		location.setCountryCode("US");
		
		
		//creates Realtime Weather object
		RealtimeWeather realtimeWeather = new RealtimeWeather();
		
		//set values for realtime weather fields
		realtimeWeather.setTemperature(12);
		realtimeWeather.setHumidity(32);
		realtimeWeather.setLastUpdated(new Date());
		realtimeWeather.setPrecipitation(88);
		realtimeWeather.setStatus("Cloudy");
		realtimeWeather.setWindSpeed(5);
		
		//set reference of location for realtime weather object
		realtimeWeather.setLocation(location);
		
		//set reference of realtime weather for location object
		location.setRealtimeWeather(realtimeWeather);
		
		
		Mockito.when(realtimeWeatherService.getByLocationCode(locationCode))
			.thenReturn(realtimeWeather);
		
		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
		
		mockMvc.perform(get(locationUrl))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.location", is(expectedLocation)))// verify json fields for location in the response body
			.andDo(print());
	}
	
	
//	//test method that return 400 Bad Request response status for realtime weather api update
//	@Test
//	public void testUpdateShouldReturn400BadRequest() throws Exception {
//		//declare location code
//		String locationCode = "ABC_US";
//		String requestURI = END_POINT_PATH + "/" + locationCode; 
//		
//		//create RealtimeWeather object
//		RealtimeWeather realtimeWeather = new RealtimeWeather();
//		
//		//set values for updating realtime weather
//		realtimeWeather.setTemperature(12);
//		realtimeWeather.setHumidity(32);
//		realtimeWeather.setPrecipitation(88);
//		realtimeWeather.setStatus("Cloudy");
//		realtimeWeather.setWindSpeed(50);
//		//create RealtimeWeather object
//		
//		
//		//serialize or convert java object into json object using object mapper
//		String bodyContent = mapper.writeValueAsString(dto);
//		
//		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
//			.andExpect(status().isBadRequest())
//			.andDo(print());
//		
//	}
	
	//test method that return 400 Bad Request response status for realtime weather api update for dto
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		//declare location code
		String locationCode = "ABC_US";
		String requestURI = END_POINT_PATH + "/" + locationCode; 
		
		//create RealtimeWeatherDTO object
		RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
		
		//set values for updating realtime weather
		dto.setTemperature(120);
		dto.setHumidity(320);
		dto.setPrecipitation(88);
		dto.setStatus("Cloudy");
		dto.setWindSpeed(500);

		//serialize or convert java object into json object using object mapper
		String bodyContent = mapper.writeValueAsString(dto);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
			.andExpect(status().isBadRequest())
			.andDo(print());
		
	}
	
//	//test method that return 400 Not found response status for realtime weather api update
//	//with a given location code
//	@Test
//	public void testUpdateShouldReturn400NotFound() throws Exception {
//		//declare location code
//		String locationCode = "ABC_US";
//		String requestURI = END_POINT_PATH + "/" + locationCode; 
//		
//		//create RealtimeWeather object
//		RealtimeWeather realtimeWeather = new RealtimeWeather();
//		
//		//set values for updating realtime weather
//		realtimeWeather.setTemperature(12);
//		realtimeWeather.setHumidity(32);
//		realtimeWeather.setPrecipitation(88);
//		realtimeWeather.setStatus("Cloudy");
//		realtimeWeather.setWindSpeed(5);
//		realtimeWeather.setLocationCode(locationCode);
//		
//		//create LocationNotFoundException object
//		LocationNotFoundException ex = new LocationNotFoundException(locationCode);
//		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenThrow(ex);
//		
//		//serialize or convert java object into json object using object mapper
//		String bodyContent = mapper.writeValueAsString(realtimeWeather);
//		
//		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
//			.andExpect(status().isNotFound())
//			.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
//			.andDo(print());
//		
//	}
	
	//test method that return 400 Not found response status for realtime weather api update
	//with a given location code
	//using dto object
		@Test
		public void testUpdateShouldReturn400NotFound() throws Exception {
			//declare location code
			String locationCode = "ABC_US";
			String requestURI = END_POINT_PATH + "/" + locationCode; 
			
			//create RealtimeWeather object
			RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
			
			//set values for updating realtime weather
			dto.setTemperature(12);
			dto.setHumidity(32);
			dto.setPrecipitation(88);
			dto.setStatus("Cloudy");
			dto.setWindSpeed(5);
			
			
			//create LocationNotFoundException object
			LocationNotFoundException ex = new LocationNotFoundException(locationCode);
			Mockito.when(realtimeWeatherService.update(Mockito.eq(locationCode), Mockito.any())).thenThrow(ex);
			
			//serialize or convert java object into json object using object mapper
			String bodyContent = mapper.writeValueAsString(dto);
			
			mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0]", is(ex.getMessage())))
				.andDo(print());
			
		}


	
//	//test method that return 400 Not found response status for realtime weather api update
//	//with a given location code
//	@Test
//	public void testUpdateShouldReturn200OK() throws Exception {
//		//declare location code
//		String locationCode = "NYC_USA";
//		String requestURI = END_POINT_PATH + "/" + locationCode; 
//		
//		//create RealtimeWeather object
//		RealtimeWeather realtimeWeather = new RealtimeWeather();
//		
//		//set values for updating realtime weather
//		realtimeWeather.setTemperature(12);
//		realtimeWeather.setHumidity(32);
//		realtimeWeather.setPrecipitation(88);
//		realtimeWeather.setStatus("Cloudy");
//		realtimeWeather.setWindSpeed(5);
//		realtimeWeather.setLastUpdated(new Date());
//		
//		RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
//		dto.setTemperature(12);
//		dto.setHumidity(32);
//		dto.setPrecipitation(88);
//		dto.setStatus("Cloudy");
//		dto.setWindSpeed(5);
//		dto.setLastUpdated(new Date());
//		
//		Location location = new Location();
//		
//		//set values for location fields
//		location.setCode(locationCode);
//		location.setCityName("New York City");
//		location.setRegionName("New York");
//		location.setCountryName("United States of America");
//		location.setCountryCode("US");
//
//		//set location to realtime weather object
//		realtimeWeather.setLocation(location);
//		
//		//set realtime weather to location object
//		location.setRealtimeWeather(realtimeWeather);
//		
//		
//		
//		Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);
//		
//		//serialize or convert java object into json object using object mapper
//		String bodyContent = mapper.writeValueAsString(realtimeWeather);
//		
//		String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
//		
//		mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.location", is(expectedLocation)))// verify json fields for location in the response body
//			.andDo(print());
//		
//	}
		
		//test method that return 400 Not found response status for realtime weather api update
		//with a given location code
		@Test
		public void testUpdateShouldReturn200OK() throws Exception {
			//declare location code
			String locationCode = "NYC_USA";
			String requestURI = END_POINT_PATH + "/" + locationCode; 
			
			//create RealtimeWeather object
			RealtimeWeather realtimeWeather = new RealtimeWeather();
			
			//set values for updating realtime weather
			realtimeWeather.setTemperature(12);
			realtimeWeather.setHumidity(32);
			realtimeWeather.setPrecipitation(88);
			realtimeWeather.setStatus("Cloudy");
			realtimeWeather.setWindSpeed(5);
			realtimeWeather.setLastUpdated(new Date());
			
			RealtimeWeatherDTO dto = new RealtimeWeatherDTO();
			dto.setTemperature(12);
			dto.setHumidity(32);
			dto.setPrecipitation(88);
			dto.setStatus("Cloudy");
			dto.setWindSpeed(5);
			dto.setLastUpdated(new Date());
			
			Location location = new Location();
			
			//set values for location fields
			location.setCode(locationCode);
			location.setCityName("New York City");
			location.setRegionName("New York");
			location.setCountryName("United States of America");
			location.setCountryCode("US");

			//set location to realtime weather object
			realtimeWeather.setLocation(location);
			
			//set realtime weather to location object
			location.setRealtimeWeather(realtimeWeather);
			
			
			//return object of the updated method of realtimeWeatherService
			Mockito.when(realtimeWeatherService.update(locationCode, realtimeWeather)).thenReturn(realtimeWeather);
			
			//serialize or convert java object into dto object using object mapper to be send as a requestbody
			String bodyContent = mapper.writeValueAsString(dto);
			
			String expectedLocation = location.getCityName() + ", " + location.getRegionName() + ", " + location.getCountryName();
			
			mockMvc.perform(put(requestURI).contentType("application/json").content(bodyContent))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.location", is(expectedLocation)))// verify json fields for location in the response body
				.andDo(print());
			
		}
}
