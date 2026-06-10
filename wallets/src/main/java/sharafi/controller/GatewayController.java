package sharafi.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import sharafi.service.TransferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('GATEWAY')")
@SecurityRequirement(name = "bearerAuth")
@Hidden
public class GatewayController {

	//this controller is called by the payment gateway service to acknowledge or refuse external transfers

	private final TransferService transferService;

	@PostMapping("/AcknowledgeTransfer")
	public void acknowledgeTransfer(@RequestBody String reference) {
		transferService.acknowledgeTransfer(reference);
	}

	@PostMapping("/RefuseTransfer")
	public void refuseTransfer(@RequestBody String reference) {
		transferService.refuseTransfer(reference);
	}

	@PostMapping("/AcknowledgeTransfers")
	public void acknowledgeTransfers(@RequestBody List<String> references) {
		references.forEach(transferService::acknowledgeTransfer);
	}

	@PostMapping("/RefuseTransfers")
	public void refuseTransfers(@RequestBody List<String> references) {
		references.forEach(transferService::refuseTransfer);
	}
}
