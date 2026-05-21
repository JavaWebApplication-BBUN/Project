package com.busticket.service;

import com.busticket.dto.ProfileDTO;
import com.busticket.entity.User;
import com.busticket.entity.UserProfile;
import com.busticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileDTO getUserProfile() {
        User user = getCurrentUser();
        return mapToDTO(user.getProfile());
    }

    @Transactional
    public ProfileDTO updateUserProfile(ProfileDTO profileDTO) {
        User user = getCurrentUser();
        UserProfile profile = user.getProfile();
        
        profile.setFullName(profileDTO.getFullName());
        profile.setPhoneNumber(profileDTO.getPhoneNumber());
        profile.setEmail(profileDTO.getEmail());
        
        // UserRepository sẽ tự động lưu UserProfile vì có CascadeType.ALL
        userRepository.save(user);
        
        return mapToDTO(profile);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProfileDTO mapToDTO(UserProfile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setFullName(profile.getFullName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setEmail(profile.getEmail());
        return dto;
    }
}
