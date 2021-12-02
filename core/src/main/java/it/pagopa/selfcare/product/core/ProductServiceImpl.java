package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
class ProductServiceImpl implements ProductService {

    private final ProductConnector productConnector;


    @Autowired
    public ProductServiceImpl(ProductConnector productConnector) {
        this.productConnector = productConnector;
    }

    @Override
    public List<ProductOperations> getProducts() {
        return productConnector.findByEnabled(true);
    }

    @Override
    public ProductOperations createProduct(ProductOperations product) {
        validateRoleMappings(product);
        OffsetDateTime now = OffsetDateTime.now();
        product.setCreatedAt(now);
        product.setContractTemplateUpdatedAt(now);
        return productConnector.insert(product);
    }

    private void validateRoleMappings(ProductOperations product) {
        product.getRoleMappings().forEach((partyRole, productRoles) -> {
            if (productRoles == null
                    || productRoles.isEmpty()
                    || (productRoles.size() > 1 && !PartyRole.OPERATOR.equals(partyRole))) {
                throw new InvalidRoleMappingException(String.format("ProductOperations roles cannot be null nor empty and only '%s' Party role can have more than one ProductOperations role", PartyRole.OPERATOR.name()),
                        new IllegalArgumentException(String.format("partyRole = %s => productRoles = %s", partyRole, productRoles)));
            }
        });
    }

    @Override
    public void deleteProduct(String id) {
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (foundProduct.isEnabled()) {
            foundProduct.setEnabled(false);
            productConnector.save(foundProduct);
        }
    }

    @Override
    public ProductOperations getProduct(String id) {
        ProductOperations foundProduct = productConnector.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        return foundProduct;
    }


    @Override
    public ProductOperations updateProduct(String id, ProductOperations product) {
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
        return productConnector.save(foundProduct);
    }

}
