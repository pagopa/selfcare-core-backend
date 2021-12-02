package it.pagopa.selfcare.product.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import it.pagopa.selfcare.commons.base.security.Authority;
import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.commons.web.security.JwtService;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.web.config.SecurityTestConfig;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductDto;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import it.pagopa.selfcare.product.web.security.PartyAuthenticationProvider;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static it.pagopa.selfcare.commons.base.security.Authority.ADMIN;
import static it.pagopa.selfcare.commons.base.security.Authority.LIMITED;

@WebMvcTest(value = {ProductController.class})
@ContextConfiguration(classes = {
        ProductController.class,
        SecurityTestConfig.class
})
class ProductControllerAuthTest {

    private static final String BASE_URL = "/products";
    public static final TestingAuthenticationToken LIMITED_AUTHENTICATION =
            new TestingAuthenticationToken("user", "", Collections.singletonList(new SimpleGrantedAuthority(LIMITED.name())));
    public static final TestingAuthenticationToken ADMIN_AUTHENTICATION =
            new TestingAuthenticationToken("admin", "", List.of(new SimpleGrantedAuthority(ADMIN.name()),
                    new SimpleGrantedAuthority(LIMITED.name())
            ));
    private static final EnumMap<Authority, Authentication> role2userMap = new EnumMap<>(Authority.class) {{
        put(LIMITED, LIMITED_AUTHENTICATION);
        put(ADMIN, ADMIN_AUTHENTICATION);
    }};
    private static final CreateProductDto CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto());
    private static final UpdateProductDto UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto());
    private static final ProductOperations PRODUCT = TestUtils.mockInstance(new ProductDto());
    public static final Claims CLAIMS_MOCK = Mockito.mock(Claims.class);

    static {
        CREATE_PRODUCT_DTO.setRoleMappings(new EnumMap<>(PartyRole.class));
        UPDATE_PRODUCT_DTO.setRoleMappings(new EnumMap<>(PartyRole.class));
    }

    @MockBean
    private ProductService productServiceMock;

    @MockBean
    private JwtService jwtServiceMock;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private PartyAuthenticationProvider partyAuthenticationProvider;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @ParameterizedTest
    @EnumSource(value = Authority.class, names = {"ADMIN", "LIMITED"})
    void getProducts_checkRole(Authority role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.of(CLAIMS_MOCK));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.getProducts())
                .thenAnswer(invocationOnMock -> Collections.emptyList());
        ResultMatcher matcher;
        switch (role) {
            case ADMIN:
            case LIMITED:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
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
            case ADMIN:
            case LIMITED:
                Mockito.verify(productServiceMock, Mockito.times(1)).getProducts();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void getProducts_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.empty());
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
    @EnumSource(value = Authority.class, names = {"ADMIN", "LIMITED"})
    void getProduct_checkRole(Authority role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.of(CLAIMS_MOCK));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    ProductOperations p = new ProductDto();
                    p.setId(id);
                    return p;
                });
        ResultMatcher matcher;
        switch (role) {
            case ADMIN:
            case LIMITED:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
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
            case ADMIN:
            case LIMITED:
                Mockito.verify(productServiceMock, Mockito.times(1)).getProduct(Mockito.eq(uuid));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Test
    void getProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.empty());
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
    @EnumSource(value = Authority.class, names = {"ADMIN", "LIMITED"})
    void createProduct_checkRole(Authority role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.of(CLAIMS_MOCK));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.createProduct(Mockito.any()))
                .thenReturn(PRODUCT);
        ResultMatcher matcher;
        switch (role) {
            case ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case LIMITED:
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
            case ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1)).createProduct(Mockito.any());
                break;
            case LIMITED:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    @Test
    void createProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.empty());
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
    @EnumSource(value = Authority.class, names = {"ADMIN", "LIMITED"})
    void updateProduct_checkRole(Authority role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.of(CLAIMS_MOCK));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    ProductOperations product = invocationOnMock.getArgument(1, ProductOperations.class);
                    product.setId(id);
                    product.setLogo("logo1");
                    return product;
                });
        ResultMatcher matcher;
        switch (role) {
            case ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case LIMITED:
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
            case ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1))
                        .updateProduct(Mockito.eq(uuid), Mockito.any());
                break;
            case LIMITED:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void updateProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.empty());
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
    @EnumSource(value = Authority.class, names = {"ADMIN", "LIMITED"})
    void deleteProduct_checkRole(Authority role) throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.of(CLAIMS_MOCK));
        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(role2userMap.get(role));
        Mockito.doNothing()
                .when(productServiceMock)
                .deleteProduct(Mockito.anyString());

        ResultMatcher matcher;
        switch (role) {
            case ADMIN:
                matcher = MockMvcResultMatchers.status().is2xxSuccessful();
                break;
            case LIMITED:
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
            case ADMIN:
                Mockito.verify(productServiceMock, Mockito.times(1))
                        .deleteProduct(Mockito.eq(uuid));
                break;
            case LIMITED:
                Mockito.verifyNoInteractions(productServiceMock);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Test
    void deleteProduct_unauthorized() throws Exception {
        // given
        Mockito.when(jwtServiceMock.getClaims(Mockito.any()))
                .thenReturn(Optional.empty());
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