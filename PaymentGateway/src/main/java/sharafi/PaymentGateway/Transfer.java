package sharafi.PaymentGateway;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reference;

    @Column(name = "signed_amount")
    private long signedAmount;

    @Column(name = "bank_details_id")
    private long bankDetailsId;

    @Column(updatable = false)
    @CreationTimestamp
    private Instant initiated;

    private Instant cleared;

    private String status;
}
