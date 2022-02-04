package it.pagopa.selfcare.product.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.web.model.CreateProductDto;
import it.pagopa.selfcare.product.web.model.ProductResource;
import it.pagopa.selfcare.product.web.model.UpdateProductDto;
import it.pagopa.selfcare.product.web.model.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "product")
public class ProductController {

    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProducts}")
    public List<ProductResource> getProducts() {
        List<ProductOperations> products = productService.getProducts();
        return products.stream()
                .map(ProductMapper::toResource)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.saveProductLogo}")
    public Object saveProductLogo(@ApiParam("${swagger.product.model.id}")
                                  @PathVariable("id") String id,
                                  @ApiParam("${swagger.product.model.logo}")
                                  @RequestPart("logo") MultipartFile logo) throws IOException {

        log.trace("saveProductLogo start");
        log.debug("id = {}, logo = {}", id, logo);
        productService.saveProductLogo(id, logo.getInputStream(), logo.getContentType(), logo.getOriginalFilename());
        log.trace("saveProductLogo end");
        return null;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProduct}")
    public ProductResource getProduct(@ApiParam("${swagger.product.model.id}")
                                      @PathVariable("id")
                                              String id) {
        log.trace("getProduct start");
        log.debug("id = {}", id);
        ProductOperations product = productService.getProduct(id);
        log.trace("getProduct end");
        return ProductMapper.toResource(product);
    }


    @GetMapping("/{id}/role-mappings")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProductRoleMappings}")
    public Map<PartyRole, List<String>> getProductRoles(@ApiParam("${swagger.product.model.id}")
                                                        @PathVariable("id")
                                                                String id) {
        log.trace("getProductRoles");
        log.debug("id = {}", id);
        return productService.getProduct(id).getRoleMappings();
    }


    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.product.operation.createProduct}")
    public ProductResource createProduct(@RequestBody
                                         @Valid
                                                 CreateProductDto product) {
        log.trace("createProduct start");
        log.debug("product = {}", product);
        ProductOperations p = productService.createProduct(ProductMapper.fromDto(product));
        log.trace("createProduct end");
        return ProductMapper.toResource(p);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.updateProduct}")
    public ProductResource updateProduct(@ApiParam("${swagger.product.model.id}")
                                         @PathVariable("id")
                                                 String id,
                                         @RequestBody
                                         @Valid
                                                 UpdateProductDto product) {
        log.trace("updateProduct start");
        log.debug("id = {}, product = {}", id, product);
        ProductOperations updatedProduct = productService.updateProduct(id, ProductMapper.fromDto(product));
        log.debug("updatedProduct = {}", updatedProduct);
        log.trace("updateProduct end");
        return ProductMapper.toResource(updatedProduct);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.product.operation.deleteProduct}")
    public void deleteProduct(@ApiParam("${swagger.product.model.id}")
                              @PathVariable("id")
                                      String id) {
        log.trace("deleteProduct start");
        log.debug("id = {}", id);
        productService.deleteProduct(id);
        log.trace("deleteProduct end");
    }

}
