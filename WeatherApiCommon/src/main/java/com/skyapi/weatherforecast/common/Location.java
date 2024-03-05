package com.skyapi.weatherforecast.common;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/*NB all validation constraint is removed because of LocationDTO and validation of this class is now optional.
 * See Location2.java for the validation constraints that was used before LocationDTO was introduced.
 * before using @JsonProperty ensure that  jackson depedency is added to pom.xml
 */
@Entity
@Table(name = "locations")
public class Location {

	@Column(length = 12, nullable = false, unique = true)
	@Id
	private String code;
	
	@Column(length = 128, nullable = false)
	private String cityName;
	
	@Column(length = 128)
	private String regionName;
	
	@Column(length = 64, nullable = false)
	private String countryName;
	
	@Column(length = 2, nullable = false)
	private String countryCode;
	
	private boolean enabled;
	
	@JsonIgnore
	private boolean trashed;
	
	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL) //location here refers to location in RealtimeWeather.java
	@PrimaryKeyJoinColumn 
	private RealtimeWeather realtimeWeather; //foreign key 
	
	//Check Location1.java to see all comments and codes on Location before this field for further explanation
	//One location has many hourly weather data
	//NB Note how this mappedBy is referenced. HourlyWeather class has a field variable named id which is an embedded Id. Which
	//reference an embeddable HourlyWeatherId class which contains the field location, which also reference the Location class
	@OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true) 
	private List<HourlyWeather> listHourlyWeather = new ArrayList<>();
	
	@OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DailyWeather> listDailyWeather = new ArrayList<>();
	
	public Location() {}
	
	//this constructor will be used by geolocation
	public Location(String cityName, String regionName, String countryName, String countryCode) {
		super();
		this.cityName = cityName;
		this.regionName = regionName;
		this.countryName = countryName;
		this.countryCode = countryCode;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isTrashed() {
		return trashed;
	}

	public void setTrashed(boolean trashed) {
		this.trashed = trashed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		return Objects.equals(code, other.code);
	}

	
	//tostring method for all the fields
//	@Override
//	public String toString() {
//		return "Location [code=" + code + ", cityName=" + cityName + ", regionName=" + regionName + ", countryName="
//				+ countryName + ", countryCode=" + countryCode + ", enabled=" + enabled + ", trashed=" + trashed + "]";
//	}
	
	//toString modified to stricty match entity object to dto object
	@Override
	public String toString() { //(regionName != null ? regionName : "") return regionName if it is not null else it will return empty string
		return  cityName + ", " + (regionName != null ? regionName + ", ": "") + countryName;
	}

	public RealtimeWeather getRealtimeWeather() {
		return realtimeWeather;
	}

	public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
		this.realtimeWeather = realtimeWeather;
	}

	public List<HourlyWeather> getListHourlyWeather() {
		return listHourlyWeather;
	}

	public void setListHourlyWeather(List<HourlyWeather> listHourlyWeather) {
		this.listHourlyWeather = listHourlyWeather;
	} 
	
	public List<DailyWeather> getListDailyWeather() {
		return listDailyWeather;
	}

	public void setListDailyWeather(List<DailyWeather> listDailyWeather) {
		this.listDailyWeather = listDailyWeather;
	}

	//builder method that set location code. this will be used to set location
	//for hourly weather data instead of using setter and getter of the variable code
	public Location code(String code) {
		setCode(code);
		return this;
	}
	
	
	
	
	
	//NB THE COMMENTTED CODE BELOW CAN BE USE IN PLACE OF SETTERS AND
	//GETTER TO SET LOCATION DETAIL AS IN LocationRepositoryTest.java. (method chaining)	
//	public Location countryCode(String countryCode) {
//		setCountryCode(countryCode);
//		return this;
//	}
//	
//	public Location regionName(String regionName) {
//		setRegionName(regionName);
//		return this;
//	}
//	
//	public Location cityName(String cityName) {
//		setCityName(regionName);
//		return this;
//	}
//	
//	public Location countryName(String countryName) {
//		setCountryName(countryName);
//		return this;
//	}
//	public Location enabled(boolean enabled) {
//		enabled(enabled);
//		return this;
//	}
}
