package it.pagopa.selfcare.product.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.model.PartyRole;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.handler.ProductExceptionsHandler;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = {ProductController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ProductController.class,
        ProductExceptionsHandler.class
})
class ProductControllerTest {

    private static final String BASE_URL = "/products";
    private static final CreateProductDto CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto());
    private static final UpdateProductDto UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto());
    private static final Product PRODUCT = TestUtils.mockInstance(new Product());

    static {
        CREATE_PRODUCT_DTO.setRoleMappings(new EnumMap<>(PartyRole.class));
        UPDATE_PRODUCT_DTO.setRoleMappings(new EnumMap<>(PartyRole.class));
    }

    @MockBean
    private ProductService productServiceMock;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @Test
    void getProducts_atLeastOneProduct() throws Exception {
        // given
        Mockito.when(productServiceMock.getProducts())
                .thenReturn(Collections.singletonList(PRODUCT));
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        List<ProductResource> products = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(products);
        assertFalse(products.isEmpty());
    }

    @Test
    void getProducts_noProducts() throws Exception {
        // given
        Mockito.when(productServiceMock.getProducts())
                .thenReturn(Collections.emptyList());
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        List<ProductResource> products = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }


    @Test
    void getProduct_exists() throws Exception {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    Product p = new Product();
                    p.setId(id);
                    return p;
                });
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/id")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        ProductResource product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);
        assertNotNull(product);
    }

    @Test
    void getProduct_notExists() throws Exception {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenThrow(ResourceNotFoundException.class);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/id")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
        // then
        ErrorResource error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResource.class);
        assertNotNull(error);
    }

    @Test
    void createProduct() throws Exception {
        // given
        Mockito.when(productServiceMock.createProduct(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/")
                .content(objectMapper.writeValueAsString(CREATE_PRODUCT_DTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        ProductResource product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);
        assertNotNull(product);
        TestUtils.reflectionEqualsByName(CREATE_PRODUCT_DTO, product);
    }

    @Test
    void updateProduct_exists() throws Exception {
        // given
        Product prod = new Product();
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    Product product = invocationOnMock.getArgument(1, Product.class);
                    product.setId(id);
                    return product;
                });
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .put(BASE_URL + "/id")
                .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        ProductResource product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);

        assertNotNull(product);
        TestUtils.reflectionEqualsByName(UPDATE_PRODUCT_DTO, product);
    }

    @Test
    void updateProduct_notExists() throws Exception {
        // given
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(Product.class)))
                .thenThrow(ResourceNotFoundException.class);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .put(BASE_URL + "/id")
                .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
        // then
        ErrorResource error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResource.class);
        assertNotNull(error);
    }


    @Test
    void deleteProduct_exists() throws Exception {
        // given
        Mockito.doNothing()
                .when(productServiceMock).deleteProduct(Mockito.anyString());
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .delete(BASE_URL + "/id")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        assertEquals("", result.getResponse().getContentAsString());
    }


    @Test
    void deleteProduct_notExists() throws Exception {
        // given
        Mockito.doThrow(ResourceNotFoundException.class)
                .when(productServiceMock).deleteProduct(Mockito.anyString());
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .delete(BASE_URL + "/id")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
        // then
        ErrorResource error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResource.class);
        assertNotNull(error);
    }

}