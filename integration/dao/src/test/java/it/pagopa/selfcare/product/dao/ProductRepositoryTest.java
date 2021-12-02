package it.pagopa.selfcare.product.dao;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.dao.config.DaoTestConfig;
import it.pagopa.selfcare.product.dao.model.Product;
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
@ContextConfiguration(classes = {Product.class, ProductRepository.class, DaoTestConfig.class})
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
        Product product = TestUtils.mockInstance(new Product());
        // when
        Product savedProduct = repository.save(product);
        // then
        assertNotNull(savedProduct.getId(), "id cannot be null after entity creation");
    }


    @Test
    public void findAll_noProducts() {
        // given
        // when
        List<Product> products = repository.findAll();
        // then
        assertTrue(products.isEmpty());
    }


    @Test
    public void findAll_atLeastOneProduct() {
        // given
        create();
        // when
        List<Product> products = repository.findAll();
        // then
        assertTrue(products.size() > 0);
    }


    @Test
    public void update() {
        // given
        Product product = TestUtils.mockInstance(new Product(), "setId");
        Product savedProduct = repository.save(product);
        // when
        Optional<Product> foundProduct = repository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        Product p = foundProduct.get();
        p.setDescription("Description");
        Product p1 = repository.save(p);
        // then
        assertNotEquals(p1.getDescription(), product.getDescription());
    }


    @Test
    public void deleteById() {
        // given
        Product product = TestUtils.mockInstance(new Product(), "setId");
        Product savedProduct = repository.save(product);
        // when
        repository.deleteById(savedProduct.getId());
        // then
        Optional<Product> foundProduct = repository.findById(savedProduct.getId());
        assertFalse(foundProduct.isPresent());
    }


    @Test
    public void findByEnabled_found() {
        // given
        Product product = TestUtils.mockInstance(new Product(), "setId");
        repository.save(product);
        // when
        List<Product> result = repository.findByEnabled(true);
        // then
        assertFalse(result.isEmpty());
    }


    @Test
    public void findByEnabled_notFound() {
        // given
        Product product = TestUtils.mockInstance(new Product(), "setId");
        product.setEnabled(false);
        repository.save(product);
        // when
        List<Product> result = repository.findByEnabled(true);
        // then
        assertTrue(result.isEmpty());
    }

}