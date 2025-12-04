package com.example.web_ai.service;

import com.example.web_ai.client.ColabApiClient;
import com.example.web_ai.dto.FaceRegistrationResponse;
import com.example.web_ai.dto.FaceStatusResponse;
import com.example.web_ai.dto.HybridCheckInResponse;
import com.example.web_ai.entity.User;
import com.example.web_ai.exception.*;
import com.example.web_ai.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FaceRecognitionService {

    private final ColabApiClient colabApiClient;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public FaceRegistrationResponse registerFace(UUID userId, List<MultipartFile> images) {
        if (images == null || images.size() < 3 || images.size() > 10) {
            throw new InvalidImageException("Requires 3-10 images for registration");
        }

        for (MultipartFile img : images) {
            if (img.isEmpty() || img.getSize() > 5 * 1024 * 1024) { // 5MB limit
                throw new InvalidImageException("Image must not be empty and under 5MB");
            }
            String contentType = img.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                throw new InvalidImageException("Only JPEG and PNG images are allowed");
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Call Colab API to extract average vector
            Map<String, Object> response = colabApiClient.extractVectorsBatch(images);

            if (!Boolean.TRUE.equals(response.get("success"))) {
                throw new FaceRecognitionApiException((String) response.get("message"));
            }

            List<Double> averageVector = (List<Double>) response.get("average_vector");
            String vectorJson = objectMapper.writeValueAsString(averageVector);

            // Update user
            user.setFaceVector(vectorJson);
            user.setFaceRegistered(true);
            user.setFaceRegisteredAt(LocalDateTime.now());
            // user.setFaceQualityScore(...) // If API returns quality score

            userRepository.save(user);

            return FaceRegistrationResponse.builder()
                    .success(true)
                    .message("Face registered successfully")
                    .imagesProcessed((Integer) response.get("images_processed"))
                    .registeredAt(user.getFaceRegisteredAt())
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing face vector", e);
        }
    }

    public HybridCheckInResponse verifyFace(UUID userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!Boolean.TRUE.equals(user.getFaceRegistered()) || user.getFaceVector() == null) {
            throw new FaceNotRegisteredException("User has not registered face data");
        }

        try {
            Map<String, Object> response = colabApiClient.verify(image, user.getFaceVector());

            if (!Boolean.TRUE.equals(response.get("success"))) {
                throw new FaceRecognitionApiException((String) response.get("message"));
            }

            Boolean isMatch = (Boolean) response.get("is_match");
            Double confidence = (Double) response.get("confidence"); // API returns float/double

            return HybridCheckInResponse.builder()
                    .success(true)
                    .isMatch(isMatch)
                    .confidence(confidence != null ? confidence.floatValue() : 0f)
                    .message((String) response.get("message"))
                    .build();

        } catch (Exception e) {
            log.error("Face verification failed", e);
            throw new FaceRecognitionApiException("Face verification failed: " + e.getMessage());
        }
    }

    public FaceStatusResponse getFaceStatus(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return FaceStatusResponse.builder()
                .isRegistered(user.getFaceRegistered())
                .registeredAt(user.getFaceRegisteredAt())
                .qualityScore(user.getFaceQualityScore())
                .build();
    }

    @Transactional
    public void deleteFaceData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFaceVector(null);
        user.setFaceRegistered(false);
        user.setFaceRegisteredAt(null);
        user.setFaceQualityScore(null);

        userRepository.save(user);
    }

    public org.springframework.data.util.Pair<User, Double> identifyStudent(List<User> students, MultipartFile image) {
        // 1. Extract vector from input image
        Map<String, Object> response = colabApiClient.extractVector(image);
        if (!Boolean.TRUE.equals(response.get("success"))) {
            throw new FaceRecognitionApiException((String) response.get("message"));
        }

        List<Double> inputVector = (List<Double>) response.get("vector");
        if (inputVector == null) {
            throw new NoFaceDetectedException("No face detected in the image");
        }

        // 2. Compare with each student
        User bestMatch = null;
        double maxSimilarity = -1.0;
        double THRESHOLD = 0.35; // Same as Python API

        for (User student : students) {
            if (!Boolean.TRUE.equals(student.getFaceRegistered()) || student.getFaceVector() == null) {
                continue;
            }

            try {
                List<Double> studentVector = objectMapper.readValue(student.getFaceVector(), List.class);
                double similarity = calculateCosineSimilarity(inputVector, studentVector);

                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestMatch = student;
                }
            } catch (JsonProcessingException e) {
                log.error("Error parsing face vector for user {}", student.getId(), e);
            }
        }

        if (bestMatch != null && maxSimilarity >= THRESHOLD) {
            log.info("Identified student: {} with similarity: {}", bestMatch.getEmail(), maxSimilarity);
            return org.springframework.data.util.Pair.of(bestMatch, maxSimilarity);
        }

        throw new FaceVerificationFailedException("No matching student found (Max similarity: " + maxSimilarity + ")");
    }

    private double calculateCosineSimilarity(List<Double> v1, List<Double> v2) {
        if (v1.size() != v2.size()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1.get(i) * v2.get(i);
            normA += Math.pow(v1.get(i), 2);
            normB += Math.pow(v2.get(i), 2);
        }

        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
