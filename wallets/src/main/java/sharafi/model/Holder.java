package sharafi.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holder {

	@Id
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "USERNAME CAN ONLY CONTAIN ALPHANUMERIC OR _ CHARACTERS")
    private String username;

    @Pattern(regexp = "\\S+", message = "PASSWORD CAN NOT CONTAIN BLANK CHARACTERS")
    private String password;
    
    @OneToMany(mappedBy = "holder", fetch = FetchType.EAGER)
	@JsonIgnore
	private List<Wallet> wallets;
    
    @JsonIgnore
    private String role;
}