package by.cryptic.springmarket;

import org.springframework.boot.SpringApplication;

public class TestSpringMarketApplication {

	public static void main(String[] args) {
		SpringApplication.from(SpringMarketApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
