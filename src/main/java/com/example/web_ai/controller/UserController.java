package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getUserById")
    public UserResponse getUserByUsername(@RequestParam UUID id) {
        return userService.getUserById(id);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication authentication){
        String username = authentication.getName();
        log.info("🔹 Username from Authentication: {}", username);
        UserResponse res = userService.getUserByUsername(username);


        return ResponseEntity.ok(res);

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable UUID id,
                                               @RequestBody UserRequest req) {
        return ResponseEntity.ok(userService.updateUser(id, req));
    }

    @PutMapping("/updatePassword/{id}")
    public void updatePassword(@RequestParam UUID id, @RequestBody ResetPassword req) {
        userService.updatePassword(id, req);
    }
}
