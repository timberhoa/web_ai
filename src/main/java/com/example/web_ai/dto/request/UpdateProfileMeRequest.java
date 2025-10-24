package com.example.web_ai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class UpdateProfileMeRequest {
    @NotBlank
    String fullName;

    @NotBlank
    String username;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String phone;
}