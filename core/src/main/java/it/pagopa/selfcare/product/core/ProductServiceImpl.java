package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.connector.model.*;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.ValidationException;
import java.io.InputStream;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;

@Slf4j
@Service
class ProductServiceImpl implements ProductService {

    protected static final String REQUIRED_PRODUCT_ID_MESSAGE = "A product id is required";
    protected static final String REQUIRED_PRODUCT_STATUS_MESSAGE = "A product status is required";
    protected static final String REQUIRED_INSTITUTION_TYPE = "An institutionType is required";


    private final ProductConnector productConnector;
    private final ProductImageService productLogoImageService;
    private final ProductImageService productDepictImageService;

    @Autowired
    public ProductServiceImpl(ProductConnector productConnector,
                              @Qualifier("productLogoImageService") ProductImageService productLogoImageService,
                              @Qualifier("productDepictImageService") ProductImageService productDepictImageService) {
        this.productConnector = productConnector;
        this.productLogoImageService = productLogoImageService;
        this.productDepictImageService = productDepictImageService;
    }

    @Override
    public List<ProductOperations> getProducts(boolean rootOnly) {
        log.trace("getProducts start");
        List<ProductOperations> products;
        if (rootOnly) {
            products = productConnector.findByParentAndStatusIsNotInactive(null);
        } else {
            products = productConnector.findByStatusIsNot(ProductStatus.INACTIVE);
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
            product.setLogo(productLogoImageService.getDefaultImageUrl());
            product.setDepictImageUrl(productDepictImageService.getDefaultImageUrl());
        } else if (!productConnector.existsById(product.getParentId())) {
            throw new ValidationException("Parent not found", new ResourceNotFoundException("For id = " + product.getParentId()));
        }
        product.setContractTemplateUpdatedAt(Instant.now());
        product.getInstitutionContractMappings().forEach((key, value) -> value.setContractTemplateUpdatedAt(Instant.now()));
        ProductOperations insert;
        try {
            insert = productConnector.insert(product);
        } catch (ResourceAlreadyExistsException e) {
            if (productConnector.existsByIdAndStatus(product.getId(), ProductStatus.INACTIVE)) {
                insert = productConnector.save(product);
            } else {
                throw new ResourceAlreadyExistsException(String.format("Product %s already exists and is still active", product.getId()), e);
            }
        }
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
        productConnector.disableById(id);
        log.trace("deleteProduct end");
    }


    @Override
    public ProductOperations getProduct(String id, InstitutionType institutionType) {
        log.trace("getProduct start");
        log.debug("getProduct id = {}", id);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (foundProduct.getStatus() == ProductStatus.INACTIVE) {
            throw new ResourceNotFoundException();
        }
        if (institutionType != null && foundProduct.getInstitutionContractMappings().containsKey(institutionType)) {
            foundProduct.setContractTemplatePath(foundProduct.getInstitutionContractMappings().get(institutionType).getContractTemplatePath());
            foundProduct.setContractTemplateVersion(foundProduct.getInstitutionContractMappings().get(institutionType).getContractTemplateVersion());
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
        if (foundProduct.getStatus() == ProductStatus.INACTIVE) {
            throw new ResourceNotFoundException();
        }
        if (foundProduct.getParentId() == null) {
            validateRoleMappings(product.getRoleMappings());
        }
        foundProduct.setTitle(product.getTitle());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setLogoBgColor(product.getLogoBgColor());
        foundProduct.setUrlPublic(product.getUrlPublic());
        foundProduct.setUrlBO(product.getUrlBO());
        foundProduct.setRoleMappings(product.getRoleMappings());
        foundProduct.setIdentityTokenAudience(product.getIdentityTokenAudience());
        foundProduct.setBackOfficeEnvironmentConfigurations(product.getBackOfficeEnvironmentConfigurations());
        foundProduct.setContractTemplatePath(product.getContractTemplatePath());
        if (!product.getContractTemplateVersion().equals(foundProduct.getContractTemplateVersion())) {
            foundProduct.setContractTemplateUpdatedAt(Instant.now());
        }
        foundProduct.setContractTemplateVersion(product.getContractTemplateVersion());
        foundProduct.getInstitutionContractMappings().forEach((key, value) -> {
            value.setContractTemplatePath(product.getInstitutionContractMappings().get(key).getContractTemplatePath());
            if (!value.getContractTemplateVersion().equals(product.getInstitutionContractMappings().get(key).getContractTemplateVersion())) {
                value.setContractTemplateUpdatedAt(Instant.now());
                value.setContractTemplateVersion(product.getInstitutionContractMappings().get(key).getContractTemplateVersion());
            }
        });

        ProductOperations updatedProduct = productConnector.save(foundProduct);
        log.debug("updateProduct result = {}", updatedProduct);
        log.trace("updateProduct end");
        return updatedProduct;
    }

    @Override
    public void updateProductStatus(String id, ProductStatus status) {
        log.trace("updateProductStatus start");
        log.debug("updateProductStatus id = {}, status = {}", id, status);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        Assert.notNull(status, REQUIRED_PRODUCT_STATUS_MESSAGE);
        productConnector.updateProductStatus(id, status);
        log.trace("updateProductStatus end");
    }

    @Override
    public void saveProductLogo(String id, InputStream logo, String contentType, String fileName) {
        log.trace("saveProductLogo start");
        log.debug("saveProductLogo id = {}, logo = {}, contentType = {}, fileName = {}", id, logo, contentType, fileName);
        ProductOperations productToUpdate = getProduct(id, null);
        if (productToUpdate.getParentId() != null) {
            throw new ValidationException("Given product Id = " + id + " is of a subProduct");
        }
        productLogoImageService.saveImage(productToUpdate, logo, contentType, fileName);
        log.trace("saveProductLogo end");
    }

    @Override
    public void saveProductDepictImage(String id, InputStream depictImage, String contentType, String fileName) {
        log.trace("saveProductDepictImage start");
        log.debug("saveProductDepictImage id = {}, logo = {}, contentType = {}, fileName = {}", id, depictImage, contentType, fileName);
        Assert.hasText(id, REQUIRED_PRODUCT_ID_MESSAGE);
        ProductOperations productToUpdate = getProduct(id, null);
        if (productToUpdate.getParentId() != null) {
            throw new ValidationException("Given product Id = " + id + " is of a subProduct");
        }
        productDepictImageService.saveImage(productToUpdate, depictImage, contentType, fileName);
        log.trace("saveProductDepictImage end");
    }


}
