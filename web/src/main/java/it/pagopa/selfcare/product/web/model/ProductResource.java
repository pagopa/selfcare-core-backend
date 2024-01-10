package it.pagopa.selfcare.product.web.model;

import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductResource {

    @ApiModelProperty(value = "${swagger.product.model.id}", required = true)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "${swagger.product.model.title}", required = true)
    @NotBlank
    private String title;

    @ApiModelProperty(value = "${swagger.product.model.description}", required = true)
    @NotBlank
    private String description;

    @ApiModelProperty(value = "${swagger.product.model.logoBgColor}", example = "#000000")
    private String logoBgColor;

    @ApiModelProperty(value = "${swagger.product.model.identityTokenAudience}")
    private String identityTokenAudience;

    @ApiModelProperty(value = "${swagger.product.model.urlPublic}")
    private String urlPublic;

    @ApiModelProperty(value = "${swagger.product.model.urlBO}", required = true)
    @NotBlank
    private String urlBO;

    @ApiModelProperty(value = "${swagger.product.model.roleMappings}", required = true)
    @NotEmpty
    private EnumMap<PartyRole, ProductRoleInfo> roleMappings;

    @ApiModelProperty(value = "${swagger.product.model.status}", required = true)
    @NotNull
    private ProductStatus status;

    @ApiModelProperty(value = "${swagger.product.model.delegable}")
    private boolean delegable;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplatePath}", required = true)
    @NotBlank
    private String contractTemplatePath;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateVersion}", required = true)
    @NotBlank
    private String contractTemplateVersion;

    @ApiModelProperty(value = "${swagger.product.model.contractTemplateUpdateDateTime}")
    private Instant contractTemplateUpdatedAt;

    @ApiModelProperty(value = "${swagger.product.model.logo}")
    private String logo;

    @ApiModelProperty(value = "${swagger.product.model.depictImageUrl}")
    private String depictImageUrl;

    @ApiModelProperty(value = "${swagger.product.model.createdAt}")
    private Instant createdAt;

    @ApiModelProperty(value = "${swagger.product.model.createdBy}")
    private UUID createdBy;

    @ApiModelProperty(value = "${swagger.product.model.modifiedAt}")
    private Instant modifiedAt;

    @ApiModelProperty(value = "${swagger.product.model.modifiedBy}")
    private UUID modifiedBy;

    @ApiModelProperty(value = "${swagger.product.model.parentProduct}")
    private String parentId;

    @ApiModelProperty(value = "${swagger.product.model.backOfficeEnvironmentConfigurations}")
    private Map<String, BackOfficeConfigurationsResource> backOfficeEnvironmentConfigurations;

    @ApiModelProperty(value = "${swagger.product.model.roleManagementURL}")
    private String roleManagementURL;

    private ProductOperations productOperations;

    @ApiModelProperty(value = "${swagger.product.model.invoiceable}")
    private boolean invoiceable;

}
