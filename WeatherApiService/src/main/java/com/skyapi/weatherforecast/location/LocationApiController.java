package com.skyapi.weatherforecast.location;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skyapi.weatherforecast.common.Location;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/v1/locations")
public class LocationApiController {
	
	private LocationService service;
	private ModelMapper modelMapper; //NB ModelMapper is a bean configured in WeatherApiServiceApplication.java

	//constructor based injection
	public LocationApiController(LocationService service, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.modelMapper = modelMapper;
	}
	
	//code before LocationDTO
	//controller method that add a location
//	@PostMapping
//	public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location){
//		Location addLocation = service.add(location);
//		//uri to be send to the client after location is added
//		URI uri = URI.create("/v1/locations/" + addLocation.getCode());
//		
//		return ResponseEntity.created(uri).body(addLocation);
//	}
	
	//code modified with dto
	//controller method that add a location
	@PostMapping
	public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO dto){ //parameter change from location object to dto object
		Location addedLocation = service.add(dto2Entity(dto));
		//uri to be send to the client after location is added
		URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
		
		return ResponseEntity.created(uri).body(entity2DTO(addedLocation));
	}
	

	//method that list location
	@GetMapping
	public ResponseEntity<?> listLocations(){
		//get the list of locations
		List<Location> locations = service.list();
		
		//check if results is empty
		if(locations.isEmpty()) {
			//return no content response 204 if location is empty
			return ResponseEntity.noContent().build();
		}
		
		//returns 200 OK response if data is found
		return ResponseEntity.ok(listEntity2ListDTO(locations));
	}
	
	//GlobalExceptionHandler.java now handles LocationNotFoundException 
	//controller method that get location by a given location code
	@GetMapping("/{code}")
	public ResponseEntity<?> getLocation(@PathVariable("code") String code){
		
		//get location by code
		Location location = service.get(code);
		
		//code moved to location service class with ResponseEntity.notFound().build() change to
		//throw new LocationNotFoundException(code)
		//check of location is null
//		if(location == null) {
//			//returns not found response status
//			return ResponseEntity.notFound().build();
//		}
		
		//return ok response status if location is not null
		return ResponseEntity.ok(entity2DTO(location));
	}

	//code modified see LocationApiController1.java for code before dto
	//controller method that update location
//	@PutMapping
//	public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO dto){
//		
//		try {
//			//updates location
//			Location updatedLocation =  service.update(dto2Entity(dto));
//			
//			return ResponseEntity.ok(entity2DTO(updatedLocation));
//		} catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
	
	
	//GlobalExceptionHandler.java now handles LocationNotFoundException
	@PutMapping
	public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO dto){

			//updates location
			Location updatedLocation =  service.update(dto2Entity(dto));
			
			return ResponseEntity.ok(entity2DTO(updatedLocation));
	}
	
	
	//controller method that delete or trash location by code
//	@DeleteMapping("/{code}")
//	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
//		try {
//			service.delete(code); //trash location
//			
//			return ResponseEntity.noContent().build();//returns no content response status code after location is trashed 
//		} catch (LocationNotFoundException e) {
//			//throws not found exception if location does not exist
//			return ResponseEntity.notFound().build();
//		}
//	}
	
	//GlobalExceptionHandler.java now handles LocationNotFoundException
	@DeleteMapping("/{code}")
	public ResponseEntity<?> deleteLocation(@PathVariable("code") String code){
	
			service.delete(code); //trash location
			
			return ResponseEntity.noContent().build();//returns no content response status code after location is trashed 
		
	}
	
	//method that convert entity to dto
	public LocationDTO entity2DTO(Location location) {
		return modelMapper.map(location, LocationDTO.class);
	}
	
	//method that convert DTO to entity
	public Location dto2Entity(LocationDTO dto) {
		return modelMapper.map(dto, Location.class);
	}
	
	//NB notice how the convertion is done
	//method that convert List Entity to List DTO
	public List<LocationDTO> listEntity2ListDTO(List<Location> listEntity){
		return listEntity.stream().map(entity -> entity2DTO(entity))
								 						.collect(Collectors.toList());
	}
}
