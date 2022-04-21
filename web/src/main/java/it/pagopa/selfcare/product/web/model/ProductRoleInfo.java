package it.pagopa.selfcare.product.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleOperations;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductRoleInfo implements ProductRoleInfoOperations {

    @ApiModelProperty(value = "${swagger.product-role-info.model.multiroleAllowed}", required = true)
    @JsonProperty(required = true)
    @NotNull
    private Boolean multiroleAllowed;

    @ApiModelProperty(value = "${swagger.product-role-info.model.roles}", required = true)
    @JsonProperty(required = true)
    @NotEmpty
    @Valid
    private List<ProductRole> roles;


    public ProductRoleInfo(ProductRoleInfoOperations productRoleInfoOperations) {
        multiroleAllowed = productRoleInfoOperations.isMultiroleAllowed();
        setRoles(productRoleInfoOperations.getRoles());
    }


    @Override
    public void setRoles(List<? extends ProductRoleOperations> roles) {
        if (roles != null) {
            this.roles = roles.stream()
                    .map(ProductRole::new)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean isMultiroleAllowed() {
        return multiroleAllowed != null && multiroleAllowed;
    }

    @Override
    public void setMultiroleAllowed(boolean multiroleAllowed) {
        this.multiroleAllowed = multiroleAllowed;
    }

}
