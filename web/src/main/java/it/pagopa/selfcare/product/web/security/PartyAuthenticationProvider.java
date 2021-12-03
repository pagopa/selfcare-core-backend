package it.pagopa.selfcare.product.web.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static it.pagopa.selfcare.commons.base.security.SelfCareAuthority.ADMIN;

@Component
public class PartyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // Do nothing
    }


    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO: implement real logic after deciding how to manage roles (Admin, Reviewer)
        // TODO: remove mock
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ADMIN.name());
        return new User(username, authentication.getCredentials().toString(), Collections.singletonList(authority));
    }

}