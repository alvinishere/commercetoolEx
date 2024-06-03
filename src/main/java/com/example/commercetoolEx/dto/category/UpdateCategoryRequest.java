package com.example.commercetoolEx.dto.category;

import com.commercetools.api.models.category.CategoryUpdateAction;

import java.util.List;

public record UpdateCategoryRequest(List<CategoryUpdateAction> actions) {
}
