package it.pagopa.selfcare.product.web.model;

import lombok.Data;

import java.util.List;

@Data
public class ProductTreeResource {

    private ProductResource node;
    private List<ProductResource> children;

}
