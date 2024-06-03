package com.example.commercetoolEx.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.models.category.*;
import com.commercetools.api.models.common.LocalizedString;
import com.example.commercetoolEx.dto.category.AddCategoryRequest;
import io.vrap.rmf.base.client.ApiHttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final ProjectApiRoot apiRoot;

    public Mono<CategoryPagedQueryResponse> getAllCategory() {
        return Mono.fromCompletionStage(apiRoot
                        .categories()
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody);
    }

    public Mono<Category> getCategoryById(String id) {
        return Mono.fromCompletionStage(apiRoot
                        .categories()
                        .withId(id)
                        .get()
                        .execute())
                .map(ApiHttpResponse::getBody);
    }

    public Mono<Category> addCategory(String name) {
        return Mono.fromCompletionStage(apiRoot
                .categories()
                .post(CategoryDraft.builder()
                        .key(name + "-category")
                        .name(LocalizedString.of(Locale.US, name))
                        .slug(LocalizedString.of(Locale.US, name.toLowerCase().replace(" ", "-") + "-slug"))
                        .build())
                .execute()).map(ApiHttpResponse::getBody);
    }

    public Mono<Category> updateCategory(String id, List<CategoryUpdateAction> actions) {
        return getCategoryById(id)
                .flatMap(category -> Mono.fromCompletionStage(apiRoot
                        .categories()
                        .withId(id)
                        .post(CategoryUpdate.builder()
                                .version(category.getVersion())
                                .actions(actions)
                                .build())
                        .execute()).map(ApiHttpResponse::getBody));
    }

    public Mono<Category> deleteCategory(String id) {
        return getCategoryById(id)
                .flatMap(category -> Mono.fromCompletionStage(apiRoot
                                .categories()
                                .withId(id)
                                .delete()
                                .withVersion(category.getVersion())
                                .execute())
                        .map(ApiHttpResponse::getBody));
    }
}
