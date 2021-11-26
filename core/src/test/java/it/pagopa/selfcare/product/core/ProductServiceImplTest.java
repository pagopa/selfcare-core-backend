package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ProductServiceImpl.class})
class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductRepository repositoryMock;


    @Test
    void getProducts_emptyList() {
        // given and when
        List<Product> products = productService.getProducts();
        // then
        assertTrue(products.isEmpty());
    }

    @Test
    void getProducts_notEmptyList() {
        // given
        Mockito.when(repositoryMock.findByEnabled(Mockito.anyBoolean()))
                .thenReturn(Collections.singletonList(new Product()));
        // when
        List<Product> products = productService.getProducts();
        // then
        assertEquals(1, products.size());
        // add Mockito verify only one interaction with repositoryMock.findByEnabled
        Mockito.verify(repositoryMock, Mockito.times(1)).findByEnabled(Mockito.anyBoolean());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProduct() {
        // given
        OffsetDateTime inputCreationDateTime = OffsetDateTime.now();
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(false);
        Product input = new Product();
        Map<String,List<String>> map = new HashMap<>();
        List<String>list=new ArrayList<>();
        list.add("v1"); list.add("v2");
        map.put("operator",list);
        input.setRoleMappings(map);
        input.setCode("code");
        input.setCreationDateTime(inputCreationDateTime);
        Mockito.when(repositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        Product output = productService.createProduct(input);
        // then
        assertNotNull(output);
        assertNotNull(output.getCreationDateTime());
        assertNotNull(output.getContractTemplateUpdateDateTime());
        if (input.getCreationDateTime() != null && input.getContractTemplateUpdateDateTime() != null) {
            assertTrue(output.getCreationDateTime().isAfter(inputCreationDateTime));
        }
        Mockito.verify(repositoryMock, Mockito.times(1)).existsByCode(Mockito.eq("code"));
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Product.class));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProductDupKey() {
        // given
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(true);
        Product input = new Product();
        input.setCode("code");
        // when
        // then
            assertThrows(DuplicateKeyException.class, () -> productService.createProduct(input));
            Mockito.verify(repositoryMock, Mockito.times(1)).existsByCode(Mockito.eq("code"));
            Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProductValidateRoleMappingsNull(){
        // given
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(false);
        Product input = new Product();
        Map<String,List<String>> map = new HashMap<>();
        map.put("operator",null);
        input.setRoleMappings(map);
        input.setCode("code");
        // when and then
        assertThrows(InvalidRoleMappingException.class, () -> productService.createProduct(input));
        Mockito.verify(repositoryMock, Mockito.times(1)).existsByCode(Mockito.eq("code"));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProductValidateRoleMappingsIsEmpty(){
        // given
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(false);
        Product input = new Product();
        Map<String,List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        map.put("operator",list);
        input.setRoleMappings(map);
        input.setCode("code");
        // when and then
        assertThrows(InvalidRoleMappingException.class, () -> productService.createProduct(input));
        Mockito.verify(repositoryMock, Mockito.times(1)).existsByCode(Mockito.eq("code"));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProductValidateRoleMappingsNotEmptyAndPartyRole(){
        // given
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(false);
        Product input = new Product();
        Map<String,List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("v1"); list.add("v2");
        map.put("delegate",list);
        input.setRoleMappings(map);
        input.setCode("code");
        // when and then
        assertThrows(InvalidRoleMappingException.class, () -> productService.createProduct(input));
        Mockito.verify(repositoryMock, Mockito.times(1)).existsByCode(Mockito.eq("code"));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_existEnabled() {
        // given
        Product product = TestUtils.mockInstance(new Product());
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(repositoryMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        productService.deleteProduct("productId");
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_existNotEnabled() {
        // given
        Product product = TestUtils.mockInstance(new Product());
        product.setEnabled(false);
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        productService.deleteProduct(Mockito.anyString());
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_NotExist() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.existsById(productId))
                .thenReturn(false);
        // when - then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void getProduct_Enabled() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(new Product()));
        // when
        Product product = productService.getProduct(productId);
        // then
        assertNotNull(product);
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void getProduct_notEnabled() {
        // given
        Product p = new Product();
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(p));
       p.setEnabled(false);
        // when
        // then
        assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(Mockito.anyString()));
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void getProduct_null() {
        // given
        String productId = "productId";
        // when - then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(productId));
    }

    @Test
    void updateProduct_foundProductEnabledDiffVersionContract() {
        // given
        String productId = "productId";
        Product product = TestUtils.mockInstance(new Product(), "setId");

        Mockito.when(repositoryMock.findById(Mockito.eq(productId)))
                .thenReturn(Optional.of(new Product()));

        Map<String,List<String>> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("v1"); list.add("v2");
        map.put("operator",list);
        product.setRoleMappings(map);

        product.setContractTemplateVersion("1.2.4");

        Mockito.when(repositoryMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        Product savedProduct = productService.updateProduct(productId, product);
        // then
        assertEquals(savedProduct.getLogo(), product.getLogo());
        assertEquals(savedProduct.getTitle(), product.getTitle());
        assertEquals(savedProduct.getDescription(), product.getDescription());
        assertEquals(savedProduct.getUrlPublic(), product.getUrlPublic());
        assertEquals(savedProduct.getUrlBO(), product.getUrlBO());
        assertEquals(savedProduct.getCode(), product.getCode());
        assertEquals(savedProduct.getRoleMappings(), product.getRoleMappings());
        assertEquals(savedProduct.getRoleManagementURL(), product.getRoleManagementURL());
        assertEquals(savedProduct.getContractTemplatePath(), product.getContractTemplatePath());
        assertEquals(savedProduct.getContractTemplateVersion(), product.getContractTemplateVersion());
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.eq(productId));
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void updateProduct_foundProductNotEnabled() {
        // given
        String productId = "productId";
        Product product = TestUtils.mockInstance(new Product(), "setId");
        product.setEnabled(false);
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when And then
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, product));
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void updateProduct_notExists() {
        // given
        String productId = "productId";
        Product product = new Product();
        // when
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, product));
        // then
    }
}