package it.pagopa.selfcare.product.web.security;

import it.pagopa.selfcare.commons.base.security.SelfCareGrantedAuthority;
import it.pagopa.selfcare.product.connector.rest.PartyRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;

@Component
public class PartyAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final PartyRestClient restClient;


    @Autowired
    public PartyAuthenticationProvider(PartyRestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
    }


    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO: implement real logic after deciding how to manage roles (Admin, Reviewer)
        // TODO: remove mock
        String role = ADMIN.name();
        List<SelfCareGrantedAuthority> authorities = Collections.singletonList(new SelfCareGrantedAuthority(role));
        return new User(username, authentication.getCredentials().toString(), authorities);
    }

}