package sharafi.model;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

	@Serial
    private static final long serialVersionUID = 4960221956734565930L;

	private final String username;
	
    private final String password;
    
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Holder holder) {
		super();
		this.username = holder.getUsername();
		this.password = holder.getPassword();
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority(holder.getRole()));
	}

	@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
