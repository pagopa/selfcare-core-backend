package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class ProductConnectorImpl implements ProductConnector {

    private final ProductRepository repository;


    @Autowired
    public ProductConnectorImpl(ProductRepository repository) {
        this.repository = repository;
    }


    @Override
    public ProductOperations insert(ProductOperations entity) {
        ProductEntity insert;
        try {
            insert = repository.insert(new ProductEntity(entity));
        } catch (DuplicateKeyException e) {
            throw new ResourceAlreadyExistsException("Product id = " + entity.getId(), e);
        }
        return insert;
    }


    @Override
    public ProductOperations save(ProductOperations entity) {
        return repository.save(new ProductEntity(entity));
    }


    @Override
    public Optional<ProductOperations> findById(String id) {
        return repository.findById(id).map(Function.identity());
    }


    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public boolean existsByIdAndEnabledFalse(String id) {
        return repository.existsByIdAndEnabledFalse(id);
    }


    @Override
    public List<ProductOperations> findAll() {
        return new ArrayList<>(repository.findAll());
    }


    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }


    @Override
    public List<ProductOperations> findByEnabled(boolean enabled) {
        return new ArrayList<>(repository.findByEnabled(enabled));
    }

    @Override
    public List<ProductOperations> findByParentAndEnabled(String parent, boolean enabled) {
        return new ArrayList<>(repository.findByParentIdAndEnabled(parent, enabled));
    }

}
