package sharafi.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sharafi.advice.InsufficientBalanceException;
import sharafi.advice.RecordNotFoundException;
import sharafi.dto.AddWalletDTO;
import sharafi.model.Holder;
import sharafi.model.Wallet;
import sharafi.repository.WalletRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

	private final CurrencyService currencyService;
	private final WalletRepository walletRepository;

	//creating a new wallet for user
	public void addWallet(AddWalletDTO dto, Holder holder) {

		//ensuring tag uniqueness among a user's wallets
		if (walletRepository.existsByHolderAndTag(holder, dto.currency() + '_' + dto.name()))
			throw new IllegalArgumentException("TAG ALREADY EXISTS");

		Wallet wallet = Wallet.builder()
				.holder(holder)
				.tag(dto.currency() + "_" + dto.name())
				.currency(currencyService.getCurrency(dto.currency()))
				.build();

		log.info("Wallet {} Created For {}", wallet.getTag(), holder.getUsername());
		
        walletRepository.save(wallet);
	}

	//update balance of a wallet in the database
	public void updateWalletBalance(Wallet wallet, long signedAmount) throws InsufficientBalanceException {

		//ensuring balance is greater than the amount to be paid
		if (wallet.getBalance() + signedAmount < 0)
			throw new InsufficientBalanceException("INSUFFICIENT BALANCE");
		else
			wallet.setBalance(wallet.getBalance() + signedAmount);

		log.info("Balance Updated For Wallet {}", wallet.getId());

		walletRepository.save(wallet);
	}
	
	public Wallet getWallet(Holder holder, String tag) {
		return walletRepository.findByHolderAndTag(holder, tag).
				orElseThrow(() -> new RecordNotFoundException(
						holder.getUsername() + " HAS NO WALLET TAGGED: " + tag));
	}

	//listing wallets of a user
	public List<Wallet> getWallets(Holder holder) {
		return walletRepository.findAllByHolderOrderByCreatedDesc(holder);
	}
}
