//package com.skyapi.weatherforecast.common;
//
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import org.hibernate.validator.constraints.Length;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.OneToMany;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.PrimaryKeyJoinColumn;
//import jakarta.persistence.Table;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//
///*
// * before using @JsonProperty ensure that  jackson depedency is added to pom.xml
// */
//@Entity
//@Table(name = "locations")
//public class Location1 {
//
//	@Column(length = 12, nullable = false, unique = true)
//	@Id
//	@NotNull(message = "Location code cannot be null")
//	@Length(min = 3, max = 12, message = "Location code must have 3-12 characters") //for length validation
//	private String code;
//	
//	@Column(length = 128, nullable = false)
//	@JsonProperty("city_name") //to match the field names used in swagger api design
//	//NB without @JsonProperty("city_name") the field name will be printed as cityName instead of city_name
//	//To display the field name like city_name in the json document or format use @JsonProperty("city_name")
//	@NotNull(message = "City name cannot be null")
//	@Length(min = 3, max = 128, message = "Location code must have 3-128 characters")
//	private String cityName;
//	
//	@Column(length = 128)
//	@JsonProperty("region_name")
//	@Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
//	private String regionName;
//	
//	@Column(length = 64, nullable = false)
//	@JsonProperty("country_name")
//	@NotNull(message = "Country name cannot be null")
//	@Length(min = 3, max = 64, message = "Location code must have 3-64 characters")
//	private String countryName;
//	
//	@Column(length = 2, nullable = false)
//	@JsonProperty("country_code")
//	@NotNull(message = "Country code cannot be null")
//	@Length(min = 2, max = 2, message = "Country code must have 2 characters")
//	private String countryCode;
//	
//	private boolean enabled;
//	
//	//NB in this application (Weather api service application) locations will not be deleted permanently there fore it
//	//will be marked trashed
//	@JsonIgnore
//	private boolean trashed;
//	
//	//references the  realtimeWeather. the relationship is managed by Location class 
//	@OneToOne(mappedBy = "location", cascade = CascadeType.ALL) //location here refers to location in RealtimeWeather.java
//	@PrimaryKeyJoinColumn /* @PrimaryKeyJoinColumn is used to specify the primary key of the associated entity used as a 
//	 	foreign key of the current entity. This annotation is used to establish a OneToOne relationship between the two entities
//	 	where the primary key of one entity is considered the foreign key in another entity*/
//	@JsonIgnore
//	private RealtimeWeather realtimeWeather; //foreign key 
//	
//	public Location1() {}
//	
//	//this constructor will be used by geolocation
//	public Location1(String cityName, String regionName, String countryName, String countryCode) {
//		super();
//		this.cityName = cityName;
//		this.regionName = regionName;
//		this.countryName = countryName;
//		this.countryCode = countryCode;
//	}
//	
//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}
//
//	public String getCityName() {
//		return cityName;
//	}
//
//	public void setCityName(String cityName) {
//		this.cityName = cityName;
//	}
//
//	public String getRegionName() {
//		return regionName;
//	}
//
//	public void setRegionName(String regionName) {
//		this.regionName = regionName;
//	}
//
//	public String getCountryName() {
//		return countryName;
//	}
//
//	public void setCountryName(String countryName) {
//		this.countryName = countryName;
//	}
//
//	public String getCountryCode() {
//		return countryCode;
//	}
//
//	public void setCountryCode(String countryCode) {
//		this.countryCode = countryCode;
//	}
//
//	public boolean isEnabled() {
//		return enabled;
//	}
//
//	public void setEnabled(boolean enabled) {
//		this.enabled = enabled;
//	}
//
//	public boolean isTrashed() {
//		return trashed;
//	}
//
//	public void setTrashed(boolean trashed) {
//		this.trashed = trashed;
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(code);
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Location1 other = (Location1) obj;
//		return Objects.equals(code, other.code);
//	}
//
//	
//	//tostring method for all the fields
////	@Override
////	public String toString() {
////		return "Location [code=" + code + ", cityName=" + cityName + ", regionName=" + regionName + ", countryName="
////				+ countryName + ", countryCode=" + countryCode + ", enabled=" + enabled + ", trashed=" + trashed + "]";
////	}
//	
//	//toString modified to stricty match entity object to dto object
//	@Override
//	public String toString() { //(regionName != null ? regionName : "") return regionName if it is not null else it will return empty string
//		return  cityName + ", " + (regionName != null ? regionName + ", ": "") + countryName;
//	}
//
//	public RealtimeWeather getRealtimeWeather() {
//		return realtimeWeather;
//	}
//
//	public void setRealtimeWeather(RealtimeWeather realtimeWeather) {
//		this.realtimeWeather = realtimeWeather;
//	} 
//	
//	
//	
//	
//	
//}
