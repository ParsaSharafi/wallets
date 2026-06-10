package sharafi.ExchangeRateUpdater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExchangeRateUpdaterApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExchangeRateUpdaterApplication.class, args);
	}
}