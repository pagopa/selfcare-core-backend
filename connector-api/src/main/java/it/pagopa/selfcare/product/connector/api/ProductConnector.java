package it.pagopa.selfcare.product.connector.api;

import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductStatus;

import java.util.List;
import java.util.Optional;

public interface ProductConnector {

    ProductOperations insert(ProductOperations entity);

    ProductOperations save(ProductOperations entity);

    Optional<ProductOperations> findById(String id);

    boolean existsById(String id);

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    boolean existsByIdAndEnabledFalse(String id);

    boolean existsByIdAndStatus(String id, ProductStatus status);

    List<ProductOperations> findAll();

    void deleteById(String id);

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    List<ProductOperations> findByEnabled(boolean enabled);

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    List<ProductOperations> findByParentAndEnabled(String parent, boolean enabled);

    List<ProductOperations> findByParentAndStatusIsNotInactive(String parent);

    List<ProductOperations> findByStatusIsNot(ProductStatus status);

    void disableById(String id);

    void updateProductStatus(String id, ProductStatus status);
}
