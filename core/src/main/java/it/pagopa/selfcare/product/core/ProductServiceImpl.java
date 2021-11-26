package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
        String keyCode = product.getCode();
        if (repository.existsByCode(keyCode)){
            throw new DuplicateKeyException(keyCode);
        }
        validateRoleMappings(product);
        OffsetDateTime now = OffsetDateTime.now();
        product.setCreationDateTime(now);
        product.setContractTemplateUpdateDateTime(now);
        return repository.save(product);
    }

    private void validateRoleMappings(Product product) {
        product.getRoleMappings().forEach((partyRole, productRoles) -> {
            if (productRoles == null
            || productRoles.isEmpty()
            || (productRoles.size() > 1 && !"operator".equals(partyRole))) {
                throw new InvalidRoleMappingException();
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
        if (!foundProduct.isEnabled()){
            throw new ResourceNotFoundException();
        }
        validateRoleMappings(product);
        foundProduct.setLogo(product.getLogo());
        foundProduct.setTitle(product.getTitle());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setUrlPublic(product.getUrlPublic());
        foundProduct.setUrlBO(product.getUrlBO());
        foundProduct.setCode(product.getCode());
        foundProduct.setRoleMappings(product.getRoleMappings());
        foundProduct.setRoleManagementURL(product.getRoleManagementURL());
        foundProduct.setContractTemplatePath(product.getContractTemplatePath());
        if (!product.getContractTemplateVersion().equals(foundProduct.getContractTemplateVersion())){
            foundProduct.setContractTemplateUpdateDateTime(OffsetDateTime.now());
        }
        foundProduct.setContractTemplateVersion(product.getContractTemplateVersion());
        return repository.save(foundProduct);
    }

}
