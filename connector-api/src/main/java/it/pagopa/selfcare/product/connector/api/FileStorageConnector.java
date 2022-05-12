package it.pagopa.selfcare.product.connector.api;

import it.pagopa.selfcare.product.connector.exception.FileUploadException;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public interface FileStorageConnector {

    URL uploadProductImg(InputStream file, String fileName, String contentType, String operation) throws FileUploadException, MalformedURLException;
}
