package sharafi.PaymentGateway;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private static final int MAX_RETRIES = 3;
    private final TokenService tokenService;
    private final RestClient restClient;
    private final Repository repository;

    //this api is called from the wallets service to request external deposit or withdrawal
    @Hidden
    @PostMapping("/Transfer")
    @Transactional(rollbackOn =  Exception.class)
    public String saveTransfer(@RequestBody Long signedAmount) {

        Transfer transfer = Transfer.builder()
                .signedAmount(signedAmount)
                .status("PENDING")
                //bank details are provided by the customer, but one is generated here for illustration purposes
                .bankDetailsId((long) (Math.random() * 1_000_000_000_000L))
                .build();

        Transfer saved = repository.save(transfer);
        log.info("New Pending Transfer Registered: {}", saved.getReference());

        //returning the reference of the pending transfer
        return saved.getReference();
    }

    //this api is called by the wallets service on a schedule to remind of old still-pending transfers
    @Hidden
    @PostMapping("/Pending")
    public String getStatus(@RequestBody List<String> references) {

        //storing references to files to upload pending transfers to ftp and reinform wallets service of the rest
        repository.findAllByReferenceIn(references).
            forEach(transfer -> writeToFile(transfer.getReference(), transfer.getStatus()));

        return references.size() + " Transfer References Received And Processed";
    }

    //internal api
    @Operation(summary = "Internal API", tags = "Get List Of Pending Transfers")
    @GetMapping("/ListPendingTransfers")
    public List<Transfer> list() {
        return repository.findAllByStatus("PENDING");
    }

    @Operation(summary = "Acknowledges Transfer And Then, If Possible, Informs The Wallets Service",
            tags = "Post Pending Transfer Reference Without Quotes To Acknowledge Payment")
    @PostMapping("/AcknowledgePayment")
    public ResponseEntity<String> acknowledge(@RequestBody String reference) {
        return updateTransfer(reference, "SUCCESSFUL", "/AcknowledgeTransfer", " Acknowledged");
    }

    @Operation(summary = "Refuses Transfer And Then, If Possible, Informs The Wallets Service",
            tags = "Post Pending Transfer Reference Without Quotes To Refuse Payment")
    @PostMapping("/RefusePayment")
    public ResponseEntity<String> refuse(@RequestBody String reference) {
        return updateTransfer(reference, "UNSUCCESSFUL", "/RefuseTransfer", " Refused");
    }

    //updating transfer status
    @Transactional(rollbackOn =  Exception.class)
    private ResponseEntity<String> updateTransfer(String reference, String status, String uri, String response) {

        Transfer transfer = repository.findById(reference).orElse(null);

        if (transfer == null || ! transfer.getStatus().equals("PENDING"))
            return ResponseEntity.status(500).body("No PENDING Transfer Found With Reference: " + reference);

        transfer.setStatus(status);
        transfer.setCleared(Instant.now());

        repository.save(transfer);
        log.info("Status Of Transfer {} Is Now: {}", reference, status);

        //trying to inform the wallets service of the status of the refused or acknowledged transfer
        int informAttempts = 0;
        while (informAttempts < MAX_RETRIES) {

            try {
                restClient.post()
                        .uri(uri)
                        .body(reference)
                        .headers(httpHeaders -> httpHeaders.setBearerAuth(tokenService.getToken()))
                        .retrieve()
                        .toBodilessEntity();

                log.info("Successfully Informed Wallets Service Of Status Of Transfer: {}", reference);
                break;

            } catch (Exception e) {

                if (informAttempts == 0)
                    tokenService.refreshToken();

                informAttempts++;

                log.error("Attempt {} At Informing Wallets Service Failed: {}", informAttempts, e.getMessage());

                try {Thread.sleep(1000);} catch (InterruptedException ignored) {}
            }
        }

        //storing reference to file to be resent to the wallets service if it couldn't be informed in real-time
        if (informAttempts == MAX_RETRIES)
            writeToFile(reference, status);

        return ResponseEntity.status(200).body(reference + response);
    }

    public void writeToFile(String reference, String status) {
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(status + Calendar.getInstance().get(Calendar.MINUTE)),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(reference);
            writer.newLine();
            log.info("Status Of Transfer {} Was Written To File", reference);
        } catch (IOException e) {
            log.error("Failed To Write To The {} File: {}", status, e.getMessage());
        }
    }
}
