package it.pagopa.selfcare.product.web.model.mapper;

import it.pagopa.selfcare.product.connector.model.BackOfficeConfigurations;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.web.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductResourceMapper {

    @Mapping(source = "modifiedBy", target = "modifiedBy", qualifiedByName = "getUserUUID")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "getUserUUID")
    @Mapping(source = "backOfficeEnvironmentConfigurations", target = "backOfficeEnvironmentConfigurations", qualifiedByName = "toBackOfficeConfigurations")
    @Mapping(source = "roleMappings", target = "roleMappings", qualifiedByName = "toRoleMappings")
    ProductResource toResource(ProductOperations entity);

    @Mapping(source = "roleMappings", target = "roleMappings", qualifiedByName = "toRoleMappings")
    ProductDto fromDto(CreateProductDto dto);

    ProductDto fromDto(CreateSubProductDto dto);

    @Mapping(source = "roleMappings", target = "roleMappings", qualifiedByName = "toRoleMappings")
    ProductDto fromDto(UpdateProductDto dto);

    ProductDto fromDto(UpdateSubProductDto dto);

    @Named("toBackOfficeConfigurations")
    static Map<String, BackOfficeConfigurationsResource> toBackOfficeConfigurations(Map<String, ? extends BackOfficeConfigurations> backOfficeConfigurations) {
        Map<String, BackOfficeConfigurationsResource> result;
        if (backOfficeConfigurations == null) {
            result = null;
        } else {
            result = new HashMap<>();
            backOfficeConfigurations.forEach((key, value) -> {
                final BackOfficeConfigurationsResource resource = new BackOfficeConfigurationsResource();
                resource.setUrl(value.getUrl());
                resource.setIdentityTokenAudience(value.getIdentityTokenAudience());
                result.put(key, resource);
            });
        }
        return result;
    }

    @Named("toRoleMappings")
    static EnumMap<PartyRole, ProductRoleInfo> toRoleMappings(EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings) {
        EnumMap<PartyRole, ProductRoleInfo> result;
        if (roleMappings == null) {
            result = null;
        } else {
            result = new EnumMap<>(PartyRole.class);
            roleMappings.forEach((key, value) -> result.put(key, new ProductRoleInfo(value)));
        }
        return result;
    }

    @Named("getUserUUID")
    static UUID getUserUUID(String user) {
        if(StringUtils.hasText(user))
            return UUID.fromString(user);
        return null;
    }

    default List<ProductTreeResource> toTreeResource(List<ProductOperations> model) {
        List<ProductTreeResource> resources = null;
        if (model != null) {
            Map<String, List<ProductOperations>> collect = model.stream()
                    .filter(productOperations -> productOperations.getParentId() != null)
                    .collect(Collectors.groupingBy(ProductOperations::getParentId, Collectors.toList()));
            resources = model.stream()
                    .filter(productOperations -> productOperations.getParentId() == null)
                    .map(productOperations -> {
                        ProductTreeResource productTreeResource = new ProductTreeResource();
                        productTreeResource.setNode(this.toResource(productOperations));
                        if (collect.get(productOperations.getId()) != null) {
                            productTreeResource.setChildren(collect.get(productOperations.getId()).stream()
                                    .map(this::toResource)
                                    .collect(Collectors.toList()));
                        }
                        return productTreeResource;
                    }).collect(Collectors.toList());
        }
        return resources;
    }
}
