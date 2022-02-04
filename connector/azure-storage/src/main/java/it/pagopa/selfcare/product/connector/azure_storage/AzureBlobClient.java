package it.pagopa.selfcare.product.connector.azure_storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import it.pagopa.selfcare.product.connector.api.FileStorageConnector;
import it.pagopa.selfcare.product.connector.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;

@Slf4j
@Service
@Profile("AzureStorage")
class AzureBlobClient implements FileStorageConnector {

    private final String institutionsLogoContainerReference;
    private final CloudBlobClient blobClient;
    private final String publicHost;


    AzureBlobClient(@Value("${blobStorage.connectionString}") String storageConnectionString,
                    @Value("${blobStorage.product.logo.containerReference}") String productLogoContainerReference,
                    @Value("${blobStorage.product.upload.host}") String publicHost)
            throws URISyntaxException, InvalidKeyException {

        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();
        this.institutionsLogoContainerReference = productLogoContainerReference;
        this.publicHost = publicHost;
    }


    @Override
    public URL uploadProductLogo(InputStream file, String fileName, String contentType) throws FileUploadException, MalformedURLException {
        log.trace("uploadInstitutionLogo start");
        log.debug("uploadInstitutionLogo file = {}, fileName = {}, contentType = {}%n", file, fileName, contentType);
        URI logoUri = null;

        try {
            final CloudBlobContainer blobContainer = blobClient.getContainerReference(institutionsLogoContainerReference);
            final CloudBlockBlob blob = blobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(contentType);
            blob.upload(file, file.available());
            logoUri = blob.getUri();// like this https://selcdcheckoutsa.z6.web.core.windows.net/institutions/inst-id/logo.png
            log.info("Uploaded {}", fileName);


        } catch (StorageException | URISyntaxException | IOException e) {
            throw new FileUploadException(e);
        }
        log.debug("uploadProductLogo file = {}, fileName = {}, contentType = {}", file, fileName, contentType);
        log.trace("uploadProductLogo end");
        return new URL(logoUri.toURL().getProtocol(), publicHost, logoUri.toURL().getFile().substring(5));
    }

}
