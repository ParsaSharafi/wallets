package sharafi.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Currency {

	@Id
	private String code;
	
	@Column(precision = 8, scale = 6)
	private BigDecimal rate;

	@UpdateTimestamp
	@JsonIgnore
	private Instant updated;
	
	@OneToMany(mappedBy = "currency", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<Wallet> wallets;
}
