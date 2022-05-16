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

    private final String institutionsImgContainerReference;
    private final CloudBlobClient blobClient;
    private final String publicHost;


    AzureBlobClient(@Value("${blobStorage.connectionString}") String storageConnectionString,
                    @Value("${blobStorage.product.img.containerReference}") String productImgContainerReference,
                    @Value("${blobStorage.product.upload.host}") String publicHost)
            throws URISyntaxException, InvalidKeyException {

        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        this.blobClient = storageAccount.createCloudBlobClient();
        this.institutionsImgContainerReference = productImgContainerReference;
        this.publicHost = publicHost;
    }


    @Override
    public URL uploadProductImg(InputStream file, String fileName, String contentType) throws FileUploadException, MalformedURLException {
        log.trace("uploadInstitutionLogo start");
        log.debug("uploadInstitutionLogo file = {}, fileName = {}, contentType = {}", file, fileName, contentType);
        URI logoUri = null;

        try {
            final CloudBlobContainer blobContainer = blobClient.getContainerReference(institutionsImgContainerReference);
            final CloudBlockBlob blob = blobContainer.getBlockBlobReference(fileName);
            blob.getProperties().setContentType(contentType);
            blob.upload(file, file.available());
            logoUri = blob.getUri();
            log.info("Uploaded {}", fileName);


        } catch (StorageException | URISyntaxException | IOException e) {
            throw new FileUploadException(e);
        }
        URL url = new URL(logoUri.toURL().getProtocol(), publicHost, logoUri.toURL().getFile().substring(5));
        log.debug("uploadProductLogo result = {}", url);
        log.trace("uploadProductLogo end");
        return url;
    }

}
