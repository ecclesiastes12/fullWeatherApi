package com.skyapi.weatherforecast.common;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/*
 * A composite primary key, also called a composite key, is a combination 
 * of two or more columns to form a primary key for a table.
 * In this case we will use location object reference  and hourOfDay to represent the 
 * composite key.
 */

/*
 * HourlyWeather is supposed to have a composite primary key which consist of 
 * location_code and hour_of_day. This class represent a composite key for
 * HourlyWeather.java entity class
 * 
 * 
 * Serializable interface is a requirement for composite primary key class*/

@Embeddable
public class HourlyWeatherId implements Serializable{

	private int hourOfDay;
	//map the relationship between  hourlyWeather forecast and location
	@ManyToOne  //many hourly weather data belongs to one location
	@JoinColumn(name = "location_code")
	private Location location; //this field is a reference to the location object
	
	public HourlyWeatherId() {}

	public HourlyWeatherId(int hourOfDay, Location location) {
		super();
		this.hourOfDay = hourOfDay;
		this.location = location;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}

	public void setHourOfDay(int hourOfDay) {
		this.hourOfDay = hourOfDay;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		return Objects.hash(hourOfDay, location);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HourlyWeatherId other = (HourlyWeatherId) obj;
		return hourOfDay == other.hourOfDay && Objects.equals(location, other.location);
	}

	
	
}
