//package com.skyapi.weatherforecast.location;
//
//import java.net.URI;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.skyapi.weatherforecast.common.Location;
//
//import jakarta.validation.Valid;
//
//
//
//@RestController
//@RequestMapping("v1/locations")
//public class LocationApiController1 {
//	
//	private LocationService service;
//	private ModelMapper modelMapper; //NB ModelMapper is a bean configured in WeatherApiServiceApplication.java
//
//	//constructor based injection
//	public LocationApiController1(LocationService service, ModelMapper modelMapper) {
//		super();
//		this.service = service;
//		this.modelMapper = modelMapper;
//	}
//	
//	//controller method that add a location
//	@PostMapping
//	public ResponseEntity<Location> addLocation(@RequestBody @Valid Location location){
//		Location addLocation = service.add(location);
//		//uri to be send to the client after location is added
//		URI uri = URI.create("/v1/locations/" + addLocation.getCode());
//		
//		return ResponseEntity.created(uri).body(addLocation);
//	}
//	
//
//	//method that list location
//	@GetMapping
//	public ResponseEntity<?> listLocations(){
//		//get the list of locations
//		List<Location> locations = service.list();
//		
//		//check if results is empty
//		if(locations.isEmpty()) {
//			//return no content response 204 if location is empty
//			return ResponseEntity.noContent().build();
//		}
//		
//		//returns 200 OK response if data is found
//		return ResponseEntity.ok(locations);
//	}
//	
//	//controller method that get location by a given location code
//	@GetMapping("/{code}")
//	public ResponseEntity<?> getLocation(@PathVariable("code") String code){
//		
//		//get location by code
//		Location location = service.get(code);
//		
//		//check of location is null
//		if(location == null) {
//			//returns not found response status
//			return ResponseEntity.notFound().build();
//		}
//		
//		//return ok response status if location is not null
//		return ResponseEntity.ok(location);
//	}
//
//	//controller method that update location
//	@PutMapping
//	public ResponseEntity<?> updateLocation(@RequestBody @Valid Location location){
//		
//		try {
//			//updates location
//			Location updatedLocation =  service.update(location);
//			
//			return ResponseEntity.ok(updatedLocation);
//		} catch (LocationNotFoundException e) {
//			return ResponseEntity.notFound().build();
//		}
//	}
//
//	//controller method that delete or trash location by code
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
//}
