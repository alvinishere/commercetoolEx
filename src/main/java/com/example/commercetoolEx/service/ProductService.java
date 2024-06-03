package com.example.commercetoolEx.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.common.LocalizedString;
import com.commercetools.api.models.common.UpdateAction;
import com.commercetools.api.models.product.*;
import com.commercetools.api.models.product_type.AttributeDefinition;
import com.commercetools.api.models.product_type.ProductType;
import com.commercetools.api.models.product_type.ProductTypeReference;
import com.commercetools.api.models.product_type.ProductTypeResourceIdentifier;
import com.example.commercetoolEx.dto.product.AddProductRequest;
import com.example.commercetoolEx.dto.product.ProductTypeAttributePojo;
import com.example.commercetoolEx.dto.product.UpdateProductRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProjectApiRoot apiRoot;
    private final ProductTypeService productTypeService;

    public Mono<ProductPagedQueryResponse> getAllProducts(int limit, int offset) {
        return Mono.fromCompletionStage(apiRoot
                        .products()
                        .get()
                        .addLimit(limit)
                        .addOffset(offset)
                        .execute())
                .map(ApiHttpResponse::getBody);
    }

    public Mono<Product> getProductByIdentifier(String identifier) {
        return Mono.fromCompletionStage(apiRoot
                        .products()
                        .withKey(identifier)
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody)
                .onErrorResume(ex -> Mono.fromCompletionStage(apiRoot
                                .products()
                                .withId(identifier)
                                .get()
                                .execute())
                        .map(ApiHttpResponse::getBody)
                ).onErrorResume(Mono::error);
    }

    public Mono<Product> addProduct(AddProductRequest requestBody) {
        return productTypeService.getProductTypeByIdentifier(requestBody.productTypeKey())
                .flatMap(productType -> Mono.fromCompletionStage(apiRoot
                                .products()
                                .post(buildProductDraft(requestBody, productType))
                                .execute())
                        .map(ApiHttpResponse::getBody)
                )
                .doOnError(Mono::error);
    }

    private ProductDraft buildProductDraft(AddProductRequest requestBody, ProductType productType) {
        return ProductDraft.builder()
                .key(UUID.randomUUID().toString())
                .productType(ProductTypeResourceIdentifier.builder().id(productType.getId()).build())
                .name(LocalizedString.of(Locale.US, requestBody.productName()))
                .slug(LocalizedString.of(Locale.US, requestBody.productName().toLowerCase().replace(" ", "-")))
                .masterVariant(buildVariantDraft(productType, requestBody.attributes()))
                .build();
    }

    private ProductVariantDraft buildVariantDraft(ProductType productType, List<ProductTypeAttributePojo> attributesData) {
        String uniqueIdentifier = UUID.randomUUID().toString();
        return ProductVariantDraft.builder()
                .sku(uniqueIdentifier)
                .key(uniqueIdentifier)
                .attributes(getAttributes(productType.getAttributes(), attributesData))
                .build();
    }

    private List<Attribute> getAttributes(List<AttributeDefinition> attributes, List<ProductTypeAttributePojo> attributesData) {
        Map<String, String> requestAttributeMap = new HashMap<>();
        attributesData.forEach(data -> {
            requestAttributeMap.put(data.key(), data.value());
        });
        return attributes.stream().map(data -> {
            String attributeName = data.getName();
            if (requestAttributeMap.containsKey(attributeName)) {
                return Attribute.builder()
                        .name(attributeName)
                        .value(requestAttributeMap.get(attributeName))
                        .build();
            }
            return null;
        }).collect(Collectors.toList());
    }

    public Mono<Product> updateProduct(String id, UpdateProductRequest request) {
        return Mono.fromCompletionStage(apiRoot
                        .products()
                        .withId(id)
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody)
                .flatMap(product -> productTypeService.getProductTypeByIdentifier(
                        product.getProductType().getId()
                ).flatMap(productType -> Mono.fromCompletionStage(apiRoot
                                .products()
                                .withId(id)
                                .post(ProductUpdate.builder()
                                        .version(product.getVersion())
                                        .actions(updateActions(productType.getAttributes(), request))
                                        .build())
                                .execute())
                        .map(ApiHttpResponse::getBody)));
    }

    private List<ProductUpdateAction> updateActions(List<AttributeDefinition> attributeList, UpdateProductRequest request) {
        return request.actions().stream().map(action -> {
            if (action.getAction().equals("addVariant")) {
                String uniqueIdentifier = UUID.randomUUID().toString();
                return ProductUpdateAction.addVariantBuilder()
                        .key(uniqueIdentifier)
                        .sku(uniqueIdentifier)
                        .attributes(getAttributes(attributeList, request.attributes()))
                        .build();
            }
            return action;
        }).collect(Collectors.toList());
    }

    public Mono<Product> deleteProduct(String id) {
        return Mono.fromCompletionStage(apiRoot
                        .products()
                        .withId(id)
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody)
                .flatMap(product -> Mono.fromCompletionStage(apiRoot
                        .products()
                        .withId(id)
                        .delete()
                        .withVersion(product.getVersion())
                        .execute()).map(ApiHttpResponse::getBody));
    }
}
