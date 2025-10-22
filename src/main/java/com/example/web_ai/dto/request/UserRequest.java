package com.example.web_ai.dto.request;

import com.example.web_ai.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class UserRequest {
    @NotBlank
    String fullName;

    @NotBlank
    String username;

    @NotBlank
    String password;

    @Email
    @NotBlank
    String email;

    @NotBlank
    String phone;

    Role role;

    @NotNull
    Boolean active;

}
