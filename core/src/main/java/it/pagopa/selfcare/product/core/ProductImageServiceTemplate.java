package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.exception.FileValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Set;

@Slf4j
public abstract class ProductImageServiceTemplate implements ProductImageService {


    private final FileStorageConnector fileStorageConnector;
    private final ProductConnector productConnector;

    @Autowired
    protected ProductImageServiceTemplate(FileStorageConnector fileStorageConnector,
                                          ProductConnector productConnector) {
        this.productConnector = productConnector;
        this.fileStorageConnector = fileStorageConnector;
    }

    @Override
    public void saveImage(ProductOperations productToUpdate, InputStream inputStream, String contentType, String fileName) {
        try {
            validate(contentType, fileName);
        } catch (Exception e) {
            throw new FileValidationException(e.getMessage(), e);
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        try {
            String savedUrl = fileStorageConnector.uploadProductImg(inputStream, getFileName(productToUpdate.getId(), fileExtension), contentType).toString();
            setImageUrl(productToUpdate, savedUrl);
            productConnector.save(productToUpdate);

        } catch (FileUploadException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void validate(String contentType, String fileName) {
        log.trace("validate start");
        log.debug("validate contentType = {}, fileName = {}", contentType, fileName);
        Assert.notNull(fileName, "file name cannot be null");
        Set<String> allowedMimeTypes = getAllowedMimeTypes();
        if (!allowedMimeTypes.contains(contentType)) {
            throw new InvalidMimeTypeException(contentType, String.format("allowed only %s", allowedMimeTypes));
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        Set<String> allowedExtensions = getAllowedExtensions();
        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(String.format("Invalid file extension \"%s\": allowed only %s", fileExtension, allowedExtensions));
        }
        log.trace("validate end");
    }

    protected abstract Set<String> getAllowedMimeTypes();

    protected abstract Set<String> getAllowedExtensions();

    protected abstract String getFileName(String id, String fileExtension);

    protected abstract void setImageUrl(ProductOperations productToUpdate, String url);

}
