package sharafi.dto;

import java.time.Instant;

public record TransDTO(long signedAmount, String info, Instant initiated, String status) {
}
