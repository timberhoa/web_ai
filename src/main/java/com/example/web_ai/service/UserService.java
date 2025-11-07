package com.example.web_ai.service;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UpdateProfileMeRequest;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.FacultySimpleResponse;
import com.example.web_ai.dto.response.GradeResponse;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.entity.Image;
import com.example.web_ai.entity.User;
import com.example.web_ai.enums.Role;
import com.example.web_ai.exception.BadRequestException;
import com.example.web_ai.exception.NotFoundException;
import com.example.web_ai.mapper.UserMapper;
import com.example.web_ai.repository.ImageRepository;
import com.example.web_ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;
    private final UserMapper userMapper;

    public UserResponse getUserById(UUID id) {
        User u = userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        return userMapper.toResponse(u);
    }

    public UserResponse getUserByUsername(String username){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));

        return userMapper.toResponse(user);
    }

    public UserResponse updateProfileMe(String username, UpdateProfileMeRequest req){
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new NotFoundException("User Not Found"));
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    public UserResponse updateUser(UUID id, UserRequest req) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));

        if (req.getFullName() != null) u.setFullName(req.getFullName());
        if (req.getUsername() != null) u.setUsername(req.getUsername());
        if (req.getEmail() != null) u.setEmail(req.getEmail());
        if (req.getPhone() != null) u.setPhone(req.getPhone());

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

    public void uploadImage(UUID userId, MultipartFile file) throws IOException{
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

    public GradeResponse getALlGrade(UUID userId){
        // necessary logic to get all grade here
        return GradeResponse.builder().message("Retrieve successfully").build();
    }

    public List<UserResponse> getAllStudents() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        
        return students.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }
}
