package sharafi.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sharafi.dto.ResponseDTO;
import sharafi.model.Holder;
import sharafi.service.HolderService;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    //this is the only controller that can be called without authentication

	private final HolderService holderService;
	
	@PostMapping("/SignUp")
    public ResponseDTO signUp(@Valid @RequestBody Holder holder) {
        holderService.register(holder);
        return new ResponseDTO(true, holderService.verify(holder));
    }
	
	@PostMapping("/LogIn")
    public ResponseDTO logIn(@Valid @RequestBody Holder holder) {
        return new ResponseDTO(true, holderService.verify(holder));
    }
}
