package com.example.web_ai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class LoginRequest {
    @NotBlank
    String username;
    @NotBlank
    String password;


}
