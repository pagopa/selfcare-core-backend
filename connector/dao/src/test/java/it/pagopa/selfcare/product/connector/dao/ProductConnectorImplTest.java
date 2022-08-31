package it.pagopa.selfcare.product.connector.dao;

import com.mongodb.client.result.UpdateResult;
import it.pagopa.selfcare.commons.base.security.SelfCareUser;
import it.pagopa.selfcare.product.connector.dao.auditing.SpringSecurityAuditorAware;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import org.bson.BsonValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.TestSecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProductConnectorImplTest {

    private final static String LOGGED_USER_ID = "id";

    private final SelfCareUser selfCareUser;
    private final ProductRepository repositoryMock;
    private final MongoTemplate mongoTemplateMock;
    private final ProductConnectorImpl productConnector;


    public ProductConnectorImplTest() {
        selfCareUser = SelfCareUser.builder(LOGGED_USER_ID).build();
        TestingAuthenticationToken authenticationToken = new TestingAuthenticationToken(selfCareUser, null);
        TestSecurityContextHolder.setAuthentication(authenticationToken);
        this.repositoryMock = Mockito.mock(ProductRepository.class);
        this.mongoTemplateMock = Mockito.mock(MongoTemplate.class);
        this.productConnector = new ProductConnectorImpl(repositoryMock, mongoTemplateMock, new SpringSecurityAuditorAware());
    }


    @AfterEach
    void clear() {
        repositoryMock.deleteAll();
        Mockito.reset(repositoryMock, mongoTemplateMock);
    }


    @Test
    void insert_duplicateKey() {
        // given
        ProductEntity entity = mockInstance(new ProductEntity());
        doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(any(ProductEntity.class));
        // when
        Executable executable = () -> productConnector.insert(entity);
        // then
        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class, executable);
        assertEquals("Product id = " + entity.getId(), e.getMessage());
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void insert() {
        // given
        ProductEntity entity = mockInstance(new ProductEntity());
        when(repositoryMock.insert(any(ProductEntity.class)))
                .thenReturn(entity);
        // when
        ProductOperations saved = productConnector.insert(entity);
        // then
        Assertions.assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .insert(entity);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void save() {
        // given
        ProductEntity entity = mockInstance(new ProductEntity());
        when(repositoryMock.save(any()))
                .thenReturn(entity);
        // when
        ProductOperations saved = productConnector.save(entity);
        // then
        Assertions.assertEquals(entity, saved);
        verify(repositoryMock, times(1))
                .save(entity);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findById() {
        // given
        String id = "id";
        Optional<ProductEntity> entity = Optional.of(mockInstance(new ProductEntity()));
        when(repositoryMock.findById(any()))
                .thenReturn(entity);
        // when
        Optional<ProductOperations> found = productConnector.findById(id);
        // then
        Assertions.assertEquals(entity, found);
        verify(repositoryMock, times(1))
                .findById(id);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void existsById() {
        // given
        String id = "id";
        boolean expected = true;
        when(repositoryMock.existsById(any()))
                .thenReturn(expected);
        // when
        boolean exists = productConnector.existsById(id);
        // then
        Assertions.assertEquals(expected, exists);
        verify(repositoryMock, times(1))
                .existsById(id);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findAll() {
        // given
        List<ProductEntity> expected = List.of(mockInstance(new ProductEntity()));
        when(repositoryMock.findAll())
                .thenReturn(expected);
        // when
        List<ProductOperations> found = productConnector.findAll();
        // then
        Assertions.assertEquals(expected, found);
        verify(repositoryMock, times(1))
                .findAll();
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void deleteById() {
        // given
        String id = "id";
        Mockito.doNothing()
                .when(repositoryMock)
                .deleteById(any());
        // when
        productConnector.deleteById(id);
        // then
        verify(repositoryMock, times(1))
                .deleteById(id);
        verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findByEnabled() {
        // given
        boolean enabled = true;
        List<ProductEntity> expected = List.of(mockInstance(new ProductEntity()));
        when(repositoryMock.findByEnabled(anyBoolean()))
                .thenReturn(expected);
        // when
        List<ProductOperations> found = productConnector.findByEnabled(enabled);
        // then
        Assertions.assertEquals(expected, found);
        verify(repositoryMock, times(1))
                .findByEnabled(enabled);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void findByParentAndEnabled() {
        // given
        String parent = "parentId";
        boolean enabled = true;
        List<ProductEntity> expected = List.of(mockInstance(new ProductEntity()));
        when(repositoryMock.findByParentIdAndEnabled(anyString(), anyBoolean()))
                .thenReturn(expected);
        // when
        List<ProductOperations> found = productConnector.findByParentAndEnabled(parent, enabled);
        //then
        Assertions.assertEquals(expected, found);
        verify(repositoryMock, times(1))
                .findByParentIdAndEnabled(parent, enabled);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void existsByIdAndEnabledFalse_found() {
        //given
        String id = "id";
        boolean expected = true;
        when(repositoryMock.existsByIdAndEnabledFalse(anyString()))
                .thenReturn(expected);
        //when
        boolean found = productConnector.existsByIdAndEnabledFalse(id);
        //then
        assertTrue(found);
        verify(repositoryMock, times(1))
                .existsByIdAndEnabledFalse(id);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void existsByIdAndEnabledFalse_notFound() {
        //given
        String id = "id";
        boolean expected = false;
        when(repositoryMock.existsByIdAndEnabledFalse(anyString()))
                .thenReturn(expected);
        //when
        boolean found = productConnector.existsByIdAndEnabledFalse(id);
        //then
        assertFalse(found);
        verify(repositoryMock, times(1))
                .existsByIdAndEnabledFalse(id);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void disableById_notEnabled() {
        // given
        String id = "id";
        UpdateResult result = mockInstance(new UpdateResult() {
            @Override
            public boolean wasAcknowledged() {
                return false;
            }

            @Override
            public long getMatchedCount() {
                return 0;
            }

            @Override
            public long getModifiedCount() {
                return 0;
            }

            @Override
            public BsonValue getUpsertedId() {
                return null;
            }
        });
        when(mongoTemplateMock.updateFirst(any(Query.class), any(Update.class), (Class<?>) any()))
                .thenReturn(result);
        // when
        final Executable executable = () -> productConnector.disableById(id);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        verify(mongoTemplateMock, times(1))
                .updateFirst(any(Query.class), any(Update.class), (Class<?>) any());
        verifyNoMoreInteractions(mongoTemplateMock);
        verifyNoInteractions(repositoryMock);
    }


    @Test
    void disableById() {
        // given
        String id = "id";
        UpdateResult result = mockInstance(new UpdateResult() {
            @Override
            public boolean wasAcknowledged() {
                return false;
            }

            @Override
            public long getMatchedCount() {
                return 1;
            }

            @Override
            public long getModifiedCount() {
                return 1;
            }

            @Override
            public BsonValue getUpsertedId() {
                return null;
            }
        });
        when(mongoTemplateMock.updateFirst(any(Query.class), any(Update.class), (Class<?>) any()))
                .thenReturn(result);
        // when
        productConnector.disableById(id);
        // then
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplateMock, times(1))
                .updateFirst(queryCaptor.capture(), updateCaptor.capture(), (Class<?>) any());
        Query query = queryCaptor.getValue();
        Update update = updateCaptor.getValue();
        Map<String, Object> set = (Map<String, Object>) update.getUpdateObject().get("$set");
        Map<String, Object> currentDate = (Map<String, Object>) update.getUpdateObject().get("$currentDate");
        assertEquals(id, query.getQueryObject().get(ProductEntity.Fields.id));
        assertEquals(false, set.get("enabled"));
        assertEquals(selfCareUser.getId(), set.get("modifiedBy"));
        assertTrue(currentDate.containsKey("modifiedAt"));
        verifyNoMoreInteractions(mongoTemplateMock);
        verifyNoInteractions(repositoryMock);
    }

}