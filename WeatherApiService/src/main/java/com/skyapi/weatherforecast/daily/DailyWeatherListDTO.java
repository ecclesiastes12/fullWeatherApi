package com.skyapi.weatherforecast.daily;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skyapi.weatherforecast.common.DailyWeather;

public class DailyWeatherListDTO {

	private String location;
	
	private List<DailyWeatherDTO> dailyForecast = new ArrayList<>();

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<DailyWeatherDTO> getDailyForecast() {
		return dailyForecast;
	}

	public void setDailyForecast(List<DailyWeatherDTO> dailyForecast) {
		this.dailyForecast = dailyForecast;
	}
	
	
	//NB Don't forget to create a method in DailyWeatherApiController that 
	//map or convert a ListDTO  to listEntityDTO
	//method that add DailyWeatherDTO to the list of DailyWeatherListDTO  
	public void addDailyWeatherDTO(DailyWeatherDTO dto) {
		this.dailyForecast.add(dto);
	}
	
}
