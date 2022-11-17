package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    @Deprecated
    List<ProductEntity> findByEnabled(boolean enabled);

    @Deprecated
    List<ProductEntity> findByParentIdAndEnabled(String parentId, boolean enabled);

    List<ProductEntity> findByParentIdAndStatusIsNot(String parentId, ProductStatus status);

    List<ProductEntity> findByStatusIsNot(ProductStatus status);

    @Deprecated
    boolean existsByIdAndEnabledFalse(String id);

    boolean existsByIdAndStatus(String id, ProductStatus status);
}
