//package com.skyapi.weatherforecast.hourly;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.skyapi.weatherforecast.BadRequestException;
//import com.skyapi.weatherforecast.CommonUtility;
//import com.skyapi.weatherforecast.GeolocationException;
//import com.skyapi.weatherforecast.GeolocationService;
//import com.skyapi.weatherforecast.common.HourlyWeather;
//import com.skyapi.weatherforecast.common.Location;
//import com.skyapi.weatherforecast.location.LocationNotFoundException;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//
//@RestController
//@RequestMapping("/v1/hourly")
//@Validated
//public class HourlyWeatherApiController1 {
//
//	private HourlyWeatherService hourlyWeatherService;
//	private GeolocationService locationSevice; //for getting location from ip address
//	private ModelMapper modelMapper;
//	
//	public HourlyWeatherApiController1(HourlyWeatherService hourlyWeatherService, 
//					GeolocationService locationSevice, ModelMapper modelMapper) {
//		super();
//		this.hourlyWeatherService = hourlyWeatherService;
//		this.locationSevice = locationSevice;
//		this.modelMapper = modelMapper;
//	}
//	
//	
//	//handler method that list hourly weather forecast by ip address
//	@GetMapping
//	public ResponseEntity<?> listHourlyForecastByIPAddress(HttpServletRequest request){
//		
//		//get ip address from http request using common utility method
//		String ipAddress = CommonUtility.getIPAddress(request);
//		
//		try {
//			//get the current hour value from the request header
//			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
//			
//			//get location from the ip address
//			Location locationFromIP = locationSevice.getLocation(ipAddress);
//			
//			//get list of hourly weather forecast base on ip and current hour
//			List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocation(locationFromIP, currentHour);
//		
//			//check if hourly forecast data is empty
//			if(hourlyForecast.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//			
//			//return ResponseEntity.ok(hourlyForecast);
//			return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
//		
//			//NumberFormatException and GeolocationException is catch in the same catch blocl because
//			//they throw or return the same status code 
//		} catch (NumberFormatException | GeolocationException e) { //NumberFormatException will be thrown if you don't specify X-Current-Hour
//			
//			//throw geolocation exception or statusfor invalid value from request header X-Current-Hour or during 
//			//geolocation process
//			return ResponseEntity.badRequest().build();
//		} catch (LocationNotFoundException e) {
//			//throws location not found exception or status if location does not exist in the location table
//			return ResponseEntity.notFound().build();
//		}
//	}
//	
//	
//	//handler method that list hourly weather forecast by location code
//	@GetMapping("/{locationCode}")
//	public ResponseEntity<?> listHourlyForecastByLocationCode(
//			@PathVariable("locationCode") String locationCode, 
//			HttpServletRequest request){
//		
//		try {
//			//get the current hour value from the request header
//			int currentHour = Integer.parseInt(request.getHeader("X-Current-Hour"));
//			
//			//list hourly forecast based on a given location code
//			List<HourlyWeather> hourlyForecast = hourlyWeatherService.getByLocationCode(locationCode, currentHour);
//			
//			//check if hourly forecast is empty
//			if(hourlyForecast.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//			
//			return ResponseEntity.ok(listEntity2DTO(hourlyForecast));
//		} catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		} catch (NumberFormatException e) {
//			return ResponseEntity.badRequest().build();
//		}
//	}
//	
//	
//	//handler method that update hourly weather forecast
//	@PutMapping("/{locationCode}")
//	public ResponseEntity<?> updateHourlyForecast(@PathVariable("locationCode") String locationCode,
//			@RequestBody @Valid List<HourlyWeatherDTO> listDTO) throws BadRequestException{//NB HourlyWeatherDTO is used because we will update hourly forecast for each hour
//		
//		//throw exception if listDTO is empty
//		if(listDTO.isEmpty()) {
//			//NB this exception is also configured in the global exception handler class
//			throw new BadRequestException("Hourly forecast data cannot be empty");
//		}
//		
//		//print out the content of the listDTO
//		listDTO.forEach(System.out :: println);
//		
//		List<HourlyWeather> listHourlyWeather = listDTO2ListEntity(listDTO);
//		
//		System.out.println();
//		
//		listHourlyWeather.forEach(System.out::println); 
//		
//		try {
//			List<HourlyWeather> updatedHourlyWeather = hourlyWeatherService.updateByLocationCode(locationCode, listHourlyWeather);
//		
//			return ResponseEntity.ok(listEntity2DTO(updatedHourlyWeather));
//		} catch (LocationNotFoundException e) {
//			
//			return ResponseEntity.notFound().build();
//		}
//		
//		
//	}
//	
//	//method that convert or map listDto objects to listEntity objects
//	private List<HourlyWeather> listDTO2ListEntity(List<HourlyWeatherDTO> listDTO){
//		List<HourlyWeather> listEntity = new ArrayList<>();
//		
//		listDTO.forEach(dto -> {
//			listEntity.add(modelMapper.map(dto, HourlyWeather.class));
//		});
//		
//		return listEntity;
//	}
//	
//	//method that convert listEntity to dto
//	private HourlyWeatherListDTO listEntity2DTO(List<HourlyWeather> hourlyForecast) {
//		//Get location object from the first element of list of hourly weather forecast data 
//		Location location = hourlyForecast.get(0).getId().getLocation();
//		
//		//create new HourlyWeatherListDTO object
//		HourlyWeatherListDTO listDTO = new HourlyWeatherListDTO();
//		
//		//set value for location field which is the return value of the toString object in Location.java
//		listDTO.setLocation(location.toString());
//		
//		//iterate through each HourlyWeather object in the list of hourly forecast
//		//this is a lambda expression
//		hourlyForecast.forEach(
//				//object
//				hourlyWeather -> {
//					//convert hourlyWeather to dto
//					HourlyWeatherDTO dto = modelMapper.map(hourlyWeather, HourlyWeatherDTO.class);
//					//add dto to list of list of hourly weather forecast dto
//					listDTO.addWeatherHourlyDTO(dto);
//				});
//		return listDTO;
//	}
//	
//	
//}
