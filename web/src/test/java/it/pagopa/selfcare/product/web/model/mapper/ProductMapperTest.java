package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.connector.model.BackOfficeConfigurations;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.web.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static it.pagopa.selfcare.commons.utils.TestUtils.reflectionEqualsByName;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toResource_notNull() {
        // given
        Instant now = Instant.now().minusSeconds(1);
        ProductOperations product = mockInstance(new ProductDto(), "setRoleMappings", "setCreatedBy", "setModifiedBy");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            productRoleInfo.setMultiroleAllowed(true);
            roleMappings.put(partyRole, productRoleInfo);
        }
        product.setRoleMappings(roleMappings);
        product.setCreatedBy(randomUUID().toString());
        product.setModifiedBy(randomUUID().toString());
        // when
        ProductResource productResource = ProductMapper.toResource(product);
        // then
        assertEquals(product.getId(), productResource.getId());
        assertEquals(product.getLogo(), productResource.getLogo());
        assertEquals(product.getStatus(), productResource.getStatus());
        assertEquals(product.getDepictImageUrl(), productResource.getDepictImageUrl());
        assertEquals(product.getTitle(), productResource.getTitle());
        assertEquals(product.getDescription(), productResource.getDescription());
        assertEquals(product.getUrlPublic(), productResource.getUrlPublic());
        assertEquals(product.getUrlBO(), productResource.getUrlBO());
        assertEquals(product.getIdentityTokenAudience(), productResource.getIdentityTokenAudience());
        assertTrue(now.isBefore(productResource.getContractTemplateUpdatedAt()));
        assertEquals(product.getContractTemplateVersion(), productResource.getContractTemplateVersion());
        assertEquals(product.getContractTemplatePath(), productResource.getContractTemplatePath());
        assertEquals(product.getRoleMappings(), productResource.getRoleMappings());
        assertEquals(product.getCreatedBy(), productResource.getCreatedBy().toString());
        assertEquals(product.getModifiedBy(), productResource.getModifiedBy().toString());
        reflectionEqualsByName(productResource, product, "createdBy", "modifiedBy");
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
        CreateProductDto dto = mockInstance(new CreateProductDto(), "setRoleMappings");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        dto.setRoleMappings(roleMappings);
        // when
        ProductOperations product = ProductMapper.fromDto(dto);
        // then
        assertNotNull(product);
        reflectionEqualsByName(product, dto);
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
    void fromCreateSubProductDto_notNull() {
        //given
        CreateSubProductDto dto = mockInstance(new CreateSubProductDto());
        // when
        ProductOperations product = ProductMapper.fromDto(dto);
        // then
        assertNotNull(product);
        reflectionEqualsByName(product, dto);
    }

    @Test
    void fromCreateSubProduct_null() {
        //given
        //when
        ProductOperations product = ProductMapper.fromDto((CreateSubProductDto) null);
        // then
        assertNull(product);
    }

    @Test
    void fromUpdateProductDto_notNull() {
        // given
        UpdateProductDto dto = mockInstance(new UpdateProductDto(), "setRoleMappings");
        EnumMap<PartyRole, ProductRoleInfo> roleMappings = new EnumMap<>(PartyRole.class);
        for (PartyRole partyRole : PartyRole.values()) {
            ProductRoleInfo productRoleInfo = new ProductRoleInfo();
            List<ProductRole> roles = new ArrayList<>();
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 2));
            productRoleInfo.setRoles(roles);
            roleMappings.put(partyRole, productRoleInfo);
        }
        dto.setRoleMappings(roleMappings);
        // when
        ProductOperations product = ProductMapper.fromDto(dto);
        // then
        assertNull(product.getId());
        reflectionEqualsByName(product, dto);
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
    void fromUpdateSubProduct_notNull() {
        //given
        UpdateSubProductDto updateSubProductDto = mockInstance(new UpdateSubProductDto());
        //when
        ProductOperations productOperations = ProductMapper.fromDto(updateSubProductDto);
        //then
        assertNull(productOperations.getId());
        reflectionEqualsByName(productOperations, updateSubProductDto);
    }

    @Test
    void fromUpdateSubProduct_null() {
        //given
        //when
        ProductOperations product = ProductMapper.fromDto((UpdateSubProductDto) null);
        //then
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
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 1));
            roles.add(mockInstance(new ProductRole(), partyRole.ordinal() + 2));
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


    @Test
    void toBackOfficeConfigurations_null() {
        // given
        final Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations = null;
        // when
        final Map<String, BackOfficeConfigurationsResource> result = ProductMapper.toBackOfficeConfigurations(backOfficeEnvironmentConfigurations);
        // then
        assertNull(result);
    }


    @Test
    void toBackOfficeConfigurations_notNull() {
        // given
        final Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations
                = Map.of("test", mockInstance(new BackOfficeConfigurationsResource()));
        // when
        final Map<String, BackOfficeConfigurationsResource> result = ProductMapper.toBackOfficeConfigurations(backOfficeEnvironmentConfigurations);
        // then
        assertNotNull(result);
        assertIterableEquals(backOfficeEnvironmentConfigurations.entrySet(), result.entrySet());
    }


    @Test
    void toTreeResource_null() {
        //given
        List<ProductOperations> model = null;
        //when
        List<ProductTreeResource> resource = ProductMapper.toTreeResource(model);
        //then
        assertNull(resource);
    }

    @Test
    void toTreeResource_notNull() {
        //given
        ProductOperations node = mockInstance(new ProductDto(), "setParentId", "setId", "setCreatedBy", "setModifiedBy");
        node.setId("parentId");
        node.setCreatedBy(randomUUID().toString());
        node.setModifiedBy(randomUUID().toString());
        ProductOperations children = mockInstance(new ProductDto(), "setParentId");
        children.setParentId(node.getId());
        children.setCreatedBy(randomUUID().toString());
        children.setModifiedBy(randomUUID().toString());
        //when
        List<ProductTreeResource> resource = ProductMapper.toTreeResource(List.of(node, children));
        //then
        assertNotNull(resource);
        assertEquals(1, resource.size());
        assertEquals(node.getCreatedBy(), resource.get(0).getNode().getCreatedBy().toString());
        reflectionEqualsByName(node, resource.get(0).getNode(), "createdBy", "modifiedBy");
        assertEquals(children.getModifiedBy(), resource.get(0).getChildren().get(0).getModifiedBy().toString());
        reflectionEqualsByName(children, resource.get(0).getChildren().get(0), "createdBy", "modifiedBy");
    }

    @Test
    void toTreeResource_notNull_noChildren() {
        //given
        ProductOperations node = mockInstance(new ProductDto(), "setParentId", "setId", "setCreatedBy", "setModifiedBy");
        node.setId("parentId");
        node.setCreatedBy(randomUUID().toString());
        node.setModifiedBy(randomUUID().toString());
        //when
        List<ProductTreeResource> resource = ProductMapper.toTreeResource(List.of(node));
        //then
        assertNotNull(resource);
        assertEquals(1, resource.size());
        assertEquals(node.getCreatedBy(), resource.get(0).getNode().getCreatedBy().toString());
        assertEquals(node.getModifiedBy(), resource.get(0).getNode().getModifiedBy().toString());
        reflectionEqualsByName(node, resource.get(0).getNode(), "createdBy", "modifiedBy");
    }

}