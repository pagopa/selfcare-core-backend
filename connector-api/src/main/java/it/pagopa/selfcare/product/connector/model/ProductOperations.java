package it.pagopa.selfcare.product.connector.model;

import java.time.Instant;
import java.util.EnumMap;

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

    Instant getContractTemplateUpdatedAt();

    void setContractTemplateUpdatedAt(Instant contractTemplateUpdatedAt);

    EnumMap<PartyRole, ? extends ProductRoleInfoOperations> getRoleMappings();

    void setRoleMappings(EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings);

    String getContractTemplatePath();

    void setContractTemplatePath(String contractTemplatePath);

    String getContractTemplateVersion();

    void setContractTemplateVersion(String contractTemplateVersion);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void setParentId(String parentId);

    String getParentId();

    void setIdentityTokenAudience(String identityTokenAudience);

    String getIdentityTokenAudience();

}
