package com.example.commercetoolEx.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.product_type.ProductType;
import com.commercetools.api.models.product_type.ProductTypePagedQueryResponse;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifier;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifierBuilder;
import io.vrap.rmf.base.client.ApiHttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductTypeService {
    private final ProjectApiRoot apiRoot;

    public Mono<ProductTypePagedQueryResponse> getAllProductTypes(int limit) {
        return Mono.fromCompletionStage(apiRoot
                        .productTypes()
                        .get()
                        .addLimit(limit)
                        .execute())
                .map(ApiHttpResponse::getBody);
    }


    public Mono<ProductType> getProductTypeByIdentifier(String identifier) {
        return Mono.fromCompletionStage(apiRoot
                        .productTypes()
                        .withKey(identifier)
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody)
                .onErrorResume(ex -> {
                    log.warn("Error fetching via key {}", identifier, ex);
                    return Mono.fromCompletionStage(apiRoot
                                    .productTypes()
                                    .withId(identifier)
                                    .get()
                                    .execute())
                            .map(ApiHttpResponse::getBody);
                });
    }
//    public ProductTypeResourceIdentifier getProductTypeById(String s) {
//        ProductType productType = apiRoot.productTypes().withId(s).get().executeBlocking().getBody();
//        return ProductTypeResourceIdentifier.builder().id(productType.getId()).build();
//    }
}
