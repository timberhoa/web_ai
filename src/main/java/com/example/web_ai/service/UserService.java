package com.example.web_ai.service;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UpdateProfileMeRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.ForgotPasswordResponse;
import com.example.web_ai.dto.response.GradeResponse;
import com.example.web_ai.dto.response.ResetPasswordResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.dto.response.ValidateTokenResponse;
import com.example.web_ai.entity.Image;
import com.example.web_ai.entity.PasswordResetToken;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.ImageRepository;
import com.example.web_ai.repository.PasswordResetTokenRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
//    private final EmailService emailService;

    public UserResponse getUserById(UUID id) {
        User u = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        return userMapper.toResponse(u);
    }

    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    public UserResponse updateProfileMe(String username, UpdateProfileMeRequest req) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        if (req.getFullName() != null)
            user.setFullName(req.getFullName());
        if (req.getUsername() != null)
            user.setUsername(req.getUsername());
        if (req.getEmail() != null)
            user.setEmail(req.getEmail());
        if (req.getPhone() != null)
            user.setPhone(req.getPhone());

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (req.getFullName() != null)
            u.setFullName(req.getFullName());
        if (req.getUsername() != null)
            u.setUsername(req.getUsername());
        if (req.getEmail() != null)
            u.setEmail(req.getEmail());
        if (req.getPhone() != null)
            u.setPhone(req.getPhone());

        User saved = userRepository.save(u);

        return userMapper.toResponse(saved);
    }

    public void updatePassword(UUID id, ResetPassword req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (req.getConfirmPassword() != null &&
                !req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BadRequestException("PASSWORD_CONFIRM_NOT_MATCH");
        }

        if (!passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
            throw new BadRequestException("OLD_PASSWORD_INCORRECT");
        }

        if (req.getNewPassword().length() < 8) {
            throw new BadRequestException("PASSWORD_TOO_WEAK");
        }
        if (passwordEncoder.matches(req.getNewPassword(), u.getPassword())) {
            throw new BadRequestException("NEW_PASSWORD_MUST_DIFFER_FROM_OLD");
        }

        u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(u);
    }

    public void uploadImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        byte[] imageBytes = file.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String base64WithPrefix = "data:" + file.getContentType() + ";base64," + base64Image;

        Image image = Image.builder()
                .imageData(base64WithPrefix)
                .contentType(file.getContentType())
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .user(user)
                .build();

        // Save image directly instead of adding to user's images collection
        // This avoids potential issues with bidirectional mapping
        imageRepository.save(image);
    }

    public Image getImage(UUID imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image Not Found"));

        return image;
    }

    public GradeResponse getALlGrade(UUID userId) {
        // necessary logic to get all grade here
        return GradeResponse.builder().message("Retrieve successfully").build();
    }

    public Page<UserResponse> getAllStudents(Pageable pageable) {
        Page<User> students = userRepository.findByRole(Role.STUDENT, pageable);

        return students.map(userMapper::toResponse);
    }

    public Page<UserResponse> searchUsers(String searchTerm, String roleStr, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BadRequestException("SEARCH_TERM_REQUIRED");
        }

        String trimmedSearchTerm = searchTerm.trim();
        Page<User> users;

        if (roleStr != null && !roleStr.trim().isEmpty()) {
            try {
                Role role = Role.valueOf(roleStr.toUpperCase().trim());
                users = userRepository.searchByMultipleFieldsAndRole(trimmedSearchTerm, role, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("INVALID_ROLE: " + roleStr);
            }
        } else {
            users = userRepository.searchByMultipleFields(trimmedSearchTerm, pageable);
        }

        return users.map(userMapper::toResponse);
    }

    // ========== Password Reset Methods ==========

    /**
     * Create a password reset token and send it via email
     * 
     * @param email User's email address
     * @return Response with masked email
     */
    public ForgotPasswordResponse createPasswordResetToken(String email) {
        // Find user by email (case insensitive via repository query)
        User user = userRepository.findByEmail(email)
                .orElse(null);

        // Security: Always return success message even if user not found
        // This prevents email enumeration attacks
        if (user == null) {
            log.warn("Password reset requested for non-existent email: {}", email);
            return ForgotPasswordResponse.builder()
                    .message("If an account exists with this email, a password reset link has been sent.")
                    .tokenSentTo(maskEmail(email))
                    .build();
        }

        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        // Generate secure random token
        String token = UUID.randomUUID().toString();

        // Create token entity with 24-hour expiration
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Send email (or log to console in dev mode)
        String resetUrl = "http://localhost:3000/reset-password?token=" + token;
//        emailService.sendPasswordResetEmail(user.getEmail(), token, resetUrl);

        log.info("Password reset token created for user: {}", user.getUsername());

        return ForgotPasswordResponse.builder()
                .message("If an account exists with this email, a password reset link has been sent.")
                .tokenSentTo(maskEmail(email))
                .build();
    }

    /**
     * Validate a password reset token
     * 
     * @param token Reset token
     * @return Validation response
     */
    public ValidateTokenResponse validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElse(null);

        if (resetToken == null) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Invalid reset token")
                    .build();
        }

        if (resetToken.getUsed()) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("This reset token has already been used")
                    .build();
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Reset token has expired")
                    .build();
        }

        return ValidateTokenResponse.builder()
                .valid(true)
                .message("Token is valid")
                .build();
    }

    /**
     * Reset user password using a valid token
     * 
     * @param token       Reset token
     * @param newPassword New password
     * @return Response message
     */
    public ResetPasswordResponse resetPasswordWithToken(String token, String newPassword, String confirmPassword) {
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("PASSWORD_CONFIRM_NOT_MATCH");
        }

        // Validate password strength
        if (newPassword.length() < 8) {
            throw new BadRequestException("PASSWORD_TOO_WEAK");
        }

        // Find and validate token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("INVALID_RESET_TOKEN"));

        if (resetToken.getUsed()) {
            throw new BadRequestException("TOKEN_ALREADY_USED");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("TOKEN_EXPIRED");
        }

        // Get user and update password
        User user = resetToken.getUser();

        // Check if new password is same as old password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("NEW_PASSWORD_MUST_DIFFER_FROM_OLD");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getUsername());

        return ResetPasswordResponse.builder()
                .message("Password has been reset successfully. You can now login with your new password.")
                .build();
    }

    /**
     * Mask email for privacy (e.g., "user@example.com" -> "u***@example.com")
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 1) {
            return username + "***@" + domain;
        }

        return username.charAt(0) + "***@" + domain;
    }
}
