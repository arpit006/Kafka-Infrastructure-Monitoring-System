package com.navis.consumerlagmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConsumerLagMonitoringApplication {

	/**
	 * Main method which gets called by the spring
	 * @param inArgs String
	 */
	public static void main(String[] inArgs) {
		SpringApplication.run(ConsumerLagMonitoringApplication.class, inArgs);
	}

}
