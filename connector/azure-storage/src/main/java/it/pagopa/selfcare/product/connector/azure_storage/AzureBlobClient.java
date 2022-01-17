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
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

@Slf4j
@Service
@Profile("AzureStorage")
class AzureBlobClient implements FileStorageConnector {

    private final String institutionsLogoContainerReference;
    private final CloudBlobClient blobClient;


    AzureBlobClient(@Value("${blobStorage.connectionString}") String storageConnectionString,
                    @Value("${blobStorage.product.logo.containerReference}") String productLogoContainerReference)
            throws URISyntaxException, InvalidKeyException {
        if (log.isDebugEnabled()) {
            log.trace("AzureBlobClient.AzureBlobClient");
            log.debug("storageConnectionString = {}, containerReference = {}",
                    storageConnectionString, productLogoContainerReference);
        }
        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();
        this.institutionsLogoContainerReference = productLogoContainerReference;
    }


    @Override
    public void uploadProductLogo(InputStream file, String fileName, String contentType) throws FileUploadException {
        if (log.isDebugEnabled()) {
            log.trace("AzureBlobClient.uploadInstitutionLogo");
            log.debug("fileName = {}, contentType = {}", fileName, contentType);
        }

        try {
            final CloudBlobContainer blobContainer = blobClient.getContainerReference(institutionsLogoContainerReference);
            final CloudBlockBlob blob = blobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(contentType);
            blob.upload(file, file.available());
            log.info("Uploaded {}", fileName);

        } catch (StorageException | URISyntaxException | IOException e) {
            throw new FileUploadException(e);
        }
    }

}
