package it.pagopa.selfcare.product.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

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
    private OffsetDateTime activationDateTime;
    private boolean enabled = true;

}
