package it.pagopa.selfcare.product.web.security;

import it.pagopa.selfcare.product.core.security.Role;
import it.pagopa.selfcare.product.web.config.SecurityConfig;
import it.pagopa.selfcare.product.web.handler.RestAuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;


@WebMvcTest(value = {TestController.class})
@ContextConfiguration(classes = {
        TestController.class,
        SecurityConfig.class,
        RestAuthenticationSuccessHandler.class
})
class AuthTokenFilterTest {

    private static final String BASE_URL = "/test";
    private static final User USER = new User("user", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name())));


    @MockBean
    private JwtService jwtServiceMock;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    protected MockMvc mvc;


    @Test
    void testWithoutJwt() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        // when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }


    @Test
    void testWithValidJwt() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(USER);
        // when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testWithInvalidJwt() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        // when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }


    @Test
    void testWithException() throws Exception {
        // given
        Mockito.doThrow(RuntimeException.class)
                .when(jwtServiceMock).validateJwtToken(Mockito.any());
        // when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }

}