package com.busticket.controller;

import com.busticket.config.JwtUtil;
import com.busticket.dto.LoginRequestDTO;
import com.busticket.dto.RegisterRequestDTO;
import com.busticket.entity.User;
import com.busticket.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.busticket.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        User user = authService.login(dto);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("role", user.getRole());
        response.put("message", "Đăng nhập thành công!");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.equals("anonymousUser")) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                Map<String, String> userInfo = new HashMap<>();
                if (user.getProfile() != null) {
                    userInfo.put("fullName", user.getProfile().getFullName());
                    userInfo.put("phone", user.getProfile().getPhoneNumber());
                } else {
                    userInfo.put("fullName", user.getUsername());
                    userInfo.put("phone", "");
                }
                userInfo.put("username", user.getUsername());
                return ResponseEntity.ok(userInfo);
            }
        }
        return ResponseEntity.status(401).body("Không tìm thấy thông tin người dùng.");
    }
}