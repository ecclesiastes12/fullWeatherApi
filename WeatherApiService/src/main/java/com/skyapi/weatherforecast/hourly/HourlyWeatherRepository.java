package com.skyapi.weatherforecast.hourly;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.skyapi.weatherforecast.common.HourlyWeather;
import com.skyapi.weatherforecast.common.HourlyWeatherId;

public interface HourlyWeatherRepository extends CrudRepository<HourlyWeather, HourlyWeatherId>{

	//method that return list hourly weather forecast base on location code and upcoming hours.
	//That is the hour of the weather forecast must be greater than the current hour
	
	//The use of 3 """ in @Query is only available in java 17 or later 
	@Query("""
			SELECT h FROM HourlyWeather h WHERE
			h.id.location.code = ?1 AND h.id.hourOfDay > ?2
			AND h.id.location.trashed = false
			""")
	public List<HourlyWeather> findByLocationCode(String locationCode, int currentHour);
}
