package it.pagopa.selfcare.product.connector.model;

import it.pagopa.selfcare.commons.base.utils.InstitutionType;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

public interface ProductOperations {

    String getId();

    void setId(String id);

    String getLogo();

    void setLogo(String logo);

    String getLogoBgColor();

    void setLogoBgColor(String logoBgColor);

    String getDepictImageUrl();

    void setDepictImageUrl(String depictImageUrl);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    String getUrlPublic();

    void setUrlPublic(String urlPublic);

    String getUrlBO();

    void setUrlBO(String urlBO);

    Instant getCreatedAt();

    void setCreatedAt(Instant createdAt);

    Instant getModifiedAt();

    void setModifiedAt(Instant modifiedAt);

    String getCreatedBy();

    void setCreatedBy(String createdBy);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);

    EnumMap<PartyRole, ? extends ProductRoleInfoOperations> getRoleMappings();

    void setRoleMappings(EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings);

    Instant getContractTemplateUpdatedAt();

    void setContractTemplateUpdatedAt(Instant contractTemplateUpdatedAt);

    String getContractTemplatePath();

    void setContractTemplatePath(String contractTemplatePath);

    String getContractTemplateVersion();

    void setContractTemplateVersion(String contractTemplateVersion);

    Map<InstitutionType, ? extends ContractOperations> getInstitutionContractMappings();

    void setInstitutionContractMappings(Map<InstitutionType, ? extends ContractOperations> contractMap);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    ProductStatus getStatus();

    void setStatus(ProductStatus status);

    void setParentId(String parentId);

    String getParentId();

    void setIdentityTokenAudience(String identityTokenAudience);

    String getIdentityTokenAudience();

    Map<String, ? extends BackOfficeConfigurations> getBackOfficeEnvironmentConfigurations();

    void setBackOfficeEnvironmentConfigurations(Map<String, ? extends BackOfficeConfigurations> backOfficeEnvironmentConfigurations);

    String getRoleManagementURL();

    void setRoleManagementURL(String roleManagementURL);
    boolean isDelegable();

    void setDelegable(boolean delegable);

    ProductOperations getProductOperations();

    void setProductOperations(ProductOperations productOperations);
}
