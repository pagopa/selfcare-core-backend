package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.web.model.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toResource_notNull() {
        // given
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(1);
        ProductOperations product = TestUtils.mockInstance(new ProductDto(), "setRoleMappings");
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
        product.setRoleMappings(roleMappings);
        // when
        ProductResource productResource = ProductMapper.toResource(product);
        // then
        assertEquals(product.getId(), productResource.getId());
        assertEquals(product.getLogo(), productResource.getLogo());
        assertEquals(product.getTitle(), productResource.getTitle());
        assertEquals(product.getDescription(), productResource.getDescription());
        assertEquals(product.getUrlPublic(), productResource.getUrlPublic());
        assertEquals(product.getUrlBO(), productResource.getUrlBO());
        assertTrue(now.isBefore(productResource.getContractTemplateUpdatedAt()));
        assertEquals(product.getContractTemplateVersion(), productResource.getContractTemplateVersion());
        assertEquals(product.getContractTemplatePath(), productResource.getContractTemplatePath());
        assertEquals(product.getRoleMappings(), productResource.getRoleMappings());
        assertEquals(product.getRoleManagementURL(), productResource.getRoleManagementURL());
        TestUtils.reflectionEqualsByName(productResource, product);
    }

    @Test
    void toResource_null() {
        // given
        ProductOperations entity = null;
        // when
        ProductResource productResource = ProductMapper.toResource(entity);
        // then
        assertNull(productResource);
    }

    @Test
    void fromCreateProductDto_notNull() {
        // given
        CreateProductDto dto = TestUtils.mockInstance(new CreateProductDto(), "setRoleMappings");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        dto.setRoleMappings(roleMappings);
        // when
        ProductOperations product = ProductMapper.fromDto(dto);
        // then
        assertNotNull(product);
        TestUtils.reflectionEqualsByName(product, dto);
    }

    @Test
    void fromCreateProductDto_null() {
        // given
        // when
        ProductOperations product = ProductMapper.fromDto((CreateProductDto) null);
        // then
        assertNull(product);
    }

    @Test
    void fromUpdateProductDto_notNull() {
        // given
        UpdateProductDto dto = TestUtils.mockInstance(new UpdateProductDto(), "setRoleMappings");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(TestUtils.mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        dto.setRoleMappings(roleMappings);
        // when
        ProductOperations product = ProductMapper.fromDto(dto);
        // then
        assertNull(product.getId());
        TestUtils.reflectionEqualsByName(product, dto);
    }

    @Test
    void fromUpdateProductDto_null() {
        // given
        // when
        ProductOperations product = ProductMapper.fromDto((UpdateProductDto) null);
        // then
        assertNull(product);
    }

    @Test
    void toRoleMappings_null() {
        // given
        EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings = null;
        // when
        EnumMap<PartyRole, ProductRoleInfo> result = ProductMapper.toRoleMappings(roleMappings);
        // then
        assertNull(result);
    }

    @Test
    void toRoleMappings_notNull() {
        // given
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
        // when
        EnumMap<PartyRole, ProductRoleInfo> result = ProductMapper.toRoleMappings(roleMappings);
        // then
        assertNotNull(result);
        assertIterableEquals(roleMappings.entrySet(), result.entrySet());
    }

}