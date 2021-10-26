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
        // given
        // when
        List<Product> products = productService.getProducts();
        // then
        assertTrue(products.isEmpty());
    }

    @Test
    void getProducts_notEmptyList() {
        // given
        Mockito.when(repositoryMock.findAll())
                .thenReturn(Collections.singletonList(new Product()));
        // when
        List<Product> products = productService.getProducts();
        // then
        assertEquals(1, products.size());
    }

    @Test
    void createProduct() {
        // given
        OffsetDateTime inputActivationDateTime = OffsetDateTime.now();
        Product input = new Product();
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
    }

    @Test
    void deleteProduct_exist() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.existsById(Mockito.anyString()))
                .thenReturn(true);
        Mockito.doNothing()
                .when(repositoryMock).deleteById(Mockito.anyString());
        // when
        productService.deleteProduct(productId);
        // then
        Mockito.verify(repositoryMock, Mockito.times(1)).deleteById(Mockito.anyString());
    }

    @Test
    void deleteProduct_NotExist() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.existsById(Mockito.anyString()))
                .thenReturn(false);
        // when - then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void getProduct_notNull() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.findById(Mockito.any()))
                .thenAnswer(invocationOnMock -> Optional.of(new Product()));
        // when
        Product product = productService.getProduct(productId);
        // then
        assertNotNull(product);
    }

    @Test
    void getProduct_null() {
        // given
        String productId = "productId";
        // when - then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.getProduct(productId));
    }

    @Test
    void updateProduct_foundProductNotNull() {
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