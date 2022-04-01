package it.pagopa.selfcare.product.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.commons.web.model.ErrorResource;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.web.config.WebTestConfig;
import it.pagopa.selfcare.product.web.handler.ProductExceptionsHandler;
import it.pagopa.selfcare.product.web.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(value = {ProductController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        ProductController.class,
        ProductExceptionsHandler.class,
        WebTestConfig.class
})
class ProductControllerTest {

    private static final String BASE_URL = "/products";
    private static final CreateProductDto CREATE_PRODUCT_DTO = TestUtils.mockInstance(new CreateProductDto(), "setRoleMappings");
    private static final UpdateProductDto UPDATE_PRODUCT_DTO = TestUtils.mockInstance(new UpdateProductDto(), "setRoleMappings");
    private static final CreateSubProductDto CREATE_SUB_PRODUCT_DTO = TestUtils.mockInstance(new CreateSubProductDto());
    private static final UpdateSubProductDto UPDATE_SUB_PRODUCT_DTO = TestUtils.mockInstance(new UpdateSubProductDto());

    static {
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            productRoleInfo.setMultiroleAllowed(true);
            roleMappings.put(partyRole, productRoleInfo);
        }
        CREATE_PRODUCT_DTO.setRoleMappings(roleMappings);
        UPDATE_PRODUCT_DTO.setRoleMappings(roleMappings);
    }

    @MockBean
    private ProductService productServiceMock;


    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;


    @Test
    void saveProductLogo() throws Exception {
        String productId = "productId";
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String filename = "test.png";
        MockMultipartFile multipartFile = new MockMultipartFile("logo", filename,
                contentType, "test prodoct logo".getBytes(StandardCharsets.UTF_8));
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart(BASE_URL + "/" + productId + "/logo")
                .file(multipartFile);
        requestBuilder.with(request -> {
            request.setMethod(HttpMethod.PUT.name());
            return request;
        });
        //when
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
        //then
        Mockito.verify(productServiceMock, Mockito.times(1))
                .saveProductLogo(Mockito.eq(productId), Mockito.any(), Mockito.eq(contentType), Mockito.eq(filename));
        Mockito.verifyNoMoreInteractions(productServiceMock);

    }

    @Test
    void getProducts_atLeastOneProduct() throws Exception {
        // given
        ProductOperations product = TestUtils.mockInstance(new ProductDto(), "setRoleMappings", "setParentId");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        product.setRoleMappings(roleMappings);
        Mockito.when(productServiceMock.getProducts(true))
                .thenReturn(Collections.singletonList(product));
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
        Mockito.when(productServiceMock.getProducts(true))
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
                    ProductOperations product = TestUtils.mockInstance(new ProductDto(), "setId", "setRoleMappings");
                    product.setId(id);
                    EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
                    for (PartyRole partyRole : PartyRole.values()) {
                        ProductRoleInfo productRoleInfo = new ProductRoleInfo();
                        List<ProductRole> roles = new ArrayList<>();
                        roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
                        roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
                        productRoleInfo.setRoles(roles);
                        roleMappings.put(partyRole, productRoleInfo);
                    }
                    product.setRoleMappings(roleMappings);
                    return product;
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
        Mockito.when(productServiceMock.createProduct(Mockito.any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
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
    void createSubProduct() throws Exception {
        // given
        String productId = "productId";
        Mockito.when(productServiceMock.createProduct(Mockito.any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .post(BASE_URL + "/" + productId + "/sub-products")
                .content(objectMapper.writeValueAsString(CREATE_SUB_PRODUCT_DTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        ProductResource product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);
        assertNotNull(product);
        assertEquals(productId, product.getParentId());
        TestUtils.reflectionEqualsByName(CREATE_SUB_PRODUCT_DTO, product);
    }

    @Test
    void updateProduct_exists() throws Exception {
        // given
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    ProductOperations product = invocationOnMock.getArgument(1, ProductOperations.class);
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
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(ProductOperations.class)))
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
    void setUpdateSubProductDto() throws Exception {
        // given
        Mockito.when(productServiceMock.updateProduct(Mockito.anyString(), Mockito.any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    ProductOperations product = invocationOnMock.getArgument(1, ProductOperations.class);
                    product.setId(id);
                    return product;
                });
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .put(BASE_URL + "/id/sub-products")
                .content(objectMapper.writeValueAsString(UPDATE_SUB_PRODUCT_DTO))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        ProductResource product = objectMapper.readValue(result.getResponse().getContentAsString(), ProductResource.class);
        assertNotNull(product);
        TestUtils.reflectionEqualsByName(UPDATE_SUB_PRODUCT_DTO, product);
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


    @Test
    void getProductRoles_exists() throws Exception {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    String id = invocationOnMock.getArgument(0, String.class);
                    ProductOperations product = TestUtils.mockInstance(new ProductDto(), "setId", "setRoleMappings");
                    product.setId(id);
                    EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
                    for (PartyRole partyRole : PartyRole.values()) {
                        ProductRoleInfo productRoleInfo = new ProductRoleInfo();
                        List<ProductRole> roles = new ArrayList<>();
                        roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
                        roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
                        productRoleInfo.setRoles(roles);
                        roleMappings.put(partyRole, productRoleInfo);
                    }
                    product.setRoleMappings(roleMappings);
                    return product;
                });
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/id/role-mappings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        // then
        Map<String, ProductRoleInfo> roles = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(roles);
        assertFalse(roles.isEmpty());
    }

    @Test
    void getProductRoles_notExists() throws Exception {
        // given
        Mockito.when(productServiceMock.getProduct(Mockito.anyString()))
                .thenThrow(ResourceNotFoundException.class);
        // when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/id/role-mappings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.NOT_FOUND.value()))
                .andReturn();
        // then
        ErrorResource error = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResource.class);
        assertNotNull(error);
    }

    @Test
    void getProductsTree() throws Exception {
        //given
        ProductOperations node = TestUtils.mockInstance(new ProductDto(), "setParentId", "setId");
        node.setId("parentId");
        ProductOperations children = TestUtils.mockInstance(new ProductDto(), "setParentId");
        children.setParentId(node.getId());
        Mockito.when(productServiceMock.getProducts(Mockito.anyBoolean()))
                .thenReturn(List.of(node, children));
        //when
        MvcResult result = mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + "/tree")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
        //then
        List<ProductTreeResource> treeResources = objectMapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        assertNotNull(treeResources);
        assertEquals(1, treeResources.size());
        Mockito.verify(productServiceMock, Mockito.times(1))
                .getProducts(false);
        Mockito.verifyNoMoreInteractions(productServiceMock);

    }


}