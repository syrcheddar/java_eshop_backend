package cz.cvut.kbss.ear.meeting_scheduler.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class AuthenticationToken extends AbstractAuthenticationToken implements Principal {

    private UserAccountDetails userAccountDetails;

    public AuthenticationToken(Collection<? extends GrantedAuthority> authorities, UserAccountDetails userAccountDetails) {
        super(authorities);
        this.userAccountDetails = userAccountDetails;
        super.setAuthenticated(true);
        super.setDetails(userAccountDetails);
    }

    @Override
    public String getCredentials() {
        return userAccountDetails.getPassword();
    }

    @Override
    public UserAccountDetails getPrincipal() {
        return userAccountDetails;
    }
}
