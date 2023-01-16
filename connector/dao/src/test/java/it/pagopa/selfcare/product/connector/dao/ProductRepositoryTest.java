package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.product.connector.dao.config.DaoTestConfig;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.model.InstitutionType;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.*;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
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
        ProductEntity product = mockInstance(new ProductEntity(), "setRoleMappings");
        EnumMap<PartyRole, ProductEntity.ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            List<ProductEntity.ProductRole> roles = new ArrayList<>();
            roles.add(mockInstance(new ProductEntity.ProductRole(), 1));
            roles.add(mockInstance(new ProductEntity.ProductRole(), 2));
            ProductEntity.ProductRoleInfo productRoleInfo = new ProductEntity.ProductRoleInfo();
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        Map<InstitutionType, ProductEntity.EntityContract> institutionContractMappings = new HashMap<>();
        for (InstitutionType type : InstitutionType.values()) {
            ProductEntity.EntityContract contract = mockInstance(new ProductEntity.EntityContract());
            institutionContractMappings.put(type, contract);
        }
        product.setInstitutionContractMappings(institutionContractMappings);
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
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
        Map<InstitutionType, ProductEntity.EntityContract> institutionContractMappings = new HashMap<>();
        for (InstitutionType type : InstitutionType.values()) {
            ProductEntity.EntityContract contract = mockInstance(new ProductEntity.EntityContract());
            institutionContractMappings.put(type, contract);
        }
        product.setInstitutionContractMappings(institutionContractMappings);
        ProductEntity savedProduct = repository.save(product);
        // when
        Optional<ProductEntity> foundProduct = repository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        ProductEntity p = foundProduct.get();
        p.setDescription("Description");
        p.setNew(false);
        ProductEntity p1 = repository.save(p);
        // then
        assertNotEquals(p1.getDescription(), product.getDescription());
    }


    @Test
    void deleteById() {
        // given
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
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
        ProductEntity product = mockInstance(new ProductEntity(), "setRoleMappings");
        EnumMap<PartyRole, ProductEntity.ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            List<ProductEntity.ProductRole> roles = new ArrayList<>();
            roles.add(mockInstance(new ProductEntity.ProductRole(), 1));
            roles.add(mockInstance(new ProductEntity.ProductRole(), 2));
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
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
        repository.save(product);
        // when
        List<ProductEntity> result = repository.findByEnabled(true);
        // then
        assertFalse(result.isEmpty());
    }


    @Test
    void findByEnabled_notFound() {
        // given
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
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
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndEnabled("setParentId", true);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByParentAndEnabled_notFound() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setId");
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
        ProductEntity product = mockInstance(new ProductEntity(), "setId", "setParentId");
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndEnabled(null, true);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByParentIdAndStatusIsNot_found() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setId", "setStatus");
        product.setStatus(ProductStatus.TESTING);
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndStatusIsNot("setParentId", ProductStatus.INACTIVE);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByParentIdAndStatusIsNot_notFound() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setId", "setStatus");
        product.setStatus(ProductStatus.INACTIVE);
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndStatusIsNot("setParentId", ProductStatus.INACTIVE);
        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void findByParentIdAndStatusIsNot_parentNull() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setId", "setStatus", "setParentId");
        product.setStatus(ProductStatus.ACTIVE);
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByParentIdAndStatusIsNot(null, ProductStatus.INACTIVE);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByStatusIsNot_found() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setStatus");
        product.setStatus(ProductStatus.ACTIVE);
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByStatusIsNot(ProductStatus.INACTIVE);
        //then
        assertFalse(result.isEmpty());
    }

    @Test
    void findByStatusIsNot_notFound() {
        //given
        ProductEntity product = mockInstance(new ProductEntity(), "setStatus");
        product.setStatus(ProductStatus.INACTIVE);
        repository.save(product);
        //when
        List<ProductEntity> result = repository.findByStatusIsNot(ProductStatus.INACTIVE);
        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void existsByIdAndEnabledFalse() {
        //given
        String productId = "productId";
        ProductEntity product = mockInstance(new ProductEntity(), "enabled");
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
        ProductEntity product = mockInstance(new ProductEntity(), "enabled");
        product.setId(productId);
        product.setEnabled(true);
        repository.save(product);
        //when
        boolean found = repository.existsByIdAndEnabledFalse(productId);
        //then
        assertFalse(found);
    }

    @Test
    void existsByIdAndStatusInactive() {
        //given
        String productId = "productId";
        ProductEntity product = mockInstance(new ProductEntity(), "setStatus");
        product.setId(productId);
        product.setStatus(ProductStatus.INACTIVE);
        repository.save(product);
        //when
        boolean found = repository.existsByIdAndStatus(productId, ProductStatus.INACTIVE);
        //then
        assertTrue(found);
    }

    @Test
    void existsByIdAndStatusInactive_notFound() {
        //given
        String productId = "productId";
        ProductEntity product = mockInstance(new ProductEntity(), "setStatus");
        product.setId(productId);
        product.setStatus(ProductStatus.ACTIVE);
        repository.save(product);
        //when
        boolean found = repository.existsByIdAndStatus(productId, ProductStatus.INACTIVE);
        //then
        assertFalse(found);
    }
}