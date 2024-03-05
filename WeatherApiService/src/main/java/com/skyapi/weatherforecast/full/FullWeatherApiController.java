package com.skyapi.weatherforecast.full;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skyapi.weatherforecast.BadRequestException;
import com.skyapi.weatherforecast.CommonUtility;
import com.skyapi.weatherforecast.GeolocationService;
import com.skyapi.weatherforecast.common.Location;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/full")
public class FullWeatherApiController {
	
	//reference of Geolocation service class
	private GeolocationService locationService;
	private FullWeatherService weatherSerivice;
	private ModelMapper modelMapper;
	
	public FullWeatherApiController(GeolocationService locationService, FullWeatherService weatherSerivice,
			ModelMapper modelMapper) {
		super();
		this.locationService = locationService;
		this.weatherSerivice = weatherSerivice;
		this.modelMapper = modelMapper;
	}


	//handler method that get full weather by ip address
	@GetMapping
	public ResponseEntity<?> getFullWeatherByIPAddress(HttpServletRequest request){
		//get ip address
		String ipAddress = CommonUtility.getIPAddress(request);
		
		//get location from ipAddress
		Location locationFromIP = locationService.getLocation(ipAddress);
		
		//get location in db using the location object from the ip Address
		Location locationInDB = weatherSerivice.getByLocation(locationFromIP);
		
		return ResponseEntity.ok(entity2DTO(locationInDB));
	}

	
	//handle method that get location by location code
	@GetMapping("/{locationCode}")
	public ResponseEntity<?> getFullWeatherByLocationCode(@PathVariable("locationCode") String locationCode){
		Location locationInDB = weatherSerivice.get(locationCode);
		return ResponseEntity.ok(entity2DTO(locationInDB));
	}
	
	
	
	
	//handler method that update full weather service
	@PutMapping("/{locationCode}")
	public ResponseEntity<?> updateFullWeather(@PathVariable("locationCode") String locationCode,
			@RequestBody @Valid FullWeatherDTO dto) throws BadRequestException{ //FullWeatherDTO  dto represent the information in the requestbody
		
		//throws badrequest exception if hourly weather list is empty
		if(dto.getListHourlyWeather().isEmpty()) {
			throw new BadRequestException("Hourly Weather data cannot be empty");
		}
		if (dto.getListDailyWeather().isEmpty()) {
			throw new BadRequestException("Daily Weather data cannot be empty");
		}
		
		Location locationInRequest = dto2Entity(dto);
		
		Location updatedLocation = weatherSerivice.update(locationCode, locationInRequest);
		
		return ResponseEntity.ok(entity2DTO(updatedLocation));
	}
	
	//private method that convert FullWeather entity object to FullwWeatherDTO
		private FullWeatherDTO entity2DTO(Location entity) {
			FullWeatherDTO dto = modelMapper.map(entity, FullWeatherDTO.class);
			//set the value of location to null because as per the api documentation realtime_weather has no location
			//NB don't forget to add @JsonInclude(JsonInclude.Include.NON_NULL) to the location property in RealtimeWeatherDTO
			dto.getRealtimeWeather().setLocation(null);
			return dto;
		}
		
	//private method that convert dto to entity
	private Location dto2Entity(FullWeatherDTO dto) {
		return modelMapper.map(dto, Location.class);
	}
	
}
