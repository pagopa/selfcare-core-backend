package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    List<ProductEntity> findByEnabled(boolean enabled);

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    List<ProductEntity> findByParentIdAndEnabled(String parentId, boolean enabled);

    List<ProductEntity> findByParentIdAndStatusIsNot(String parentId, ProductStatus status);

    List<ProductEntity> findByStatusIsNot(ProductStatus status);

    /**
     * @deprecated method has been deprecated because a new method has been implemented.
     * Remove the query from the repository
     */
    @Deprecated(forRemoval = true)
    boolean existsByIdAndEnabledFalse(String id);

    boolean existsByIdAndStatus(String id, ProductStatus status);
}
