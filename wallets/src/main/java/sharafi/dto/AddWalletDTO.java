package sharafi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddWalletDTO(
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "NAME CAN ONLY CONTAIN ALPHANUMERIC OR _ CHARACTERS") String name,
                           @NotBlank(message = "NO CURRENCY CHOSEN") String currency) {
}
