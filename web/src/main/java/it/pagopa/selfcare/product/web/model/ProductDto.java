package it.pagopa.selfcare.product.web.model;


import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.EnumMap;

@Data
public class ProductDto implements ProductOperations {

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
    private String parent;

}
