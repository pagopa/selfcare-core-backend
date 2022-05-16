package it.pagopa.selfcare.product.core;

import it.pagopa.selfcare.commons.utils.TestUtils;
import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.api.ProductConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import it.pagopa.selfcare.product.connector.model.*;
import it.pagopa.selfcare.product.core.config.CoreTestConfig;
import it.pagopa.selfcare.product.core.config.ImageProperties;
import it.pagopa.selfcare.product.core.exception.FileValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ProductLogoImageServiceImpl.class, CoreTestConfig.class})
@TestPropertySource(properties = {
        "product.img.logo.allowed-mime-types=image/png",
        "product.img.logo.allowed-extensions=png",
        "product.img.logo.default-url=https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png"
})
class ProductLogoImageServiceImplTest {

    @Autowired
    @Qualifier("productLogoImageService")
    ProductLogoImageServiceImpl productLogoImageService;

    @MockBean
    ProductConnector productConnectorMock;

    @MockBean
    FileStorageConnector fileStorageConnectorMock;

    @Autowired
    @Qualifier("logoImageProperties")
    ImageProperties logoImagePropertiesMock;

    @Test
    void saveImage_nullFileName() {
        //given
        String fileName = null;
        String contentType = MimeTypeUtils.IMAGE_JPEG_VALUE;
        InputStream inputImage = InputStream.nullInputStream();
        ProductOperations product = TestUtils.mockInstance(new DummyProduct());
        //when
        Executable executable = () -> productLogoImageService.saveImage(product, inputImage, contentType, fileName);
        //then
        FileValidationException e = assertThrows(FileValidationException.class, executable);
        assertEquals("file name cannot be null", e.getMessage());
        Mockito.verifyNoInteractions(fileStorageConnectorMock, productConnectorMock);

    }

    @Test
    void getDefaultImageUrl() {
        //given

        //when
        //then
        assertEquals(logoImagePropertiesMock.getDefaultUrl(), productLogoImageService.getDefaultImageUrl());
    }

    @Test
    void saveImage_invalidContentType() {
        //given
        InputStream inputImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_GIF_VALUE;
        String fileName = "filename.gif";
        ProductOperations productOperations = TestUtils.mockInstance(new DummyProduct());
        //when
        Executable executable = () -> productLogoImageService.saveImage(productOperations, inputImage, contentType, fileName);
        //then
        FileValidationException fileValidationException = assertThrows(FileValidationException.class, executable);
        assertTrue(InvalidMimeTypeException.class.isAssignableFrom(fileValidationException.getCause().getClass()));
        Mockito.verifyNoInteractions(fileStorageConnectorMock, productConnectorMock);
    }

    @Test
    void saveImage_invalidExtension() {
        //given
        InputStream inputImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.gif";
        ProductOperations productOperations = TestUtils.mockInstance(new DummyProduct());
        //when
        Executable executable = () -> productLogoImageService.saveImage(productOperations, inputImage, contentType, fileName);
        //then
        FileValidationException fileValidationException = assertThrows(FileValidationException.class, executable);
        assertTrue(IllegalArgumentException.class.isAssignableFrom(fileValidationException.getCause().getClass()));
        assertEquals(fileValidationException.getMessage(), String.format("Invalid file extension \"%s\": allowed only %s", StringUtils.getFilenameExtension(fileName), logoImagePropertiesMock.getAllowedExtensions()));
        Mockito.verifyNoInteractions(fileStorageConnectorMock, productConnectorMock);
    }

