package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductMapper {

    public static ProductResource toResource(ProductOperations entity) {
        log.trace("toResource start");
        log.debug("entity = {}", entity);
        ProductResource resource = null;

        if (entity != null) {
            resource = new ProductResource();
            resource.setId(entity.getId());
            resource.setLogo(entity.getLogo());
            resource.setTitle(entity.getTitle());
            resource.setDescription(entity.getDescription());
            resource.setUrlPublic(entity.getUrlPublic());
            resource.setUrlBO(entity.getUrlBO());
            resource.setCreatedAt(entity.getCreatedAt());
            resource.setContractTemplateUpdatedAt(entity.getContractTemplateUpdatedAt());
            resource.setRoleMappings(entity.getRoleMappings());
            resource.setContractTemplatePath(entity.getContractTemplatePath());
            resource.setContractTemplateVersion(entity.getContractTemplateVersion());
            resource.setRoleManagementURL(entity.getRoleManagementURL());
        }
        log.debug("resource = {}", resource);
        log.trace("toResource end");
        return resource;
    }

    public static ProductOperations fromDto(CreateProductDto dto) {
        log.trace("fromDto start");
        log.debug("createProductDto = {}", dto);
        ProductOperations product = null;
        if (dto != null) {
            product = new ProductDto();
            product.setId(dto.getId());
            product.setTitle(dto.getTitle());
            product.setDescription(dto.getDescription());
            product.setUrlPublic(dto.getUrlPublic());
            product.setUrlBO(dto.getUrlBO());
            product.setRoleMappings(dto.getRoleMappings());
            product.setContractTemplatePath(dto.getContractTemplatePath());
            product.setContractTemplateVersion(dto.getContractTemplateVersion());
            product.setRoleManagementURL(dto.getRoleManagementURL());
        }
        log.debug("product = {}", product);
        log.trace("fromDto end");
        return product;
    }

    public static ProductOperations fromDto(UpdateProductDto dto) {
        log.trace("fromDto start");
        log.debug("updateProductDto = {}", dto);
        ProductOperations product = null;
        if (dto != null) {
            product = new ProductDto();
            product.setTitle(dto.getTitle());
            product.setDescription(dto.getDescription());
            product.setUrlPublic(dto.getUrlPublic());
            product.setUrlBO(dto.getUrlBO());
            product.setRoleMappings(dto.getRoleMappings());
            product.setContractTemplatePath(dto.getContractTemplatePath());
            product.setContractTemplateVersion(dto.getContractTemplateVersion());
            product.setRoleManagementURL(dto.getRoleManagementURL());
        }
        log.debug("product = {}", product);
        log.trace("fromDto end");
        return product;
    }

}
