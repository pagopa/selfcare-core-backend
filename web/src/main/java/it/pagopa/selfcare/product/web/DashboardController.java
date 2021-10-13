package it.pagopa.selfcare.product.web;

import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/products")
public class DashboardController {

    private ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResource> getProducts() {
        List<Product> products = productService.getProducts();
        return products.stream()
                .map(ProductResource::create)
                .collect(Collectors.toList());
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource createProduct(@RequestBody CreateProductDto product) {
        Product p = productService.createProduct(CreateProductDto.toEntity(product));
        return ProductResource.create(p);
    }

}
