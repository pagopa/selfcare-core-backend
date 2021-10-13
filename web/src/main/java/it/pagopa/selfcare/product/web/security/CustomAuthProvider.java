package it.pagopa.selfcare.product.web.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;


//@Component
public class CustomAuthProvider implements AuthenticationProvider {

//    @Autowired
//    AuthService authService;
//
//    @Override
//    public Authentication authenticate(Authentication auth)
//            throws AuthenticationException {
//        String username = auth.getName();
//        String password = auth.getCredentials()
//                .toString();
//
//        try {
//            User user = authService.login(username, password);
//            JwtUser jwtUser = JwtUserFactory.create(user);
//            if (user != null) {
//                //login tramite intranet
//                //return new UsernamePasswordAuthenticationToken(username, password, jwtUser.getAuthorities());
//                //login tramite token
//                return new GcdmUsernamePasswordAuthenticationToken(username, password, jwtUser.getAuthorities(), user.getSessionToken());
//            } else {
//                throw new AuthException(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
//            }
//        } catch (AuthException e) {
//            throw e;
//        }
//    }
//
//    @Override
//    public boolean supports(Class<?> auth) {
//        return auth.equals(GcdmUsernamePasswordAuthenticationToken.class);
//    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}