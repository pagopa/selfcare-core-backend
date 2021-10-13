package it.pagopa.selfcare.product.dao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    //private Image logo;
    private String logo;
    private String title;
    private String description;
    private String urlPublic;
    private String urlBO;
    private String codeProduct;

    public Product(String logo, String title, String description, String urlPublic, String urlBO) {
        this.logo = logo;
        this.title = title;
        this.description = description;
        this.urlPublic = urlPublic;
        this.urlBO = urlBO;
    }

//    public Product(String title, String description) {
//        this.title = title;
//        this.description = description;
//    }
}
