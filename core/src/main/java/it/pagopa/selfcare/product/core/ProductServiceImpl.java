package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.exception.FileValidationException;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
class ProductServiceImpl implements ProductService {
    public static final String LOGO_PATH_TEMPLATE = "resources/products/%s/logo.%s";
    private final ProductConnector productConnector;

    private final FileStorageConnector fileStorageConnector;

    private final Set<String> allowedProductLogoMimeTypes;
    private final Set<String> allowedProductLogoExtensions;
    private final String defaultUrl;

    @Autowired
    public ProductServiceImpl(ProductConnector productConnector,
                              FileStorageConnector fileStorageConnector,
                              @Value("${product.logo.allowed-mime-types}") String[] allowedProductLogoMimeTypes,
                              @Value("${product.logo.allowed-extensions}") String[] allowedProductLogoExtensions,
                              @Value("${product.logo.default-url}") String defaultUrl) {
        this.productConnector = productConnector;
        this.fileStorageConnector = fileStorageConnector;
        this.allowedProductLogoMimeTypes = Set.of(allowedProductLogoMimeTypes);
        this.allowedProductLogoExtensions = Set.of(allowedProductLogoExtensions);
        this.defaultUrl = defaultUrl;
    }

    @Override
    public List<ProductOperations> getProducts() {
        log.trace("ProductServiceImpl.getProducts");
        return productConnector.findByEnabled(true);
    }

    @Override
    public ProductOperations createProduct(ProductOperations product) {
        log.trace("ProductServiceImpl.createProduct");
        log.debug("product = {}", product);
        validateRoleMappings(product);
        OffsetDateTime now = OffsetDateTime.now();
        product.setCreatedAt(now);
        product.setLogo(defaultUrl);
        product.setContractTemplateUpdatedAt(now);
        log.trace("ProductServiceImpl.createProduct end");
        return productConnector.insert(product);
    }

    private void validateRoleMappings(ProductOperations product) {
        log.trace("ProductServiceImpl.validateRoleMappings start");
        log.debug("product = %s%n", product);
        product.getRoleMappings().forEach((partyRole, productRoles) -> {
            if (productRoles == null
                    || productRoles.isEmpty()
                    || (productRoles.size() > 1 && !PartyRole.OPERATOR.equals(partyRole))) {
                throw new InvalidRoleMappingException(String.format("ProductOperations roles cannot be null nor empty and only '%s' Party role can have more than one ProductOperations role", PartyRole.OPERATOR.name()),
                        new IllegalArgumentException(String.format("partyRole = %s => productRoles = %s", partyRole, productRoles)));
            }
        });
        log.trace("ProductServiceImpl.validateRoleMappings end");
    }

    @Override
    public void deleteProduct(String id) {
        log.trace("ProductServiceImpl.deleteProduct start");
        log.debug("id = {}}", id);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (foundProduct.isEnabled()) {
            foundProduct.setEnabled(false);
            productConnector.save(foundProduct);
        }

        log.trace("ProductServiceImpl.deleteProduct end");

    }


    @Override
    public ProductOperations getProduct(String id) {
        log.trace("ProductServiceImpl.getProduct start");
        log.debug("id = {}", id);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        log.debug("result = {}", foundProduct);
        log.trace("ProductServiceImpl.getProduct end");

        return foundProduct;
    }


    @Override
    public ProductOperations updateProduct(String id, ProductOperations product) {
        log.trace("updateProduct start");
        log.debug("id = {}, product = {}", id, product);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        validateRoleMappings(product);
        foundProduct.setLogo(product.getLogo());
        foundProduct.setTitle(product.getTitle());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setUrlPublic(product.getUrlPublic());
        foundProduct.setUrlBO(product.getUrlBO());
        foundProduct.setRoleMappings(product.getRoleMappings());
        foundProduct.setRoleManagementURL(product.getRoleManagementURL());
        foundProduct.setContractTemplatePath(product.getContractTemplatePath());
        if (!product.getContractTemplateVersion().equals(foundProduct.getContractTemplateVersion())) {
            foundProduct.setContractTemplateUpdatedAt(OffsetDateTime.now());
        }
        foundProduct.setContractTemplateVersion(product.getContractTemplateVersion());
        log.trace("updateProduct end");
        return productConnector.save(foundProduct);
    }

    @Override
    public void saveProductLogo(String id, InputStream logo, String contentType, String fileName) {
        log.trace("saveProductLogo start");
        log.debug("id = {}, logo = {}, contentType = {}, fileName = {}", id, logo, contentType, fileName);
        ProductOperations productToUpdate = getProduct(id);
        URL savedUrl = null;
        try {
            validate(contentType, fileName);

        } catch (Exception e) {
            throw new FileValidationException(e.getMessage(), e);
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        try {
            savedUrl = fileStorageConnector.uploadProductLogo(logo, String.format(LOGO_PATH_TEMPLATE, id, fileExtension), contentType);

        } catch (FileUploadException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        String stringUrl = savedUrl.toString();
        if (productToUpdate.getLogo() == null || !productToUpdate.getLogo().equals(stringUrl)) {

            productToUpdate.setLogo(stringUrl);
            productConnector.save(productToUpdate);
        }
        log.debug("updatedProduct = {}", productToUpdate);
        log.trace("saveProductLogo end");
    }

    private void validate(String contentType, String fileName) {
        Assert.notNull(fileName, "file name cannot be null");

        if (!allowedProductLogoMimeTypes.contains(contentType)) {
            throw new InvalidMimeTypeException(contentType, String.format("allowed only %s", allowedProductLogoMimeTypes));
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        if (!allowedProductLogoExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(String.format("Invalid file extension \"%s\": allowed only %s", fileExtension, allowedProductLogoExtensions));
        }
    }
}
