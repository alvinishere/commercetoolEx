package com.example.commercetoolEx.config;

import com.commercetools.api.client.ProjectApiRoot;
import com.commercetools.api.defaultconfig.ApiRootBuilder;
import com.commercetools.api.defaultconfig.ServiceRegion;
import io.vrap.rmf.base.client.oauth2.ClientCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommerceToolConfig {
    @Value("${ctp.clientId}")
    private String clientId;
    @Value("${ctp.clientSecret}")
    private String clientSecret;
    @Value("${ctp.projectKey}")
    private String projectKey;

    @Bean
    public ProjectApiRoot createApiClient() {
        Object ServiceRegion;
        return ApiRootBuilder.of()
                .defaultClient(ClientCredentials.of()
                                .withClientId(clientId)
                                .withClientSecret(clientSecret)
                                .build(),
                        com.commercetools.api.defaultconfig.ServiceRegion.GCP_AUSTRALIA_SOUTHEAST1)
                .build(projectKey);
    }
}
