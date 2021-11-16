package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        OffsetDateTime inputActivationDateTime = OffsetDateTime.now();
        Mockito.when(repositoryMock.existsByCode(Mockito.eq("code")))
                .thenReturn(false);
        Product input = new Product();
        input.setCode("code");
        input.setActivationDateTime(inputActivationDateTime);
        Mockito.when(repositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        Product output = productService.createProduct(input);
        // then
        assertNotNull(output);
        assertNotNull(output.getActivationDateTime());
        if (input.getActivationDateTime() != null) {
            assertTrue(output.getActivationDateTime().isAfter(inputActivationDateTime));
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
    void updateProduct_foundProductEnabled() {
        // given
        String productId = "productId";
        Product product = TestUtils.mockInstance(new Product(), "setId");
        Mockito.when(repositoryMock.findById(Mockito.eq(productId)))
                .thenReturn(Optional.of(new Product()));
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