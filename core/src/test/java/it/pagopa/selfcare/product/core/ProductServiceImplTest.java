package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.dao.ProductRepository;
import it.pagopa.selfcare.product.dao.model.PartyRole;
import it.pagopa.selfcare.product.dao.model.Product;
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
    private ProductRepository repositoryMock;


    @Test
    void getProducts_emptyList() {
        // given and when
        List<Product> products = productService.getProducts();
        // then
        assertTrue(products.isEmpty());
    }

    @Test
    void getProducts_notEmptyList() {
        // given
        Mockito.when(repositoryMock.findByEnabled(Mockito.anyBoolean()))
                .thenReturn(Collections.singletonList(new Product()));
        // when
        List<Product> products = productService.getProducts();
        // then
        assertEquals(1, products.size());
        Mockito.verify(repositoryMock, Mockito.times(1)).findByEnabled(Mockito.anyBoolean());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProduct() {
        // given
        OffsetDateTime now = OffsetDateTime.now();
        String id = "id";
        Mockito.when(repositoryMock.existsById(Mockito.eq(id)))
                .thenReturn(false);
        Product input = new Product();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        list.add("v1");
        list.add("v2");
        map.put(PartyRole.OPERATOR, list);
        input.setRoleMappings(map);
        input.setId(id);
        Mockito.when(repositoryMock.save(Mockito.any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        Product output = productService.createProduct(input);
        // then
        assertNotNull(output);
        assertNotNull(output.getCreatedAt());
        assertNotNull(output.getContractTemplateUpdatedAt());
        assertTrue(output.getCreatedAt().isAfter(now));
        assertTrue(output.getContractTemplateUpdatedAt().isAfter(now));
        Mockito.verify(repositoryMock, Mockito.times(1)).existsById(Mockito.eq(id));
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any(Product.class));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProductDupKey() {
        // given
        String id = "id";
        Mockito.when(repositoryMock.existsById(Mockito.eq(id)))
                .thenReturn(true);
        Product input = new Product();
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(ResourceAlreadyExistsException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).existsById(Mockito.eq(id));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProduct_NullRoleMappings() {
        // given
        String id = "id";
        Mockito.when(repositoryMock.existsById(Mockito.eq(id)))
                .thenReturn(false);
        Product input = new Product();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        map.put(PartyRole.OPERATOR, null);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(InvalidRoleMappingException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).existsById(Mockito.eq(id));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProduct_EmptyRoleMappings() {
        // given
        String id = "id";
        Mockito.when(repositoryMock.existsById(Mockito.eq(id)))
                .thenReturn(false);
        Product input = new Product();
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        map.put(PartyRole.OPERATOR, list);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertThrows(InvalidRoleMappingException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).existsById(Mockito.eq(id));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void createProduct_RoleMappingsNotEmptyAndIncorrectPartyRoleConfig() {
        // given
        String id = "id";
        Mockito.when(repositoryMock.existsById(Mockito.eq(id)))
                .thenReturn(false);
        Product input = new Product();
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
        Mockito.verify(repositoryMock, Mockito.times(1)).existsById(Mockito.eq(id));
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_existEnabled() {
        // given
        Product product = TestUtils.mockInstance(new Product());
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(repositoryMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        productService.deleteProduct("productId");
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_existNotEnabled() {
        // given
        Product product = TestUtils.mockInstance(new Product());
        product.setEnabled(false);
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        productService.deleteProduct(Mockito.anyString());
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void deleteProduct_NotExist() {
        // given
        String productId = "productId";
        // when
        Executable executable = () -> productService.deleteProduct(productId);
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void getProduct_Enabled() {
        // given
        String productId = "productId";
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(new Product()));
        // when
        Product product = productService.getProduct(productId);
        // then
        assertNotNull(product);
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void getProduct_notEnabled() {
        // given
        Product p = new Product();
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(p));
        p.setEnabled(false);
        // when
        Executable executable = () -> productService.getProduct(Mockito.anyString());
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
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
        Product product = TestUtils.mockInstance(new Product(), "setId");
        Mockito.when(repositoryMock.findById(Mockito.eq(productId)))
                .thenReturn(Optional.of(new Product()));
        EnumMap<PartyRole, List<String>> map = new EnumMap<>(PartyRole.class);
        List<String> list = new ArrayList<>();
        list.add("v1");
        list.add("v2");
        map.put(PartyRole.OPERATOR, list);
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        Mockito.when(repositoryMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Product.class));
        // when
        Product savedProduct = productService.updateProduct(productId, product);
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
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.eq(productId));
        Mockito.verify(repositoryMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void updateProduct_foundProductNotEnabled() {
        // given
        String productId = "productId";
        Product product = TestUtils.mockInstance(new Product(), "setId");
        product.setEnabled(false);
        Mockito.when(repositoryMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(repositoryMock, Mockito.times(1)).findById(Mockito.anyString());
        Mockito.verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    void updateProduct_notExists() {
        // given
        String productId = "productId";
        Product product = new Product();
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
    }
}