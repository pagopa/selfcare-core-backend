package it.pagopa.selfcare.product.connector.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FileUploadException extends RuntimeException{
    public  FileUploadException(Throwable cause){super(cause);}
}
