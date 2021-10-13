package it.pagopa.selfcare.product.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document("products")
public class Product {
    @Id
    private String id;

    private String title;
    private String description;

    public Product(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
