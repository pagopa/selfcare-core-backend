package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    @Test
    void toResource_notNull() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        Product product = TestUtils.mockInstance(new Product());
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
        // when
        ProductResource productResource = ProductMapper.toResource(null);
        // then
        assertNull(productResource);
    }

    @Test
    void fromCreateProductDto_notNull() {
        // given
        CreateProductDto dto = TestUtils.mockInstance(new CreateProductDto());
        // when
        Product product = ProductMapper.fromDto(dto);
        // then
        TestUtils.reflectionEqualsByName(product, dto);
    }

    @Test
    void fromCreateProductDto_null() {
        // given
        // when
        Product product = ProductMapper.fromDto((CreateProductDto) null);
        // then
        assertNull(product);
    }

    @Test
    void fromUpdateProductDto_notNull() {
        // given
        UpdateProductDto dto = TestUtils.mockInstance(new UpdateProductDto());
        // when
        Product product = ProductMapper.fromDto(dto);
        // then
        assertNull(product.getId());
        TestUtils.reflectionEqualsByName(product, dto);
    }

    @Test
    void fromUpdateProductDto_null() {
        // given
        // when
        Product product = ProductMapper.fromDto((UpdateProductDto) null);
        // then
        assertNull(product);
    }
}