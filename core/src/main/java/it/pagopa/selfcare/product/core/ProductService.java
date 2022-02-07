package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.model.ProductOperations;

import java.io.InputStream;
import java.util.List;

public interface ProductService {

    List<ProductOperations> getProducts();

    ProductOperations createProduct(ProductOperations product);

    void deleteProduct(String id);

    ProductOperations getProduct(String id);

    ProductOperations updateProduct(String id, ProductOperations product);

    void saveProductLogo(String logoId, InputStream logo, String contentType, String fileName);
}
