package com.example.commercetoolEx.dto.product;

import java.util.List;
import java.util.Map;

public record AddProductRequest(String productName, List<ProductTypeAttributePojo> attributes, String productTypeKey) {
}
