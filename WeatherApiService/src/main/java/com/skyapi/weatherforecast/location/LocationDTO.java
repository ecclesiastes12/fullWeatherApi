package com.skyapi.weatherforecast.location;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@JsonPropertyOrder({"code","city_name","region_name","country_code","country_name","enabled"})
public class LocationDTO {
	
	
	@NotNull(message = "Location code cannot be null")
	@Length(min = 3, max = 12, message = "Location code must have 3-12 characters") //for length validation
	private String code;
	
	@JsonProperty("city_name") //to match the field names used in swagger api design
	//NB without @JsonProperty("city_name") the field name will be printed as cityName instead of city_name
	//To display the field name like city_name in the json document or format use @JsonProperty("city_name")
	@NotNull(message = "City name cannot be null")
	@Length(min = 3, max = 128, message = "Location code must have 3-128 characters")
	private String cityName;
	
	@JsonProperty("region_name")
	@Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
	@JsonInclude(JsonInclude.Include.NON_NULL)//specifies that it should include this field only when the value
	//is not null
	private String regionName;
	
	@JsonProperty("country_name")
	@NotNull(message = "Country name cannot be null")
	@Length(min = 3, max = 64, message = "Location code must have 3-64 characters")
	private String countryName;
	

	@JsonProperty("country_code")
	@NotNull(message = "Country code cannot be null")
	@Length(min = 2, max = 2, message = "Country code must have 2 characters")
	private String countryCode;
	
	private boolean enabled;

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
	
	
}
