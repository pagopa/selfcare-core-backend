package it.pagopa.selfcare.product.web.model;


import it.pagopa.selfcare.product.connector.model.*;
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
    private EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings;
    private String roleManagementURL;
    private Instant contractTemplateUpdatedAt;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    private Map<InstitutionType, ? extends ContractOperations> institutionContractMappings;
    private boolean enabled = true;
    private boolean delegable;
    private ProductStatus status = ProductStatus.TESTING;
    private String parentId;
    private String identityTokenAudience;
    private Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations;

}
