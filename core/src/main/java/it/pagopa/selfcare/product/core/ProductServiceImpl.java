package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.PartyRole;
import it.pagopa.selfcare.product.dao.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;


    @Autowired
    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> getProducts() {
        return repository.findByEnabled(true);
    }

    @Override
    public Product createProduct(Product product) {
        String id = product.getId();
        if (repository.existsById(id)) {
            throw new ResourceAlreadyExistsException(id);
        }
        validateRoleMappings(product);
        OffsetDateTime now = OffsetDateTime.now();
        product.setCreatedAt(now);
        product.setContractTemplateUpdatedAt(now);
        return repository.save(product);
    }

    private void validateRoleMappings(Product product) {
        product.getRoleMappings().forEach((partyRole, productRoles) -> {
            if (productRoles == null
                    || productRoles.isEmpty()
                    || (productRoles.size() > 1 && !PartyRole.OPERATOR.equals(partyRole))) {
                throw new InvalidRoleMappingException(String.format("Product roles cannot be null nor empty and only '%s' Party role can have more than one Product role", PartyRole.OPERATOR.name()),
                        new IllegalArgumentException(String.format("partyRole = %s => productRoles = %s", partyRole, productRoles)));
            }
        });
    }

    @Override
    public void deleteProduct(String id) {
        Product foundProduct = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (foundProduct.isEnabled()) {
            foundProduct.setEnabled(false);
            repository.save(foundProduct);
        }
    }

    @Override
    public Product getProduct(String id) {
        Product foundProduct = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        if (!foundProduct.isEnabled()) {
            throw new ResourceNotFoundException();
        }
        return foundProduct;
    }


    @Override
    public Product updateProduct(String id, Product product) {
        Product foundProduct = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
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
        return repository.save(foundProduct);
    }

}
