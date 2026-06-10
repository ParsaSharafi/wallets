package sharafi.PaymentGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {

    @Value("${wallets.base.url}")
    private String baseURL;

    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(baseURL).build();
    }
}
