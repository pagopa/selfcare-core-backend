package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductRoleInfoOperations;
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

import javax.validation.ValidationException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
class ProductServiceImpl implements ProductService {

    private static final String LOGO_PATH_TEMPLATE = "resources/products/%s/logo.%s";
    private static final String DEPICT_IMG_PATH_TEMPLATE = "resources/products/%s/depict-image.%s";
    private static final String REQUIRED_PRODUCT_ID_MESSAGE = "A product id is required";
    private static final String SAVE_LOGO = "logo";
    private static final String SAVE_DEPICT_IMG = "depict";

    private final ProductConnector productConnector;
    private final FileStorageConnector fileStorageConnector;
    private final Set<String> allowedProductImgMimeTypes;
    private final Set<String> allowedProductImgExtensions;
    private final String defaultLogoUrl;
    private final String defaultDepictImageUrl;

    @Autowired
    public ProductServiceImpl(ProductConnector productConnector,
                              FileStorageConnector fileStorageConnector,
                              @Value("${product.img.allowed-mime-types}") String[] allowedProductImgMimeTypes,
                              @Value("${product.img.allowed-extensions}") String[] allowedProductImgExtensions,
                              @Value("${product.img.default-logo-url}") String defaultLogoUrl,
                              @Value("${product.img.default-depict-image-url}") String defaultDepictImageUrl) {
        this.productConnector = productConnector;
        this.fileStorageConnector = fileStorageConnector;
        this.allowedProductImgMimeTypes = Set.of(allowedProductImgMimeTypes);
        this.allowedProductImgExtensions = Set.of(allowedProductImgExtensions);
        this.defaultLogoUrl = defaultLogoUrl;
        this.defaultDepictImageUrl = defaultDepictImageUrl;
    }

    @Override
    public List<ProductOperations> getProducts(boolean rootOnly) {
        log.trace("getProducts start");
        List<ProductOperations> products;
        if (rootOnly) {
            products = productConnector.findByParentAndEnabled(null, true);
        } else {
            products = productConnector.findByEnabled(true);
        }
        log.debug("getProducts result = {}", products);
        log.trace("getProducts end");
        return products;
    }

    @Override
    public ProductOperations createProduct(ProductOperations product) {
        log.trace("createProduct start");
        log.debug("createProduct product = {}", product);
        Assert.notNull(product, "A product is required");
        if (product.getParentId() == null) {
            validateRoleMappings(product.getRoleMappings());
            product.setLogo(defaultLogoUrl);
            product.setDepictImageUrl(defaultDepictImageUrl);
        } else if (!productConnector.existsById(product.getParentId())) {
            throw new ValidationException("Parent not found", new ResourceNotFoundException("For id = " + product.getParentId()));
        }
        OffsetDateTime now = OffsetDateTime.now();
        product.setCreatedAt(now);
        product.setContractTemplateUpdatedAt(now);
        ProductOperations insert = productConnector.insert(product);
        log.debug("createProduct result = {}", insert);
        log.trace("createProduct end");
        return insert;
    }

    private void validateRoleMappings(EnumMap<PartyRole, ? extends ProductRoleInfoOperations> roleMappings) {
        log.trace("validateRoleMappings start");
        log.debug("validateRoleMappings roleMappings = {}", roleMappings);
        Assert.notEmpty(roleMappings, "A product role mappings is required");
        roleMappings.forEach((partyRole, productRoleInfo) -> {
            Assert.notNull(productRoleInfo, "A product role info is required");
            Assert.notEmpty(productRoleInfo.getRoles(), "At least one Product role are required");
            if (productRoleInfo.getRoles().size() > 1 && !PartyRole.OPERATOR.equals(partyRole)) {
                throw new InvalidRoleMappingException(String.format("Only '%s' Party-role can have more than one Product-role", PartyRole.OPERATOR.name()),
                        new IllegalArgumentException(String.format("partyRole = %s => productRoleInfo = %s", partyRole, productRoleInfo)));
            }
        });
        log.trace("validateRoleMappings end");
    }


    @Override
    public void deleteProduct(String id) {
        log.trace("deleteProduct start");
        log.debug("deleteProduct id = {}", id);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (foundProduct.isEnabled()) {
            foundProduct.setEnabled(false);
            productConnector.save(foundProduct);
        }
        log.trace("deleteProduct end");
    }


    @Override
    public ProductOperations getProduct(String id) {
        log.trace("getProduct start");
        log.debug("getProduct id = {}", id);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        log.debug("getProduct result = {}", foundProduct);
        log.trace("getProduct end");

        return foundProduct;
    }


