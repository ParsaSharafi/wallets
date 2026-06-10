package sharafi.PaymentGateway;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileService {

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    private final TokenService tokenService;
    private final RestClient restClient;

    //reinforming the wallets service of successful transfers if api call failed the first time
    @Scheduled(cron = "${cron.successful}")
    public void successful() {

        tokenService.refreshToken();

        Path path = Paths.get("SUCCESSFUL" + (Calendar.MINUTE - 1));

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

        Path path = Paths.get("UNSUCCESSFUL" + (Calendar.MINUTE - 1));

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

        Path path = Paths.get("PENDING" + (Calendar.MINUTE - 1));

        try {

            JSch jsch = new JSch();

            Session session = jsch.getSession(ftpUser, ftpHost, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(ftpPassword);
            session.connect();

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            sftpChannel.put(path.toString(), ".");

            sftpChannel.disconnect();
            session.disconnect();

            Files.deleteIfExists(path);

            log.info("Successfully Uploaded Pending Transfers To FTP");

        } catch (IOException e) {
            log.warn("Failed To Delete The PENDING File: {}", e.toString());
        } catch (Exception e) {
            log.warn("Failed To Upload Pending Transfers To FTP: {}", e.toString());
        }
    }
}
