package com.example.commercetoolEx.dto.product;

import com.commercetools.api.models.product.ProductUpdateAction;

import java.util.List;

public record UpdateProductRequest(List<ProductUpdateAction> actions, List<ProductTypeAttributePojo> attributes) {
}
