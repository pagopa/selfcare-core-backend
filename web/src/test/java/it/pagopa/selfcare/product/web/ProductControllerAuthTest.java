package it.pagopa.selfcare.product.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.core.security.Role;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.config.SecurityConfig;
import it.pagopa.selfcare.product.web.handler.RestAuthenticationSuccessHandler;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import it.pagopa.selfcare.product.web.security.JwtService;
import it.pagopa.selfcare.product.web.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.EnumMap;
import java.util.UUID;

@WebMvcTest(value = {ProductController.class})
@ContextConfiguration(classes = {
        ProductController.class,
        SecurityConfig.class,
        RestAuthenticationSuccessHandler.class
})
class ProductControllerAuthTest {

    private static final String BASE_URL = "/products";
    private static final User USER = new User("user", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_USER.name())));
    private static final User ADMIN = new User("admin", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name())));
    private static final User LEGAL = new User("legal", "", Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_LEGAL.name())));
    private static final EnumMap<Role, User> role2userMap = new EnumMap<>(Role.class) {{
        put(Role.ROLE_USER, USER);
        put(Role.ROLE_ADMIN, ADMIN);
        put(Role.ROLE_LEGAL, LEGAL);
    }};
    private static final CreateProductDto CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto());
    private static final UpdateProductDto UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto());
    private static final Product PRODUCT = TestUtils.mockInstance(new Product());

    @MockBean
    private ProductService productServiceMock;

    @MockBean
    private JwtService jwtServiceMock;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ROLE_ADMIN", "ROLE_USER", "ROLE_LEGAL"})
    void getProducts_checkRole(Role role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.getProducts())
                .thenAnswer(invocationOnMock -> Collections.emptyList());
        ResultMatcher matcher;
        switch (role) {
            case ROLE_ADMIN:
            case ROLE_USER:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case ROLE_LEGAL:
                matcher = MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value());
                break;
            default:
                throw new IllegalArgumentException();
        }
        // when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(matcher)
                .andReturn();
        // then
        switch (role) {
            case ROLE_ADMIN:
            case ROLE_USER:
                Mockito.verify(productServiceMock, Mockito.times(1)).getProducts();
                break;
            case ROLE_LEGAL:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void getProducts_unauthorized() throws Exception {
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


    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ROLE_ADMIN", "ROLE_USER", "ROLE_LEGAL"})
    void getProduct_checkRole(Role role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    Product p = new Product();
                    p.setId(id);
                    return p;
                });
        ResultMatcher matcher;
        switch (role) {
            case ROLE_ADMIN:
            case ROLE_USER:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case ROLE_LEGAL:
                matcher = MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value());
                break;
            default:
                throw new IllegalArgumentException();
        }
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{id}", uuid)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(matcher)
                .andReturn();
        // then
        switch (role) {
            case ROLE_ADMIN:
            case ROLE_USER:
                Mockito.verify(productServiceMock, Mockito.times(1)).getProduct(Mockito.eq(uuid));
                break;
            case ROLE_LEGAL:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Test
    void getProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{id}", uuid)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }


    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ROLE_ADMIN", "ROLE_USER"})
    void createProduct_checkRole(Role role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.createProduct(Mockito.any()))
                .thenReturn(PRODUCT);
        ResultMatcher matcher;
        switch (role) {
            case ROLE_ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case ROLE_USER:
                matcher = MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value());
                break;
            default:
                throw new IllegalArgumentException();
        }
        // when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/")
                        .content(objectMapper.writeValueAsString(CREATE_PRODUCT_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(matcher)
                .andReturn();
        // then
        switch (role) {
            case ROLE_ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1)).createProduct(Mockito.any());
                break;
            case ROLE_USER:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Test
    void createProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        // when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/")
                        .content(objectMapper.writeValueAsString(CREATE_PRODUCT_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ROLE_ADMIN", "ROLE_USER"})
    void updateProduct_checkRole(Role role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    Product product = invocationOnMock.getArgument(1, Product.class);
                    product.setId(id);
                    product.setLogo("logo1");
                    return product;
                });
        ResultMatcher matcher;
        switch (role) {
            case ROLE_ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case ROLE_USER:
                matcher = MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value());
                break;
            default:
                throw new IllegalArgumentException();
        }
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/{id}", uuid)
                        .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(matcher)
                .andReturn();
        // then
        switch (role) {
            case ROLE_ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1))
                        .updateProduct(Mockito.eq(uuid), Mockito.any());
                break;
            case ROLE_USER:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void updateProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/{id}", uuid)
                        .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DTO))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ROLE_ADMIN", "ROLE_USER"})
    void deleteProduct_checkRole(Role role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(true);
        Mockito.when(userDetailsService.loadUserByUsername(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.doNothing()
                .when(productServiceMock)
                .deleteProduct(Mockito.anyString());

        ResultMatcher matcher;
        switch (role) {
            case ROLE_ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case ROLE_USER:
                matcher = MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value());
                break;
            default:
                throw new IllegalArgumentException();
        }
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/{id}", uuid)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(matcher)
                .andReturn();
        // then
        switch (role) {
            case ROLE_ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1))
                        .deleteProduct(Mockito.eq(uuid));
                break;
            case ROLE_USER:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void deleteProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.validateJwtToken(Mockito.any()))
                .thenReturn(false);
        String uuid = UUID.randomUUID().toString();
        // when
        mvc.perform(MockMvcRequestBuilders
                        .delete(BASE_URL + "/{id}", uuid)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andReturn();
    }

}