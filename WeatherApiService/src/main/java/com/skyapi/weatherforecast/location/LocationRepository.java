package com.skyapi.weatherforecast.location;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.skyapi.weatherforecast.common.Location;

public interface LocationRepository extends CrudRepository<Location, String>{

	
	//repository method that return only untrashed locations. thus location that are
	//marked as trashed or deleted but not deleted completely from the database.
	//NB Because of the nature of the api locations are not deleted completely from the database
	
	@Query("SELECT l FROM Location l WHERE l.trashed = false")
	public List<Location> findUntrashed();
	
	//repository method that find existing location by code
	//NB we will always find locations which are not deleted therefore locations
	//where trashed = false will always be selected
	
	@Query("SELECT l FROM Location l WHERE l.trashed = false AND l.code = ?1")
	public Location findByCode(String code);
	
	//repository method that marked the location as trashed. This is a delete method but
	//in this case we cannot use deleteById because, deleteById will delete the location
	//completely from the database and that is not what we want.
	
	@Modifying
	@Query("UPDATE Location SET trashed = true WHERE code = ?1")
	public void trashByCode(String code);
	
	
	//query method that find location based on country code and city name
	@Query("SELECT l FROM Location l WHERE l.countryCode = ?1 AND l.cityName = ?2 AND l.trashed = false ")
	public Location findByCountryCodeAndCityName(String countryCode, String cityName);
	
	
	
	
	
	
}
