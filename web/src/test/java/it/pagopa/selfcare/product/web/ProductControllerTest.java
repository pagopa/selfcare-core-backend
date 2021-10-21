package it.pagopa.selfcare.product.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import it.pagopa.selfcare.product.web.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = {ProductController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ProductController.class
})
class ProductControllerTest {

    private static final String BASE_URL = "/products";
    private static final CreateProductDto CREATE_PRODUCT_DTO;
    private static final UpdateProductDto UPDATE_PRODUCT_DTO;
    private static final Product PRODUCT;

    static {
        CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto());
        UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto());
        PRODUCT = TestUtils.mockInstance(new Product());
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
    void getProductNotNull() throws Exception {
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
    void getProductNull() throws Exception {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenReturn(null);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/id")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    void createProduct() throws Exception {
        // given
        Mockito.when(productServiceMock.createProduct(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> {
                    Product product = invocationOnMock.getArgument(0, Product.class);
                    product.setId(UUID.randomUUID().toString());
                    return product;
                });
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
        assertNotNull(product.getId());
        TestUtils.reflectionEqualsByName(CREATE_PRODUCT_DTO, product, "id");
    }

    @Test
    void updateProductNotNul() throws Exception {
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
        TestUtils.reflectionEqualsByName(UPDATE_PRODUCT_DTO, product, "id");
    }

    @Test
    void updateProductNull() throws Exception {
        // given
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(Product.class)))
                .thenReturn(null);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/id")
                        .content(objectMapper.writeValueAsString(UPDATE_PRODUCT_DTO))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    void deleteProduct() throws Exception {
        // given
        productServiceMock.deleteProduct(Mockito.anyString());
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

}