    @Test
    void saveImage_uploadException() throws MalformedURLException {
        //given
        InputStream inputImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String productId = "productId";
        String fileName = "fileName.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct());
        product.setId(productId);
        Mockito.doThrow(FileUploadException.class)
                .when(fileStorageConnectorMock).uploadProductImg(Mockito.any(), Mockito.any(), Mockito.anyString());
        //when
        Executable executable = () -> productLogoImageService.saveImage(product, inputImage, contentType, fileName);
        //then
        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, executable);
        Assertions.assertNotNull(exception.getCause());
        Assertions.assertTrue(FileUploadException.class.isAssignableFrom(exception.getCause().getClass()));
        Mockito.verify(fileStorageConnectorMock, Mockito.times(1))
                .uploadProductImg(inputImage, String.format("resources/products/%s/logo.png", productId), contentType);
        Mockito.verifyNoMoreInteractions(fileStorageConnectorMock);
    }


    @Test
    void saveImage_nullUrl() throws FileUploadException, MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setParentId");
        product.setLogo(null);
        product.setId(productId);
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        URL uriToUrl = uriMock.toURL();
        Mockito.when(fileStorageConnectorMock.uploadProductImg(Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(uriToUrl);
        //when
        productLogoImageService.saveImage(product, depictImage, contentType, fileName);
        //then
        Mockito.verify(fileStorageConnectorMock, Mockito.times(1))
                .uploadProductImg(depictImage, String.format("resources/products/%s/logo.png", productId), contentType);
        ArgumentCaptor<ProductOperations> productCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .save(productCaptor.capture());
        ProductOperations capturedProduct = productCaptor.getValue();
        assertEquals(uriToUrl.toString(), capturedProduct.getLogo());
        Mockito.verifyNoMoreInteractions(fileStorageConnectorMock, productConnectorMock);
    }

    @Test
    void storeProductDepictImage_defaultUrl() throws FileUploadException, MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setRoleMappings", "setParentId");
        product.setLogo("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        product.setId(productId);
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/default/logo.png");
        URL uriToUrl = uriMock.toURL();
        Mockito.when(fileStorageConnectorMock.uploadProductImg(Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(uriToUrl);
        //when
        productLogoImageService.saveImage(product, depictImage, contentType, fileName);
        //then
        Mockito.verify(fileStorageConnectorMock, Mockito.times(1))
                .uploadProductImg(depictImage, String.format("resources/products/%s/logo.png", productId), contentType);
        ArgumentCaptor<ProductOperations> productCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .save(productCaptor.capture());
        ProductOperations capturedProduct = productCaptor.getValue();
        assertEquals(uriToUrl.toString(), capturedProduct.getLogo());
        Mockito.verifyNoMoreInteractions(fileStorageConnectorMock, productConnectorMock);
    }

    @Test
    void updateProductDepictImage_logoUrl() throws MalformedURLException, URISyntaxException {
        //give
        String productId = "productId";
        InputStream depictImage = InputStream.nullInputStream();
        String contentType = MimeTypeUtils.IMAGE_PNG_VALUE;
        String fileName = "filename.png";
        ProductOperations product = TestUtils.mockInstance(new DummyProduct(), "setId", "setParentId", "setRoleMappings");
        product.setLogo("https://selcdcheckoutsa.blob.core.windows.net/$web/resources/products/default/logo.png");
        product.setId(productId);
        EnumMap<PartyRole, DummyProductRoleInfo> map = new EnumMap<>(PartyRole.class);
        List<DummyProductRole> list = new ArrayList<>();
        list.add(TestUtils.mockInstance(new DummyProductRole(), 1));
        list.add(TestUtils.mockInstance(new DummyProductRole(), 2));
        map.put(PartyRole.OPERATOR, new DummyProductRoleInfo(true, list));
        URI uriMock = new URI("https://selcdcheckoutsa.z6.web.core.windows.net/resources/products/prod-1/logo.png");
        URL uriToUrl = uriMock.toURL();
        product.setRoleMappings(map);
        product.setContractTemplateVersion("1.2.4");
        Mockito.when(fileStorageConnectorMock.uploadProductImg(Mockito.any(), Mockito.any(), Mockito.anyString()))
                .thenReturn(uriToUrl);
        //when
        productLogoImageService.saveImage(product, depictImage, contentType, fileName);
        //then
        Mockito.verify(fileStorageConnectorMock, Mockito.times(1))
                .uploadProductImg(depictImage, String.format("resources/products/%s/logo.png", productId), contentType);
        ArgumentCaptor<ProductOperations> productCaptor = ArgumentCaptor.forClass(ProductOperations.class);
        Mockito.verify(productConnectorMock, Mockito.times(1))
                .save(productCaptor.capture());
        ProductOperations capturedProduct = productCaptor.getValue();
        assertEquals(uriToUrl.toString(), capturedProduct.getLogo());
        Mockito.verifyNoMoreInteractions(productConnectorMock);
    }
}