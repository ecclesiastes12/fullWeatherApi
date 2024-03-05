package com.skyapi.weatherforecast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;

/*
 * this class is for retrieving the ip address of client
 */


public class CommonUtility {
	//logger for logging the ip address
	private static Logger LOGGER = LoggerFactory.getLogger(CommonUtility.class);

	//static method that get ip address
	public static String getIPAddress(HttpServletRequest request) {
		//in a production environment the application may run behind a load balancer
		//which hides the real IP of the client. To retrieve the clients real IP address
		//We have to get it through the request header X-FORWARED-FOR
		
		String ip = request.getHeader("X-FORWARDED-FOR"); //get ip via request header
		
		//check if the returned value is null or empty
		if(ip == null || ip.isEmpty()) {
			ip = request.getRemoteAddr(); // get ip address of the client or last proxy that sent the request
			
		}
		
		//log the ip address
		LOGGER.info("Client's IP Address: " + ip);
		
		return ip; 
		
	}
}
