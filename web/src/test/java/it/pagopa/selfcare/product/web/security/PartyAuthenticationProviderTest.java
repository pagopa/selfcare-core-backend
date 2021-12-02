package it.pagopa.selfcare.product.web.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;

class PartyAuthenticationProviderTest {

    @Test
    void additionalAuthenticationChecks() {
    }

    @Test
    void retrieveUser() {//TODO
        // given
        String username = "username";
        PartyAuthenticationProvider authenticationProvider = new PartyAuthenticationProvider();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, "credentials");
        // when
        UserDetails userDetails = authenticationProvider.retrieveUser(username, authentication);
        // then
        Assertions.assertNotNull(userDetails);
        Assertions.assertNotNull(userDetails.getAuthorities());
        Assertions.assertEquals(1, userDetails.getAuthorities().size());
        Optional<? extends GrantedAuthority> grantedAuthority = userDetails.getAuthorities().stream().findAny();
        Assertions.assertTrue(grantedAuthority.isPresent());
        Assertions.assertEquals(ADMIN.name(), grantedAuthority.get().getAuthority());
    }
}