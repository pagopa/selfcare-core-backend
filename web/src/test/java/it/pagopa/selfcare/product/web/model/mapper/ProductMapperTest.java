package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import it.pagopa.selfcare.product.web.utils.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductMapperTest {

    @Test
    void toResource_notNull() {
        // given
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
        TestUtils.reflectionEqualsByName(product, productResource);
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
        assertNull(product.getId());
        TestUtils.reflectionEqualsByName(dto, product, "setId");
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
        TestUtils.reflectionEqualsByName(dto, product, "setId");
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