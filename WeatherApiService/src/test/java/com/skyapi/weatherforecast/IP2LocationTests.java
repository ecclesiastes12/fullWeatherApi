package com.skyapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;

public class IP2LocationTests {
	
	//Test IP2Location database
	
	
	//declare path for IP2location database
	private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";
	
	
	//test for invalid ip address
	@Test
	public void testInvalidIP() throws IOException {
		
		//create new IP2Location object
		IP2Location ipLocator = new IP2Location();
		
		//opens the IP2Location file
		ipLocator.Open(DBPath);
		
		//ip address
		String ipAddress = "abc";
		//perform ip query
		IPResult ipResult = ipLocator.IPQuery(ipAddress);
		
		//check the status
		assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
		
		//print result details
		System.out.println(ipResult);
	}

	
	//test for valid ip address
	@Test
	public void testValidIP1() throws IOException {
		//create new IP2Location object
		IP2Location ipLocator = new IP2Location();
		
		//opens the IP2Location file
		ipLocator.Open(DBPath);
		
		//ip address
		String ipAddress = "108.30.178.78"; //Ip address in New York
		//perform ip query
		IPResult ipResult = ipLocator.IPQuery(ipAddress);
		
		//check the status
		assertThat(ipResult.getStatus()).isEqualTo("OK");
		assertThat(ipResult.getCity()).isEqualTo("New York City");
		
		//print result details
		System.out.println(ipResult);
	}
	
	
	@Test
	public void testValidIP2() throws IOException {
		//create new IP2Location object
		IP2Location ipLocator = new IP2Location();
		
		//opens the IP2Location file
		ipLocator.Open(DBPath);
		
		//ip address
		String ipAddress = "103.48.198.141"; //Ip address in Delhi
		//perform ip query
		IPResult ipResult = ipLocator.IPQuery(ipAddress);
		
		//check the status
		assertThat(ipResult.getStatus()).isEqualTo("OK");
		assertThat(ipResult.getCity()).isEqualTo("Delhi");
		
		//print result details
		System.out.println(ipResult);
	}
}
