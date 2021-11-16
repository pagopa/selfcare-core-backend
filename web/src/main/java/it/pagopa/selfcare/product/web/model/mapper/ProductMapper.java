package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;

public class ProductMapper {

    public static ProductResource toResource(Product entity) {
        ProductResource resource = null;
        if (entity != null) {
            resource = new ProductResource();
            resource.setId(entity.getId());
            resource.setLogo(entity.getLogo());
            resource.setTitle(entity.getTitle());
            resource.setDescription(entity.getDescription());
            resource.setUrlPublic(entity.getUrlPublic());
            resource.setUrlBO(entity.getUrlBO());
            resource.setActivationDateTime(entity.getActivationDateTime());
            resource.setCode(entity.getCode());//TODO add code attribute mapping
        }

        return resource;
    }

    public static Product fromDto(CreateProductDto dto) {
        Product product = null;
        if (dto != null) {
            product = new Product();
            product.setLogo(dto.getLogo());
            product.setTitle(dto.getTitle());
            product.setDescription(dto.getDescription());
            product.setUrlPublic(dto.getUrlPublic());
            product.setUrlBO(dto.getUrlBO());
            product.setCode(dto.getCode());//TODO add code attribute
        }

        return product;
    }

    public static Product fromDto(UpdateProductDto dto) {
        Product product = null;
        if (dto != null) {
            product = new Product();
            product.setLogo(dto.getLogo());
            product.setTitle(dto.getTitle());
            product.setDescription(dto.getDescription());
            product.setUrlPublic(dto.getUrlPublic());
            product.setUrlBO(dto.getUrlBO());
            product.setCode(dto.getCode());//TODO add code attribute
        }

        return product;
    }

}
