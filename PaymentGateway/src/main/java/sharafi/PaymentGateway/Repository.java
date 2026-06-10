package sharafi.PaymentGateway;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Repository extends JpaRepository<Transfer, String> {

    List<Transfer> findAllByStatus(String status);

    List<Transfer> findAllByReferenceIn(List<String> references);
}
