package it.pagopa.selfcare.product.connector.dao;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.dao.model.ProductEntity;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductConnectorImplTest {

    @Mock
    private ProductRepository repositoryMock;

    @InjectMocks
    private ProductConnectorImpl productConnector;


    @Test
    void insert_duplicateKey() {
        // given
        ProductEntity entity = TestUtils.mockInstance(new ProductEntity());
        Mockito.doThrow(DuplicateKeyException.class)
                .when(repositoryMock)
                .insert(Mockito.any(ProductEntity.class));
        // when
        Executable executable = () -> productConnector.insert(entity);
        // then
        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class, executable);
        assertEquals("Product id = " + entity.getId(), e.getMessage());
        Mockito.verify(repositoryMock, Mockito.times(1))
                .insert(entity);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void insert() {
        // given
        ProductEntity entity = TestUtils.mockInstance(new ProductEntity());
        Mockito.when(repositoryMock.insert(Mockito.any(ProductEntity.class)))
                .thenReturn(entity);
        // when
        ProductOperations saved = productConnector.insert(entity);
        // then
        Assertions.assertEquals(entity, saved);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .insert(entity);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void save() {
        // given
        ProductEntity entity = TestUtils.mockInstance(new ProductEntity());
        Mockito.when(repositoryMock.save(Mockito.any()))
                .thenReturn(entity);
        // when
        ProductOperations saved = productConnector.save(entity);
        // then
        Assertions.assertEquals(entity, saved);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .save(entity);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findById() {
        // given
        String id = "id";
        Optional<ProductEntity> entity = Optional.of(TestUtils.mockInstance(new ProductEntity()));
        Mockito.when(repositoryMock.findById(Mockito.any()))
                .thenReturn(entity);
        // when
        Optional<ProductOperations> found = productConnector.findById(id);
        // then
        Assertions.assertEquals(entity, found);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .findById(id);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void existsById() {
        // given
        String id = "id";
        boolean expected = true;
        Mockito.when(repositoryMock.existsById(Mockito.any()))
                .thenReturn(expected);
        // when
        boolean exists = productConnector.existsById(id);
        // then
        Assertions.assertEquals(expected, exists);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .existsById(id);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findAll() {
        // given
        List<ProductEntity> expected = List.of(TestUtils.mockInstance(new ProductEntity()));
        Mockito.when(repositoryMock.findAll())
                .thenReturn(expected);
        // when
        List<ProductOperations> found = productConnector.findAll();
        // then
        Assertions.assertEquals(expected, found);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .findAll();
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void deleteById() {
        // given
        String id = "id";
        Mockito.doNothing()
                .when(repositoryMock)
                .deleteById(Mockito.any());
        // when
        productConnector.deleteById(id);
        // then
        Mockito.verify(repositoryMock, Mockito.times(1))
                .deleteById(id);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }


    @Test
    void findByEnabled() {
        // given
        boolean enabled = true;
        List<ProductEntity> expected = List.of(TestUtils.mockInstance(new ProductEntity()));
        Mockito.when(repositoryMock.findByEnabled(Mockito.anyBoolean()))
                .thenReturn(expected);
        // when
        List<ProductOperations> found = productConnector.findByEnabled(enabled);
        // then
        Assertions.assertEquals(expected, found);
        Mockito.verify(repositoryMock, Mockito.times(1))
                .findByEnabled(enabled);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

}