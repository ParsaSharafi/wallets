package sharafi.dto;

import java.math.BigDecimal;

public record FeesDTO(BigDecimal exchangeFee, BigDecimal transferFee) {
}
