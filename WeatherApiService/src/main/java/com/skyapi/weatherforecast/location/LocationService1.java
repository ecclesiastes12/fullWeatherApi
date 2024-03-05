//package com.skyapi.weatherforecast.location;
//
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.skyapi.weatherforecast.common.Location;
//
//@Service
//@Transactional
//public class LocationService1 {
//
//	private LocationRepository repo;
//	
//	//constructor based injection
//	public LocationService1(LocationRepository repo) {
//		super();
//		this.repo = repo;
//	}
//	
//	
//	//service method that add a location
//	public Location add(Location location) {
//		return repo.save(location);
//	}
//	
//	//service method that list locations
//	public List<Location> list(){
//		return repo.findUntrashed();
//	}
//	
//	
////	//service method that get location by a give location code
////	public Location get(String code) {
////		Location location = repo.findByCode(code);
////	
////		if(location == null) {
////			throw new LocationNotFoundException("Location not found with the given code: " + code);
////		}	
////		return location;
////	}
//	
//	//method modified. code before LocationNotFoundException is handled by GlobalExceptionHandler.java 
//	//service method that get location by a give location code
//	public Location get(String code) throws LocationNotFoundException {
//		Location location = repo.findByCode(code);
//		
//		if(location == null) {
//			//NB "Location not found with the given code: " is moved to LocationNotFoundException.java
//			throw new LocationNotFoundException(code);
//		}
//		
//		return location;
//	}
//	
//  //code before LocationNotFoundException is handled by GlobalExceptionHandler.java 
//	//service method that update location
//	public Location update(Location locationInRequest) throws LocationNotFoundException {
//		
//		//get a code for the location to be updated
//		String code = locationInRequest.getCode(); //this code will be from user input
//		
//		//get the location in the database
//		Location locationInDB = repo.findByCode(code);
//		
//		//check if location in db is null. meaning the location does not exist
//		if(locationInDB == null) {
//			//NB "Location not found with the given code: " is moved to LocationNotFoundException.java
//			//throws exception
//			throw new LocationNotFoundException(code);
//		}
//		
//		//update location if the location code exist in the database. This is done by
//		//coping the values in the fields of locationInRequest object(from the web client or user endpoint)
//		//into locationInDB object
//		locationInDB.setCityName(locationInRequest.getCityName());
//		locationInDB.setRegionName(locationInRequest.getRegionName());
//		locationInDB.setCountryCode(locationInRequest.getCountryCode());
//		locationInDB.setCountryName(locationInRequest.getCountryName());
//		locationInDB.setEnabled(locationInRequest.isEnabled());
//		
//		return repo.save(locationInDB);
//		
//	}
//	
////	//NB without transactional annotation from spring framework this delete method will not work
////	//service method that delete location. NB this method does not delete the location completely
////	//but disable it or mark it as trashed
////	public void delete(String code) throws LocationNotFoundException {
////		//get the location code to be delete first
////		Location location = repo.findByCode(code);
////		//check if the return result from location is null
////		if(location == null) {
////			//throws exception
////			throw new LocationNotFoundException("No location found with the given code:" + code);
////		}
////		
////		//trash location if it exist
////		repo.trashByCode(code);
////	}
//	
//     //code before LocationNotFoundException is handled by GlobalExceptionHandler.java 
//	//NB without transactional annotation from spring framework this delete method will not work
//		//service method that delete location. NB this method does not delete the location completey
//		//but disable it or mark it as trashed
//		public void delete(String code) throws LocationNotFoundException {
//			//get the location code to be delete first
//			Location location = repo.findByCode(code);
//			//check if the return result from location is null
//			if(location == null) {
//				//NB "Location not found with the given code: " is moved to LocationNotFoundException.java
//				//throws exception
//				throw new LocationNotFoundException(code);
//			}
//			
//			//trash location if it exist
//			repo.trashByCode(code);
//		}
//	
//}
