package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.dao.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product createProduct(Product product);
}
