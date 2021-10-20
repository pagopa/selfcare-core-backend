package it.pagopa.selfcare.product.web;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.dao.model.Product;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/products")
@Api(tags = "product")
public class ProductController {

    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "${swagger.product.operation.getProducts}")
    public List<ProductResource> getProducts() {
        List<Product> products = productService.getProducts();
        return products.stream()
                .map(ProductResource::create)
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "${swagger.product.operation.getProduct}")
    public ProductResource getProduct(@PathVariable("id") String id) {
        Product product = productService.getProduct(id);
        return ProductResource.create(product);
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "${swagger.product.operation.createProduct}")
    public ProductResource createProduct(@RequestBody
                                                 CreateProductDto product) {
        Product p = productService.createProduct(CreateProductDto.toEntity(product));
        return ProductResource.create(p);
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "${swagger.product.operation.updateProduct}")
    public ProductResource updateProduct(@Parameter(description = "${swagger.product.model.id}")
                                         @PathVariable("id")
                                                 String id,
                                         @RequestBody
                                                 UpdateProductDto product) {
        Product updatedProduct = productService.updateProduct(id, UpdateProductDto.toEntity(product));
        return ProductResource.create(updatedProduct);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(description = "${swagger.product.operation.deleteProduct}")
    public void deleteProduct(@Parameter(description = "${swagger.product.model.id}")
                              @PathVariable("id")
                                      String id) {
        productService.deleteProduct(id);
    }


    @DeleteMapping("/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProducts() {
        productService.deleteProducts();
    }

}
