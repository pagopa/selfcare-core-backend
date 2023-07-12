package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

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
    private ProductStatus status;
    private EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings;
    private Instant contractTemplateUpdatedAt;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    private Map<InstitutionType, ? extends ContractOperations> institutionContractMappings;
    private String identityTokenAudience;
    private boolean enabled = true;
    private boolean delegable;
    private String parentId;
    private Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations;
    private String roleManagementURL;

}