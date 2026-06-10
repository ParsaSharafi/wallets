package sharafi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SendMoneyDTO(@NotBlank(message = "NO WALLET CHOSEN") String sourceWalletTag,
                           @Min(value = 1, message = "AMOUNT MUST BE GREATER THAN ZERO") long amount,
                           String destinationUsername, String destinationWalletTag) {
}
