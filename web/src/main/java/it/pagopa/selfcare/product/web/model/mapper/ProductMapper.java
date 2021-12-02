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
            resource.setCreatedAt(entity.getCreatedAt());
            resource.setContractTemplateUpdatedAt(entity.getContractTemplateUpdatedAt());
            resource.setRoleMappings(entity.getRoleMappings());
            resource.setContractTemplatePath(entity.getContractTemplatePath());
            resource.setContractTemplateVersion(entity.getContractTemplateVersion());
            resource.setRoleManagementURL(entity.getRoleManagementURL());
        }

        return resource;
    }

    public static Product fromDto(CreateProductDto dto) {
        Product product = null;
        if (dto != null) {
            product = new Product();
            product.setId(dto.getId());
            product.setLogo(dto.getLogo());
            product.setTitle(dto.getTitle());
            product.setDescription(dto.getDescription());
            product.setUrlPublic(dto.getUrlPublic());
            product.setUrlBO(dto.getUrlBO());
            product.setRoleMappings(dto.getRoleMappings());
            product.setContractTemplatePath(dto.getContractTemplatePath());
            product.setContractTemplateVersion(dto.getContractTemplateVersion());
            product.setRoleManagementURL(dto.getRoleManagementURL());
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
            product.setRoleMappings(dto.getRoleMappings());
            product.setContractTemplatePath(dto.getContractTemplatePath());
            product.setContractTemplateVersion(dto.getContractTemplateVersion());
            product.setRoleManagementURL(dto.getRoleManagementURL());
        }

        return product;
    }

}
