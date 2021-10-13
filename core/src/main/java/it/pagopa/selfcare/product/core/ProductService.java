package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.dao.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getProducts();

    Product createProduct(Product product);

    void deleteProducts();

    void deleteProduct(String id);

    Optional<Product> getProduct(String id);

    Product updateProduct(String id, Product product);
}
