package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.web.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;

@Slf4j
public class ProductMapper {

    public static ProductResource toResource(ProductOperations entity) {
        log.trace("toResource start");
        log.debug("toResource entity = {}", entity);
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
            resource.setRoleMappings(toRoleMappings(entity.getRoleMappings()));
            resource.setContractTemplatePath(entity.getContractTemplatePath());
            resource.setContractTemplateVersion(entity.getContractTemplateVersion());
            resource.setRoleManagementURL(entity.getRoleManagementURL());
        }
        log.debug("toResource result = {}", resource);
        log.trace("toResource end");
        return resource;
    }

    public static ProductOperations fromDto(CreateProductDto dto) {
        log.trace("fromDto start");
        log.debug("fromDto createProductDto = {}", dto);
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
        log.debug("fromDto result = {}", product);
        log.trace("fromDto end");
        return product;
    }

    public static ProductOperations fromDto(UpdateProductDto dto) {
        log.trace("fromDto start");
        log.debug("fromDto updateProductDto = {}", dto);
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
        log.debug("fromDto result = {}", product);
        log.trace("fromDto end");
        return product;
    }


    public static EnumMap<PartyRole, ProductRoleInfo> toRoleMappings(EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings) {
        EnumMap<PartyRole, ProductRoleInfo> result;
        if (roleMappings == null) {
            result = null;
        } else {
            result = new EnumMap<>(PartyRole.class);
            roleMappings.forEach((key, value) -> result.put(key, new ProductRoleInfo(value)));
        }
        return result;
    }

}
