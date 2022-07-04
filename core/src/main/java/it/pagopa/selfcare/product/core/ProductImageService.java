package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.model.ProductOperations;

import java.io.InputStream;

public interface ProductImageService {

    void saveImage(ProductOperations productToUpdate, InputStream inputStream, String contentType, String fileName);

    String getDefaultImageUrl();

}
