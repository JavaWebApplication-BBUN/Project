package com.busticket.service;

import com.busticket.dto.LoginRequestDTO;
import com.busticket.dto.RegisterRequestDTO;
import com.busticket.entity.User;
import com.busticket.entity.UserProfile;
import com.busticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequestDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole("PASSENGER");

        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setEmail(dto.getEmail());

        user.setProfile(profile);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
    }

    @Transactional(readOnly = true)
    public User login(LoginRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Tài khoản hoặc mật khẩu không chính xác!");
        }

        return user;
    }
}
