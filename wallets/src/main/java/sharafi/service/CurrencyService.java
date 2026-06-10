package sharafi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sharafi.advice.RecordNotFoundException;
import sharafi.model.Currency;
import sharafi.repository.CurrencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
	
	private final CurrencyRepository currencyRepository;
	
	public Currency getCurrency(String code) {
		return currencyRepository.findById(code).
				orElseThrow(() -> new RecordNotFoundException(code + " IS NOT RECOGNIZED AS A CURRENCY CODE"));
	}

	//this method updates currency exchange rates through kafka messages from the exchange rate updater service
	@KafkaListener(topics = "topic", groupId = "exchangeRate")
	public void saveCurrency(Currency currency) {
		currencyRepository.save(currency);

		log.info("Exchange Rate Updated: {}", currency);
	}

	public List<Currency> getCurrencies() {
		return currencyRepository.findAllByOrderByUpdatedDesc();
	}
}
