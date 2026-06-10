package sharafi.PaymentGateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private static String token;
    private static final int MAX_RETRIES = 3;
    private static int loginAttempts = 0;

    @Value("${wallets.gateway.username}")
    private String username;

    @Value("${wallets.gateway.password}")
    private String password;

    private final RestClient restClient;

    //to refresh jwt token every 45 minutes
    @Scheduled(fixedRate = 45 * 60 * 1000, initialDelay = 0)
    public void refreshToken() {

        try {
            token = Objects.requireNonNull(restClient.post()
                            .uri("/LogIn")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(new User(username, password))
                            .retrieve()
                            .body(ResponseDTO.class), "No Token Returned")
                    .data()
                    .toString();

            log.info("JWT Token Has Been Refreshed");
            loginAttempts = 0;

        } catch (Exception e) {

            loginAttempts++;

            log.error("Attempt {} At Retrieving Token Failed: {}", loginAttempts, e.getMessage());

            try {Thread.sleep(3000);} catch (InterruptedException ignored) {}

            if (loginAttempts < MAX_RETRIES)
                refreshToken();
        }
    }

    public String getToken() {
        return token;
    }
}
