package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return repository.save(new Product(product.getLogo(), product.getTitle(), product.getDescription(), product.getUrlPublic(), product.getUrlBO()));
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
        Optional<Product> foundProduct = repository.findById(id);

        return foundProduct.orElseThrow(ResourceNotFoundException::new);
    }


    @Override
    public Product updateProduct(String id, Product product) {
        Optional<Product> foundProduct = repository.findById(id);
        Product p = foundProduct.orElseThrow(ResourceNotFoundException::new);
        p.setLogo(product.getLogo());
        p.setTitle(product.getTitle());
        p.setDescription(product.getDescription());
        p.setUrlPublic(product.getUrlPublic());
        p.setUrlBO(product.getUrlBO());
        return repository.save(p);
    }

}
