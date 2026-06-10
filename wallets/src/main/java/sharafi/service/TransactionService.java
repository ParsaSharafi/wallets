package sharafi.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sharafi.dto.FeesDTO;
import sharafi.dto.SendMoneyDTO;
import sharafi.dto.TransDTO;
import sharafi.model.Holder;
import sharafi.model.Transaction;
import sharafi.model.Wallet;
import sharafi.repository.TransactionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
	
	@Value("${fee.exchange}")
	private BigDecimal _exchangeFee;
	
	@Value("${fee.transfer}")
	private BigDecimal _transferFee;
	
	private final HolderService holderService;
	private final TransactionRepository transactionRepository;
	private final WalletService walletService;

	@Transactional(rollbackOn = Exception.class)
	public void saveTransaction(SendMoneyDTO dto, Holder holder) {

		BigDecimal _totalFee = _exchangeFee.add(_transferFee);
		long feeAmount = 0;
		long payableAmount;

		Wallet payerWallet = walletService.getWallet(holder, dto.sourceWalletTag());

		//if no destination username is provided, both wallets will be assumed to be owned by the initiator
		Wallet payeeWallet = dto.destinationUsername().isEmpty() ?
				walletService.getWallet(holder, dto.destinationWalletTag()) :
				walletService.getWallet(
						holderService.getUser(dto.destinationUsername()), dto.destinationWalletTag());

		//if both wallets share currency
		if (payerWallet.getCurrency().getCode().equals(payeeWallet.getCurrency().getCode())) {

			//if both wallets share currency but belong to different users, only transfer fee is deducted
			if (! payerWallet.getHolder().getUsername().equals(payeeWallet.getHolder().getUsername()))
				feeAmount = _transferFee.multiply(new BigDecimal(dto.amount())).longValue();

			payableAmount = dto.amount() - feeAmount;

		}

		//if wallets are of different currencies
		else {

			//if wallets are of different currencies but belong to the same user, only exchange fee is deducted
			if (payerWallet.getHolder().getUsername().equals(payeeWallet.getHolder().getUsername()))
				feeAmount = _exchangeFee.multiply(new BigDecimal(dto.amount())).longValue();

			//if wallets are of different currencies and belong to different users, both exchange and transfer fee is deducted
			else
				feeAmount = _totalFee.multiply(new BigDecimal(dto.amount())).longValue();

			payableAmount = dto.amount() - feeAmount;

			BigDecimal _payableConverted = new BigDecimal(payableAmount);

			//if wallets are of different currencies, conversion is required
			if(! payerWallet.getCurrency().getCode().equals("USD"))
				_payableConverted = payerWallet.getCurrency().getRate().multiply(_payableConverted);

			if(! payeeWallet.getCurrency().getCode().equals("USD"))
				_payableConverted = _payableConverted.divide(payeeWallet.getCurrency().getRate(), RoundingMode.FLOOR);

			payableAmount = _payableConverted.longValue();
		}

		//updating balances of the wallets
		walletService.updateWalletBalance(payerWallet, -dto.amount());
		walletService.updateWalletBalance(payeeWallet, payableAmount);

		Transaction transaction = Transaction.builder()
				.payerWallet(payerWallet)
				.payerAmount(dto.amount())
				.payerCurrency(payerWallet.getCurrency())
				.feeAmount(feeAmount)
				.payeeWallet(payeeWallet)
				.payeeAmount(payableAmount)
				.payeeCurrency(payeeWallet.getCurrency())
				.status("SUCCESSFUL")
				.build();

		log.info("Transaction From Wallet {} TO Wallet {}", payerWallet.getId(), payeeWallet.getId());

		transactionRepository.save(transaction);
	}

	//list all transactions and external transfers of a wallet
	public List<TransDTO> getTransactions(Holder holder, String tag) {
		return transactionRepository.getAllTransactions(walletService.getWallet(holder, tag).getId())
				.stream()
				.map(obj ->
						new TransDTO((Long) obj[0], (String) obj[1], (Instant) obj[2], (String) obj[3]))
				.toList();
	}

	public FeesDTO getFees() {
		return new FeesDTO(_exchangeFee, _transferFee);
	}
}
