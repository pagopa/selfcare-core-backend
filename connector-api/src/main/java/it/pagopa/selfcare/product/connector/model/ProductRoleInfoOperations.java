package it.pagopa.selfcare.product.connector.model;

import java.util.List;

public interface ProductRoleInfoOperations {

    boolean isMultiroleAllowed();

    void setMultiroleAllowed(boolean multiroleAllowed);

    List<? extends ProductRoleOperations> getRoles();

    void setRoles(List<? extends ProductRoleOperations> roles);

}
