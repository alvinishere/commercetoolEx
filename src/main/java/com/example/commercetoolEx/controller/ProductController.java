package com.example.commercetoolEx.controller;

import com.commercetools.api.models.product.Product;
import com.commercetools.api.models.product.ProductPagedQueryResponse;
import com.example.commercetoolEx.dto.product.AddProductRequest;
import com.example.commercetoolEx.dto.product.UpdateProductRequest;
import com.example.commercetoolEx.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping(path = "/all")
    public Mono<ProductPagedQueryResponse> getAllProducts(
            @RequestParam(name = "limit", defaultValue = "100") int limit,
            @RequestParam(name = "offset", defaultValue = "0") int offset
    ) {
        return productService.getAllProducts(limit, offset);
    }

    @GetMapping(path = "/{identifier}")
    public Mono<Product> getProductByIdentifier(@PathVariable String identifier) {
        return productService.getProductByIdentifier(identifier);
    }

    @PostMapping(path = "/add")
    public Mono<Product> addProduct(@RequestBody AddProductRequest requestBody) {
        return productService.addProduct(requestBody);
    }

    @PostMapping(path = "/update/{id}")
    public Mono<Product> updateProduct(@PathVariable String id , @RequestBody UpdateProductRequest request){
        return productService.updateProduct(id, request);
    }

    @DeleteMapping(path = "/{id}")
    public Mono<Product>deleteProduct(@PathVariable String id){
        return productService.deleteProduct(id);
    }
}
