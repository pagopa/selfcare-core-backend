package it.pagopa.selfcare.product.connector.dao.model;


import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleOperations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Document("products")
public class ProductEntity implements ProductOperations {

    public ProductEntity(ProductOperations product) {
        this();
        id = product.getId();
        logo = product.getLogo();
        title = product.getTitle();
        description = product.getDescription();
        urlPublic = product.getUrlPublic();
        urlBO = product.getUrlBO();
        createdAt = product.getCreatedAt();
        contractTemplateUpdatedAt = product.getContractTemplateUpdatedAt();
        roleMappings = product.getRoleMappings();
        contractTemplatePath = product.getContractTemplatePath();
        contractTemplateVersion = product.getContractTemplateVersion();
        roleManagementURL = product.getRoleManagementURL();
        enabled = product.isEnabled();
    }

    @Id
    private String id;
    private String logo;
    private String title;
    private String description;
    private String urlPublic;
    private String urlBO;
    private OffsetDateTime createdAt;
    private OffsetDateTime contractTemplateUpdatedAt;
    private EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    private String roleManagementURL;
    private boolean enabled = true;


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

}
