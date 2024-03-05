package com.skyapi.weatherforecast.realtime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.skyapi.weatherforecast.common.RealtimeWeather;

public interface RealtimeWeatherRepository extends CrudRepository<RealtimeWeather, String>{

	/*
	 * NB From a Location returned by Geolocation service, find a location in the database
	 * is based on country code and city name. this is because are cities with the same name
	 * in different countries. eg Berlin in USA and Germany, Manchester in UK and USA
	 */
	
	@Query("SELECT r FROM RealtimeWeather r WHERE r.location.countryCode = ?1 AND r.location.cityName = ?2")
	public RealtimeWeather findByCountryCodeAndCity(String countryCode, String city);
	
	
	//find realtime weather by location code
	//@Query("SELECT r FROM RealtimeWeather r WHERE r.location.code = ?1 AND r.location.trashed = false")
	@Query("SELECT r FROM RealtimeWeather r WHERE r.id = ?1 AND r.location.trashed = false")
	public RealtimeWeather findByLocationCode(String location);
	
}
