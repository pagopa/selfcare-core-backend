package it.pagopa.selfcare.product.connector.api;

import it.pagopa.selfcare.product.connector.exception.FileUploadException;

import java.io.InputStream;

public interface FileStorageConnector {

    void uploadProductLogo(InputStream file, String fileName, String contentType) throws FileUploadException;
}
