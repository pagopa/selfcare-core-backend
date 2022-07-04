package it.pagopa.selfcare.product.connector.dao;

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

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductConnectorImplTest {

    @Mock
    private ProductRepository repositoryMock;

    @InjectMocks
    private ProductConnectorImpl productConnector;


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

}