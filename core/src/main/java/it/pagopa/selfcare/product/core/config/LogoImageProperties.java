package it.pagopa.selfcare.product.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "product.img.logo")
@Data
@Qualifier("logoImageProperties")
public class LogoImageProperties implements ImageProperties {
    private Set<String> allowedMimeTypes;
    private Set<String> allowedExtensions;
    private String defaultUrl;
}
