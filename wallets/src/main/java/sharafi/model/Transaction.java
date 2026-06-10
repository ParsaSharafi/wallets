package sharafi.model;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(referencedColumnName = "id", name = "payer_wallet_id")
	private Wallet payerWallet;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "code", name = "payer_currency")
	private Currency payerCurrency;
	
	@Column(name = "payer_amount")
	private long payerAmount;
	
	@Column(name = "fee_amount")
	private long feeAmount;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(referencedColumnName = "id", name = "payee_wallet_id")
	private Wallet payeeWallet;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "code", name = "payee_currency")
	private Currency payeeCurrency;
	
	@Column(name = "payee_amount")
	private long payeeAmount;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Instant initiated;
	
	private String status;
}
