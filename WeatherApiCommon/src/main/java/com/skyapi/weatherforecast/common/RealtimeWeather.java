package com.skyapi.weatherforecast.common;

import java.util.Date;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {

	//NB All validation constraint now moved to RealtimeWeatherDTO
	
	
	//@JsonIgnore //this will prevent spring from serializing or converting the field into json object in the request body 
	@Id @Column(name = "location_code")
	private String locationCode;
	
	//for temperature value range
	//@Range(min = -50, max = 50, message = "Temperature must be in the range of -50 to 50 Celsius degree")
	private int temperature;
	
	//@Range(min = 0, max = 100, message = "Humidity must be in the range of 0 to 100 percentage")
	private int humidity;
	
	//@Range(min = 0, max = 100, message = "Percipitation must be in the range of 0 to 100 percentage")
	private int precipitation;
	
	
	//@Range(min = 0, max = 200, message = "Wind speed must be in the range of 0 to 200 km/h")
	private int windSpeed;
	
	@Column(length = 50)
	@NotBlank(message = "Status must not be empty")
	@Length(min = 3, max = 50, message = "Status must be in between 3 - 50 characters")
	private String status;
	
	@JsonProperty("last_updated")
	@JsonIgnore
	private Date lastUpdated;

	@OneToOne
	@JoinColumn(name = "location_code")
	@MapsId /*@MapsId is used here to map the Id field (code) of Location class to 
	this field (location) which is a foreign key in this RealtimeWeather class.
	NB this annotation is used to obtain OneToOne relationship between two entities by 
	   mapping the primary key of one entity to the foreign key of another entity. This 
	   annotation is used when we have to use a shared primary key between to entities*/
	@JsonIgnore
	private Location location;

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}

	public int getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(int precipitation) {
		this.precipitation = precipitation;
	}

	public int getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(int windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Location getLocation() {
		return location;
	}

//	public void setLocation(Location location) {
//		this.location = location;
//	}
	
	//setter modified to set location code
	public void setLocation(Location location) {
		//set location code
		this.locationCode = location.getCode();
		this.location = location;
	}

	@Override
	public int hashCode() {
		return Objects.hash(locationCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealtimeWeather other = (RealtimeWeather) obj;
		return Objects.equals(locationCode, other.locationCode);
	}
	
	

}