    @Override
    public ProductOperations updateProduct(String id, ProductOperations product) {
        log.trace("updateProduct start");
        log.debug("updateProduct id = {}, product = {}", id, product);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        Assert.notNull(product, "A product is required");
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        if (foundProduct.getParentId() == null) {
            validateRoleMappings(product.getRoleMappings());
        }
        foundProduct.setTitle(product.getTitle());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setUrlPublic(product.getUrlPublic());
        foundProduct.setUrlBO(product.getUrlBO());
        foundProduct.setRoleMappings(product.getRoleMappings());
        foundProduct.setIdentityTokenAudience(product.getIdentityTokenAudience());
        foundProduct.setRoleManagementURL(product.getRoleManagementURL());
        foundProduct.setContractTemplatePath(product.getContractTemplatePath());
        if (!product.getContractTemplateVersion().equals(foundProduct.getContractTemplateVersion())) {
            foundProduct.setContractTemplateUpdatedAt(OffsetDateTime.now());
        }
        foundProduct.setContractTemplateVersion(product.getContractTemplateVersion());

        ProductOperations updatedProduct = productConnector.save(foundProduct);
        log.debug("updateProduct result = {}", updatedProduct);
        log.trace("updateProduct end");
        return updatedProduct;
    }

    @Override
    public void saveProductLogo(String id, InputStream logo, String contentType, String fileName) {
        log.trace("saveProductLogo start");
        log.debug("saveProductLogo id = {}, logo = {}, contentType = {}, fileName = {}", id, logo, contentType, fileName);
        saveImg(id, logo, contentType, fileName, SAVE_LOGO);
        log.trace("saveProductLogo end");
    }

    @Override
    public void saveProductDepictImage(String id, InputStream depictImage, String contentType, String fileName) {
        log.trace("saveProductDepictImage start");
        log.debug("saveProductDepictImage id = {}, logo = {}, contentType = {}, fileName = {}", id, depictImage, contentType, fileName);
        saveImg(id, depictImage, contentType, fileName, SAVE_DEPICT_IMG);
        log.trace("saveProductDepictImage end");
    }

    private void saveImg(String id, InputStream image, String contentType, String fileName, String operation) {
        log.trace("saveImg start");
        log.debug("saveImg id = {}, image = {}, contentType = {}, fileName = {}, operation = {}", id, image, contentType, fileName, operation);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        ProductOperations productToUpdate = getProduct(id);
        if (productToUpdate.getParentId() != null) {
            throw new ValidationException("Given product Id = " + id + " is of a subProduct");
        }
        try {
            validate(contentType, fileName);
        } catch (Exception e) {
            throw new FileValidationException(e.getMessage(), e);
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        URL savedUrl;
        String stringUrl = null;
        try {
            switch (operation) {
                case SAVE_LOGO:
                    savedUrl = fileStorageConnector.uploadProductImg(image, String.format(LOGO_PATH_TEMPLATE, id, fileExtension), contentType, "logo");
                    stringUrl = savedUrl.toString();
                    if (productToUpdate.getLogo() == null || !productToUpdate.getLogo().equals(stringUrl)) {
                        productToUpdate.setLogo(stringUrl);
                        productConnector.save(productToUpdate);
                    }
                    break;
                case SAVE_DEPICT_IMG:
                    savedUrl = fileStorageConnector.uploadProductImg(image, String.format(DEPICT_IMG_PATH_TEMPLATE, id, fileExtension), contentType, "depict-image");
                    stringUrl = savedUrl.toString();
                    if (productToUpdate.getDepictImageUrl() == null || !productToUpdate.getDepictImageUrl().equals(stringUrl)) {
                        productToUpdate.setDepictImageUrl(stringUrl);
                        productConnector.save(productToUpdate);
                    }
                    break;
                default:
                    break;
            }

        } catch (FileUploadException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        log.trace("saveImg end");
    }

    private void validate(String contentType, String fileName) {
        log.trace("validate start");
        log.debug("validate contentType = {}, fileName = {}", contentType, fileName);
        Assert.notNull(fileName, "file name cannot be null");

        if (!allowedProductImgMimeTypes.contains(contentType)) {
            throw new InvalidMimeTypeException(contentType, String.format("allowed only %s", allowedProductImgMimeTypes));
        }

        String fileExtension = StringUtils.getFilenameExtension(fileName);
        if (!allowedProductImgExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException(String.format("Invalid file extension \"%s\": allowed only %s", fileExtension, allowedProductImgExtensions));
        }
        log.trace("validate end");
    }
}
