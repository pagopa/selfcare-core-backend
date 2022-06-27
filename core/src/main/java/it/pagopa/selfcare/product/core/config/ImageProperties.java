package it.pagopa.selfcare.product.core.config;

import java.util.Set;

public interface ImageProperties {

    Set<String> getAllowedMimeTypes();

    Set<String> getAllowedExtensions();

    String getDefaultUrl();

}
