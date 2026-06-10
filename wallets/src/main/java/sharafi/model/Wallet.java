package sharafi.model;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"tag", "holder"}))
public class Wallet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private long id;

	private String tag;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "username", name = "holder", updatable = false)
	@JsonIgnore
	private Holder holder;

	private long balance;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(referencedColumnName = "code", name = "currency", updatable = false)
	@JsonIgnore
	private Currency currency;

	@Column(updatable = false)
	@CreationTimestamp
	private Instant created;
	
	@OneToMany(mappedBy = "payerWallet", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Transaction> debits;
	
	@OneToMany(mappedBy = "payeeWallet", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Transaction> credits;

	@OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Transfer> transfers;
}