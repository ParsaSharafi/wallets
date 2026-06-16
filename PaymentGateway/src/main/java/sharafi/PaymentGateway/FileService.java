package sharafi.PaymentGateway;

import com.jcraft.jsch.ChannelSftp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileService {

    private final TokenService tokenService;
    private final RestClient restClient;
    private final ChannelSftp channelSftp;

    //reinforming the wallets service of successful transfers if api call failed the first time
    @Scheduled(cron = "${cron.successful}")
    public void successful() {

        tokenService.refreshToken();

        Path path = Paths.get("SUCCESSFUL" + (Calendar.getInstance().get(Calendar.MINUTE) - 1));

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            restClient.post()
                    .uri("/AcknowledgeTransfers")
                    .body(reader.lines().toList())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenService.getToken()))
                    .retrieve()
                    .toBodilessEntity();

            Files.deleteIfExists(path);

            log.info("Wallets Service Reinformed Of Successful Transfers");

        } catch (IOException e) {
            log.warn("Failed To Read The SUCCESSFUL File: {}", e.toString());
        } catch (Exception e) {
            log.warn("Failed To Reinform The Wallets Service Of Successful Transfers: {}", e.toString());
        }
    }

    //reinforming the wallets service of unsuccessful transfers if api call failed the first time
    @Scheduled(cron = "${cron.unsuccessful}")
    public void unsuccessful() {

        tokenService.refreshToken();

        Path path = Paths.get("UNSUCCESSFUL" + (Calendar.getInstance().get(Calendar.MINUTE) - 1));

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            restClient.post()
                    .uri("/RefuseTransfers")
                    .body(reader.lines().toList())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenService.getToken()))
                    .retrieve()
                    .toBodilessEntity();

            Files.deleteIfExists(path);

            log.info("Wallets Service Reinformed Of Unsuccessful Transfers");

        } catch (IOException e) {
            log.warn("Failed To Read The UNSUCCESSFUL File: {}", e.toString());
        } catch (Exception e) {
            log.warn("Failed To Reinform The Wallets Service Of Unsuccessful Transfers: {}", e.toString());
        }
    }

    //uploading old still-pending transfers to ftp to be reinquired from banks
    @Scheduled(cron = "${cron.pending}")
    public void pending() {

        Path path = Paths.get("PENDING" + (Calendar.getInstance().get(Calendar.MINUTE) - 1));

        try (FileInputStream fis = new FileInputStream(path.toString())) {

            channelSftp.put(fis, "upload/" + path);

            Files.deleteIfExists(path);

            log.info("Successfully Uploaded Pending Transfers To FTP");

        } catch (IOException e) {
            log.warn("Failed To Read The PENDING File: {}", e.toString());
        } catch (Exception e) {
            log.warn("Failed To Upload Pending Transfers To FTP: {}", e.toString());
        }
    }
}
