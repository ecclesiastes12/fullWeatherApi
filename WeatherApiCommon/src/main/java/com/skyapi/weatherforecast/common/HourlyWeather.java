package com.skyapi.weatherforecast.common;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/*
 * This class represent weather forecast information for a specific hour
 * 
 * @EmbeddedId Applied to a persistent field or property of an entity class 
 * or mapped superclass to denote a composite primarykey that is an embeddable class. 
 * The embeddable class must be annotated as Embeddable. There must be only one EmbeddedId 
 * annotation andno Id annotation when the EmbeddedId annotation is used. 
 */

@Entity
@Table(name = "weather_hourly")
public class HourlyWeather {

	/*
	 * HourlyWeather is supposed to have a composite primary key which consist of 
	 * location_code and hour_of_day. This is because this columns uniquely identifies
	 * hourlyWeather forecast for a specify hour and location. In order to implement a composite
	 * key that contains two columns (locationCcode and hourOfDay). we need to created 
	 * HourlyWeatherId.java that represent the composite key which is used as primary
	 * key in HourlyWeather.java entity class
	 *  */
	
	@EmbeddedId
	private HourlyWeatherId id = new HourlyWeatherId(); //id instantiate as an object of HourlyWeatherId
	
	private int temperature;
	private int precipitation;
	
	@Column(length = 50)
	private String status;

	public HourlyWeatherId getId() {
		return id;
	}

	public void setId(HourlyWeatherId id) {
		this.id = id;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(int precipitation) {
		this.precipitation = precipitation;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	//NB It will be more convenient to use the methods below to set
	//hourly weather data for a location than to use the setters and getters
	//above
	
	//builder method that set temperature
	public HourlyWeather temperature(int temp) {
		setTemperature(temp);
		return this;
	}
	
	//NB id variable declared above is a composite key
	//builder method that set hour of the day and location for hourly weather data
	public HourlyWeather id(Location location, int hour) {
		this.id.setHourOfDay(hour);
		this.id.setLocation(location);
		return this;
	}
	
	//builder method that set precipitation
	public HourlyWeather precipitarion(int precipitation ) {
		setPrecipitation(precipitation);
		return this;
	}
	
	//builder method that set status
	public HourlyWeather status(String status) {
		setStatus(status);
		return this;
	}
	
	//builder method that set location
	public HourlyWeather location(Location location) {
		this.id.setLocation(location);
		return this;
	}
	
	//builder method that set hour of day
	public HourlyWeather hourOfDay(int hour) {
		this.id.setHourOfDay(hour);
		return this;
	}

	@Override
	public String toString() {
		return "HourlyWeather [hourOfDay=" + id.getHourOfDay() + ", temperature=" + temperature + ", precipitation=" + precipitation
				+ ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyWeather other = (HourlyWeather) obj;
		return Objects.equals(id, other.id);
	}
	
	//NB for DELETE part of updateByLocationCode
	//method that contains a copy of the id fields only
	public HourlyWeather getShallowCopy() {
		//create new hourly weather object
		HourlyWeather copy = new HourlyWeather();
		copy.setId(this.getId());
		return copy;
	}
	
}



