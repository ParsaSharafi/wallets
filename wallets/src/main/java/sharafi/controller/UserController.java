package sharafi.controller;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import sharafi.dto.AddWalletDTO;
import sharafi.dto.ResponseDTO;
import sharafi.dto.SendMoneyDTO;
import sharafi.model.Holder;
import sharafi.service.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	//this controller is called by users to access required services
	
	private final HolderService holderService;
	private final WalletService walletService;
	private final TransactionService transactionService;
	private final TransferService transferService;
	private final CurrencyService currencyService;

	@PostMapping("/AddWallet")
    public ResponseDTO addWallet(Principal principal, @Valid @RequestBody AddWalletDTO dto) {
		Holder holder = holderService.getUser(principal.getName());
		walletService.addWallet(dto, holder);
		return new ResponseDTO(true, "NEW WALLET CREATED SUCCESSFULLY");
    }
	
	@GetMapping("/ListWallets")
	public ResponseDTO listWallets(Principal principal) {
		Holder holder = holderService.getUser(principal.getName());
		return new ResponseDTO(true, walletService.getWallets(holder));
	}

	@GetMapping("/GetCurrencies")
	public ResponseDTO getCurrencies() {
		return new ResponseDTO(true, currencyService.getCurrencies());
	}

	@GetMapping("/GetFees")
	public ResponseDTO getFees() {
		return new ResponseDTO(true, transactionService.getFees());
	}
	
	@PostMapping("/SendMoney")
    public ResponseDTO sendMoney(Principal principal, @Valid @RequestBody SendMoneyDTO dto) {
		Holder holder = holderService.getUser(principal.getName());
		transactionService.saveTransaction(dto, holder);
		return new ResponseDTO(true, "TRANSACTION WAS SUCCESSFUL");
    }

	@PostMapping("/Withdrawal")
	public ResponseDTO withdrawal(Principal principal, @Valid @RequestBody SendMoneyDTO dto) {
		Holder holder = holderService.getUser(principal.getName());
		transferService.withdrawal(dto, holder);
		return new ResponseDTO(true, "TRANSFER REQUEST WAS SENT");
	}

	@PostMapping("/Deposit")
	public ResponseDTO deposit(Principal principal, @Valid @RequestBody SendMoneyDTO dto) {
		Holder holder = holderService.getUser(principal.getName());
		transferService.deposit(dto, holder);
		return new ResponseDTO(true, "TRANSFER REQUEST WAS SENT");
	}
	
	@PostMapping("/ListTransactions")
	public ResponseDTO listTransactions(Principal principal, @RequestBody String tag) {
		Holder holder = holderService.getUser(principal.getName());
		return new ResponseDTO(true, transactionService.getTransactions(holder, tag));
	}
}
