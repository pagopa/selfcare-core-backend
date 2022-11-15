package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.ResourceAlreadyExistsException;
import it.pagopa.selfcare.product.connector.exception.ResourceNotFoundException;
import it.pagopa.selfcare.product.connector.model.*;
import it.pagopa.selfcare.product.core.config.CoreTestConfig;
import it.pagopa.selfcare.product.core.exception.InvalidRoleMappingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;

import javax.validation.ValidationException;
import java.io.InputStream;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static it.pagopa.selfcare.commons.utils.TestUtils.mockInstance;
import static it.pagopa.selfcare.product.core.ProductServiceImpl.REQUIRED_PRODUCT_ID_MESSAGE;
import static it.pagopa.selfcare.product.core.ProductServiceImpl.REQUIRED_PRODUCT_STATUS_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ProductServiceImpl.class, CoreTestConfig.class})
class ProductServiceImplTest {

    private static final String LOGO_URL = "https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png";
    private static final String DEPICT_IMAGE_URL = "https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/depict-image.jpeg";

    @Autowired
    private ProductServiceImpl productService;

    @MockBean
    private ProductConnector productConnectorMock;

    @MockBean
    @Qualifier("productLogoImageService")
    private ProductImageService productLogoImageServiceMock;

    @MockBean
    @Qualifier("productDepictImageService")
    private ProductImageService productDepictImageServiceMock;

    @Captor
    private ArgumentCaptor<ProductOperations> savedProductCaptor;


