package it.pagopa.selfcare.product.web.config;

import it.pagopa.selfcare.product.web.handler.RestAuthenticationSuccessHandler;
import it.pagopa.selfcare.product.web.security.AuthEntryPointJwt;
import it.pagopa.selfcare.product.web.security.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:config/jwt.properties")
//@EnableGlobalMethodSecurity(
//        // securedEnabled = true,
//        // jsr250Enabled = true,
//        prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private SimpleUrlAuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();

    private AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> {
        response.getOutputStream().print("Unauthorized!");
        response.setStatus(403);
    };

    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;

    @Autowired
    private RestAuthenticationSuccessHandler authenticationSuccessHandler;


    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails john = User.withUsername("john").password(encoder()
                .encode("doe")).roles("USER").build();
        UserDetails jane = User.withUsername("jane").password(encoder()
                .encode("doe")).roles("USER", "ADMIN").build();
        UserDetails admin = User.withUsername("admin").password(encoder()
                .encode("admin")).roles("ADMIN").build();
        return new InMemoryUserDetailsManager(john, jane, admin);
    }


    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
//            "/v2/api-docs",
//            "/swagger-resources",
            "/swagger-resources/**",
//            "/configuration/ui",
//            "/configuration/security",
//            "/swagger-ui.html",
//            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/favicon.ico"
            // other public endpoints of your API may be appended to this array
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authEntryPointJwt)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .antMatchers("/products").hasRole("USER")
                .antMatchers("/products/**").hasRole("ADMIN")
                .antMatchers("/**").hasAnyRole("ADMIN", "USER")
                .and()
                .formLogin()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .httpBasic()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .headers().cacheControl();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}