package it.pagopa.selfcare.product.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = "code")
@NoArgsConstructor
@AllArgsConstructor
public class DummyProductRole implements ProductRoleOperations {

    private String code;
    private String label;
    private String description;


    public DummyProductRole(ProductRoleOperations productRoleOperations) {
        code = productRoleOperations.getCode();
        label = productRoleOperations.getLabel();
        description = productRoleOperations.getDescription();
    }

}