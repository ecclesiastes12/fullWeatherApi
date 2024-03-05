package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.DailyWeather;
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/daily")
@Validated
public class DailyWeatherApiController {

	private DailyWeatherService dailyWeatherService;
	private GeolocationService locationService;
	private ModelMapper modelMapper;
	
	public DailyWeatherApiController(DailyWeatherService dailyWeatherService, GeolocationService locationService,
			ModelMapper modelMapper) {
		super();
		this.dailyWeatherService = dailyWeatherService;
		this.locationService = locationService;
		this.modelMapper = modelMapper;
	}


	//Before dto
//	@GetMapping
//	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request){
//		
//		//get the ip address of a location
//		String ipAddress = CommonUtility.getIPAddress(request);
//		
//		try {
//			//get location from the ip address
//			Location locationFromIP = locationService.getLocation(ipAddress);
//			
//			//get the list of daily weather forecast for the location
//			List<DailyWeather> dailyWeather = dailyWeatherService.getByLocation(locationFromIP);
//			dailyWeather.forEach(System.out::println);
//			
//			//check if data returned by daily weather forecast is empty
//			//for 204 status code no content found
//			if(dailyWeather.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//			
//			return ResponseEntity.ok(dailyWeather);
//			
//		} catch (GeolocationException e) {
//			// TODO Auto-generated catch block
//			return ResponseEntity.badRequest().build();
//		}catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
	
	//after dto
//	@GetMapping
//	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request){
//		
//		//get the ip address of a location
//		String ipAddress = CommonUtility.getIPAddress(request);
//		
//		try {
//			//get location from the ip address
//			Location locationFromIP = locationService.getLocation(ipAddress);
//			
//			//get the list of daily weather forecast for the location
//			List<DailyWeather> dailyWeather = dailyWeatherService.getByLocation(locationFromIP);
//			dailyWeather.forEach(System.out::println);
//			
//			//check if data returned by daily weather forecast is empty
//			//for 204 status code no content found
//			if(dailyWeather.isEmpty()) {
//				return ResponseEntity.noContent().build();
//			}
//			
//			return ResponseEntity.ok(listEntity2DTO(dailyWeather));
//			
//		} catch (GeolocationException e) {
//			// TODO Auto-generated catch block
//			return ResponseEntity.badRequest().build();
//		}catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
	
//	//NB Before GeolocationException  is  handled by 
//	//GlobalException
//	@GetMapping
//	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) throws GeolocationException {
//		
//		//get the ip address of a location
//		String ipAddress = CommonUtility.getIPAddress(request);
//		
//	
//			//get location from the ip address
//			
//			
//				//get location from the ip address
//				Location locationFromIP = locationService.getLocation(ipAddress);
//				
//				//get the list of daily weather forecast for the location
//				List<DailyWeather> dailyWeather = dailyWeatherService.getByLocation(locationFromIP);
//				//dailyWeather.forEach(System.out::println);
//				//check if data returned by daily weather forecast is empty
//				//for 204 status code no content found
//				if(dailyWeather.isEmpty()) {
//					return ResponseEntity.noContent().build();
//				}
//			
//			
//			return ResponseEntity.ok(listEntity2DTO(dailyWeather));
//
//	}
	
	
	//NB GeolocationException and LocationNotFoundException is now handled by 
		//GlobalException. Don't forget to change Exception to RuntimeException in GeolocationException.java
	@GetMapping
	public ResponseEntity<?> listDailyForecastByIPAddress(HttpServletRequest request) {
			
			//get the ip address of a location
		String ipAddress = CommonUtility.getIPAddress(request);
	
				//get location from the ip address
		Location locationFromIP = locationService.getLocation(ipAddress);
				
				//get the list of daily weather forecast for the location
		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocation(locationFromIP);
				//dailyWeather.forEach(System.out::println);
				//check if data returned by daily weather forecast is empty
				//for 204 status code no content found
		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
				
		return ResponseEntity.ok(listEntity2DTO(dailyForecast));

		}
	
	//handler method that get daily weather by location code
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> listDailyForecastByLocationCode(@PathVariable("locationCode") String locationCode) {
		List<DailyWeather> dailyForecast = dailyWeatherService.getByLocationCode(locationCode);
			
		//check if the return results is empty
		if (dailyForecast.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(listEntity2DTO(dailyForecast));
	}
	
	
	//method that update daily weaether forecast
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateDailyForecast(@PathVariable("locationCode") String code,
			@RequestBody @Valid List<DailyWeatherDTO> listDTO) throws BadRequestException {
		//throw exception if listDTO is empty
		if (listDTO.isEmpty()) {
			throw new BadRequestException("Daily forecast data cannot be empty");
		}
		
		listDTO.forEach(System.out::println);
		
		//convert listDTO to listEntity
		List<DailyWeather> dailyWeather = listDTO2ListEntity(listDTO);
		
		dailyWeather.forEach(System.out::println);
		
		//update daily weather
		List<DailyWeather> updatedForecast = dailyWeatherService.updateByLocationCode(code, dailyWeather);
		
		return ResponseEntity.ok(listEntity2DTO(updatedForecast));//return and convert updated dailyweather to dto
		
	}
	
	//private method that convert listEntityDTO object (DailyWeather) to DailyWeatherListDTO, thus convert listDTO to listEntity object
	private DailyWeatherListDTO listEntity2DTO(List<DailyWeather> dailyForecast) {
		//get the location from the location object
		//NB take note of how location is accessed from the DailyWeather object
		//get(0)  --> get the first element of the dailyWeatherForecast
		//getid() --> get the id
		//getLocation --. get the location which consist of the city name,region name and country name
		Location location = dailyForecast.get(0).getId().getLocation();
		
		//create DailyWeatherListDTO object
		DailyWeatherListDTO listDTO = new DailyWeatherListDTO();
		
		//set value for the field location which is the return value of the toString method
		//of the location object
		listDTO.setLocation(location.toString());
		
		//iterate through each dailyWeatherForest in the list
		dailyForecast.forEach(dailyWeather -> {
			//map single object using modelMapper
			//DailyWeatherDTO dto = modelMapper.map(dailyWeather, DailyWeatherDTO.class);
			
			//add or put the dto object to the list dto
			listDTO.addDailyWeatherDTO(modelMapper.map(dailyWeather, DailyWeatherDTO.class));
		});
		
		return listDTO;
	}
	
	
	//private method that converts listEntity to listDTO
	private List<DailyWeather> listDTO2ListEntity(List<DailyWeatherDTO> listDTO) {
		List<DailyWeather> listEntity = new ArrayList<>();
		listDTO.forEach(dto -> {
			//add new daily weather object to the list of entities
			listEntity.add(modelMapper.map(dto, DailyWeather.class));
		});
		
		return listEntity;
	}
	
	
	
	
	
	
}
