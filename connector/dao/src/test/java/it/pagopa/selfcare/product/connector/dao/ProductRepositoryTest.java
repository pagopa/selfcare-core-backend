package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.dao.config.DaoTestConfig;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.EnumMap;
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
    void clear() {
        repository.deleteAll();
    }


    @Test
    void create() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setRoleMappings");
        EnumMap<PartyRole, ProductEntity.ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            List<ProductEntity.ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductEntity.ProductRole(), 1));
            roles.add(TestUtils.mockInstance(new ProductEntity.ProductRole(), 2));
            ProductEntity.ProductRoleInfo productRoleInfo = new ProductEntity.ProductRoleInfo();
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        product.setRoleMappings(roleMappings);
        // when
        ProductEntity savedProduct = repository.save(product);
        // then
        assertNotNull(savedProduct.getId(), "id cannot be null after entity creation");
    }


    @Test
    void findAll_noProducts() {
        // given
        // when
        List<ProductEntity> products = repository.findAll();
        // then
        assertTrue(products.isEmpty());
    }


    @Test
    void findAll_atLeastOneProduct() {
        // given
        create();
        // when
        List<ProductEntity> products = repository.findAll();
        // then
        assertTrue(products.size() > 0);
    }


    @Test
    void update() {
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
    void deleteById() {
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
    void findById_found() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setRoleMappings");
        EnumMap<PartyRole, ProductEntity.ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            List<ProductEntity.ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductEntity.ProductRole(), 1));
            roles.add(TestUtils.mockInstance(new ProductEntity.ProductRole(), 2));
            ProductEntity.ProductRoleInfo productRoleInfo = new ProductEntity.ProductRoleInfo();
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        product.setRoleMappings(roleMappings);
        repository.save(product);
        // when
        Optional<ProductEntity> result = repository.findById(product.getId());
        // then
        assertTrue(result.isPresent());
        assertNotNull(result.get().getRoleMappings());
        assertEquals(PartyRole.values().length, result.get().getRoleMappings().keySet().size());
    }


    @Test
    void findByEnabled_found() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        repository.save(product);
        // when
        List<ProductEntity> result = repository.findByEnabled(true);
        // then
        assertFalse(result.isEmpty());
    }


    @Test
    void findByEnabled_notFound() {
        // given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        product.setEnabled(false);
        repository.save(product);
        // when
        List<ProductEntity> result = repository.findByEnabled(true);
        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByParentAndEnabled_found() {
        //given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndEnabled("setParentId", true);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByParentAndEnabled_notFound() {
        //given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId");
        product.setParentId("differentParent");
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndEnabled("setParentId", true);
        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByParentAndEnabled_parentNull() {
        //given
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "setId", "setParentId");
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndEnabled(null, true);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void existsByIdAndEnabledFalse() {
        //given
        String productId = "productId";
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "enabled");
        product.setId(productId);
        product.setEnabled(false);
        repository.save(product);
        //when
        boolean found = repository.existsByIdAndEnabledFalse(productId);
        //then
        assertTrue(found);
    }

    @Test
    void existsByIdAndEnableFalse_notFound() {
        //given
        String productId = "productId";
        ProductEntity product = TestUtils.mockInstance(new ProductEntity(), "enabled");
        product.setId(productId);
        product.setEnabled(true);
        repository.save(product);
        //when
        boolean found = repository.existsByIdAndEnabledFalse(productId);
        //then
        assertFalse(found);
    }
}