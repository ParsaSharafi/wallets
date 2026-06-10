package sharafi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sharafi.advice.RecordNotFoundException;
import sharafi.dto.SendMoneyDTO;
import sharafi.model.Holder;
import sharafi.model.Transfer;
import sharafi.model.Wallet;
import sharafi.repository.TransferRepository;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class TransferService {

    @Value("${send.pending.minutes.old}")
    private long minutesOldPending;

    private final TransferRepository transferRepository;
    private final WalletService walletService;
    private final RestClient restClient;

    public void withdrawal(SendMoneyDTO dto, Holder holder) {

        Wallet wallet = walletService.getWallet(holder, dto.sourceWalletTag());

        //subtracting the requested amount to be withdrawn
        walletService.updateWalletBalance(wallet, -dto.amount());

        Transfer transfer = Transfer.builder()
                .wallet(wallet)
                .signedAmount(-dto.amount())
                .status("PENDING")
                //retrieve reference of transfer from the payment gateway service
                .paymentGatewayReference(callGatewayApi(-dto.amount()))
                .build();

        log.info("Request To Withdraw From Wallet {}", wallet.getId());

        transferRepository.save(transfer);
    }

    public void deposit(SendMoneyDTO dto, Holder holder) {

        Wallet wallet = walletService.getWallet(holder, dto.sourceWalletTag());

        Transfer transfer = Transfer.builder()
                .wallet(wallet)
                .signedAmount(dto.amount())
                .status("PENDING")
                //retrieve reference of transfer from the payment gateway service
                .paymentGatewayReference(callGatewayApi(dto.amount()))
                .build();

        log.info("Request To Deposit To Wallet {}", wallet.getId());

        transferRepository.save(transfer);
    }

    //calling the payment gateway api to request external transfer and retrieve a reference to it from that service
    private String callGatewayApi(long signedAmount) {

        return restClient.post()
                .uri("/Transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(signedAmount)
                .retrieve()
                .body(String.class);
    }

    //called by payment gateway service (through controller) to acknowledge a pending transfer
    public void acknowledgeTransfer(String reference) {

        Transfer transfer = transferRepository.findByPaymentGatewayReference(reference).
                orElseThrow(() -> new RecordNotFoundException("REFERENCE NOT FOUND: " + reference));

        if (! transfer.getStatus().equals("PENDING"))
            throw new IllegalArgumentException("TRANSFER IS ALREADY " + transfer.getStatus());

        //depositing to the wallet after transfer acknowledgement
        if (transfer.getSignedAmount() > 0)
            walletService.updateWalletBalance(transfer.getWallet(), transfer.getSignedAmount());

        transfer.setStatus("SUCCESSFUL");

        log.info("Acknowledged Transfer {}", transfer.getPaymentGatewayReference());

        transferRepository.save(transfer);
    }

    //called by payment gateway service (through controller) to refuse a pending transfer
    public void refuseTransfer(String reference) {

        Transfer transfer = transferRepository.findByPaymentGatewayReference(reference).
                orElseThrow(() -> new RecordNotFoundException("REFERENCE NOT FOUND: " + reference));

        if (! transfer.getStatus().equals("PENDING"))
            throw new IllegalArgumentException("TRANSFER IS ALREADY " + transfer.getStatus());

        //refunding the withdrawn amount after transfer refusal
        if (transfer.getSignedAmount() < 0)
            walletService.updateWalletBalance(transfer.getWallet(), -transfer.getSignedAmount());

        transfer.setStatus("UNSUCCESSFUL");

        log.info("Refused Transfer {}", transfer.getPaymentGatewayReference());

        transferRepository.save(transfer);
    }

    //sending old still-pending transfer references to the payment gateway service on a schedule
    @Scheduled(cron = "${send.pending.cron}")
    public void sendPendingTransfers() {
        try {
            String response = restClient.post()
                    .uri("/Pending")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(transferRepository.findAllByStatusAndInitiatedBefore
                                    ("PENDING", Instant.now().minusSeconds(minutesOldPending * 60)).stream()
                            .map(Transfer::getPaymentGatewayReference).collect(Collectors.toList()))
                    .retrieve()
                    .body(String.class);

            log.info("Pending Transfer References Sent: {}", response);
        } catch (Exception e) {
            log.error("Error While Sending Pending Transfer References: {}", e.getMessage());
        }
    }
}
