package sharafi.ExchangeRateUpdater;

import jakarta.annotation.PostConstruct;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RateUpdater {

    private final int size = 10;
    private int index = 0;

    private final Currency[] currencies =  new Currency[size];
    private final KafkaTemplate<String, Currency> kafkaTemplate;

    public RateUpdater(KafkaTemplate<String, Currency> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    private void populateCurrencies()
    {
        currencies[0] = new Currency("EUR", new BigDecimal("1.17827"));
        currencies[1] = new Currency("JPY", new BigDecimal("0.00645"));
        currencies[2] = new Currency("GBP", new BigDecimal("1.34763"));
        currencies[3] = new Currency("CHF", new BigDecimal("1.23872"));
        currencies[4] = new Currency("SEK", new BigDecimal("0.110"));
        currencies[5] = new Currency("NOK", new BigDecimal("0.10544"));
        currencies[6] = new Currency("DKK", new BigDecimal("0.157"));
        currencies[7] = new Currency("PLN", new BigDecimal("0.2796"));
        currencies[8] = new Currency("HUF", new BigDecimal("0.00313"));
        currencies[9] = new Currency("CZK", new BigDecimal("0.0485"));
    }

    @Scheduled(fixedRate = 10000, initialDelay = 0)
    public void updateExchangeRate() {

        index = index == size - 1 ? 0 : index + 1;

        Currency currency = new Currency(currencies[index].code(),
                currencies[index].rate().multiply(BigDecimal.valueOf(Math.random() / 25 + 0.98)));

        Message<Currency> message = MessageBuilder
                .withPayload(currency)
                .setHeader(KafkaHeaders.TOPIC, "topic")
                .build();
        kafkaTemplate.send(message);
    }
}
