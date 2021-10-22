package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
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
        return repository.findAll();
    }

    @Override
    public Product createProduct(Product product) {
        product.setActivationDateTime(OffsetDateTime.now());
        return repository.save(product);
    }

    @Override
    public void deleteProduct(String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @Override
    public Product getProduct(String id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }


    @Override
    public Product updateProduct(String id, Product product) {
        Product foundProduct = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        foundProduct.setLogo(product.getLogo());
        foundProduct.setTitle(product.getTitle());
        foundProduct.setDescription(product.getDescription());
        foundProduct.setUrlPublic(product.getUrlPublic());
        foundProduct.setUrlBO(product.getUrlBO());
        return repository.save(foundProduct);
    }

}
