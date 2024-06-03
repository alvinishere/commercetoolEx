package com.example.commercetoolEx.controller;

import com.commercetools.api.models.category.Category;
import com.commercetools.api.models.category.CategoryPagedQueryResponse;
import com.example.commercetoolEx.dto.category.AddCategoryRequest;
import com.example.commercetoolEx.dto.category.UpdateCategoryRequest;
import com.example.commercetoolEx.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping(path = "/all")
    public Mono<CategoryPagedQueryResponse> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @GetMapping(path = "/{id}")
    public Mono<Category> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping(path = "/add")
    public Mono<Category> addCategory(@RequestBody AddCategoryRequest request) {
        return categoryService.addCategory(request.categoryName());
    }

    @PostMapping(path = "/update/{id}")
    public Mono<Category> updateCategory(@PathVariable String id, @RequestBody UpdateCategoryRequest request) {
        return categoryService.updateCategory(id, request.actions());
    }

    @DeleteMapping(path = "/{id}")
    public Mono<Category> deleteCategory(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }
}
