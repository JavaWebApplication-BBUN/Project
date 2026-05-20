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

        // Tạo session và lưu thông tin đăng nhập của user vào server
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);

        return ResponseEntity.ok("Đăng nhập thành công! Vai trò của bạn là: " + user.getRole());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Xóa sạch session khi đăng xuất
        }
        return ResponseEntity.ok("Đã đăng xuất thành công!");
    }
}