package com.skyapi.weatherforecast.full;

import com.skyapi.weatherforecast.realtime.RealtimeWeatherDTO;

//custom json filter method that filter out field based on a certain condition
public class RealtimeWeatherFieldFilter {

	//override the equals object
	public boolean equals(Object object) {
		//check if the object is of type RealtimeWeatherDTO
		if (object instanceof RealtimeWeatherDTO) {
			//cast the object to dto object
			RealtimeWeatherDTO dto = (RealtimeWeatherDTO) object;
			
			//does not include the status value if the value is null
			return dto.getStatus() == null;
		}
		
		return false;
	}
}
