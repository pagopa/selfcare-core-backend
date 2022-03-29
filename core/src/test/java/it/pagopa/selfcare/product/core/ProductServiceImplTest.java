package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import it.pagopa.selfcare.product.connector.model.*;
import it.pagopa.selfcare.product.core.config.CoreTestConfig;
import it.pagopa.selfcare.product.core.exception.FileValidationException;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import it.pagopa.selfcare.product.core.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ProductServiceImpl.class, CoreTestConfig.class})
@TestPropertySource(properties = {
        "PRODUCT_LOGO_ALLOWED_MIME_TYPES:image/png, image/jpeg",
        "PRODUCT_LOGO_ALLOWED_EXTENSIONS:png,jpeg",
        "LOGO_STORAGE_URL:https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png"
})
class ProductServiceImplTest {

    private static final String URL = "https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png";

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductConnector productConnectorMock;

    @MockBean
    private FileStorageConnector storageConnectorMock;

    @Captor
    private ArgumentCaptor<ProductOperations> savedProductCaptor;


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
        String parent = null;
        boolean enabled = true;
        DummyProduct product = TestUtils.mockInstance(new DummyProduct(), "setParent");
        Mockito.when(productConnectorMock.findByParentAndEnabled(Mockito.any(), Mockito.anyBoolean()))
                .thenReturn(List.of(product));
        // when
        List<ProductOperations> products = productService.getProducts();
        // then
        assertEquals(1, products.size());
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .findByParentAndEnabled(parent, enabled);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void createProduct_nullProduct() {
        // given
        ProductOperations input = null;
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_nullRoleMappings() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A product role mappings is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_emptyRoleMappings() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A product role mappings is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_nullProductRoleInfo() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        map.put(PartyRole.OPERATOR, null);
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A product role info is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_nullProductRoles() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, null));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("At least one Product role are required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_emptyProductRoles() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, Collections.emptyList()));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("At least one Product role are required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_incorrectMultiProductRoleConfig() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.DELEGATE, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        InvalidRoleMappingException e = assertThrows(InvalidRoleMappingException.class, executable);
        assertEquals(String.format("Only '%s' Party-role can have more than one Product-role", PartyRole.OPERATOR.name()), e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_correctMultiProductRoleConfig() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertDoesNotThrow(executable);
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .insert(Mockito.any(ProductOperations.class));
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void createProduct_ok() {
        // given
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        map.put(PartyRole.MANAGER, new DummyProductRoleInfo(true, list));
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
        assertEquals(URL, output.getLogo());
        assertTrue(output.getContractTemplateUpdatedAt().isAfter(now));
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .insert(Mockito.any(ProductOperations.class));
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void deleteProduct_existEnabled() {
        // given
        String productId = "productId";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setRoleMappings");
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        productService.deleteProduct(productId);
        // then
        assertFalse(product.isEnabled());
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verify(productConnectorMock, Mockito.times(1)).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void deleteProduct_existNotEnabled() {
        // given
        String id = "id";
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setEnabled", "setRoleMappings");
                    product.setId(invocationOnMock.getArgument(0, String.class));
                    product.setEnabled(false);
                    return Optional.of(product);
                });
        // when
        productService.deleteProduct(id);
        // then
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(id);
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
    void deleteProduct_nullId() {
        // given
        String id = null;
        // when
        Executable executable = () -> productService.deleteProduct(id);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A product id is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
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
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_notEnabled() {
        // given
        String id = "id";
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setEnabled", "setRoleMappings");
                    product.setId(invocationOnMock.getArgument(0, String.class));
                    product.setEnabled(false);
                    return Optional.of(product);
                });
        // when
        Executable executable = () -> productService.getProduct(id);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(id);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_nullId() {
        // given
        String id = null;
        // when
        Executable executable = () -> productService.getProduct(id);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("A product id is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }

    @Test
    void getProduct_null() {
        // given
        String productId = "productId";
        // when
        Executable executable = () -> productService.getProduct(productId);
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void updateProduct_nullId() {
        // given
        String id = null;
        ProductOperations input = new DummyProduct();
        // when
        Executable executable = () -> productService.updateProduct(id, input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product id is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void updateProduct_nullProduct() {
        // given
        String id = "id";
        ProductOperations input = null;
        // when
        Executable executable = () -> productService.updateProduct(id, input);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock);
    }


    @Test
    void updateProduct_foundProductEnabledDiffVersionContract() {
        // given
        String productId = "productId";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        Mockito.when(productConnectorMock.findById(productId))
                .thenReturn(Optional.of(new DummyProduct()));
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations savedProduct = productService.updateProduct(productId, product);
        // then
        assertNull(savedProduct.getLogo());
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
    void updateProduct_foundProductEnabledSameVersionContract() {
        // given
        String productId = "productId";
        String contractTemplateVersion = "1.2.4";
        Mockito.when(productConnectorMock.findById(productId))
                .thenAnswer(invocationOnMock -> {
                    DummyProduct foundProductMock = new DummyProduct();
                    foundProductMock.setId(invocationOnMock.getArgument(0, String.class));
                    foundProductMock.setContractTemplateVersion(contractTemplateVersion);
                    return Optional.of(foundProductMock);
                });
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setContractTemplateVersion");
        product.setRoleMappings(map);
        product.setContractTemplateVersion(contractTemplateVersion);
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations savedProduct = productService.updateProduct(productId, product);
        // then
        assertNull(savedProduct.getLogo());
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
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setEnabled(false);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
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
        Mockito.verify(productConnectorMock, Mockito.times(1)).findById(productId);
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void storeProductLogo_nullid() {
        //given
        String productId = null;
        InputStream logo = InputStream.nullInputStream();
        String contentType = "contentType";
        String filename = "filename";
        // when
        Executable executable = () -> productService.saveProductLogo(productId, logo, contentType, filename);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product id is required", e.getMessage());
        Mockito.verifyNoInteractions(productConnectorMock, storageConnectorMock);
    }


    @Test
    void storeProductLogo_nullFileName() {
        //given
        String productId = "prod-id";
        InputStream logo = InputStream.nullInputStream();
        String contentType = null;
        String filename = null;
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo(null);
        product.setId(productId);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        //when
        Executable executable = () -> productService.saveProductLogo(productId, logo, contentType, filename);
        //then
        assertThrows(FileValidationException.class, executable);
        Mockito.verifyNoInteractions(storageConnectorMock);
    }

    @Test
    void storeProoductLogo_invalidMimeType() {
        // given
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_GIF_VALUE;
        String fileName = "filename";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo(null);
        product.setId(productId);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.saveProductLogo(productId, logo, contentType, fileName);
        // then
        assertThrows(FileValidationException.class, executable);
        Mockito.verifyNoInteractions(storageConnectorMock);
    }

    @Test
    void storeInstitutionLogo_invalidExtension() {
        // given
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.gif";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo(null);
        product.setId(productId);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.saveProductLogo(productId, logo, contentType, fileName);
        // then
        Assertions.assertThrows(FileValidationException.class, executable);
        Mockito.verifyNoInteractions(storageConnectorMock);
    }

    @Test
    void storeProductLogo_uploadExeption() throws FileUploadException, MalformedURLException {
        //given
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo(null);
        product.setId(productId);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.doThrow(FileUploadException.class)
                .when(storageConnectorMock).uploadProductLogo(Mockito.any(), Mockito.any(), Mockito.any());
        //when
        Executable executable = () -> productService.saveProductLogo(productId, logo, contentType, fileName);
        //then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, executable);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertTrue(FileUploadException.class.isAssignableFrom(exception.getCause().getClass()));
        Mockito.verify(storageConnectorMock, Mockito.times(1))
                .uploadProductLogo(Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verifyNoMoreInteractions(storageConnectorMock);
    }

    @Test
    void storeProductLogo_nullUrl() throws FileUploadException, MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo(null);
        product.setId(productId);
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        URL uriToUrl = uriMock.toURL();
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(storageConnectorMock.uploadProductLogo(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(uriToUrl);
        //when
        productService.saveProductLogo(productId, logo, contentType, fileName);
        //then
        Mockito.verify(storageConnectorMock, Mockito.times(1))
                .uploadProductLogo(Mockito.any(), Mockito.eq("resources/products/" + productId + "/logo.png"), Mockito.eq(contentType));
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .findById(productId);
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .save(Mockito.any());
        Mockito.verifyNoMoreInteractions(storageConnectorMock);
    }

    @Test
    void storeProductLogo_defaultUrl() throws FileUploadException, MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        product.setId(productId);
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        URL uriToUrl = uriMock.toURL();
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        Mockito.when(storageConnectorMock.uploadProductLogo(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(uriToUrl);
        //when
        productService.saveProductLogo(productId, logo, contentType, fileName);
        //then
        Mockito.verify(storageConnectorMock, Mockito.times(1))
                .uploadProductLogo(Mockito.any(), Mockito.eq("resources/products/" + productId + "/logo.png"), Mockito.eq(contentType));
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .findById(productId);
        Mockito.verify(productConnectorMock, Mockito.times(0))
                .save(Mockito.any());
        Mockito.verifyNoMoreInteractions(storageConnectorMock);
    }

    @Test
    void updateProductLogo_logoUrl() throws MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream logo = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setLogo("https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png");
        product.setId(productId);
        Mockito.when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/prod-1/logo.png");
        URL uriToUrl = uriMock.toURL();
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        Mockito.when(productConnectorMock.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        Mockito.when(storageConnectorMock.uploadProductLogo(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(uriToUrl);
        //when
        productService.saveProductLogo(productId, logo, contentType, fileName);
        //then
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .save(savedProductCaptor.capture());
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .findById(productId);
        ProductOperations savedProduct = savedProductCaptor.getValue();
        Assertions.assertEquals(uriToUrl.toString(), savedProduct.getLogo());
        Mockito.verify(storageConnectorMock, Mockito.times(1))
                .uploadProductLogo(Mockito.any(), Mockito.eq("resources/products/" + productId + "/logo.png"), Mockito.eq(contentType));
        Mockito.verifyNoMoreInteractions(storageConnectorMock);
        Mockito.verifyNoMoreInteractions(productConnectorMock);

    }
}