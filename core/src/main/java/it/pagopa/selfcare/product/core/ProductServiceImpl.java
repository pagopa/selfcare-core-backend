package it.pagopa.selfcare.product.core;

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
        repository.deleteById(id);
    }

    @Override
    public Product getProduct(String id) {
        Optional<Product> foundProduct = repository.findById(id);
        Product result = null;
        if (foundProduct.isPresent()) {
            result = foundProduct.get();
        }
        return result;
    }


    @Override
    public Product updateProduct(String id, Product product) {
        Optional<Product> foundProduct = repository.findById(id);
        Product result = null;
        if (foundProduct.isPresent()) {
            Product p = foundProduct.get();
            p.setLogo(product.getLogo());
            p.setTitle(product.getTitle());
            p.setDescription(product.getDescription());
            p.setUrlPublic(product.getUrlPublic());
            p.setUrlBO(product.getUrlBO());
            result = repository.save(p);
        }
        return result;
    }

}
