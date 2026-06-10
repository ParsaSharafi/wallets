package sharafi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sharafi.model.Transfer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByPaymentGatewayReference(String paymentGatewayReference);

    List<Transfer> findAllByStatusAndInitiatedBefore(String status, Instant instant);
}
