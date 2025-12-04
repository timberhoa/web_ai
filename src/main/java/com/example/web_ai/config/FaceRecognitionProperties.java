package com.example.web_ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "face.recognition")
@Data
public class FaceRecognitionProperties {
    private String apiUrl;
    private Integer apiTimeout = 30000;
    private Float similarityThreshold = 0.35f;
    private Boolean enabled = true;
}
