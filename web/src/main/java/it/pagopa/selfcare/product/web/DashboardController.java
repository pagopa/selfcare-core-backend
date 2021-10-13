package it.pagopa.selfcare.product.web;

import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.ProductResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController("/products")
public class DashboardController {

    private ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/allProducts")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResource> getProducts() {
        List<Product> products = productService.getProducts();
        return products.stream()
                .map(ProductResource::create)
                .collect(Collectors.toList());
    }

    @PostMapping("/createProduct")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResource createProduct(@RequestBody Product product) {
        Product p = productService.createProduct(new Product(product.getLogo(), product.getTitle(), product.getDescription(), product.getUrlPublic(), product.getUrlBO()));
        return ProductResource.create(p);
    }

    @PutMapping("/products/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResource updateProduct(@PathVariable("id") String id, @RequestBody Product product) {

        Optional<Product> product1 = productService.updateProduct(id);
        Product p = null;
        if (product1.isPresent()) {
            Product product2 = product1.get();
            product2.setLogo(product.getLogo());
            product2.setTitle(product.getTitle());
            product2.setDescription(product.getDescription());
            product2.setUrlPublic(product.getUrlPublic());
            product2.setUrlBO(product.getUrlBO());
            p = productService.save(product2);
        }
        return ProductResource.create(p);
    }

    @DeleteMapping("/products/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProductResource deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        return null;
    }

    @DeleteMapping("/products")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProductResource deleteAllProduct() {
        productService.deleteProducts();
        return null;
    }

}
