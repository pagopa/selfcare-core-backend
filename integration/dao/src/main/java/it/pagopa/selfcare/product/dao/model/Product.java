package it.pagopa.selfcare.product.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("products")
public class Product {
    @Id
    private String id;

    private String logo;
    private String title;
    private String description;
    private String urlPublic;
    private String urlBO;
    private String code;
    private OffsetDateTime creationDateTime;
    private OffsetDateTime contractTemplateUpdateDateTime;
    private Map<String, List<String>> roleMappings;
    private String contractTemplatePath;
    private String contractTemplateVersion;
    private String roleManagementURL;
    private boolean enabled = true;

}
