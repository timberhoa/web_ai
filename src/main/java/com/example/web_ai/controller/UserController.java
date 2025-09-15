package com.example.web_ai.controller;

import com.example.web_ai.dto.request.ResetPassword;
import com.example.web_ai.dto.request.UserRequest;
import com.example.web_ai.dto.response.UserResponse;
import com.example.web_ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getUserById")
    public UserResponse getUserByUsername(@RequestParam UUID id) {
        return userService.getUserById(id);
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
