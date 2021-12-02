package it.pagopa.selfcare.product.connector.model;

import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;

public interface ProductOperations {

    String getId();

    void setId(String id);

    String getLogo();

    void setLogo(String logo);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    String getUrlPublic();

    void setUrlPublic(String urlPublic);

    String getUrlBO();

    void setUrlBO(String urlBO);

    OffsetDateTime getCreatedAt();

    void setCreatedAt(OffsetDateTime createdAt);

    OffsetDateTime getContractTemplateUpdatedAt();

    void setContractTemplateUpdatedAt(OffsetDateTime contractTemplateUpdatedAt);

    EnumMap<PartyRole, List<String>> getRoleMappings();

    void setRoleMappings(EnumMap<PartyRole, List<String>> roleMappings);

    String getContractTemplatePath();

    void setContractTemplatePath(String contractTemplatePath);

    String getContractTemplateVersion();

    void setContractTemplateVersion(String contractTemplateVersion);

    String getRoleManagementURL();

    void setRoleManagementURL(String roleManagementURL);

    boolean isEnabled();

    void setEnabled(boolean enabled);

}
