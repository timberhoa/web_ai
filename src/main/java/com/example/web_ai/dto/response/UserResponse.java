package com.example.web_ai.dto.response;

import com.example.web_ai.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class UserResponse {
    UUID id;
    String fullName;
    String username;
    String email;
    String phone;
    Role role;
    Boolean active;
}
