package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.model.InstitutionType;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductStatus;

import java.io.InputStream;
import java.util.List;

public interface ProductService {

    List<ProductOperations> getProducts(boolean rootOnly);

    ProductOperations createProduct(ProductOperations product);

    void deleteProduct(String id);

    ProductOperations getProduct(String id, InstitutionType institutionType);

    ProductOperations updateProduct(String id, ProductOperations product);

    void updateProductStatus(String id, ProductStatus status);

    void saveProductLogo(String logoId, InputStream logo, String contentType, String fileName);

    void saveProductDepictImage(String id, InputStream depictImage, String contentType, String fileName);
}
