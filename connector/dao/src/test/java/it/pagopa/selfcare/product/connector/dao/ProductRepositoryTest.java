package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.dao.config.DaoTestConfig;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@EnableAutoConfiguration
@ContextConfiguration(classes = {ProductEntity.class, ProductRepository.class, DaoTestConfig.class})
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;


    @AfterEach
    public void clear() {
        repository.deleteAll();
    }


    @Test
    public void create() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity());
        // when
        ProductEntity savedProduct = repository.save(product);
        // then
        assertNotNull(savedProduct.getId(), "id cannot be null after entity creation");
    }


    @Test
    public void findAll_noProducts() {
        // given
        // when
        List<ProductEntity> products = repository.findAll();
        // then
        assertTrue(products.isEmpty());
    }


    @Test
    public void findAll_atLeastOneProduct() {
        // given
        create();
        // when
        List<ProductEntity> products = repository.findAll();
        // then
        assertTrue(products.size() > 0);
    }


    @Test
    public void update() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        ProductEntity savedProduct = repository.save(product);
        // when
        Optional<ProductEntity> foundProduct = repository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        ProductEntity p = foundProduct.get();
        p.setDescription("Description");
        ProductEntity p1 = repository.save(p);
        // then
        assertNotEquals(p1.getDescription(), product.getDescription());
    }


    @Test
    public void deleteById() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        ProductEntity savedProduct = repository.save(product);
        // when
        repository.deleteById(savedProduct.getId());
        // then
        Optional<ProductEntity> foundProduct = repository.findById(savedProduct.getId());
        assertFalse(foundProduct.isPresent());
    }


    @Test
    public void findByEnabled_found() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        repository.save(product);
        // when
        List<ProductEntity> result = repository.findByEnabled(true);
        // then
        assertFalse(result.isEmpty());
    }


    @Test
    public void findByEnabled_notFound() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        product.setEnabled(false);
        repository.save(product);
        // when
        List<ProductEntity> result = repository.findByEnabled(true);
        // then
        assertTrue(result.isEmpty());
    }

}