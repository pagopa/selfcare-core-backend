package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ProductServiceImpl implements ProductService {

    private ProductRepository repository;

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
    public void deleteProducts() {
        repository.deleteAll();
    }

    @Override
    public void deleteProduct(String id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Product> updateProduct(String id) {
        return repository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return repository.save(product);
    }

}
