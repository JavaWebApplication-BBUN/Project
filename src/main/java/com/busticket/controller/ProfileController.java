package com.busticket.controller;

import com.busticket.dto.ProfileDTO;
import com.busticket.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDTO> getUserProfile() {
        return ResponseEntity.ok(profileService.getUserProfile());
    }

    @PutMapping
    public ResponseEntity<ProfileDTO> updateUserProfile(@RequestBody ProfileDTO profileDTO) {
        return ResponseEntity.ok(profileService.updateUserProfile(profileDTO));
    }
}
