package com.example.web_ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.awt.*;
import java.net.URI;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi(
            @Value("${openapi.service.api-docs:api-service}") String apiDocsGroup
    ) {
        return GroupedOpenApi.builder()
                .group(apiDocsGroup)
                .packagesToScan("com.example.web_ai.controller")
                .build();
    }

    @Bean
    public OpenAPI openAPI(
            @Value("${openapi.service.title:API Service}") String title,
            @Value("${openapi.service.version:1.0.0}") String version,
            @Value("${openapi.service.server:http://localhost:8080}") String serverUrl
    ) {
        return new OpenAPI()
                .servers(List.of(new Server().url(serverUrl)))
                .info(new Info()
                        .title(title)
                        .description("API documents")
                        .version(version)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }

    @Value("${openapi.service.server:http://localhost:8080}")
    private String serverUrl;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;

    @Value("${app.swagger.auto-open:true}")
    private boolean autoOpen;

    @EventListener(ApplicationReadyEvent.class)
    public void openSwaggerUI() {
        if (!autoOpen) return;
        try {
            String base = serverUrl.endsWith("/") ? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
            String path = swaggerUiPath.startsWith("/") ? swaggerUiPath : "/" + swaggerUiPath;
            String url = base + path;

            System.out.println("Swagger UI URL: " + url);

            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop != null) desktop.browse(new URI(url));
            } else {
                System.out.println("Desktop API is not supported. Please open manually: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
