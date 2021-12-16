package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.model.DummyProduct;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class})
@ContextConfiguration(classes = {ProductServiceImpl.class})
class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductConnector productConnectorMock;


    @Test
    void getProducts_emptyList() {
        // given and when
        List<ProductOperations> products = productService.getProducts();
        // then
        assertTrue(products.isEmpty());
    }


    @Test
    void getProducts_notEmptyList() {
        // given
        Mockito.when(productConnectorMock.findByEnabled(Mockito.anyBoolean()))
                .thenReturn(Collections.singletonList(new DummyProduct()));
        // when
        List<ProductOperations> products = productService.getProducts();
        // then
        assertEquals(1, products.size());
        Mockito.verify(productConnectorMock, Mockito.times(1)).findByEnabled(Mockito.anyBoolean());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void createProduct() {
        // given
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        list.add("v1");
        list.add("v2");
        map.put(PartyRole.OPERATOR, list);
        input.setRoleMappings(map);
        input.setId(id);
        Mockito.when(productConnectorMock.insert(Mockito.any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations output = productService.createProduct(input);
        // then
        assertNotNull(output);
        assertNotNull(output.getCreatedAt());
        assertNotNull(output.getContractTemplateUpdatedAt());
        assertTrue(output.getCreatedAt().isAfter(now));
        assertTrue(output.getContractTemplateUpdatedAt().isAfter(now));
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .insert(Mockito.any(ProductOperations.class));
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void createProduct_NullRoleMappings() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        map.put(PartyRole.OPERATOR, null);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(InvalidRoleMappingException.class, executable);
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_EmptyRoleMappings() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        map.put(PartyRole.OPERATOR, list);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(InvalidRoleMappingException.class, executable);
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_RoleMappingsNotEmptyAndIncorrectPartyRoleConfig() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        list.add("v1");
        list.add("v2");
        map.put(PartyRole.DELEGATE, list);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(InvalidRoleMappingException.class, executable);
        Mockito.verifyNoInteractions(productConnectorMock);
    }

    @Test
    void deleteProduct_existEnabled() {
        // given
        ProductOperations product = TestUtils.mockInstance(new DummyProduct());
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        productService.deleteProduct("productId");
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verify(productConnectorMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void deleteProduct_existNotEnabled() {
        // given
        ProductOperations product = TestUtils.mockInstance(new DummyProduct());
        product.setEnabled(false);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        productService.deleteProduct(Mockito.anyString());
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void deleteProduct_NotExist() {
        // given
        String productId = "productId";
        // when
        Executable executable = () -> productService.deleteProduct(productId);
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_Enabled() {
        // given
        String productId = "productId";
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(new DummyProduct()));
        // when
        ProductOperations product = productService.getProduct(productId);
        // then
        assertNotNull(product);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_notEnabled() {
        // given
        ProductOperations p = new DummyProduct();
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(p));
        p.setEnabled(false);
        // when
        Executable executable = () -> productService.getProduct(Mockito.anyString());
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_null() {
        // given
        String productId = "productId";
        // when
        Executable executable = () -> productService.getProduct(productId);
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, executable);
    }

    @Test
    void updateProduct_foundProductEnabledDiffVersionContract() {
        // given
        String productId = "productId";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId");
        Mockito.when(productConnectorMock.findById(productId))
                .thenReturn(Optional.of(new DummyProduct()));
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        list.add("v1");
        list.add("v2");
        map.put(PartyRole.OPERATOR, list);
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations savedProduct = productService.updateProduct(productId, product);
        // then
        assertEquals(savedProduct.getLogo(), product.getLogo());
        assertEquals(savedProduct.getTitle(), product.getTitle());
        assertEquals(savedProduct.getDescription(), product.getDescription());
        assertEquals(savedProduct.getUrlPublic(), product.getUrlPublic());
        assertEquals(savedProduct.getUrlBO(), product.getUrlBO());
        assertEquals(savedProduct.getRoleMappings(), product.getRoleMappings());
        assertEquals(savedProduct.getRoleManagementURL(), product.getRoleManagementURL());
        assertEquals(savedProduct.getContractTemplatePath(), product.getContractTemplatePath());
        assertEquals(savedProduct.getContractTemplateVersion(), product.getContractTemplateVersion());
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verify(productConnectorMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void updateProduct_foundProductNotEnabled() {
        // given
        String productId = "productId";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId");
        product.setEnabled(false);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void updateProduct_notExists() {
        // given
        String productId = "productId";
        ProductOperations product = new DummyProduct();
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
    }
}