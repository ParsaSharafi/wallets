package sharafi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sharafi.advice.InvalidCredentialsException;
import sharafi.model.Holder;
import sharafi.model.UserPrincipal;
import sharafi.repository.HolderRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolderService implements UserDetailsService {

	private final HolderRepository holderRepository;
	private final ApplicationContext context;
	private final JWTService jwtService;
	
	private final BCryptPasswordEncoder passEncoder = new BCryptPasswordEncoder(12);
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Holder holder = holderRepository.findById(username).
				orElseThrow(() -> new UsernameNotFoundException("NO USER FOUND WITH USERNAME: " + username));
		return new UserPrincipal(holder);
	}

	//log in
	public String verify(Holder holder) {
		if (! holderRepository.existsById(holder.getUsername()))
			throw new UsernameNotFoundException("NO USER FOUND WITH USERNAME: " + holder.getUsername());

		Authentication authentication = context.getBean(AuthenticationManager.class).authenticate
				(new UsernamePasswordAuthenticationToken(holder.getUsername(), holder.getPassword()));
		
		if (authentication.isAuthenticated())
			return jwtService.generateToken(holder.getUsername());
		else
			throw new InvalidCredentialsException("PASSWORD IS INCORRECT");
    }

	public Holder getUser(String username) {
		return holderRepository.findById(username).
				orElseThrow(() -> new UsernameNotFoundException("NO USER FOUND WITH USERNAME: " + username));
	}

	//sign up
	public void register(Holder holder) {
		if (holderRepository.existsById(holder.getUsername()))
			throw new InvalidCredentialsException("USERNAME ALREADY EXISTS");
		else
			holderRepository.save(new Holder(
					holder.getUsername(), passEncoder.encode(holder.getPassword()), null, "ROLE_USER"));

		log.info("Registered {}", holder.getUsername());
	}
}