package it.pagopa.selfcare.product.web.config;

import it.pagopa.selfcare.commons.web.config.SecurityConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Slf4j
@Configuration
@EnableWebSecurity
@Import(SecurityConfig.class)
class ProductSecurityConfig {
//class ProductSecurityConfig extends SecurityConfig {
//
//    @Autowired
//    public ProductSecurityConfig(JwtService jwtService, AuthoritiesRetriever authoritiesRetriever) {
//        super(jwtService, authoritiesRetriever);
//    }
//
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/products/**").hasAuthority(LIMITED.name())
//                .antMatchers("/products/**").hasAuthority(ADMIN.name())
//                .anyRequest().permitAll();
//        super.configure(http);
//    }

}