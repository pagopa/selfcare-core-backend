package it.pagopa.selfcare.product.connector.dao;

import com.mongodb.client.result.UpdateResult;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
public class ProductConnectorImpl implements ProductConnector {

    private final ProductRepository repository;
    private final MongoTemplate mongoTemplate;
    private final AuditorAware<String> auditorAware;


    @Autowired
    public ProductConnectorImpl(ProductRepository repository, MongoTemplate mongoTemplate, AuditorAware<String> auditorAware) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.auditorAware = auditorAware;
    }


    @Override
    public ProductOperations insert(ProductOperations entity) {
        ProductEntity insert;
        try {
            final ProductEntity productEntity = new ProductEntity(entity);
            insert = repository.insert(productEntity);
        } catch (DuplicateKeyException e) {
            throw new ResourceAlreadyExistsException("Product id = " + entity.getId(), e);
        }
        return insert;
    }


    @Override
    public ProductOperations save(ProductOperations entity) {
        final ProductEntity productEntity = new ProductEntity(entity);
        productEntity.setNew(false);
        if (productEntity.getCreatedAt() == null) {
            productEntity.setCreatedAt(Instant.now());
        }
        if (productEntity.getCreatedBy() == null) {
            productEntity.setCreatedBy(auditorAware.getCurrentAuditor().orElse(null));
        }
        return repository.save(productEntity);
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
        return new ArrayList<>(repository.findByStatusIsNot(ProductStatus.INACTIVE));
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

    @Override
    public List<ProductOperations> findByParentAndStatusIsNotInactive(String parent) {
        return new ArrayList<>(repository.findByParentIdAndStatusIsNot(parent, ProductStatus.INACTIVE));
    }

    @Override
    public List<ProductOperations> findByStatusIsNot(ProductStatus status) {
        return new ArrayList<>(repository.findByStatusIsNot(status));
    }

    @Override
    public void disableById(String id) {
        log.trace("disableById start");
        log.debug("disableById id = {} ", id);
        UpdateResult updateResult = mongoTemplate.updateFirst(
                Query.query(Criteria.where(ProductEntity.Fields.id).is(id)
                        .and(ProductEntity.Fields.enabled).is(true)),
                Update.update(ProductEntity.Fields.enabled, false)
                        .set(ProductEntity.Fields.status, ProductStatus.INACTIVE)
                        .set(ProductEntity.Fields.modifiedBy, auditorAware.getCurrentAuditor().orElse(null))
                        .currentDate(ProductEntity.Fields.modifiedAt),
                ProductEntity.class);
        if (updateResult.getMatchedCount() == 0) {
            throw new ResourceNotFoundException();
        }
        log.trace("disableById end");
    }

    @Override
    public void updateProductStatus(String id, ProductStatus status) {
        log.trace("updateProductStatus start");
        log.debug("updateProductStatus id = {}, status = {}", id, status);
        UpdateResult updateResult = mongoTemplate.updateFirst(
                Query.query(Criteria.where(ProductEntity.Fields.id).is(id)),
                Update.update(ProductEntity.Fields.status, status)
                        .set(ProductEntity.Fields.modifiedBy, auditorAware.getCurrentAuditor().orElse(null))
                        .currentDate(ProductEntity.Fields.modifiedAt),
                ProductEntity.class);
        if (updateResult.getMatchedCount() == 0) {
            throw new ResourceNotFoundException();
        }
        log.trace("updateProductStatus end");
    }

}
