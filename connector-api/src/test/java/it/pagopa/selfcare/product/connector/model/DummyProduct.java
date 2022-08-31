package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.time.Instant;
import java.util.EnumMap;

@Data
public class DummyProduct implements ProductOperations {

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
    private String identityTokenAudience;
    private boolean enabled = true;
    private String parentId;

}