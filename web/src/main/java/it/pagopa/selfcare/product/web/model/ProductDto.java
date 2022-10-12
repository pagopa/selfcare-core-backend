package it.pagopa.selfcare.product.web.model;


import it.pagopa.selfcare.product.connector.model.BackOfficeConfigurations;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import lombok.Data;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

@Data
public class ProductDto implements ProductOperations {

    private String id;
    private String logo;
    private String logoBgColor;
    private String depictImageUrl;
    private String title;
    private String description;
    private String urlPublic;
    private String urlBO;
    private Instant createdAt;
    private String createdBy;
    private Instant modifiedAt;
    private String modifiedBy;
    private Instant contractTemplateUpdatedAt;
    private EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    private boolean enabled = true;
    private String parentId;
    private String identityTokenAudience;
    private Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations;

}
