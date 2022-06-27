package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.EnumMap;

@Data
public class DummyProduct implements ProductOperations {

    private String id;
    private String logo;
    private String depictImageUrl;
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
    private String identityTokenAudience;
    private boolean enabled = true;
    private String parentId;

}