package it.pagopa.selfcare.product.web.config;

import it.pagopa.selfcare.commons.web.config.SecurityConfig;
import it.pagopa.selfcare.commons.web.security.JwtService;
import it.pagopa.selfcare.product.web.security.PartyAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import static it.pagopa.selfcare.commons.base.security.Authority.*;

@Slf4j
@Configuration
class ProductSecurityConfig extends SecurityConfig {

    private final PartyAuthenticationProvider authenticationProvider;


    @Autowired
    public ProductSecurityConfig(JwtService jwtService, PartyAuthenticationProvider authenticationProvider) {
        super(jwtService);
        this.authenticationProvider = authenticationProvider;
    }


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(ADMIN.name() + " > " + LEGAL.name() + "\n" +
                LEGAL.name() + " > " + ADMIN_REF.name() + "\n" +
                ADMIN_REF.name() + " > " + TECH_REF.name());
        RoleHierarchyAuthoritiesMapper authoritiesMapper = new RoleHierarchyAuthoritiesMapper(roleHierarchy);
        authenticationProvider.setAuthoritiesMapper(authoritiesMapper);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.eraseCredentials(false);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/products/**").hasAuthority(TECH_REF.name())
                .antMatchers("/products/**").hasAuthority(ADMIN.name())
                .anyRequest().permitAll();
        super.configure(http);
    }

}