package it.pagopa.selfcare.product.core.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: implement real logic
        UserDetails user = null;
        if ("admin".equals(username)) {
            user = new User("admin", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
        }

        return user;
    }

}
