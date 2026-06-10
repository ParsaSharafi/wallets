package sharafi.ExchangeRateUpdater;

import java.math.BigDecimal;

public record Currency(String code, BigDecimal rate) {
}
