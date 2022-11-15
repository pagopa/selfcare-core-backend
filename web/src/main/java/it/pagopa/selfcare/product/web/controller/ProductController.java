package it.pagopa.selfcare.product.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.product.connector.model.PartyRole;
import it.pagopa.selfcare.product.connector.model.ProductOperations;
import it.pagopa.selfcare.product.connector.model.ProductStatus;
import it.pagopa.selfcare.product.core.ProductService;
import it.pagopa.selfcare.product.web.model.*;
import it.pagopa.selfcare.product.web.model.mapper.ProductMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.EnumMap;
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
        log.trace("getProducts start");
        List<ProductOperations> products = productService.getProducts(true);
        List<ProductResource> productResources = products.stream()
                .map(ProductMapper::toResource)
                .collect(Collectors.toList());
        log.debug("getProducts result = {}", productResources);
        log.trace("getProducts end");
        return productResources;
    }


    @GetMapping("/tree")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProductsTree}")
    public List<ProductTreeResource> getProductsTree() {
        log.trace("getProductsTree start");
        List<ProductOperations> products = productService.getProducts(false);
        List<ProductTreeResource> result = ProductMapper.toTreeResource(products);
        log.debug("getProductsTree result = {}", result);
        log.trace("getProductsTree end");
        return result;
    }

    @PutMapping(value = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.saveProductLogo}")
    public Object saveProductLogo(@ApiParam("${swagger.product.model.id}")
                                  @PathVariable("id") String id,
                                  @ApiParam("${swagger.product.model.logoImage}")
                                  @RequestPart("logo") MultipartFile logo) throws IOException {

        log.trace("saveProductLogo start");
        log.debug("saveProductLogo id = {}, logo = {}", id, logo);
        productService.saveProductLogo(id, logo.getInputStream(), logo.getContentType(), logo.getOriginalFilename());
        log.trace("saveProductLogo end");
        return null;
    }

    @PutMapping(value = "/{id}/depict-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.saveProductDepictImage}")
    public Object saveProductDepictImage(@ApiParam("${swagger.product.model.id}")
                                         @PathVariable("id") String id,
                                         @ApiParam("${swagger.product.model.depictImage}")
                                         @RequestPart("depictImage") MultipartFile depictImage) throws IOException {
        log.trace("saveProductDepictImage start");
        log.debug("saveProductDepictImage id = {}, logo = {}", id, depictImage);
        productService.saveProductDepictImage(id, depictImage.getInputStream(), depictImage.getContentType(), depictImage.getOriginalFilename());
        log.trace("saveProductDepictImage end");
        return null;
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProduct}")
    public ProductResource getProduct(@ApiParam("${swagger.product.model.id}")
                                      @PathVariable("id")
                                              String id) {
        log.trace("getProduct start");
        log.debug("getProduct id = {}", id);
        ProductOperations product = productService.getProduct(id);
        ProductResource productResource = ProductMapper.toResource(product);
        log.debug("getProduct result = {}", productResource);
        log.trace("getProduct end");
        return productResource;
    }

    @GetMapping("/{id}/role-mappings")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.getProductRoleMappings}")
    public Map<PartyRole, ProductRoleInfo> getProductRoles(@ApiParam("${swagger.product.model.id}")
                                                           @PathVariable("id")
                                                                   String id) {
        log.trace("getProductRoles start");
        log.debug("getProductRoles id = {}", id);
        EnumMap<PartyRole, ProductRoleInfo> productRoles = ProductMapper.toRoleMappings(productService.getProduct(id).getRoleMappings());
        log.debug("getProductRoles result = {}", productRoles);
        log.trace("getProductRoles end");

        return productRoles;
    }


    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.product.operation.createProduct}")
    public ProductResource createProduct(@RequestBody
                                         @Valid
                                                 CreateProductDto product) {
        log.trace("createProduct start");
        log.debug("createProduct product = {}", product);
        ProductOperations p = productService.createProduct(ProductMapper.fromDto(product));
        ProductResource createdProduct = ProductMapper.toResource(p);
        log.debug("createProduct result = {}", createdProduct);
        log.trace("createProduct end");
        return createdProduct;
    }


    @PostMapping(value = "/{id}/sub-products", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "", notes = "${swagger.product.operation.createProduct}")
    public ProductResource createSubProduct(@ApiParam("${swagger.product.model.id}")
                                            @PathVariable("id") String id,
                                            @RequestBody
                                            @Valid
                                                    CreateSubProductDto product) {
        log.trace("createProduct start");
        log.debug("createProduct product = {}", product);
        ProductOperations productOps = ProductMapper.fromDto(product);
        productOps.setParentId(id);
        ProductOperations p = productService.createProduct(productOps);
        ProductResource createdProduct = ProductMapper.toResource(p);
        log.debug("createProduct result = {}", createdProduct);
        log.trace("createProduct end");
        return createdProduct;
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
        log.debug("updateProduct id = {}, product = {}", id, product);
        ProductOperations updatedProduct = productService.updateProduct(id, ProductMapper.fromDto(product));
        ProductResource result = ProductMapper.toResource(updatedProduct);
        log.debug("updateProduct result = {}", result);
        log.trace("updateProduct end");
        return result;
    }

    @PutMapping(value = "/{id}/sub-products", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.updateProduct}")
    public ProductResource updateSubProduct(@ApiParam("${swagger.product.model.id}")
                                            @PathVariable("id")
                                                    String id,
                                            @RequestBody
                                            @Valid
                                            UpdateSubProductDto product) {
        log.trace("updateSubProduct start");
        log.debug("updateSubProduct id = {}, product = {}", id, product);
        ProductOperations updatedProduct = productService.updateProduct(id, ProductMapper.fromDto(product));
        ProductResource result = ProductMapper.toResource(updatedProduct);
        log.debug("updateSubProduct result = {}", result);
        log.trace("updateSubProduct end");
        return result;
    }

    @PutMapping(value = "/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.product.operation.updateProductStatus}")
    public void updateProductStatus(@ApiParam("${swagger.product.model.id}")
                                    @PathVariable("id")
                                    String id,
                                    @ApiParam("${swagger.product.model.status}")
                                    @RequestParam(value = "status")
                                    ProductStatus status) {
        log.trace("updateProductStatus start");
        log.debug("updateProductStatus id = {}, status = {}", id, status);
        productService.updateProductStatus(id, status);
        log.trace("updateProductStatus end");
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "", notes = "${swagger.product.operation.deleteProduct}")
    public void deleteProduct(@ApiParam("${swagger.product.model.id}")
                              @PathVariable("id")
                              String id) {
        log.trace("deleteProduct start");
        log.debug("deleteProduct id = {}", id);
        productService.deleteProduct(id);
        log.trace("deleteProduct end");
    }

}
