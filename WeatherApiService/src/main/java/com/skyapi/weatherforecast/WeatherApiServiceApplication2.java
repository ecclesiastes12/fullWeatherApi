//package com.skyapi.weatherforecast;
//
//import org.modelmapper.ModelMapper;
//import org.modelmapper.convention.MatchingStrategies;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import com.skyapi.weatherforecast.common.DailyWeather;
//import com.skyapi.weatherforecast.common.HourlyWeather;
//import com.skyapi.weatherforecast.common.Location;
//import com.skyapi.weatherforecast.daily.DailyWeatherDTO;
//import com.skyapi.weatherforecast.full.FullWeatherDTO;
//import com.skyapi.weatherforecast.hourly.HourlyWeatherDTO;
//
//@SpringBootApplication
//public class WeatherApiServiceApplication {
//
//	//bean for modelmapper. 
//	//NB without this bean modelmapping will not work
//	
////	@Bean
////	public ModelMapper getModelMapper() {
////		//create modelmapper object
////		ModelMapper mapper = new ModelMapper();
////		
////		//this line will strictly match the entity object to the DTO object.
////		//it can be ignored if you want the standard matching of the entiy object to the DTO object
////		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
////		return mapper;
////	}
//
//	@Bean
//	public ModelMapper getModelMapper() {
//		//create modelmapper object
//		ModelMapper mapper = new ModelMapper();
//		
//		//this line will strictly match the entity object to the DTO object.
//		//it can be ignored if you want the standard matching of the entiy object to the DTO object
//		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//		
//		//custom mapper configuration to map HourlyWeather which is an enity object class to HourlyWeatherDTO class or object
//		//because of the composite key of HourlyWeather class
//		var typeMap1 = mapper.typeMap(HourlyWeather.class, HourlyWeatherDTO.class);
//		//NB when setHourOfDay in HourlyWeatherDTO object get invoked, it will call the method getHourOfDay() on getId()
//		//on the source object of HourlyWeather object
//		typeMap1.addMapping(src -> src.getId().getHourOfDay(), HourlyWeatherDTO :: setHourOfDay);
//		
//		//from entity to dto. for updating hourly weather forecast
//		var typeMap2 = mapper.typeMap(HourlyWeatherDTO.class, HourlyWeather.class);
//		typeMap2.addMapping(src -> src.getHourOfDay(), 
//				(dest, value) -> dest.getId().setHourOfDay(value != null ? (int) value : 0));
//		
//		var typeMap3 = mapper.typeMap(DailyWeather.class, DailyWeatherDTO.class);
//		typeMap3.addMapping(src -> src.getId().getDayOfMonth(), DailyWeatherDTO :: setDayOfMonth);
//		typeMap3.addMapping(src -> src.getId().getMonth(), DailyWeatherDTO :: setMonth);
//		
//		//maps from entity to dto for updating daily weather forecast
//		var typeMap4 = mapper.typeMap(DailyWeatherDTO.class, DailyWeather.class);
//		typeMap4.addMapping(src-> src.getDayOfMonth(),
//				(dest,value) -> dest.getId().setDayOfMonth(value != null ? (int) value : 0));
//		
//		typeMap4.addMapping(src-> src.getMonth(), 
//				(dest, value) -> dest.getId().setMonth(value != null ? (int) value : 0));
//		
//		//map from Location.class to FullWeatherDTO
//		var typeMap5 = mapper.typeMap(Location.class, FullWeatherDTO.class);
//		//specify the mapping rule
//		typeMap5.addMapping(src -> src.toString(), FullWeatherDTO :: setLocation);
//		
//		
//		
//		return mapper;
//	}
//	
//	public static void main(String[] args) {
//		SpringApplication.run(WeatherApiServiceApplication.class, args);
//	}
//
//}
