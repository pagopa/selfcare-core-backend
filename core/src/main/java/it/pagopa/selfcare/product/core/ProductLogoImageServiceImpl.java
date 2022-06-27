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

@Service
@Slf4j
@Qualifier("productLogoImageService")
public class ProductLogoImageServiceImpl extends ProductImageServiceTemplate {

    private static final String LOGO_PATH_TEMPLATE = "resources/products/%s/logo.%s";
    private final Set<String> allowedProductLogoMimeTypes;
    private final Set<String> allowedProductLogoExtensions;
    private final String defaultLogoUrl;

    @Autowired
    public ProductLogoImageServiceImpl(FileStorageConnector fileStorageConnector,
                                       ProductConnector productConnector,
                                       @Qualifier("logoImageProperties") ImageProperties logoImageProperties) {
        super(fileStorageConnector, productConnector);
        this.allowedProductLogoMimeTypes = logoImageProperties.getAllowedMimeTypes();
        this.allowedProductLogoExtensions = logoImageProperties.getAllowedExtensions();
        this.defaultLogoUrl = logoImageProperties.getDefaultUrl();
    }

    @Override
    protected Set<String> getAllowedMimeTypes() {
        return allowedProductLogoMimeTypes;
    }

    @Override
    protected Set<String> getAllowedExtensions() {
        return allowedProductLogoExtensions;
    }

    @Override
    protected String getFileName(String id, String fileExtension) {
        return String.format(LOGO_PATH_TEMPLATE, id, fileExtension);

    }

    @Override
    protected void setImageUrl(ProductOperations productToUpdate, String url) {
        productToUpdate.setLogo(url);
    }

    @Override
    public String getDefaultImageUrl() {
        return defaultLogoUrl;
    }
}
