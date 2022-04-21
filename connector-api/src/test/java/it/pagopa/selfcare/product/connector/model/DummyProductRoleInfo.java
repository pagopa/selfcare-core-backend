package it.pagopa.selfcare.product.connector.model;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class DummyProductRoleInfo implements ProductRoleInfoOperations {

    private boolean multiroleAllowed;
    private List<DummyProductRole> roles;


    public DummyProductRoleInfo(boolean multiroleAllowed, List<? extends ProductRoleOperations> roles) {
        this.multiroleAllowed = multiroleAllowed;
        setRoles(roles);
    }


    @Override
    public void setRoles(List<? extends ProductRoleOperations> roles) {
        if (roles != null) {
            this.roles = roles.stream()
                    .map(DummyProductRole::new)
                    .collect(Collectors.toList());
        }
    }
}