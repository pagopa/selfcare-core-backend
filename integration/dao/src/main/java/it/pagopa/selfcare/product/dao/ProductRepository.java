package it.pagopa.selfcare.product.dao;

import it.pagopa.selfcare.product.dao.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
