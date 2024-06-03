package com.example.commercetoolEx.controller;

import com.commercetools.api.models.product_type.ProductTypePagedQueryResponse;
import com.example.commercetoolEx.service.ProductTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/productType")
public class ProductTypeController {
    private final ProductTypeService productTypeService;

    @GetMapping(path = "/all")
    public Mono<ProductTypePagedQueryResponse> getAllProductTypes(
            @RequestParam(name = "limit", defaultValue = "100") int limit
    ) {
        return productTypeService.getAllProductTypes(limit);
    }
}
