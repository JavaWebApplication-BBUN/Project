package com.busticket.controller;

import com.busticket.dto.LoginRequestDTO;
import com.busticket.dto.RegisterRequestDTO;
import com.busticket.entity.User;
import com.busticket.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.ok("Đăng ký tài khoản thành công!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        User user = authService.login(dto);

        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);

        // Đóng gói dữ liệu trả về dạng JSON gồm message và role
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công!");
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Xóa sạch session khi đăng xuất
        }
        return ResponseEntity.ok("Đã đăng xuất thành công!");
    }
}