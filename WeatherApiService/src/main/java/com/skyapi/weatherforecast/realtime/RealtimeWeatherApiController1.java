//package com.skyapi.weatherforecast.realtime;
//
//import org.modelmapper.ModelMapper;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.skyapi.weatherforecast.CommonUtility;
//import com.skyapi.weatherforecast.GeolocationException;
//import com.skyapi.weatherforecast.GeolocationService;
//import com.skyapi.weatherforecast.common.Location;
//import com.skyapi.weatherforecast.common.RealtimeWeather;
//import com.skyapi.weatherforecast.location.LocationNotFoundException;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@RestController
//@RequestMapping("/v1/realtime")
//public class RealtimeWeatherApiController1 {
//	
//	//logger for logging the exception
//	private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeWeatherApiController1.class);
//
//	//a reference of GeolocationService class and RealtimeWeatherService class
//	private GeolocationService locationService;
//	private RealtimeWeatherService realtimeWeatherService;
//	
//	//reference of moddel mapper. before creating a reference moddel mapper
//	//don't forget to create a bean of it in WeatherApiServiceApplication
//	private ModelMapper modelMapper;
//	
//	//constructor base injection
//	public RealtimeWeatherApiController1(GeolocationService locationService,
//			RealtimeWeatherService realtimeWeatherService, ModelMapper modelMapper) {
//		super();
//		this.locationService = locationService;
//		this.realtimeWeatherService = realtimeWeatherService;
//		this.modelMapper = modelMapper;
//	}
//	
//	//before ModelMapper was used
////	//handler method that get realtime weather by the client's ip address
////	@GetMapping
////	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request){
////		//get the client's ip address
////		String ipAddress = CommonUtility.getIPAddress(request);
////		
////		try {
////			//get location information from the ip address using Geolocation service
////			Location locationFromIP = locationService.getLocation(ipAddress);
////			
////			//get realtime weather data
////			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
////			
////			return ResponseEntity.ok(realtimeWeather);
////			
////		} catch (GeolocationException e) {
////			//log the error
////			LOGGER.error(e.getMessage(), e);
////			
////			return ResponseEntity.badRequest().build();
////		} catch (LocationNotFoundException e) {
////			LOGGER.error(e.getMessage(), e);
////
////			return ResponseEntity.notFound().build();
////		}
////		
////	}
//	
//	
//	//modified with modelmapper
//	//handler method that get realtime weather by the client's ip address
//	@GetMapping
//	public ResponseEntity<?> getRealtimeWeatherByIPAddress(HttpServletRequest request){
//		//get the client's ip address
//		String ipAddress = CommonUtility.getIPAddress(request);
//		
//		try {
//			//get location information from the ip address using Geolocation service
//			Location locationFromIP = locationService.getLocation(ipAddress);
//			
//			//get realtime weather data
//			RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocation(locationFromIP);
//			
//			//ModelMapper is used to map or convert entity object to DTO object and vice versa.
//			//In this case we convert realtimeWeather(entity object) to RealtimeWeatherDTO object
//			RealtimeWeatherDTO dto = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
//			
//			return ResponseEntity.ok(dto);
//			
//		} catch (GeolocationException e) {
//			//log the error
//			LOGGER.error(e.getMessage(), e);
//			
//			return ResponseEntity.badRequest().build();
//		} catch (LocationNotFoundException e) {
//			LOGGER.error(e.getMessage(), e);
//
//			return ResponseEntity.notFound().build();
//		}
//		
//	}
//
//	
//	//handler method that get realtime weather by a given location code
//	@GetMapping("/{locationCode}")
//	public ResponseEntity<?> getRealtimeWeatherByLocationCode(@PathVariable("locationCode") String locationCode) {
//		
//		
//			try {
//				RealtimeWeather realtimeWeather = realtimeWeatherService.getByLocationCode(locationCode);
//
//				//convert the return result dto
//				RealtimeWeatherDTO dto = modelMapper.map(realtimeWeather, RealtimeWeatherDTO.class);
//				
//				return ResponseEntity.ok(dto);
//			} catch (LocationNotFoundException e) {
//				LOGGER.error(e.getMessage(), e);
//				
//				return ResponseEntity.notFound().build();
//			}
//	}
//	
//	
//	//handler method that update realtime weather api data
//	//NB @RequestBody will convert information in the request body into java object
//	@PutMapping("/{locationCode}")
//	public ResponseEntity<?>  updateRealtimeWeather(@PathVariable("locationCode") String locationCode,
//			@RequestBody RealtimeWeather realtimeWeatherInRequest){
//		
//		try {
//			RealtimeWeather updateRealtimeWeather = realtimeWeatherService.update(locationCode, realtimeWeatherInRequest);
//			
//			//convert the data from java object to dto object
//			RealtimeWeatherDTO dto = modelMapper.map(updateRealtimeWeather, RealtimeWeatherDTO.class);
//			
//			return ResponseEntity.ok(updateRealtimeWeather);
//		} catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		}
//		
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//	
//}
