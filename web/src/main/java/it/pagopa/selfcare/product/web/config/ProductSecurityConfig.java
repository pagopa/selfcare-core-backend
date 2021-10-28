package it.pagopa.selfcare.product.web.config;

import it.pagopa.selfcare.commons.web.config.SecurityConfig;
import it.pagopa.selfcare.commons.web.security.JwtService;
import it.pagopa.selfcare.product.connector.rest.PartyRestClient;
import it.pagopa.selfcare.product.web.security.SelfCareAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;

@Slf4j
@Configuration
class ProductSecurityConfig extends SecurityConfig {

    private final PartyRestClient restClient;


    @Autowired
    public ProductSecurityConfig(JwtService jwtService, PartyRestClient restClient) {
        super(jwtService);
        this.restClient = restClient;
    }


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        SimpleAuthorityMapper mapper = new SimpleAuthorityMapper();
        mapper.setConvertToUpperCase(true);
        mapper.afterPropertiesSet();
        SelfCareAuthenticationProvider authenticationProvider = new SelfCareAuthenticationProvider(restClient);
        authenticationProvider.setAuthoritiesMapper(mapper);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.authenticationProvider(new TestingAuthenticationProvider()); // FIXME: remove after implemented real role based authorization
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/products/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/products/**").hasRole("ADMIN")
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated();
        super.configure(http);
    }

}