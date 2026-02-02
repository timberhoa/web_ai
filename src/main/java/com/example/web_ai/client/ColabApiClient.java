package com.example.web_ai.client;

import com.example.web_ai.config.FaceRecognitionProperties;
import com.example.web_ai.exception.FaceRecognitionApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ColabApiClient {

    private final FaceRecognitionProperties properties;
    private final WebClient.Builder webClientBuilder;

    public Map<String, Object> extractVectorsBatch(List<MultipartFile> images, String studentId, String studentName) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            for (MultipartFile file : images) {
                builder.part("files", new ByteArrayResource(file.getBytes()) {
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                });
            }

            // Add student info for logging
            if (studentId != null) {
                builder.part("student_id", studentId);
            }
            if (studentName != null) {
                builder.part("student_name", studentName);
            }

            return webClientBuilder.build()
                    .post()
                    .uri(properties.getApiUrl() + "/extract-vectors-batch")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (IOException e) {
            log.error("Error reading image bytes", e);
            throw new FaceRecognitionApiException("Error processing images: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Colab API", e);
            throw new FaceRecognitionApiException("Error calling Face Recognition API: " + e.getMessage());
        }
    }

    public Map<String, Object> extractVector(MultipartFile image) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });

            return webClientBuilder.build()
                    .post()
                    .uri(properties.getApiUrl() + "/extract-vector")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (IOException e) {
            log.error("Error reading image bytes", e);
            throw new FaceRecognitionApiException("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Colab API", e);
            throw new FaceRecognitionApiException("Error calling Face Recognition API: " + e.getMessage());
        }
    }

    public Map<String, Object> verify(MultipartFile image, String targetVector, String studentId, String studentName) {
        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
            builder.part("target_vector", targetVector);

            // Add student info for logging
            if (studentId != null) {
                builder.part("student_id", studentId);
            }
            if (studentName != null) {
                builder.part("student_name", studentName);
            }

            return webClientBuilder.build()
                    .post()
                    .uri(properties.getApiUrl() + "/verify")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(builder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

        } catch (IOException e) {
            log.error("Error reading image bytes", e);
            throw new FaceRecognitionApiException("Error processing image: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Colab API", e);
            throw new FaceRecognitionApiException("Error calling Face Recognition API: " + e.getMessage());
        }
    }
}
