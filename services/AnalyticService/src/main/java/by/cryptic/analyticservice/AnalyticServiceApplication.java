package by.cryptic.analyticservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
public class AnalyticServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticServiceApplication.class, args);
	}

}
