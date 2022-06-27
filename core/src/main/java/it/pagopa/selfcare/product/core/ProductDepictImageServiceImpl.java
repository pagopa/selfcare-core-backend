package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.config.ImageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@Qualifier("productDepictImageService")
public class ProductDepictImageServiceImpl extends ProductImageServiceTemplate {

    private static final String DEPICT_IMG_PATH_TEMPLATE = "resources/products/%s/depict-image.%s";
    private final Set<String> allowedProductDepictImageMimeTypes;
    private final Set<String> allowedProductDepictImageExtensions;
    private final String defaultImageUrl;

    @Autowired
    public ProductDepictImageServiceImpl(FileStorageConnector fileStorageConnector,
                                         ProductConnector productConnector,
                                         @Qualifier("depictImageProperties") ImageProperties depictImageProperties) {
        super(fileStorageConnector, productConnector);
        this.allowedProductDepictImageMimeTypes = depictImageProperties.getAllowedMimeTypes();
        this.allowedProductDepictImageExtensions = depictImageProperties.getAllowedExtensions();
        this.defaultImageUrl = depictImageProperties.getDefaultUrl();
    }

    @Override
    protected Set<String> getAllowedMimeTypes() {
        return allowedProductDepictImageMimeTypes;
    }

    @Override
    protected Set<String> getAllowedExtensions() {
        return allowedProductDepictImageExtensions;
    }

    @Override
    protected String getFileName(String id, String fileExtension) {
        return String.format(DEPICT_IMG_PATH_TEMPLATE, id, fileExtension);
    }

    @Override
    protected void setImageUrl(ProductOperations productToUpdate, String url) {
        productToUpdate.setDepictImageUrl(url);
    }

    @Override
    public String getDefaultImageUrl() {
        return defaultImageUrl;
    }
}
