package sharafi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WalletsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletsApplication.class, args);
	}
}
