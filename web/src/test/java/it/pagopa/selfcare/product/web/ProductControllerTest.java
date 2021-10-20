package it.pagopa.selfcare.product.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
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

@WebMvcTest(value = {ProductController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ProductController.class
})
class ProductControllerTest {

    private static final String BASE_URL = "/products";
    private static final CreateProductDto CREATE_PRODUCT_DTO;
    private static final Product PRODUCT;

    static {
        CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto());
//        CREATE_PRODUCT_DTO = new CreateProductDto();
//        CREATE_PRODUCT_DTO.setDescription("Description");
//        CREATE_PRODUCT_DTO.setLogo("Logo");
//        CREATE_PRODUCT_DTO.setTitle("Title");
//        CREATE_PRODUCT_DTO.setUrlBO("UrlBO");
//        CREATE_PRODUCT_DTO.setUrlPublic("UrlPublic");

        PRODUCT = TestUtils.mockInstance(new Product());
//        PRODUCT.setId(UUID.randomUUID().toString());
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
                new TypeReference<List<ProductResource>>() {
                });
        Assertions.assertNotNull(products);
        Assertions.assertFalse(products.isEmpty());
        // TODO: check that all expected ProductResult fields are populated
        Assertions.assertEquals(PRODUCT.getId(), products.get(0).getId());
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
                new TypeReference<List<ProductResource>>() {
                });
        Assertions.assertNotNull(products);
        Assertions.assertTrue(products.isEmpty());
        // TODO: check that all expected ProductResult fields are populated
    }


    @Test
    void getProduct() {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    Product p = new Product();
                    p.setId(id);
                    return p;
                });
        // then
        // TODO: check that all expected ProductResult fields are populated
//        ProductResource resource = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);
    }

    @Test
    void createProduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }

}