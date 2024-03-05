
//package com.skyapi.weatherforecast.location;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.hamcrest.CoreMatchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.Collections;
//import java.util.List;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.skyapi.weatherforecast.common.Location;
//
//
////NB @WebMvcTest loads only spring mvc components eg REST controllers
//@WebMvcTest(LocationApiController.class) //LocationApiController is the controller class we want to load for the test
//public class LocationApiControllerTests1 {
//
//	//static end point path
//	private static final String END_POINT_PATH = "/v1/locations";
//	
//	//reference of the MockMvc
//	@Autowired MockMvc mockMvc; //MockMVc performs api calls (http request) and assertion on the responses
//
//	//ObjectMapper serializes or converts java objects to json and vice versa
//	@Autowired ObjectMapper mapper; //for faster xml jackson data bind
//
//	//reference of LocationService 
//	//NB since @WebMvcTest does not load service components we will use MockBean
//	//to create a fake location service
//	@MockBean LocationService service;
//	
//	//test for bad request. thus when the request contains invalid data
//	@Test
//	public void testAddShouldReturn400BadRequest() throws Exception {
//		
//		//create location  object
//		Location location = new Location();
//		
//		//convert java objects to json string using ObjectMapper
//		String bodyContent = mapper.writeValueAsString(location);
//		
//		//use mockMvc object to perform http post request. you have to specify the
//		//end point and the content type which in this case is application/json
//		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isBadRequest())
//			.andDo(print()); //the print() used here will print the details of the request and the response
//		
//	}
//	
//	//wthout dto
//	//test method for location added successful
////	@Test
////	public void testAddShouldReturn201Created() throws Exception {
////		//creates location object
////		Location location = new Location();
////		
////		//set values for code, region, country and other 
////		location.setCode("NYC_USA");
////		location.setCityName("New York City");
////		location.setRegionName("New York");
////		location.setCountryCode("US");
////		location.setCountryName("United States of America");
////		location.setEnabled(true);
////		
////		//When the add method is called(invoked) then return location
////		Mockito.when(service.add(location)).thenReturn(location);
////		
////		//convert the body content from java object to json object
////		String bodyContent =  mapper.writeValueAsString(location);
////		
////		//use mockMvc object to perform http post request. you have to specify the
////		//end point and the content type which in this case is application/json
////		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
////			.andExpect(status().isCreated())
////			.andExpect(content().contentType("application/json"))
////			//NB with data available in the response body, we can use jsonPath expression 
////			//to perform more data or strict data assertion verify the content of the json document.
////			//JsonPath expression can also be used to navigate json document
////			
////			.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
////			.andExpect(jsonPath("$.city_name", is("New York City")))
////			
////			//verify the header response.
////			//parameter 1 in ...string("Location", "/v1/locations/NYC_USA") is the header name
////			//parameter 2 is the value to compare with thus endpoint url
////			.andExpect(header().string("Location", "/v1/locations/NYC_USA"))
////			.andDo(print()); //the print() used here will print the details of the request and the response
////		
////	}
//	
//	
//	//with dto
//	//test method for location added successful
//	@Test
//	public void testAddShouldReturn201Created() throws Exception {
//		//creates location object
//		Location location = new Location();
//		
//		//set values for code, region, country and other 
//		location.setCode("NYC_USA");
//		location.setCityName("New York City");
//		location.setRegionName("New York");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//
//		
//		//create a dto object
//		LocationDTO dto = new LocationDTO();
//		
//		//set values for location through dto
//		dto.setCode(location.getCode());
//		dto.setCityName(location.getCityName());
//		dto.setRegionName(location.getRegionName());
//		dto.setCountryCode(location.getCountryCode());
//		dto.setCountryName(location.getCountryName());
//		dto.setEnabled(location.isEnabled());
//		
//		
//		//When the add method is called(invoked) then return location
//		Mockito.when(service.add(location)).thenReturn(location);
//		
//		//convert the body content from java object to json object
//		String bodyContent = mapper.writeValueAsString(dto);
//		
//		//use mockMvc object to perform http post request. you have to specify the
//		//end point and the content type which in this case is application/json
//		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isCreated())
//			.andExpect(content().contentType("application/json"))
//			//NB with data available in the response body, we can use jsonPath expression 
//			//to perform more data or strict data assertion verify the content of the json document.
//			//JsonPath expression can also be used to navigate json document
//			
//			.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
//			.andExpect(jsonPath("$.city_name", is("New York City")))
//			
//			//verify the header response.
//			//parameter 1 in ...string("Location", "/v1/locations/NYC_USA") is the header name
//			//parameter 2 is the value to compare with thus endpoint url
//			.andExpect(header().string("Location", "/v1/locations/NYC_USA"))
//			.andDo(print()); //the print() used here will print the details of the request and the response
//		
//	}
//	
//	//test method that validate request body of the location code 
//	
//	@Test
//	public void testValidateRequestBodyLocationCode() throws Exception {
//		//for test purpose no need of mokito you only need to perform some assertion
//		
//		//creates location object
//		Location location = new Location();
//		
//		//set values for code, region, country and other 
//		location.setCityName("New York City");
//		location.setRegionName("New York");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//		
//		//convert the body content from java object to json object
//		String bodyContent =  mapper.writeValueAsString(location);
//				
//		mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
//		.andExpect(status().isBadRequest())
//		.andExpect(content().contentType("application/json"))
//		//NB with data available in the response body, we can use jsonPath expression 
//		//to perform more data or strict data assertion verify the content of the json document.
//		//JsonPath expression can also be used to navigate json document
//		
//		//.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
//		.andDo(print()); //the print() used here will print the details of the request and the response
//		
//	}
//	
//	
//	//test method that validate location code length
//	
//		@Test
//		public void testValidateRequestBodyLocationCodeLength() throws Exception {
//			//for test purpose no need of mokito you only need to perform some assertion
//			
//			//creates location object
//			Location location = new Location();
//			
//			//set values for code, region, country and other 
//			location.setCode("AV");
//			location.setCityName("New York City");
//			location.setRegionName("New York");
//			location.setCountryCode("US");
//			location.setCountryName("United States of America");
//			location.setEnabled(true);
//			
//			//convert the body content from java object to json object
//			String bodyContent =  mapper.writeValueAsString(location);
//					
//			mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isBadRequest())
//			.andExpect(content().contentType("application/json"))
//			//NB with data available in the response body, we can use jsonPath expression 
//			//to perform more data or strict data assertion verify the content of the json document.
//			//JsonPath expression can also be used to navigate json document
//			
//			//.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
//			.andDo(print()); //the print() used here will print the details of the request and the response
//			
//		}
//	
//	
//	//test method that validate all fields of the request body
//	
//	@Test
//	public void testValidateRequestBodyAllFieldsInvalid() throws Exception {
//		//for test purpose no need of mokito you only need to perform some assertion
//		
//		//creates location object
//		Location location = new Location();
//		location.setRegionName("");
//		
//		
//		//convert the body content from java object to json object
//		String bodyContent =  mapper.writeValueAsString(location);
//				
//		MvcResult mvcResult = mockMvc.perform(post(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isBadRequest())
//			.andExpect(content().contentType("application/json"))
//			//NB with data available in the response body, we can use jsonPath expression 
//			//to perform more data or strict data assertion verify the content of the json document.
//			//JsonPath expression can also be used to navigate json document
//			
//			//.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
//			.andDo(print()) //the print() used here will print the details of the request and the response
//			
//			//NB In order not to display the errors at random you need to add the next line of code
//			.andReturn();
//		
//		
//		String responseBody = mvcResult.getResponse().getContentAsString();
//		
//		assertThat(responseBody).contains("Location code cannot be null");
//		assertThat(responseBody).contains("City name cannot be null");
//		//assertThat(responseBody).contains("Region name must have 3-128 characters");
//		assertThat(responseBody).contains("Country name cannot be null");
//		assertThat(responseBody).contains("Country code cannot be null");
//		
//	}
//
//		
//	
//	//test method for no content found response
//	@Test
//	public void testListShouldReturn204NoContent() throws Exception {
//		//invoked the service method that list location
//		Mockito.when(service.list()).thenReturn(Collections.emptyList());
//		
//		//makes http request
//		mockMvc.perform(get(END_POINT_PATH)) //end point url
//			.andExpect(status().isNoContent()) //expected response code
//			.andDo(print());//print response details
//	}
//	
//	//test method that returns 200 ok response. thus if the return results contains some data
//	@Test
//	public void testListShouldResturn200OK() throws Exception {
//		//creates location object
//		Location location1 = new Location();
//		//set value for location fields
//		location1.setCode("NYC_USA");
//		location1.setCityName("New York City");
//		location1.setRegionName("New York");
//		location1.setCountryCode("US");
//		location1.setCountryName("United States of America");
//		location1.setEnabled(true);
//		
//		
//		Location location2 = new Location();
//		//set value for location fields
//		location2.setCode("LACA_USA");
//		location2.setCityName("Los Angeles");
//		location2.setRegionName("Califonia");
//		location2.setCountryCode("US");
//		location2.setCountryName("United States of America");
//		location2.setEnabled(true);
//		
////		Location location3 = new Location();
////		//set value for location fields
////		location3.setCode("DELHI_IN");
////		location3.setCityName("New Delhi");
////		location3.setRegionName("Delhi");
////		location3.setCountryCode("IN");
////		location3.setCountryName("India");
////		location3.setEnabled(true);
////		location3.setTrashed(true);
//		
//		//use mockito to invoke the service method that fetch list of location
//		Mockito.when(service.list()).thenReturn(List.of(location1,location2));
//		
//		//perform http request
//		mockMvc.perform(get(END_POINT_PATH))
//			.andExpect(status().isOk()) //expected http response status code
//			.andExpect(content().contentType("application/json")) //expected content type which should be json
//			
//			//verifty the json data which json path which in this case is an array data
//			.andExpect(jsonPath("$[0].code", is("NYC_USA"))) //$[0].code represent the first index of the array data
//			.andExpect(jsonPath("$[0].city_name", is("New York City")))
//			.andExpect(jsonPath("$[0].region_name", is("New York")))
//			.andExpect(jsonPath("$[0].country_name", is("United States of America")))
//			.andExpect(jsonPath("$[1].code", is("LACA_USA"))) //$[0].code represent the first index of the array data
//			.andExpect(jsonPath("$[1].city_name", is("Los Angeles")))
//			.andExpect(jsonPath("$[1].region_name", is("Califonia")))
//			.andExpect(jsonPath("$[1].country_name", is("United States of America")))
//			.andDo(print()); //print the response body details
//	}
//
//	//test method that return 405 not allowed response for get location by a given location code
//	@Test
//	public void testGetShouldReturn405MethodNotAllowed() throws Exception {
//		//request url
//		String requestURI = END_POINT_PATH + "/ABCDEF";
//		
//		//perform http request
//		mockMvc.perform(post(requestURI))
//			.andExpect(status().isMethodNotAllowed()) //expects not allowed method status code
//			.andDo(print());
//	}
//	
//	//test method that return 404 not found response for get location by a given location code
//@Test
//public void testGetShouldReturn404NotFound() throws Exception {
//	String locationCode = "ABCDEF";
//	String requestURI = END_POINT_PATH + "/"+ locationCode;
//	
//	Mockito.when(service.get(locationCode)).thenThrow(LocationNotFoundException.class);
//	
//	mockMvc.perform(get(requestURI))
//		.andExpect(status().isNotFound())
//		.andDo(print());			
//}
//	
//
//	//test method that return 200 OK response for get location by a given location code
//	@Test
//	public void testGetShouldReturn200OK() throws Exception {
//		//location code
//		String code = "LACA_USA";
//		//request url
//		String requestURI = END_POINT_PATH + "/" + code;
//		
//		//creates location object
//		Location location = new Location();
//		
//		//set field values for location
//		location.setCode(code);
//		location.setCityName("Los Angeles");
//		location.setRegionName("Califonia");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//		
//		//fakes the service class or invoked the service method using mockito
//		Mockito.when(service.get(code)).thenReturn(location);
//		
//		//perform http request
//		mockMvc.perform(get(requestURI))
//			.andExpect(status().isOk()) //expects not found status code
//			//verify the response body using json path
//			.andExpect(jsonPath("$.code", is(code)))
//			//.andExpect(jsonPath("$.city_name", is("Los Angeles")))
//			.andDo(print());
//	}
//	
//	//without dto
////	//test method for update location that return 404 not found response
////	//with a given location code
////	
////	@Test
////	public void testUpdateShouldReturn404NotFound() throws Exception {
////		//creates location object
////		Location location = new Location();
////		
////		//set field values for location
////		location.setCode("ABCDEF");
////		location.setCityName("Los Angeles");
////		location.setRegionName("Califonia");
////		location.setCountryCode("US");
////		location.setCountryName("United States of America");
////		location.setEnabled(true);
////		
////		//we use mockito into fake or invoke the service method that update location
////		Mockito.when(service.update(location)).thenThrow(new LocationNotFoundException("No location found"));
////	
////		//convert the java object to json using object mapper
////		String bodyContent = mapper.writeValueAsString(location);
////		
////		//perform http request
////		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
////			.andExpect(status().isNotFound()) //expected status code
////			.andDo(print()); //print details of request body
////	}
//	
//	
//	//with DTO
//	//test method for update location that return 404 not found response
//	//with a given location code
//	
//	@Test
//	public void testUpdateShouldReturn404NotFound() throws Exception {
//		//creates location object
//		LocationDTO location = new LocationDTO();
//		
//		//set field values for location
//		location.setCode("ABCDEF");
//		location.setCityName("Los Angeles");
//		location.setRegionName("Califonia");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//		
//		
//		//we use mockito into fake or invoke the service method that update location
//		Mockito.when(service.update(Mockito.any())).thenThrow(new LocationNotFoundException("No location found"));
//	
//		//convert the java object to json using object mapper
//		String bodyContent = mapper.writeValueAsString(location);
//		
//		//perform http request
//		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isNotFound()) //expected status code
//		
//			.andDo(print()); //print details of request body
//	}
//	
//	//test method for update location that return 400 bad request response
//	//when no location code is given
//	
//	@Test
//	public void testUpdateShouldReturn400BadRequest() throws Exception {
//		
//		//create location object
//		Location location = new Location();
//		
//		//set field values for location
//		location.setCityName("Los Angeles");
//		location.setRegionName("Califonia");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//		
//		//convert from java object to json object
//		String bodyContent = mapper.writeValueAsString(location);
//		
//		//perform http request using mockMvc
//		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isBadRequest())
//			.andDo(print());
//	}
//	
//	//without DTO
////	//test method for location updated successful
////	@Test
////	public void testUpdateShouldReturn200OK() throws Exception {
////		//creates location object
////		Location location = new Location();
////		
////		//set values for code, region, country and other 
////		location.setCode("NYC_USA");
////		location.setCityName("New York City");
////		location.setRegionName("New York");
////		location.setCountryCode("US");
////		location.setCountryName("United States of America");
////		location.setEnabled(true);
////		
////		//When the add method is called(invoked) then return location
////		Mockito.when(service.update(location)).thenReturn(location);
////		
////		//convert the body content from java object to json object
////		String bodyContent =  mapper.writeValueAsString(location);
////		
////		//use mockMvc object to perform http post request. you have to specify the
////		//end point and the content type which in this case is application/json
////		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
////			.andExpect(status().isOk())
////			.andExpect(content().contentType("application/json"))
////			//NB with data available in the response body, we can use jsonPath expression 
////			//to perform more data or strict data assertion verify the content of the json document.
////			//JsonPath expression can also be used to navigate json document
////			
////			.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
////			.andExpect(jsonPath("$.city_name", is("New York City")))
////			.andDo(print()); //the print() used here will print the details of the request and the response
////		
////	}
//	
//	
//	//with DTO
//	//test method for location updated successful
//	@Test
//	public void testUpdateShouldReturn200OK() throws Exception {
//		//creates location object
//		Location location = new Location();
//		
//		//set values for code, region, country and other 
//		location.setCode("NYC_USA");
//		location.setCityName("New York City");
//		location.setRegionName("New York");
//		location.setCountryCode("US");
//		location.setCountryName("United States of America");
//		location.setEnabled(true);
//		
//		//create a new  dto object
//		LocationDTO dto = new LocationDTO();
//		
//		//update location
//		dto.setCode(location.getCode());
//		dto.setCityName(location.getCityName());
//		dto.setRegionName(location.getRegionName());
//		dto.setCountryName(location.getCountryName());
//		dto.setCountryCode(location.getCountryCode());
//		dto.setEnabled(location.isEnabled());
//		
//		//When the add method is called(invoked) then return location
//		Mockito.when(service.update(location)).thenReturn(location);
//		
//		//convert the body content from java object to json object
//		//String bodyContent =  mapper.writeValueAsString(location);
//		String bodyContent =  mapper.writeValueAsString(dto);
//		
//		//use mockMvc object to perform http post request. you have to specify the
//		//end point and the content type which in this case is application/json
//		mockMvc.perform(put(END_POINT_PATH).contentType("application/json").content(bodyContent))
//			.andExpect(status().isOk())
//			.andExpect(content().contentType("application/json"))
//			//NB with data available in the response body, we can use jsonPath expression 
//			//to perform more data or strict data assertion verify the content of the json document.
//			//JsonPath expression can also be used to navigate json document
//			
//			.andExpect(jsonPath("$.code", is("NYC_USA"))) //jsonpath is used to verify the value of the fields code
//			.andExpect(jsonPath("$.city_name", is("New York City")))
//			.andDo(print()); //the print() used here will print the details of the request and the response
//		
//	}
//	
//	//controller test method for delete or trash location which return 404 not found response
//
//	@Test
//	public void testDeleteShouldReturn404NotFound() throws Exception {
//		//location code
//		String code = "LACA_USA";
//		String requestURI = END_POINT_PATH + "/" + code;
//		
//		//throws exception if the location with the given code to delete is found when the
//		//delete method of the service class is invoked
//		Mockito.doThrow(LocationNotFoundException.class).when(service).delete(code);
//		
//		mockMvc.perform(delete(requestURI))
//			.andExpect(status().isNotFound())
//			.andDo(print());
//	}
//	
//	//controller test method that indicates that the location with a given code is removed
//	@Test
//	public void testDeleteShouldReturn204NoContent() throws Exception {
//		//location code
//		String code = "LACA_USA";
//		String requestURI = END_POINT_PATH + "/" + code;
//		
//		//do nothing when the delete method of the service class is invoked with
//		//a given location code. thus when no content for the given location is available
//		//after location is trashed or deleted
//		Mockito.doNothing().when(service).delete(code);
//		
//		mockMvc.perform(delete(requestURI))
//			.andExpect(status().isNoContent())
//			.andDo(print());
//	}
//	
//	
//}
//
//
//
//
//