    @Test
    void getProducts_emptyListRootOnly() {
        // given
        String parent = null;
        boolean enabled = true;
        // when
        List<ProductOperations> products = productService.getProducts(true);
        // then
        assertTrue(products.isEmpty());
        verify(productConnectorMock, times(1))
                .findByParentAndEnabled(parent, enabled);
        verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void getProducts_notEmptyListRootOnly() {
        // given
        String parent = null;
        boolean enabled = true;
        DummyProduct product = mockInstance(new DummyProduct(), "setParentId");
        when(productConnectorMock.findByParentAndEnabled(any(), Mockito.anyBoolean()))
                .thenReturn(List.of(product));
        // when
        List<ProductOperations> products = productService.getProducts(true);
        // then
        assertEquals(1, products.size());
        verify(productConnectorMock, times(1))
                .findByParentAndEnabled(parent, enabled);
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProducts_emptyListAll() {
        //given
        boolean enabled = true;
        // when
        List<ProductOperations> products = productService.getProducts(false);
        // then
        assertTrue(products.isEmpty());
        verify(productConnectorMock, times(1))
                .findByEnabled(enabled);
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProducts_notEmptyAll() {
        // given
        boolean enabled = true;
        DummyProduct product = mockInstance(new DummyProduct());
        when(productConnectorMock.findByEnabled(Mockito.anyBoolean()))
                .thenReturn(List.of(product));
        // when
        List<ProductOperations> products = productService.getProducts(false);
        // then
        assertEquals(1, products.size());
        verify(productConnectorMock, times(1))
                .findByEnabled(enabled);
        verifyNoMoreInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
    }


    @Test
    void createProduct_incorrectMultiProductRoleConfig() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        list.add(mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.DELEGATE, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        InvalidRoleMappingException e = assertThrows(InvalidRoleMappingException.class, executable);
        assertEquals(String.format("Only '%s' Party-role can have more than one Product-role", PartyRole.OPERATOR.name()), e.getMessage());
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void createProduct_correctMultiProductRoleConfig() {
        // given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        list.add(mockInstance(new DummyProductRole(), 2));
        when(productLogoImageServiceMock.getDefaultImageUrl())
                .thenReturn(LOGO_URL);
        when(productDepictImageServiceMock.getDefaultImageUrl())
                .thenReturn(DEPICT_IMAGE_URL);
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        // when
        Executable executable = () -> productService.createProduct(input);
        // then
        assertDoesNotThrow(executable);
        verify(productLogoImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productDepictImageServiceMock, times(1))
                .getDefaultImageUrl();
        ArgumentCaptor<ProductOperations> saveCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        verify(productConnectorMock, times(1))
                .insert(saveCaptor.capture());
        ProductOperations savedProduct = saveCaptor.getValue();
        assertEquals(DEPICT_IMAGE_URL, savedProduct.getDepictImageUrl());
        assertEquals(LOGO_URL, savedProduct.getLogo());
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void createProduct_existsNotEnabled() {
        //given
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        map.put(PartyRole.MANAGER, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        when(productConnectorMock.insert(any(ProductOperations.class)))
                .thenThrow(ResourceAlreadyExistsException.class);
        when(productLogoImageServiceMock.getDefaultImageUrl())
                .thenReturn(LOGO_URL);
        when(productDepictImageServiceMock.getDefaultImageUrl())
                .thenReturn(DEPICT_IMAGE_URL);
        when(productConnectorMock.existsByIdAndEnabledFalse(id))
                .thenReturn(true);
        //when
        Executable executable = () -> productService.createProduct(input);
        //then
        assertDoesNotThrow(executable);
        verify(productLogoImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productDepictImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productConnectorMock, times(1))
                .insert(input);
        verify(productConnectorMock, times(1))
                .existsByIdAndEnabledFalse(id);
        verify(productConnectorMock, times(1))
                .save(input);
        verifyNoMoreInteractions(productConnectorMock);

    }

    @Test
    void createProduct_alreadyExistsActive() {
        //given
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        map.put(PartyRole.MANAGER, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        when(productConnectorMock.insert(any(ProductOperations.class)))
                .thenThrow(ResourceAlreadyExistsException.class);
        when(productLogoImageServiceMock.getDefaultImageUrl())
                .thenReturn(LOGO_URL);
        when(productDepictImageServiceMock.getDefaultImageUrl())
                .thenReturn(DEPICT_IMAGE_URL);
        when(productConnectorMock.existsByIdAndEnabledFalse(id))
                .thenReturn(false);
        //when
        Executable executable = () -> productService.createProduct(input);
        //then
        ResourceAlreadyExistsException e = assertThrows(ResourceAlreadyExistsException.class, executable);
        assertEquals(String.format("Product %s already exists and is still active", input.getId()), e.getMessage());
        verify(productLogoImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productDepictImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productConnectorMock, times(1))
                .insert(input);
        verify(productConnectorMock, times(1))
                .existsByIdAndEnabledFalse(id);
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void createProduct_ok() {
        // given
        Instant now = Instant.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        map.put(PartyRole.MANAGER, new DummyProductRoleInfo(true, list));
        input.setRoleMappings(map);
        input.setId(id);
        when(productConnectorMock.insert(any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        when(productLogoImageServiceMock.getDefaultImageUrl())
                .thenReturn(LOGO_URL);
        when(productDepictImageServiceMock.getDefaultImageUrl())
                .thenReturn(DEPICT_IMAGE_URL);
        // when
        ProductOperations output = productService.createProduct(input);
        // then
        verify(productLogoImageServiceMock, times(1))
                .getDefaultImageUrl();
        verify(productDepictImageServiceMock, times(1))
                .getDefaultImageUrl();
        assertNotNull(output);
        assertNotNull(output.getContractTemplateUpdatedAt());
        assertEquals(LOGO_URL, output.getLogo());
        assertEquals(DEPICT_IMAGE_URL, output.getDepictImageUrl());
        assertTrue(output.getContractTemplateUpdatedAt().isAfter(now));
        ArgumentCaptor<ProductOperations> saveCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        verify(productConnectorMock, times(1))
                .insert(saveCaptor.capture());
        ProductOperations savedProduct = saveCaptor.getValue();
        assertEquals(DEPICT_IMAGE_URL, savedProduct.getDepictImageUrl());
        assertEquals(LOGO_URL, savedProduct.getLogo());
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void createProduct_subProduct() {
        //given
        Instant now = Instant.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        input.setId(id);
        input.setParentId("parentId");
        input.setContractTemplatePath("templatePath");
        input.setContractTemplateVersion("contractVersion");
        input.setTitle("title");
        when(productConnectorMock.existsById(any()))
                .thenReturn(true);
        when(productConnectorMock.insert(any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations output = productService.createProduct(input);
        // then
        assertNotNull(output);
        assertNotNull(output.getContractTemplateUpdatedAt());
        assertTrue(output.getContractTemplateUpdatedAt().isAfter(now));
        ArgumentCaptor<ProductOperations> createCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        verify(productConnectorMock, times(1))
                .insert(createCaptor.capture());
        ProductOperations capturedOps = createCaptor.getValue();
        assertEquals(input.getContractTemplatePath(), capturedOps.getContractTemplatePath());
        assertEquals(input.getId(), capturedOps.getId());
        assertEquals(input.getContractTemplateVersion(), capturedOps.getContractTemplateVersion());
        assertEquals(input.getTitle(), capturedOps.getTitle());
        assertEquals(input.getParentId(), capturedOps.getParentId());
        verify(productConnectorMock, times(1))
                .existsById(input.getParentId());
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void createProduct_parentNotFound() {
        //given
        OffsetDateTime now = OffsetDateTime.now().minusSeconds(1);
        String id = "id";
        ProductOperations input = new DummyProduct();
        input.setId(id);
        input.setParentId("parentId");
        input.setContractTemplatePath("templatePath");
        input.setContractTemplateVersion("contractVersion");
        input.setTitle("title");
        when(productConnectorMock.existsById(any()))
                .thenReturn(false);
        when(productConnectorMock.insert(any(ProductOperations.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        Executable executable = () -> productService.createProduct(input);
        //then
        ValidationException e = assertThrows(ValidationException.class, executable);
        assertEquals("For id = " + input.getParentId(), e.getCause().getMessage());
        verify(productConnectorMock, times(1))
                .existsById(any());
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void deleteProduct_existEnabled() {
        // given
        String productId = "productId";
        ProductOperations product = mockInstance(new DummyProduct(), "setRoleMappings");
        // when
        productService.deleteProduct(productId);
        // then
        verify(productConnectorMock, times(1)).
                disableById(productId);
        verifyNoMoreInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void getProduct_Enabled() {
        // given
        String productId = "productId";
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> Optional.of(new DummyProduct()));
        // when
        ProductOperations product = productService.getProduct(productId);
        // then
        assertNotNull(product);
        verify(productConnectorMock, times(1)).findById(productId);
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void getProduct_notEnabled() {
        // given
        String id = "id";
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenAnswer(invocationOnMock -> {
                    ProductOperations product = mockInstance(new DummyProduct(), "setId", "setEnabled", "setRoleMappings");
                    product.setId(invocationOnMock.getArgument(0, String.class));
                    product.setEnabled(false);
                    return Optional.of(product);
                });
        // when
        Executable executable = () -> productService.getProduct(id);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        verify(productConnectorMock, times(1)).findById(id);
        verifyNoMoreInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void getProduct_null() {
        // given
        String productId = "productId";
        // when
        Executable executable = () -> productService.getProduct(productId);
        // then
        Assertions.assertThrows(ResourceNotFoundException.class, executable);
        verify(productConnectorMock, times(1)).findById(productId);
        verifyNoMoreInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
    }


    @Test
    void updateProduct_foundProductEnabledDiffVersionContract() {
        // given
        String productId = "productId";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setParentId");
        when(productConnectorMock.findById(productId))
                .thenReturn(Optional.of(new DummyProduct()));
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        list.add(mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        product.setBackOfficeEnvironmentConfigurations(Map.of("test", mockInstance(new DummyBackOfficeConfigurations())));
        when(productConnectorMock.save(any()))
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
        assertEquals(savedProduct.getLogoBgColor(), product.getLogoBgColor());
        assertEquals(savedProduct.getContractTemplatePath(), product.getContractTemplatePath());
        assertEquals(savedProduct.getContractTemplateVersion(), product.getContractTemplateVersion());
        assertEquals(savedProduct.getBackOfficeEnvironmentConfigurations(), product.getBackOfficeEnvironmentConfigurations());
        verify(productConnectorMock, times(1)).findById(productId);
        verify(productConnectorMock, times(1)).save(any());
        verifyNoMoreInteractions(productConnectorMock);
    }


    @Test
    void updateProduct_foundProductEnabledSameVersionContract() {
        // given
        String productId = "productId";
        String contractTemplateVersion = "1.2.4";
        when(productConnectorMock.findById(productId))
                .thenAnswer(invocationOnMock -> {
                    DummyProduct foundProductMock = new DummyProduct();
                    foundProductMock.setId(invocationOnMock.getArgument(0, String.class));
                    foundProductMock.setContractTemplateVersion(contractTemplateVersion);
                    return Optional.of(foundProductMock);
                });
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(mockInstance(new DummyProductRole(), 1));
        list.add(mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        ProductOperations product = mockInstance(new DummyProduct(), "setId",
                "setRoleMappings",
                "setContractTemplateVersion",
                "setParentId");
        product.setRoleMappings(map);
        product.setContractTemplateVersion(contractTemplateVersion);
        product.setBackOfficeEnvironmentConfigurations(Map.of("test", mockInstance(new DummyBackOfficeConfigurations())));
        when(productConnectorMock.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations savedProduct = productService.updateProduct(productId, product);
        // then
        assertNull(savedProduct.getLogo());
        assertEquals(savedProduct.getTitle(), product.getTitle());
        assertEquals(savedProduct.getDescription(), product.getDescription());
        assertEquals(savedProduct.getUrlPublic(), product.getUrlPublic());
        assertEquals(savedProduct.getUrlBO(), product.getUrlBO());
        assertEquals(savedProduct.getLogoBgColor(), product.getLogoBgColor());
        assertEquals(savedProduct.getRoleMappings(), product.getRoleMappings());
        assertEquals(savedProduct.getContractTemplatePath(), product.getContractTemplatePath());
        assertEquals(savedProduct.getContractTemplateVersion(), product.getContractTemplateVersion());
        assertEquals(savedProduct.getBackOfficeEnvironmentConfigurations(), product.getBackOfficeEnvironmentConfigurations());
        verify(productConnectorMock, times(1)).findById(productId);
        verify(productConnectorMock, times(1)).save(any());
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void updateProduct_foundProductNotEnabled() {
        // given
        String productId = "productId";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setEnabled(false);
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.updateProduct(productId, product);
        // then
        assertThrows(ResourceNotFoundException.class, executable);
        verify(productConnectorMock, times(1)).findById(productId);
        verifyNoMoreInteractions(productConnectorMock);
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
        verify(productConnectorMock, times(1)).findById(productId);
        verifyNoMoreInteractions(productConnectorMock);
    }

    @Test
    void updateProduct_subProduct() {
        // given
        String productId = "productId";
        String parentId = "parentId";
        String contractTemplateVersion = "1.2.4";
        when(productConnectorMock.findById(productId))
                .thenAnswer(invocationOnMock -> {
                    DummyProduct foundProductMock = new DummyProduct();
                    foundProductMock.setId(invocationOnMock.getArgument(0, String.class));
                    foundProductMock.setContractTemplateVersion(contractTemplateVersion);
                    foundProductMock.setParentId(parentId);
                    return Optional.of(foundProductMock);
                });
        ProductOperations product = mockInstance(new DummyProduct(),
                "setId",
                "setRoleMappings",
                "setContractTemplateVersion"
        );
        product.setContractTemplateVersion(contractTemplateVersion);
        when(productConnectorMock.save(any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ProductOperations.class));
        // when
        ProductOperations savedProduct = productService.updateProduct(productId, product);
        //then
        assertEquals(savedProduct.getTitle(), product.getTitle());
        assertEquals(savedProduct.getDescription(), product.getDescription());
        assertEquals(savedProduct.getUrlPublic(), product.getUrlPublic());
        assertEquals(savedProduct.getUrlBO(), product.getUrlBO());
        assertNull(savedProduct.getRoleMappings());
        assertEquals(savedProduct.getContractTemplatePath(), product.getContractTemplatePath());
        assertEquals(savedProduct.getContractTemplateVersion(), product.getContractTemplateVersion());
        verify(productConnectorMock, times(1)).findById(productId);
        verify(productConnectorMock, times(1)).save(any());
        verifyNoMoreInteractions(productConnectorMock);

    }

    @Test
    void updateProductStatus_nullId() {
        // given
        String id = null;
        ProductStatus status = ProductStatus.ACTIVE;
        // when
        Executable executable = () -> productService.updateProductStatus(id, status);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(REQUIRED_PRODUCT_ID_MESSAGE, e.getMessage());
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void updateProductStatus_nullStatus() {
        // given
        String id = "id";
        ProductStatus status = null;
        // when
        Executable executable = () -> productService.updateProductStatus(id, status);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals(REQUIRED_PRODUCT_STATUS_MESSAGE, e.getMessage());
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void updateProductStatus() {
        // given
        String id = "id";
        ProductStatus status = ProductStatus.ACTIVE;
        // when
        Executable executable = () -> productService.updateProductStatus(id, status);
        // then
        assertDoesNotThrow(executable);
        verify(productConnectorMock, times(1)).updateProductStatus(id, status);
        verifyNoMoreInteractions(productConnectorMock);
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
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void storeProductLogo_subProductException() {
        // given
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_GIF_VALUE;
        String fileName = "filename";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setDepictImageUrl(null);
        product.setId(productId);
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.saveProductLogo(productId, depictImage, contentType, fileName);
        // then
        ValidationException e = assertThrows(ValidationException.class, executable);
        assertEquals("Given product Id = " + productId + " is of a subProduct", e.getMessage());
        verifyNoInteractions(productLogoImageServiceMock);
    }

    @Test
    void storeProductLogoImage_ok() {
        //given
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_JPEG_VALUE;
        String fileName = "filename.jpeg";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setParentId");
        product.setDepictImageUrl(null);
        product.setId(productId);
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        //when
        Executable executable = () -> productService.saveProductLogo(productId, depictImage, contentType, fileName);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<ProductOperations> productCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        verify(productLogoImageServiceMock, times(1))
                .saveImage(productCaptor.capture(), Mockito.eq(depictImage), Mockito.eq(contentType), Mockito.eq(fileName));
        ProductOperations capturedProduct = productCaptor.getValue();
        TestUtils.reflectionEqualsByName(product, capturedProduct);
        verify(productConnectorMock, times(1))
                .findById(productId);
        verifyNoMoreInteractions(productLogoImageServiceMock, productConnectorMock);

    }

    @Test
    void storeProductDepictImage_nullId() {
        //given
        String productId = null;
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = "contentType";
        String filename = "filename";
        // when
        Executable executable = () -> productService.saveProductDepictImage(productId, depictImage, contentType, filename);
        // then
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        Assertions.assertEquals("A product id is required", e.getMessage());
        verifyNoInteractions(productConnectorMock);
    }

    @Test
    void storeProductDepictImage_subProductException() {
        // given
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_GIF_VALUE;
        String fileName = "filename";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings");
        product.setDepictImageUrl(null);
        product.setId(productId);
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        // when
        Executable executable = () -> productService.saveProductDepictImage(productId, depictImage, contentType, fileName);
        // then
        ValidationException e = assertThrows(ValidationException.class, executable);
        assertEquals("Given product Id = " + productId + " is of a subProduct", e.getMessage());
        verifyNoInteractions(productLogoImageServiceMock);
    }

    @Test
    void storeProductDepictImage_ok() {
        //given
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_JPEG_VALUE;
        String fileName = "filename.jpeg";
        ProductOperations product = mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setParentId");
        product.setDepictImageUrl(null);
        product.setId(productId);
        when(productConnectorMock.findById(Mockito.anyString()))
                .thenReturn(Optional.of(product));
        //when
        Executable executable = () -> productService.saveProductDepictImage(productId, depictImage, contentType, fileName);
        //then
        assertDoesNotThrow(executable);
        ArgumentCaptor<ProductOperations> productCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        verify(productDepictImageServiceMock, times(1))
                .saveImage(productCaptor.capture(), Mockito.eq(depictImage), Mockito.eq(contentType), Mockito.eq(fileName));
        ProductOperations capturedProduct = productCaptor.getValue();
        TestUtils.reflectionEqualsByName(product, capturedProduct);
        verify(productConnectorMock, times(1))
                .findById(productId);
        verifyNoMoreInteractions(productDepictImageServiceMock, productConnectorMock);

    }


}