package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.ProductRoleOperations;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "code")
public class ProductRole implements ProductRoleOperations {

    @ApiModelProperty(value = "${swagger.product-role.model.code}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String code;

    @ApiModelProperty(value = "${swagger.product-role.model.label}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String label;

    @ApiModelProperty(value = "${swagger.product-role.model.description}", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String description;


    public ProductRole(ProductRoleOperations productRoleOperations) {
        code = productRoleOperations.getCode();
        label = productRoleOperations.getLabel();
        description = productRoleOperations.getDescription();
    }


}