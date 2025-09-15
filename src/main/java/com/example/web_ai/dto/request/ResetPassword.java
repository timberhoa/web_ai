package com.example.web_ai.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
public class ResetPassword {
    String oldPassword;
    String newPassword;
    String confirmPassword;

}
