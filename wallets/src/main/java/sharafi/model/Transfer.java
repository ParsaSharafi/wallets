package sharafi.model;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "payment_gateway_reference")
    private String paymentGatewayReference;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id", name = "wallet_id")
    private Wallet wallet;

    @Column(name = "signed_amount")
    private long signedAmount;

    @Column(updatable = false)
    @CreationTimestamp
    private Instant initiated;

    private String status;
}
