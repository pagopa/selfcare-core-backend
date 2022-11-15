package it.pagopa.selfcare.product.connector.dao.model;


import it.pagopa.selfcare.product.connector.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("products")
@FieldNameConstants(onlyExplicitlyIncluded = true)
public class ProductEntity implements ProductOperations, Persistable<String> {

    @Id
    private String id;
    private String logo;
    private String depictImageUrl;
    private String title;
    private String logoBgColor;
    private String description;
    private String urlPublic;
    private String urlBO;
    @CreatedDate
    private Instant createdAt;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    @FieldNameConstants.Include
    private Instant modifiedAt;
    @LastModifiedBy
    @FieldNameConstants.Include
    private String modifiedBy;
    private Instant contractTemplateUpdatedAt;
    private EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    @FieldNameConstants.Include
    private boolean enabled = true;
    @FieldNameConstants.Include
    private ProductStatus status;
    private String parentId;
    private String identityTokenAudience;
    private Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations;
    @Transient
    private boolean isNew = true;


    @Data
    public static class ProductRoleInfo implements ProductRoleInfoOperations {
        private boolean multiroleAllowed;
        private List<? extends ProductRoleOperations> roles;
    }


    @Data
    @EqualsAndHashCode(of = "code")
    public static class ProductRole implements ProductRoleOperations {
        private String code;
        private String label;
        private String description;
    }


    @Data
    public static class EntityBackOfficeConfigurations implements BackOfficeConfigurations {
        private String url;
        private String identityTokenAudience;
    }


    public ProductEntity(ProductOperations product) {
        this();
        id = product.getId();
        logo = product.getLogo();
        logoBgColor = product.getLogoBgColor();
        depictImageUrl = product.getDepictImageUrl();
        title = product.getTitle();
        description = product.getDescription();
        urlPublic = product.getUrlPublic();
        urlBO = product.getUrlBO();
        createdAt = product.getCreatedAt();
        createdBy = product.getCreatedBy();
        modifiedAt = product.getModifiedAt();
        modifiedBy = product.getModifiedBy();
        contractTemplateUpdatedAt = product.getContractTemplateUpdatedAt();
        roleMappings = product.getRoleMappings();
        contractTemplatePath = product.getContractTemplatePath();
        contractTemplateVersion = product.getContractTemplateVersion();
        enabled = product.isEnabled();
        status = product.getStatus();
        parentId = product.getParentId();
        identityTokenAudience = product.getIdentityTokenAudience();
        backOfficeEnvironmentConfigurations = product.getBackOfficeEnvironmentConfigurations();
    }


    public static class Fields {
        public static String id = org.springframework.data.mongodb.core.aggregation.Fields.UNDERSCORE_ID;
    }

}
