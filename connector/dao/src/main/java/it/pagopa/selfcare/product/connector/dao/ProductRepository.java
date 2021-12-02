package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    List<ProductEntity> findByEnabled(boolean enabled);

}
